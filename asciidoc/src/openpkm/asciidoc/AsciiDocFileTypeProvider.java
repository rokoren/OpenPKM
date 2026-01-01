/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import openpkm.base.ArticleProvider;
import openpkm.base.AsciiDocSupport;
import openpkm.base.BookProvider;
import openpkm.base.FileTypeProvider;
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
public class AsciiDocFileTypeProvider implements AsciiDocSupport, ArticleProvider, BookProvider
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
}
