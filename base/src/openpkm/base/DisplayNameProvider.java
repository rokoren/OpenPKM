/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Comparator;

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
}
