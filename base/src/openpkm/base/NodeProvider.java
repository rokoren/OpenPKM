/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.Children;

/**
 *
 * @author Rok Koren
 */
public interface NodeProvider 
{
    String getName();
    String getDisplayName();
    Image getIcon(boolean opened);
    List<Action> getActions();
    Children getChildren();
    
    public static Comparator<NodeProvider> displayNameComparator() 
    {
        return new Comparator<NodeProvider>() 
        {
            @Override
            public int compare(NodeProvider node1, NodeProvider node2) 
            {
                return node1.getDisplayName().compareTo(node2.getDisplayName());
            }
        };
    }     
}
