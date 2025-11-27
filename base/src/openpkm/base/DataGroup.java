/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import java.io.IOException;
import java.util.Comparator;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public interface DataGroup 
{
    FileObject getRootFolder() throws IOException;    
    String getName();
    String getDisplayName();
    Image getIcon(boolean hasChildren);
    Integer getPosition();
    boolean contains(FileObject file);
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);
    
    public static Comparator<DataGroup> positionComparator() 
    {
        return new Comparator<DataGroup>() 
        {
            @Override
            public int compare(DataGroup group1, DataGroup group2) 
            {
                return group1.getPosition().compareTo(group2.getPosition());
            }
        };
    }     
}
