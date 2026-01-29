/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import java.util.Comparator;

/**
 *
 * @author Rok Koren
 */
public interface NodeProvider 
{
    String getName();
    String getDisplayName();
    Image getIcon(boolean opened);
    
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
