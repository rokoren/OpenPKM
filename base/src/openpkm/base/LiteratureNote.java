/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.List;

/**
 *
 * @author rok
 */
public interface LiteratureNote extends Note
{
    String PROP_AUTHOR_NAME = "author.name"; 
    String PROP_SUMMARY     = "summary"; 
    String PROP_SUBTITLE    = "subtitle"; 
    String PROP_SOURCE_URL  = "source.url";     
    String PROP_QUOTES      = "quotes";     
    
    String getTitle();
    String getSubtitle();
    String getAuthorName();
    String getSourceUrl();
    String getSummary();
    List<Quote> getQuotes();    
}
