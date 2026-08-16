/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author rok
 */
public class HtmlUtils 
{
    public static Set<String> getBacklinks(String html)
    {
        if(html != null)
        {
            Set<String> result = new HashSet<>();
            Document document = Jsoup.parse(html);
            Elements links = document.select("a[href^=openpkm:]");

            for (Element link : links) 
            {
                String href = link.attr("href");
                if (href.startsWith("openpkm:"))
                {
                    String filename = href.substring("openpkm:".length());
                    result.add(filename);
                }
            }  

            return result;            
        }
        
        return Collections.EMPTY_SET;        
    }
    
    public static Set<String> findOpenPkmLinks(String html) 
    {
        if(html != null)
        {
            Set<String> result = new HashSet<>();
            Document document = Jsoup.parse(html);

            for (Element link : document.select("a[href]")) 
            {
                String href = link.attr("href");
                if (href.startsWith("openpkm:"))
                {
                    String filename = href.substring("openpkm:".length());
                    result.add(filename);
                }
            }

            return result;            
        }
        return Collections.EMPTY_SET;
    }  
}
