/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.beans.PropertyChangeListener;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public interface DataProviders 
{
    String PROP_DATA_SOURCE = "data.source";       
    
    String ATTR_DATA_SOURCE_ID = "data.source.id";
    String ATTR_DATA_PROVIDER  = "data.provider";     
    
    DataSource getDataSource();
    void setDataSource(DataSource source);
    DataProvider getDataProvider(String name);
    FileObject getFileWithAttrs(FileObject file, boolean refresh);   
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}
