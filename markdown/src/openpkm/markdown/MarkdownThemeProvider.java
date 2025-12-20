/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.markdown;

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
public interface MarkdownThemeProvider extends ThemeProvider
{
    Set<? extends MarkdownTheme> getThemes();
    Optional<? extends MarkdownTheme> getTheme(String name);
    
    public static SortedSet<MarkdownThemeProvider> getAll()
    {
        Collection<? extends MarkdownThemeProvider> coll = Lookup.getDefault().lookupAll(MarkdownThemeProvider.class);
        SortedSet<MarkdownThemeProvider> providers = new TreeSet<MarkdownThemeProvider>(ThemeProvider.titleComparator());
        providers.addAll(coll);
        return providers;
    }     
}
