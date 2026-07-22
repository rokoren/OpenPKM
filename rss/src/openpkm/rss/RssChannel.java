/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.rss;

import java.time.LocalDateTime;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.StateSupport;

/**
 *
 * @author Rok Koren
 */
public interface RssChannel extends StateSupport, PropertiesProvider, NodeProvider
{   
    String getTitle();
    String getDescription();
    String getRssID();  
    String getRssUrl();    
    String getLink(); 
    String getImage();
    String getIcon();
    String getUri();
    String getAuthor();  
    String getCopyright(); 
    LocalDateTime getPublishedDate();   
    String getGenerator();
    String getLanguage();
    String getManagingEditor();
    String getCategory();    
}
