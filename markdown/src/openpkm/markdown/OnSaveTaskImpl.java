/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.PdfFilesProvider;
import openpkm.base.RemoteDataProvider;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.SourceProviders;
import openpkm.utils.HtmlUtils;
import org.netbeans.api.diff.Diff;
import org.netbeans.api.diff.DiffView;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.*;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Rok Koren
 */
public class OnSaveTaskImpl implements OnSaveTask
{
    private static final Logger LOG = Logger.getLogger(OnSaveTaskImpl.class.getName());  
    
    private static final RequestProcessor RP = new RequestProcessor(OnSaveTaskImpl.class);   
    
    private final Context context;

    public OnSaveTaskImpl(Context ctx) 
    {
        context = ctx;
    }  
     
    @Override
    public void performTask() 
    {
        try
        {
            Document document = context.getDocument();                
            String text = document.getText(0, document.getLength());  
            MarkdownDataObject data = (MarkdownDataObject)NbEditorUtilities.getDataObject(document);  
            
            RemoteDataProvider providerRemote = data.getLookup().lookup(RemoteDataProvider.class);
            if(providerRemote != null)
            {
                String remoteText = providerRemote.pull(); 
                List<String> baseLines = data.getPrimaryFile().asLines();
                List<String> remoteLines = Arrays.asList(remoteText.split("\n"));
                Patch<String> patchRemote = DiffUtils.diff(baseLines, remoteLines);
                if(patchRemote.getDeltas().isEmpty())
                {
                    providerRemote.push(text);                      
                }
                else
                {
                    StreamSource sourceRemote = StreamSource.createSource("Trello", "Remote", "text/x-markdown", new StringReader(remoteText));
                    StreamSource sourceLocal = StreamSource.createSource("OpenPKM", "Local", "text/x-markdown", new StringReader(text));

                    DiffView view = Diff.getDefault().createDiff(sourceRemote, sourceLocal);                               
 
                    /*
                    Component diffComp = Diff.getDefault().createDiff(
                        "Trello", "Remote", r1,
                        "OpenPKM", "Local", r2,
                        "text/plain"
                    ); 
                    diffComp.setPreferredSize(new Dimension(500, 400));
                    */
                    
                    Component comp = view.getComponent();
                    comp.setPreferredSize(new Dimension(500, 400));
                    DialogDescriptor dd = new DialogDescriptor(
                            comp,
                            "Do you want to overwrite remote",
                            true, // modalno
                            new Object[]{DialogDescriptor.OK_OPTION, DialogDescriptor.CANCEL_OPTION},
                            DialogDescriptor.OK_OPTION,
                            DialogDescriptor.DEFAULT_ALIGN,
                            null,
                            null
                    );                                        

                    Object result = DialogDisplayer.getDefault().notify(dd);

                    if (result == DialogDescriptor.OK_OPTION) 
                    {
                        providerRemote.push(text);                                               
                    }                    
                }
            }
            
            Project project = FileOwnerQuery.getOwner(data.getPrimaryFile());
            HtmlFilesProvider providerHtml = project.getLookup().lookup(HtmlFilesProvider.class);
            if(providerHtml != null)
            {
                String oldHtml = null;
                
                FileObject file = providerHtml.getDataDirectory().getFileObject(data.getPrimaryFile().getName(), HtmlFilesProvider.FILE_EXT);
                if(file == null)
                {
                    file = providerHtml.getDataDirectory().createData(data.getPrimaryFile().getName(), HtmlFilesProvider.FILE_EXT);
                } 
                else
                {
                    oldHtml = file.asText();
                }                
                
                String html = MarkdownService.getDeafult().getRenderer().render(MarkdownService.getDeafult().getParser().parse(text));   
                
                SourceProviders sourceProviders = project.getLookup().lookup(SourceProviders.class);
                if(sourceProviders != null)
                {
                    Set<String> oldLinks = HtmlUtils.findOpenPkmLinks(oldHtml);
                    Set<String> newLinks = HtmlUtils.findOpenPkmLinks(html);                

                    for (String oldLink : oldLinks) 
                    {
                        if (!newLinks.remove(oldLink)) 
                        {
                            try 
                            {
                                FileObject backlinkFile = sourceProviders.getDataDirectory().getFileObject(oldLink);
                                if (backlinkFile != null) 
                                {
                                    DataObject backlinkData = DataObject.find(backlinkFile);
                                    SourceProviderWrapper sourceProvider = backlinkData.getLookup().lookup(SourceProviderWrapper.class);
                                    if(sourceProvider != null)
                                    {
                                        sourceProvider.removeBacklink(data.getPrimaryFile().getNameExt());
                                    }
                                }
                            } 
                            catch (IOException e) 
                            {
                                LOG.warning(e.getMessage());
                            } 
                        }
                    }

                    for (String newLink : newLinks) 
                    {
                        try 
                        {
                            FileObject backlinkFile = sourceProviders.getDataDirectory().getFileObject(newLink);
                            if (backlinkFile != null) 
                            {
                                DataObject backlinkData = DataObject.find(backlinkFile);
                                SourceProviderWrapper sourceProvider = backlinkData.getLookup().lookup(SourceProviderWrapper.class);
                                if(sourceProvider != null)
                                {
                                    sourceProvider.addBacklink(data.getPrimaryFile().getNameExt());
                                }
                            }
                        } 
                        catch (IOException e) 
                        {
                            LOG.warning(e.getMessage());
                        }
                    }                       
                }                   
                
                OutputStream os = file.getOutputStream();
                os.write(html.getBytes());
                os.close();  
                LOG.info("HTML file saved");  
                StatusDisplayer.getDefault().setStatusText("HTML file saved");      
            }  

            PdfSave pdfSave = new PdfSave(project, data, text);
            RP.post(pdfSave);
        }  
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }        
        catch(BadLocationException e)
        {
            LOG.warning(e.getMessage());
        }                          
    }

    @Override
    public void runLocked(Runnable r) 
    {
        r.run();
    }

    @Override
    public boolean cancel()
    {
        return true;
    }      
    
    @MimeRegistration(mimeType = "text/x-markdown", service = OnSaveTask.Factory.class, position = 1600)
    public static class CustomOnSaveTaskFactory1 implements OnSaveTask.Factory 
    {
        @Override
        public OnSaveTask createTask(Context cntxt) 
        {
            return new OnSaveTaskImpl(cntxt);
        }
    }  
    
    private static class PdfSave implements Runnable
    {
        private final Project project;
        private final MarkdownDataObject data;
        private final String text;

        public PdfSave(Project project, MarkdownDataObject data, String text) 
        {
            this.project = project;
            this.data = data;
            this.text = text;
        }
        
        @Override
        public void run()
        {
            try
            {
                PdfFilesProvider providerPdf = Lookup.getDefault().lookup(PdfFilesProvider.class);
                if(providerPdf != null)
                {
                    FileObject file = providerPdf.getDataDirectory().getFileObject(data.getPrimaryFile().getName(), PdfFilesProvider.FILE_EXT);
                    if(file == null)
                    {
                        file = providerPdf.getDataDirectory().createData(data.getPrimaryFile().getName(), PdfFilesProvider.FILE_EXT);
                    } 
                    /*
                    Options options = Options.builder().safe(SafeMode.UNSAFE).sourcemap(false).standalone(true).backend("pdf").toFile(FileUtil.toFile(file)).attributes(getAttributes(true)).build();
                    AsciidoctorService.getDeafult().getAsciidoctor().convert(text, options);                  
                    */
                    LOG.info("PDF file saved");  
                    StatusDisplayer.getDefault().setStatusText("PDF file saved");      
                }              
            }  
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }              
        }        
    }     
}
