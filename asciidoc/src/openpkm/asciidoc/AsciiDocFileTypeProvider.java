/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.util.List;
import openpkm.base.ArticleProvider;
import openpkm.base.AsciiDocSupport;
import openpkm.base.BookProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.LiteratureNoteProvider;
import openpkm.base.Quote;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author Rok Koren
 */
@ServiceProviders({
@ServiceProvider(service = AsciiDocSupport.class),    
@ServiceProvider(service = FileTypeProvider.class)    
})
public class AsciiDocFileTypeProvider implements AsciiDocSupport, ArticleProvider, BookProvider, LiteratureNoteProvider
{    
    @Override
    public String getExtension() 
    {
        return EXTENSION;
    }

    @Override
    public String getDisplayName() 
    {
        return "AsciiDoc";
    }   
    
    @Override
    public String toString()
    {
        return getDisplayName();
    }    

    @Override
    public String getArticle(String articleName, String authorName) 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("= " + articleName);
        sb.append("\n");
        sb.append(authorName);
        sb.append("\n");
        sb.append(":doctype: article");
        sb.append("\n");
        sb.append(":encoding: utf-8");
        sb.append("\n");
        sb.append(":lang: en");
        sb.append("\n");
        sb.append(":toc: left");   
        sb.append("\n");
        sb.append(":numbered:");   
        return sb.toString();
    }

    @Override
    public String getBook(String bookName, String authorName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("= " + bookName);
        sb.append("\n");
        sb.append(authorName);
        sb.append("\n");
        sb.append(":doctype: book");
        sb.append("\n");
        sb.append(":encoding: utf-8");
        sb.append("\n");
        sb.append(":lang: en");
        sb.append("\n");
        sb.append(":toc: left");   
        sb.append("\n");
        sb.append(":numbered:");   
        return sb.toString();
    }
    
    @Override
    public String getLiteratureNote(String title, String subtitle, String authorName, String sourceUrl, String summary, List<Quote> quotes)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("= " + title);
        
        if(subtitle != null)
        {
            sb.append(": " + subtitle);
        }
        
        sb.append("\n");
        sb.append(authorName);
        sb.append("\n");
        sb.append(":doctype: article");
        
        if(sourceUrl != null)
        {
            sb.append("\n");
            sb.append(":source-url: " + sourceUrl);              
        }              
        
        sb.append("\n");
        sb.append(":encoding: utf-8");
        sb.append("\n");
        sb.append(":lang: en");
        sb.append("\n");
        sb.append(":toc: left");   
        sb.append("\n");
        sb.append(":numbered:");          
        
        if(summary != null)
        {
            sb.append("\n\n");             
            sb.append("== Summary"); 
            sb.append("\n");
            sb.append(summary);              
        }                
        
        if(quotes != null)
        {
            sb.append("\n\n"); 
            sb.append("== Quotes"); 
            sb.append("\n");            
            for(Quote quote : quotes)
            {
                sb.append("[quote, " + quote.getAuthorName() + ", " + quote.getTitle() + "]"); 
                sb.append("\n");  
                sb.append("____");   
                sb.append("\n"); 
                sb.append(quote.getText());
                sb.append("\n"); 
                sb.append("____");  
            }                        
        }        

        sb.append("\n\n");         
        sb.append("== My Thoughts"); 
        sb.append("\n");         
        
        return sb.toString();        
    }
}
