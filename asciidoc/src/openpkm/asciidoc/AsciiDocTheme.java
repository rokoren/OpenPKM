/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.asciidoc;

import java.util.Collection;
import java.util.Optional;
import openpkm.base.Theme;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface AsciiDocTheme extends Theme
{
    AsciiDocThemeProvider getProvider();
    
    public static AsciiDocTheme getTheme(String name)
    {
        Collection<? extends AsciiDocThemeProvider> coll = Lookup.getDefault().lookupAll(AsciiDocThemeProvider.class);
        for(AsciiDocThemeProvider provider : coll)
        {
            Optional<? extends AsciiDocTheme> theme = provider.getTheme(name);
            if(theme.isPresent())
            {
                return theme.get();
            }
        }
        return null;
    }        
}
