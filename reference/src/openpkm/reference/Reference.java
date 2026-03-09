/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.reference;

import java.io.IOException;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public interface Reference extends Source, PropertiesProvider
{   
    @StaticResource()
    String ICON = "openpkm/reference/resources/link.png";        
    
    String PROP_FILE_NAME = "file.name";
    String PROP_FILE_EXT  = "file.ext";
    String PROP_FILE_PATH = "file.path";    

    FileObject getFile() throws IOException;
    void setFile(FileObject file) throws IOException; 
}
