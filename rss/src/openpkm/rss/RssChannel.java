/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.rss;

import java.time.LocalDateTime;
import openpkm.base.DescriptionProvider;
import openpkm.base.TitleProvider;

/**
 *
 * @author Rok Koren
 */
public interface RssChannel extends TitleProvider, DescriptionProvider
{    
    public static final String PROP_RSS_URL         = "rss.url";    
    public static final String PROP_LINK            = "link"; 
    public static final String PROP_IMAGE           = "image";     
    public static final String PROP_ICON            = "icon";
    public static final String PROP_URI             = "uri";
    public static final String PROP_AUTHOR          = "author";
    public static final String PROP_COPYRIGHT       = "copyright";
    public static final String PROP_PUBLISHED_DATE  = "published.date"; 
    public static final String PROP_GENERATOR       = "generator";     
    public static final String PROP_LANGUAGE        = "language";
    public static final String PROP_MANAGING_EDITOR = "managing.editor";    
    public static final String PROP_CATEGORY        = "category"; 
    
    String getRssID();  
    String getRssUrl();    
    String getLink(); 
    void setLink(String link);  
    String getImage();
    void setImage(String image);
    String getIcon();
    void setIcon(String icon);    
    String getUri();
    void setUri(String uri);
    String getAuthor();
    void setAuthor(String author);    
    String getCopyright();
    void setCopyright(String copyright);    
    LocalDateTime getPublishedDate();
    void setPublishedDate(LocalDateTime date);    
    String getGenerator();
    void setGenerator(String generator);
    String getLanguage();
    void setLanguage(String language);
    String getManagingEditor();
    void setManagingEditor(String managingEditor);
    String getCategory();
    void setCategory(String category);
}
