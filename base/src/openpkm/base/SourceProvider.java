/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface SourceProvider<T extends Source> extends SourceGroup 
{    
    Source getSource(String sourceID);
    void deleteSource(String sourceID) throws IOException;
    FileObject createData(T source, FileTypeProvider fileTypeProvider) throws IOException;
    Lookup.Provider getProvider();
    void projectClosed();
    void addSourceListener(PropertyChangeListener listener);
    void removeSourceListener(PropertyChangeListener listener);
}
