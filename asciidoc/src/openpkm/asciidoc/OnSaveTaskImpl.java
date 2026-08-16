/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.SourceProviders;
import openpkm.utils.HtmlUtils;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Rok Koren
 */
public class OnSaveTaskImpl implements OnSaveTask
{
    private static final Logger LOG = Logger.getLogger(OnSaveTaskImpl.class.getName());      
    
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
            AsciiDocDataObject data = (AsciiDocDataObject)NbEditorUtilities.getDataObject(document);                                    
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
                String html = AsciidoctorService.getDeafult().getAsciidoctor().convert(text, getOptions(true));  
                
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
    
    public static Attributes getAttributes(boolean title)
    {              
        return Attributes.builder()
        .showTitle(title) 
        .icons("font")
        .unsetStyleSheet()
        .sourceHighlighter("highlight.js")
        .build();       
    }   

    public static Options getOptions(boolean title)
    {
        return Options.builder().sourcemap(false).standalone(true).attributes(getAttributes(title)).build();
    }    
    
    @MimeRegistration(mimeType = "text/x-asciidoc", service = OnSaveTask.Factory.class, position = 1600)
    public static class CustomOnSaveTaskFactory1 implements OnSaveTask.Factory 
    {
        @Override
        public OnSaveTask createTask(Context cntxt) 
        {
            return new OnSaveTaskImpl(cntxt);
        }
    }     
}
