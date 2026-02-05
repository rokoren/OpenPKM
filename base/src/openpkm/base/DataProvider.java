/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Properties;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface DataProvider 
{
    String getName();
    DataSource getSource(String sourceID);
    FileObject createData(Properties props, FileTypeProvider fileTypeProvider);
    Lookup.Provider getProvider();    
}
