/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.LocationType;
import org.asciidoctor.jruby.internal.JRubyAsciidoctor;
import org.asciidoctor.syntaxhighlighter.SyntaxHighlighterAdapter;
import org.openide.modules.ModuleInfo;
import org.openide.modules.Modules;
import org.openide.util.Utilities;

/**
 *
 * @author Rok Koren
 */
public class AsciidoctorService 
{
    private static final Logger LOG = Logger.getLogger(AsciidoctorService.class.getName()); 

    private static AsciidoctorService service;
    
    private final Asciidoctor asciidoctor;   
    //private final Asciidoctor asciidoctor = Asciidoctor.Factory.create(); 

    public AsciidoctorService() 
    {
        asciidoctor = JRubyAsciidoctor.create(createClassLoaderFromNetbeansModule());
        asciidoctor.syntaxHighlighterRegistry().register(HighlightJsHighlighter.class, "highlight.js");        
        //asciidoctor.javaExtensionRegistry().treeprocessor(DataLineProcessor.class);   
    }
    
    public Asciidoctor getAsciidoctor()
    {
        return asciidoctor;
    }
    
    public static ClassLoader createClassLoaderFromNetbeansModule() {
        ModuleInfo findCodeNameBase = Modules.getDefault().findCodeNameBase("openpkm.asciidoc");
        if (findCodeNameBase instanceof org.netbeans.Module) {
            List<URL> resources = new ArrayList<>();
            org.netbeans.Module module = ((org.netbeans.Module)findCodeNameBase);
            List<File> jars = module.getAllJars();
            for (File jar : jars) {
                try {
                    resources.add(Utilities.toURI(jar).toURL());
                    LOG.info("Netbeans module class path with : " + Utilities.toURI(jar).toURL());
                } catch (MalformedURLException ex) {
                    throw new IllegalStateException("Malformed URL from module jar : " + jar.toString());
                }
            }
            return new URLClassLoader(resources.toArray(URL[]::new), findCodeNameBase.getClassLoader());
        }

        if (findCodeNameBase == null) {
            throw new IllegalStateException("Unable to create Asciidoc engine, not found netbean module");
        } else {
            throw new IllegalStateException("Unable to create Asciidoc engine on netbean module : " + findCodeNameBase.getClass().getCanonicalName());
        }
    }   
    
    public static class HighlightJsHighlighter implements SyntaxHighlighterAdapter 
    { 
        /*
        private final boolean isDark;
        
        public HighlightJsHighlighter()
        {
            isDark = UIManager.getBoolean("nb.dark.theme");             
        } 
        */
        
        @Override
        public boolean hasDocInfo(LocationType location)
        {
            return location == LocationType.FOOTER;         
        }

        @Override
        public String getDocinfo(LocationType location, Document document, Map<String, Object> options) 
        { 
            /*
            if(isDark)
            {
                return "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/github-dark.min.css\">\n" +
                    "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/highlight.min.js\"></script>\n" +
                    "<script>hljs.initHighlighting()</script>";
            }                                    
            return "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/github.min.css\">\n" +
                "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/highlight.min.js\"></script>\n" +
                "<script>hljs.initHighlighting()</script>";
            */
            
            /*
            if(isDark)
            {
                return "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/stackoverflow-dark.min.css\">\n" +
                    "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/highlight.min.js\"></script>\n" +
                    "<script>hljs.initHighlighting()</script>";
            }                                    
            return "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/stackoverflow-light.min.css\">\n" +
                "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/highlight.min.js\"></script>\n" +
                "<script>hljs.initHighlighting()</script>";         
            */
            
            /*
            if(isDark)
            {
                return "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/base16/default-dark.min.css\">\n" +
                    "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/highlight.min.js\"></script>\n" +
                    "<script>hljs.initHighlighting()</script>";
            }                                    
            return "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/base16/default-light.min.css\">\n" +
                "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/highlight.min.js\"></script>\n" +
                "<script>hljs.initHighlighting()</script>";               
            */
            
            return "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/base16/flat.min.css\">\n" +
                "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/highlight.min.js\"></script>\n" +
                "<script>hljs.initHighlighting()</script>";            
        }
    }    
    
    public static synchronized AsciidoctorService getDeafult()
    {
        if(service == null)
        {
            /*
            System.setProperty("jruby.compat.version", "RUBY1_9");
            System.setProperty("jruby.compile.mode", "OFF");
            */
            System.setProperty("jruby.compile.invokedynamic", "true");
            service = new AsciidoctorService();
        }
        return service;
    }    
}
