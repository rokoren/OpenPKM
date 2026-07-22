/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.domain;

import com.rometools.rome.feed.synd.SyndEntry;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import openpkm.base.SourceFactory;

/**
 *
 * @author Rok Koren
 */
public interface WebPageFactory extends SourceFactory<WebPage>
{
    String PROP_TYPE           = "web.page.type"; 
    String PROP_LINK           = "link";    
    String PROP_URI            = "uri";
    String PROP_PUBLISHED_DATE = "published.date";    
    
    WebPage getWebPage(Properties props);  
    WebPage getWebPage(SyndEntry syndEntry);     
    
    public enum Type 
    {
        LINK("link"),
        RSS("rss"),
        ARTICLE("article");

        private String name;       

        Type(String name) 
        {
            this.name = name;
        } 
        
        public String getName()
        {
            return name;
        }
        
        public static Optional<Type> get(String name) {
            return Arrays.stream(Type.values())
                    .filter(type -> type.name.equalsIgnoreCase(name))
                    .findFirst();
        }     
    }      
}
