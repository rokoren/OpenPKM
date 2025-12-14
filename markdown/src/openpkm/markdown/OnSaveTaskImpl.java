/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.PdfFilesProvider;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.*;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
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
            
            Project project = FileOwnerQuery.getOwner(data.getPrimaryFile());
            HtmlFilesProvider providerHtml = project.getLookup().lookup(HtmlFilesProvider.class);
            if(providerHtml != null)
            {
                FileObject file = providerHtml.getDataDirectory().getFileObject(data.getPrimaryFile().getName(), HtmlFilesProvider.FILE_EXT);
                if(file == null)
                {
                    file = providerHtml.getDataDirectory().createData(data.getPrimaryFile().getName(), HtmlFilesProvider.FILE_EXT);
                } 
                
                String html = MarkdownService.getDeafult().getRenderer().render(MarkdownService.getDeafult().getParser().parse(text));                 
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
