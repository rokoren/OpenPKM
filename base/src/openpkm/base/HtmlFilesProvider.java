/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import java.util.Collection;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface HtmlFilesProvider 
{
    String FILE_EXT = "html"; 
    
    String PROP_LAST_DATA_ID = "html.last.data.id";    
    
    String getLastDataID();
    void setLastDataID(String dataID);  
    FileObject getDataDirectory() throws IOException;
    Collection<FileObject> getDataFiles();
    FileObject getDataFile(String dataID); 
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);    
    Lookup.Provider getProvider();     
}
