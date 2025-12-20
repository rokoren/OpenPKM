/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import org.openide.util.Lookup;

/**
 *
 * @author rokoren
 */
public interface Theme 
{
    String getName(); 
    String getUrl(); 
    
    public static Optional<? extends Theme> getTheme(String name)
    {
        Collection<? extends ThemeProvider> providers = Lookup.getDefault().lookupAll(ThemeProvider.class);
        for(ThemeProvider provider : providers)
        {
            Optional<? extends Theme> theme = provider.getTheme(name);
            if(theme.isPresent())
            {
                return theme;
            }
        }
        return Optional.empty();
    } 
    
    public static Comparator<Theme> titleComparator() 
    {
        return new Comparator<Theme>() 
        {
            @Override
            public int compare(Theme theme1, Theme theme2) 
            {
                return theme1.toString().compareTo(theme2.toString());
            }
        };
    }     
}
