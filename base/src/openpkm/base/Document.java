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
public interface Document extends TitleProvider
{
    String PROP_SUBTITLE     = "document.subtitle"; 
    String PROP_AUTHORS      = "document.authors";  
    String PROP_INSTITUTION  = "document.institution";  
    String PROP_PUBLISH_DATE = "document.publish.date";  
    String PROP_LANGUAGE     = "document.language";  
    
    String getSubtitle();
    void setSubtitle(String subtitle);  
    String getAuthors();
    void setAuthors(String authors);
    String getInstitution();
    void setInstitution(String institution);
    LocalDate getPublishDate();
    void setPublishDate(LocalDate date);
    String getLanguage();
    void setLanguage(String lang);     
}
