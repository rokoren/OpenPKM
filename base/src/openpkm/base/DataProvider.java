/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public interface DataProvider 
{
    Lookup.Provider getProvider(); 
    List<FileObject> getFiles() throws IOException;
    boolean contains(DataObject data);
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);
}
