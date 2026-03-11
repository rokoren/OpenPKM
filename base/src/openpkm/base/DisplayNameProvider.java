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
import org.openide.util.ImageUtilities;

/**
 *
 * @author rokor
 */
public interface DisplayNameProvider 
{
    String getDisplayName(TextFormat format);    

    public enum TextFormat 
    {
        PLAIN,
        HTML,
        MARKDOWN,
        ASCIIDOC,
        RTF;    
    }      
    
    public static Comparator<DisplayNameProvider> displayNameComparator() 
    {
        return new Comparator<DisplayNameProvider>() 
        {
            @Override
            public int compare(DisplayNameProvider provider1, DisplayNameProvider provider2) 
            {
                return provider1.getDisplayName(TextFormat.PLAIN).compareTo(provider2.getDisplayName(TextFormat.PLAIN));
            }
        };
    }    
    
    public static class ListCellRendererImpl extends JLabel implements ListCellRenderer<DisplayNameProvider>
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
        public Component getListCellRendererComponent(JList list, DisplayNameProvider provider, int index, boolean isSelected, boolean cellHasFocus) 
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
                setText(provider.getDisplayName(format)); 
                
                if(provider instanceof IconProvider)
                {
                    IconProvider iconProvider = (IconProvider)provider;
                    setIcon(ImageUtilities.image2Icon(iconProvider.getIcon(BeanInfo.ICON_COLOR_16x16)));                      
                }
            }
            
            return this;
        }
    }     
}
