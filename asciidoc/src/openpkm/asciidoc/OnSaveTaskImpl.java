/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import openpkm.base.HtmlFilesProvider;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;

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
                FileObject file = providerHtml.getDataDirectory().getFileObject(data.getPrimaryFile().getName(), HtmlFilesProvider.FILE_EXT);
                if(file == null)
                {
                    file = providerHtml.getDataDirectory().createData(data.getPrimaryFile().getName(), HtmlFilesProvider.FILE_EXT);
                }                       
                String html = AsciidoctorService.getDeafult().getAsciidoctor().convert(text, getOptions(true));                 
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
