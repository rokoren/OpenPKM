/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Comparator;

/**
 *
 * @author Rok Koren
 */
public interface NodePositionProvider extends NodeProvider
{
    int getPosition();
    
    public static Comparator<NodePositionProvider> positionComparator() 
    {
        return new Comparator<NodePositionProvider>() 
        {
            @Override
            public int compare(NodePositionProvider node1, NodePositionProvider node2) 
            {
                Integer position1 = Integer.valueOf(node1.getPosition());
                Integer position2 = Integer.valueOf(node2.getPosition());
                return position1.compareTo(position2);      
            }
        };
    }     
}
