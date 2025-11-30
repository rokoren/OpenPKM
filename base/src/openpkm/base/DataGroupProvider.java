/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface DataGroupProvider 
{
    FileObject getRootFolder() throws IOException;    
    String getName();
    String getDisplayName();
    Image getIcon(boolean hasChildren);
    List<Action> getActions();
    Integer getPosition();
    boolean contains(DataObject data);
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);
    Lookup.Provider getProvider();    
    
    public static Comparator<DataGroupProvider> positionComparator() 
    {
        return new Comparator<DataGroupProvider>() 
        {
            @Override
            public int compare(DataGroupProvider group1, DataGroupProvider group2) 
            {
                return group1.getPosition().compareTo(group2.getPosition());
            }
        };
    }     
}
