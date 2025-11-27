/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author Rok Koren
 */
public interface Article 
{
    String PROP_PUBLISHER = "article.publisher";  
    String PROP_LANGUAGE  = "article.language";    
    
    String getPublisher();
    void setPublisher(String publisher);    
    String getLanguage();
    void setLanguage(String lang);     
}
