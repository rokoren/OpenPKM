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
    private static final String ICON_WEB_PAGE = "openpkm/core/resources/www_page.png";     
    
    @StaticResource()
    private static final String ICON_NOTEBOOKS = "openpkm/core/resources/books_stack.png";    
    
    @StaticResource()
    private static final String ICON_HOME_PAGE = "openpkm/core/resources/home_page.png";     
    
    @StaticResource()
    private static final String ICON_BLOG = "openpkm/core/resources/blogger.png";      
    
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
    public static final String ICON_READ_LATER = "openpkm/core/resources/watch_window.png";     
    
    @StaticResource()
    public static final String ICON_YOUTUBE_CHANNEL = "openpkm/core/resources/youtube_channel.png";  
            
    @StaticResource()
    public static final String ICON_GITHUB = "openpkm/core/resources/github.png";      
    
    @StaticResource()
    public static final String ICON_GITLAB = "openpkm/core/resources/gitlab.png";    
    
    @StaticResource()
    public static final String ICON_FACEBOOK = "openpkm/core/resources/facebook.png";  
    
    @StaticResource()
    public static final String ICON_TWITTER = "openpkm/core/resources/twitter_logo.png";     
    
    @StaticResource()
    public static final String ICON_LINKEDIN = "openpkm/core/resources/linkedin.png";     
            
    @StaticResource()
    public static final String ICON_RSS_CHANNEL = "openpkm/core/resources/feed.png";     

    @StaticResource()
    public static final String ICON_CHECK = "openpkm/core/resources/check_box.png";     
            
    @StaticResource()
    public static final String ICON_UNCHECK = "openpkm/core/resources/check_box_uncheck.png";       
    
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
    
    @StaticResource()
    public static final String ICON_BULLET_DELETE = "openpkm/core/resources/bullet_delete.png";      
    
    @StaticResource()
    public static final String ICON_TAG_YELLOW = "openpkm/core/resources/tag_yellow.png";    
    
    @StaticResource()
    public static final String ICON_TAG_PURPLE = "openpkm/core/resources/tag_purple.png";  
    
    @StaticResource()
    public static final String ICON_TAG_BLUE = "openpkm/core/resources/tag_blue.png";  
    
    @StaticResource()
    public static final String ICON_TAG_RED = "openpkm/core/resources/tag_red.png";  
    
    @StaticResource()
    public static final String ICON_TAG_GREEN = "openpkm/core/resources/tag_green.png";  
    
    @StaticResource()
    public static final String ICON_TAG_ORANGE = "openpkm/core/resources/tag_orange.png";  

    @StaticResource()
    public static final String ICON_TAG_PINK = "openpkm/core/resources/tag_pink.png";  
    
    @StaticResource()
    public static final String ICON_FILE_PDF = "openpkm/core/resources/file_extension_pdf.png"; 
    
    @StaticResource()
    public static final String ICON_FILE_PNG = "openpkm/core/resources/file_extension_png.png";  
    
    @StaticResource()
    public static final String ICON_FILE_JPG = "openpkm/core/resources/file_extension_jpg.png"; 
    
    @StaticResource()
    public static final String ICON_FILE_GIF = "openpkm/core/resources/file_extension_gif.png"; 

    @StaticResource()
    public static final String ICON_FILE_MP4 = "openpkm/core/resources/file_extension_mp4.png"; 
    
    @StaticResource()
    private static final String ICON_ATTACHMENT = "openpkm/core/resources/attach.png";     
    
    @StaticResource()
    private static final String ICON_BIN = "openpkm/core/resources/bin.png";   
    
    @StaticResource()
    private static final String ICON_COMMENTS = "openpkm/core/resources/comments.png";      

    private String getResource(ICON icon)
    {
        switch(icon)
        {
            case DOMAINS:
            return ICON_DOMAINS;
            case WEB_PAGE:
            return ICON_WEB_PAGE;            
            case NOTEBOOKS:
            return ICON_NOTEBOOKS;            
            case HOME_PAGE:
            return ICON_HOME_PAGE;   
            case BLOG:
            return ICON_BLOG;              
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
            case GITHUB:
            return ICON_GITHUB; 
            case GITLAB:
            return ICON_GITLAB;               
            case FACEBOOK:
            return ICON_FACEBOOK;                
            case TWITTER:
            return ICON_TWITTER;  
            case LINKEDIN:
            return ICON_LINKEDIN;              
            case RSS_CHANNEL:
            return ICON_RSS_CHANNEL;             
            case WATCH_LATER:
            return ICON_WATCH_LATER;   
            case READ_LATER:
            return ICON_READ_LATER;              
            case CHECK:
            return ICON_CHECK;
            case UNCHECK:
            return ICON_UNCHECK;            
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
            case BULLET_DELETE:
            return ICON_BULLET_DELETE;                         
            case TAG_YELLOW:
            return ICON_TAG_YELLOW;                                      
            case TAG_PURPLE:
            return ICON_TAG_PURPLE;  
            case TAG_BLUE:
            return ICON_TAG_BLUE;  
            case TAG_RED:
            return ICON_TAG_RED;  
            case TAG_GREEN:
            return ICON_TAG_GREEN; 
            case TAG_ORANGE:
            return ICON_TAG_ORANGE; 
            case TAG_PINK:
            return ICON_TAG_PINK; 
            case FILE_PDF:
            return ICON_FILE_PDF;   
            case FILE_PNG:
            return ICON_FILE_PNG;    
            case FILE_JPG:
            return ICON_FILE_JPG;               
            case FILE_GIF:
            return ICON_FILE_GIF;    
            case FILE_MP4:
            return ICON_FILE_MP4;                         
            case ATTACHMENT:
            return ICON_ATTACHMENT;      
            case BIN:
            return ICON_BIN;  
            case COMMENTS:
            return ICON_COMMENTS;               
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
