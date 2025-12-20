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
public class AsciiDocMaterialThemeProviderImpl implements AsciiDocThemeProvider
{
    private static final String CATEGORY = "Material";
    
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
        MATERIAL_AMBER("Amber", "material-amber"),         
        MATERIAL_BLUE("Blue", "material-blue"),        
        MATERIAL_BROWN("Brown", "material-brown"),         
        MATERIAL_GREEN("Green", "material-green"),        
        MATERIAL_GREY("Grey", "material-grey"),        
        MATERIAL_ORANGE("Orange", "material-orange"),          
        MATERIAL_PINK("Pink", "material-pink"),        
        MATERIAL_PURPLE("Purple", "material-purple"),        
        MATERIAL_RED("Red", "material-red"),             
        MATERIAL_TEAL("Teal", "material-teal");

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
            return Lookup.getDefault().lookup(AsciiDocMaterialThemeProviderImpl.class);
        }          
        
        public static Optional<Theme> get(String name) 
        {
            return Arrays.stream(Theme.values())
                    .filter(theme -> theme.name.equalsIgnoreCase(name))
                    .findFirst();
        }       
    }     
}
