/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

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
@ServiceProvider(service = MarkdownThemeProvider.class)    
})
public class GitHubThemeProvider implements MarkdownThemeProvider
{
    private static final String CATEGORY = "GitHub";
    
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
    
    public enum Theme implements MarkdownTheme
    {
        UNIVERSAL("GitHub", "github"),
        LIGHT("Light", "github-markdown-light"),              
        DARK("Dark", "github-markdown-dark");

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
            return Lookup.getDefault().lookup(GitHubThemeProvider.class);
        }         
        
        public static Optional<Theme> get(String name) 
        {
            return Arrays.stream(Theme.values())
                    .filter(theme -> theme.name.equalsIgnoreCase(name))
                    .findFirst();
        }                 
    }     
}
