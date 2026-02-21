/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Component;
import java.awt.Image;
import java.util.Comparator;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import org.openide.nodes.Children;
import org.openide.util.*;

/**
 *
 * @author Rok Koren
 */
public interface NodeProvider 
{
    String getName();
    String getDisplayName();
    Image getIcon(boolean opened);
    Children getChildren();
    
    public static class ListCellRendererImpl extends JLabel implements ListCellRenderer<NodeProvider>
    {
        public ListCellRendererImpl() 
        {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEADING);
            setVerticalAlignment(CENTER);
            setIconTextGap(10);
        }   

        @Override
        public Component getListCellRendererComponent(JList list, NodeProvider provider, int index, boolean isSelected, boolean cellHasFocus) 
        {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if(provider == null)
            {
                setText("");
                setIcon(null);
            }
            else
            {
                setIcon(ImageUtilities.image2Icon(provider.getIcon(false)));  
                setText(provider.getDisplayName());                   
            }
            
            return this;
        }
    }     
    
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
