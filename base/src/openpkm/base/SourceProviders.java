/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public interface SourceProviders 
{
    String PROP_LAST_SOURCE = "last.source";       
    
    String ATTR_SOURCE_ID       = "source.id";
    String ATTR_SOURCE_PROVIDER = "source.provider";     
    
    SourceProvider getSourceProvider(String name);
    FileObject getDataDirectory() throws IOException;
    FileObject getFileWithAttrs(FileObject file, boolean refresh);
}
