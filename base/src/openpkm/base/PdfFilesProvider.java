/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import java.util.Collection;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public interface PdfFilesProvider 
{
    String FILE_EXT = "pdf";  
    
    String PROP_DIR = "files.pdf";     
    
    FileObject getDataDirectory() throws IOException;
    Collection<FileObject> getDataFiles();
    FileObject getDataFile(String dataID);
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);      
}
