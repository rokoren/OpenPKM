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
public class AsciiDocBootThemeProviderImpl implements AsciiDocThemeProvider
{
    private static final String CATEGORY = "Boot";
    
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
        BOOT_CERULEAN("Cerulean", "boot-cerulean"),        
        BOOT_COSMO("Cosmo", "boot-cosmo"),        
        BOOT_CYBORG("Cyborg", "boot-cyborg"),         
        BOOT_DARKLY("Darkly", "boot-darkly"),        
        BOOT_FLATLY("Flatly", "boot-flatly"),        
        BOOT_JOURNAL("Journal", "boot-journal"),          
        BOOT_LUMEN("Lumen", "boot-lumen"),        
        BOOT_PAPER("Paper", "boot-paper"),        
        BOOT_READABLE("Readable", "boot-readable"),             
        BOOT_SANDSTONE("Sandstone", "boot-sandstone"),        
        BOOT_SLATE("Slate", "boot-slate"),        
        BOOT_SPACELAB("Spacelab", "boot-spacelab"),         
        BOOT_SUPERHERO("Superhero", "boot-superhero"),        
        BOOT_YETI("Yety", "boot-yeti");

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
            return Lookup.getDefault().lookup(AsciiDocBootThemeProviderImpl.class);
        }        
        
        public static Optional<Theme> get(String name) 
        {
            return Arrays.stream(Theme.values())
                    .filter(theme -> theme.name.equalsIgnoreCase(name))
                    .findFirst();
        }       
    }      
}
