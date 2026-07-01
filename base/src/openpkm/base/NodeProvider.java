/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Component;
import java.beans.BeanInfo;
import java.util.Comparator;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import openpkm.base.DisplayNameProvider.TextFormat;
import org.openide.nodes.Children;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface NodeProvider extends Lookup.Provider
{
    String getName();
    Children getChildren();
    HelpCtx getHelp();  
    
    public static Comparator<NodeProvider> displayNameComparator() 
    {
        return new Comparator<NodeProvider>() 
        {
            @Override
            public int compare(NodeProvider provider1, NodeProvider provider2) 
            {
                DisplayNameProvider dnp1 = provider1.getLookup().lookup(DisplayNameProvider.class);
                DisplayNameProvider dnp2 = provider2.getLookup().lookup(DisplayNameProvider.class);
                if(dnp1 != null && dnp2 != null)
                {
                    return dnp1.getDisplayName(TextFormat.PLAIN).compareTo(dnp2.getDisplayName(TextFormat.PLAIN));                    
                }
                return -1;
            }
        };
    } 

    public static class ListCellRendererImpl extends JLabel implements ListCellRenderer<NodeProvider>
    {
        private final TextFormat format;
        
        public ListCellRendererImpl() 
        {
            this(TextFormat.PLAIN);
        }  
        
        public ListCellRendererImpl(TextFormat format) 
        {
            this.format = format;
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
                DisplayNameProvider displayNameProvider = provider.getLookup().lookup(DisplayNameProvider.class);
                if(displayNameProvider != null)
                {
                    setText(displayNameProvider.getDisplayName(format));                     
                }
                
                IconProvider iconProvider = provider.getLookup().lookup(IconProvider.class);
                if(iconProvider != null)
                {
                    setIcon(ImageUtilities.image2Icon(iconProvider.getIcon(BeanInfo.ICON_COLOR_16x16)));                      
                }
            }
            
            return this;
        }
    } 
}
