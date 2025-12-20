/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import openpkm.base.ThemeProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author Rok Koren
 */
@ServiceProviders({
@ServiceProvider(service = ThemeProvider.class),    
@ServiceProvider(service = AsciiDocThemeProvider.class)    
})
public class AsciiDocAdocThemeProviderImpl implements AsciiDocThemeProvider
{
    private static final String CATEGORY = "Adoc";
    
    @Override
    public Set<Theme> getThemes() 
    {
        return EnumSet.allOf(Theme.class);
    }
    
    @Override
    public Optional<? extends AsciiDocTheme> getTheme(String name)
    {
        return Theme.get(name);
    }
    
    @Override
    public String toString()
    {
        return CATEGORY;
    }     
    
    public enum Theme implements AsciiDocTheme
    {
        ADOC_COLONY("Colony", "adoc-colony"),
        ADOC_FOUNDATION("Foundation", "adoc-foundation"),
        ADOC_FOUNDATION_LIME("Foundation Lime", "adoc-foundation-lime"),
        ADOC_FOUNDATION_POTION("Foundation Potion", "adoc-foundation-potion"),        
        ADOC_GITHUB("Github", "adoc-github"),  
        ADOC_GOLO("Golo", "adoc-golo"),
        ADOC_ICONIC("Iconic", "adoc-iconic"),
        ADOC_MAKER("Maker", "adoc-maker"),
        ADOC_READTHEDOCS("Read The Docs", "adoc-readthedocs"),
        ADOC_RIAK("Riak", "adoc-riak"),
        ADOC_ROCKET_PANDA("Rocket Panda", "adoc-rocket-panda"),
        ADOC_RUBYGEMS("Rubygems", "adoc-rubygems");

        private final String title;
        private final String name;

        Theme(String title, String name) 
        {
            this.title = title;
            this.name = name;
        }

        @Override
        public String toString() 
        {
            return title;
        }

        @Override
        public String getName() 
        {
            return name;
        } 
        
        @Override
        public String getUrl() 
        {
            String url = getClass().getResource("resources/" + name + ".css").toExternalForm();          
            return url;
        }         
        
        @Override
        public AsciiDocThemeProvider getProvider()
        {
            return Lookup.getDefault().lookup(AsciiDocAdocThemeProviderImpl.class);
        }
        
        public static Optional<Theme> get(String name) 
        {
            return Arrays.stream(Theme.values())
                    .filter(theme -> theme.name.equalsIgnoreCase(name))
                    .findFirst();
        }       
    }     
}
