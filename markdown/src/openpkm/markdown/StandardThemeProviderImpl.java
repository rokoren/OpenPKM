/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import javax.swing.UIManager;
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
@ServiceProvider(service = MarkdownThemeProvider.class)    
})
public class StandardThemeProviderImpl implements MarkdownThemeProvider
{
    private static final String CATEGORY = "Standard";
    
    @Override
    public Set<Theme> getThemes() 
    {
        return EnumSet.allOf(Theme.class);
    }
    
    @Override
    public Optional<? extends MarkdownTheme> getTheme(String name)
    {
        return Theme.get(name);
    }         
    
    @Override
    public String toString()
    {
        return CATEGORY;
    }
    
    public static MarkdownTheme getDefaultTheme()
    {
        boolean isDark = UIManager.getBoolean("nb.dark.theme"); 
        if(isDark)
        {
            return Theme.DARK;
        }
        return Theme.LIGHT;
    }     
    
    public enum Theme implements MarkdownTheme
    {
        LIGHT("Light", "markdown"),
        DARK("Dark", "markdown1"), 
        TUFTE("Tufte", "tufte"),        
        GITHUB("GitHub", "github-flavor"),        
        VSC("Visual Studio Code", "vsc-markdown"),              
        IDEA("IntelliJ IDEA", "intellij-markdown"),                        
        AVENIR_WHITE("Avenir White", "avenir-white"),
        FOGHORN("Foghorn", "foghorn"),
        MARKDOWN_ALT("Markdown Alt", "markdown-alt"),                 
        MARKDOWN_2("Markdown 2", "markdown2"), 
        MARKDOWN_3("Markdown 3", "markdown3"),                 
        MARKDOWN_4("Markdown 4", "markdown4"),                           
        MARKDOWN_6("Markdown 6", "markdown6"), 
        MARKDOWN_7("Markdown 7", "markdown7"),                 
        MARKDOWN_8("Markdown 8", "markdown8"),         
        MARKDOWN_9("Markdown 9", "markdown9"),         
        MARKDOWN_10("Markdown 10", "markdown10"),  
        SCREEN("Screen", "screen"),  
        SWISS("Swiss", "swiss"),                       
        PAPER("Paper", "paper");

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
        public MarkdownThemeProvider getProvider()
        {
            return Lookup.getDefault().lookup(StandardThemeProviderImpl.class);
        }         
        
        public static Optional<Theme> get(String name) 
        {
            return Arrays.stream(Theme.values())
                    .filter(theme -> theme.name.equalsIgnoreCase(name))
                    .findFirst();
        }                 
    }     
}
