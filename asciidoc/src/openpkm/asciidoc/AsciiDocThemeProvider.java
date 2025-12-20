/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.asciidoc;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import openpkm.base.ThemeProvider;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface AsciiDocThemeProvider extends ThemeProvider
{
    Set<? extends AsciiDocTheme> getThemes();
    Optional<? extends AsciiDocTheme> getTheme(String name);
    
    public static SortedSet<AsciiDocThemeProvider> getAll()
    {
        Collection<? extends AsciiDocThemeProvider> coll = Lookup.getDefault().lookupAll(AsciiDocThemeProvider.class);
        SortedSet<AsciiDocThemeProvider> providers = new TreeSet<AsciiDocThemeProvider>(ThemeProvider.titleComparator());
        providers.addAll(coll);
        return providers;
    }     
}
