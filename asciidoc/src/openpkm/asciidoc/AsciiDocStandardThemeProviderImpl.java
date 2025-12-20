/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

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
@ServiceProvider(service = AsciiDocThemeProvider.class)    
})
public class AsciiDocStandardThemeProviderImpl implements AsciiDocThemeProvider
{
    private static final String CATEGORY = "Standard";
    
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
    
    public static AsciiDocTheme getDefaultTheme()
    {
        boolean isDark = UIManager.getBoolean("nb.dark.theme"); 
        if(isDark)
        {
            return Theme.ASCIIDOCTOR_DARK;
        }
        return Theme.ASCIIDOCTOR;
    }     
    
    public enum Theme implements AsciiDocTheme
    {
        ASCIIDOCTOR("Asciidoctor", "asciidoctor"),
        ASCIIDOC_CLASSIC("Asciidoc Classic", "asciidoc-classic"),
        ASCIIDOCTOR_DARK("Asciidoctor Dark", "asciidoctor-dark"),
        PREVIEW("Preview", "preview"),
        DARK("Dark", "dark"),
        CODERAY("Coderay", "coderay-asciidoctor"),                 
        CLEAN("Clean", "clean"),  
        FEDORA("Fedora", "fedora"),  
        GAZETTE("Gazette", "gazette"),  
        ITALIAN_POP("Italian Pop", "italian-pop"),                       
        MEDIUM("Medium", "medium"),        
        MONOSPACE("Monospace", "monospace"),         
        NOTEBOOK("Notebook", "notebook"),   
        PLAIN("Plain", "plain"),        
        TEMPLATE("Template", "template"),        
        TUFTE("Tufte", "tufte"),        
        UBUNTU("Ubuntu", "ubuntu"),           
        OREILLY("O'Reilly", "oreilly"),
        PACKT("Packt", "packt"),
        APRESS("Apress", "apress"),
        INTELLIJ("IntelliJ", "intellij"),
        FLATLAF("FlatLaf", "flatlaf"),
        DARCULA("Darcula", "darcula");

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
            return Lookup.getDefault().lookup(AsciiDocStandardThemeProviderImpl.class);
        }         
        
        public static Optional<Theme> get(String name) 
        {
            return Arrays.stream(Theme.values())
                    .filter(theme -> theme.name.equalsIgnoreCase(name))
                    .findFirst();
        }                 
    }  
}
