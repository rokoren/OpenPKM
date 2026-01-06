/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import javax.swing.Icon;
import openpkm.base.IconsProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=IconsProvider.class)
public class IconsProviderImpl implements IconsProvider
{
    @StaticResource()
    private static final String ICON_DOMAINS = "openpkm/core/resources/domain.png"; 
    
    @StaticResource()
    private static final String ICON_HOME_PAGE = "openpkm/core/resources/home_page.png";      
    
    @StaticResource()
    private static final String ICON_NOTES = "openpkm/core/resources/notes_pin.png";
    
    @StaticResource()
    private static final String ICON_BOOKS = "openpkm/core/resources/books.png"; 

    @StaticResource()
    private static final String ICON_ARTICLES = "openpkm/core/resources/newspaper.png";   
    
    @StaticResource()
    private static final String ICON_DOCUMENTS = "openpkm/core/resources/inbox_document.png";   
    
    @StaticResource()
    private static final String ICON_LINKS = "openpkm/core/resources/web_layout.png"; 

    @StaticResource()
    private static final String ICON_PICTURES = "openpkm/core/resources/images.png";  
    
    @StaticResource()
    private static final String ICON_VIDEOS = "openpkm/core/resources/television.png"; 

    @StaticResource()
    public static final String ICON_YOUTUBE_VIDEO = "openpkm/core/resources/youtube_video.png";  
    
    @StaticResource()
    public static final String ICON_WATCH_LATER = "openpkm/core/resources/eye.png";      
    
    @StaticResource()
    public static final String ICON_YOUTUBE_CHANNEL = "openpkm/core/resources/youtube_channel.png";  

    @StaticResource()
    public static final String ICON_BULLET_RED = "openpkm/core/resources/bullet_red.png";  

    @StaticResource()
    public static final String ICON_BULLET_GREEN = "openpkm/core/resources/bullet_green.png"; 
    
    @StaticResource()
    public static final String ICON_BULLET_BLUE = "openpkm/core/resources/bullet_blue.png";     
    
    @StaticResource()
    public static final String ICON_BULLET_BELL = "openpkm/core/resources/bullet_bell.png";     
    
    @StaticResource()
    public static final String ICON_BULLET_STAR = "openpkm/core/resources/bullet_star.png";     

    private String getResource(ICON icon)
    {
        switch(icon)
        {
            case DOMAINS:
            return ICON_DOMAINS;
            case HOME_PAGE:
            return ICON_HOME_PAGE;            
            case NOTES:
            return ICON_NOTES;   
            case BOOKS:
            return ICON_BOOKS;    
            case ARTICLES:
            return ICON_ARTICLES;  
            case DOCUMENTS:
            return ICON_DOCUMENTS; 
            case LINKS:
            return ICON_LINKS;   
            case PICTURES:
            return ICON_PICTURES;   
            case VIDEOS:
            return ICON_VIDEOS;  
            case YOUTUBE_VIDEO:
            return ICON_YOUTUBE_VIDEO;
            case YOUTUBE_CHANNEL:
            return ICON_YOUTUBE_CHANNEL; 
            case WATCH_LATER:
            return ICON_WATCH_LATER;             
            case BULLET_RED:
            return ICON_BULLET_RED;             
            case BULLET_BLUE:
            return ICON_BULLET_BLUE;  
            case BULLET_GREEN:
            return ICON_BULLET_GREEN; 
            case BULLET_BELL:
            return ICON_BULLET_BELL; 
            case BULLET_STAR:
            return ICON_BULLET_STAR;             
        }  
        return null;
    }
    
    @Override
    public Image getImage(ICON icon) 
    {
        return ImageUtilities.loadImage(getResource(icon), false);
    }

    @Override
    public Icon getIcon(ICON icon) 
    {
        return ImageUtilities.loadIcon(getResource(icon), false);
    }       
}
