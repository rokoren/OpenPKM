/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.time.LocalDate;

/**
 *
 * @author Rok Koren
 */
public interface Book
{
    String PROP_DESCRIPTION  = "book.description";    
    String PROP_SUBTITLE     = "book.subtitle"; 
    String PROP_AUTHORS      = "book.authors";  
    String PROP_PUBLISHER    = "book.publisher";  
    String PROP_PUBLISH_DATE = "book.publish.date";  
    String PROP_LANGUAGE     = "book.language"; 
    String PROP_ISBN         = "book.isbn"; 
    
    String getTitle();
    void setTitle(String title);         
    String getDescription();
    void setDescription(String description);
    String getSubtitle();
    void setSubtitle(String subtitle);  
    String getAuthors();
    void setAuthors(String authors);
    String getPublisher();
    void setPublisher(String publisher);
    LocalDate getPublishDate();
    void setPublishDate(LocalDate date);
    String getLanguage();
    void setLanguage(String lang);
    String getISBN();
    void setISBN(String isbn);    
}
