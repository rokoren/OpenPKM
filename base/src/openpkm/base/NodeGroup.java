/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface NodeGroup
{  
    String getName();
    String getDisplayName();
    Image getIcon(boolean isEmpty, boolean isOpen);
    List<Action> getActions();
    Integer getPosition();
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);
    Lookup.Provider getProvider(); 
    SortedSet<NodeProvider> getNodes();
    
    public static Comparator<NodeGroup> positionComparator() 
    {
        return new Comparator<NodeGroup>() 
        {
            @Override
            public int compare(NodeGroup group1, NodeGroup group2) 
            {
                return group1.getPosition().compareTo(group2.getPosition());
            }
        };
    }     
}
