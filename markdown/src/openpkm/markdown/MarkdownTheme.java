/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.markdown;

import java.util.Collection;
import java.util.Optional;
import openpkm.base.Theme;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface MarkdownTheme extends Theme
{
    MarkdownThemeProvider getProvider();
    
    public static MarkdownTheme getTheme(String name)
    {
        Collection<? extends MarkdownThemeProvider> coll = Lookup.getDefault().lookupAll(MarkdownThemeProvider.class);
        for(MarkdownThemeProvider provider : coll)
        {
            Optional<? extends MarkdownTheme> theme = provider.getTheme(name);
            if(theme.isPresent())
            {
                return theme.get();
            }
        }
        return null;
    }        
}
