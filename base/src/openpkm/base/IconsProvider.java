/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import javax.swing.Icon;

/**
 *
 * @author Rok Koren
 */
public interface IconsProvider 
{
    Image getImage(ICON icon);
    Icon getIcon(ICON icon);
    
    public enum ICON 
    {
        DOMAINS,
        WEB_PAGE,
        NOTEBOOKS,
        HOME_PAGE,
        BLOG,
        NOTES,
        ARTICLES,
        BOOKS,
        DOCUMENTS,
        LINKS,
        PICTURES,
        YOUTUBE_VIDEO,
        YOUTUBE_CHANNEL,
        GITHUB,
        GITLAB, 
        FACEBOOK,
        TWITTER,
        LINKEDIN,
        RSS_CHANNEL,
        WATCH_LATER,
        CHECK,
        UNCHECK,
        ATTACHMENT,
        BULLET_RED,
        BULLET_GREEN,
        BULLET_BLUE,        
        BULLET_BELL,
        BULLET_STAR, 
        BULLET_DELETE,
        TAG_YELLOW,
        TAG_PURPLE,
        TAG_BLUE,
        TAG_RED,
        TAG_GREEN,
        TAG_ORANGE,
        TAG_PINK, 
        FILE_PDF,
        FILE_PNG,
        FILE_JPG,
        FILE_GIF,
        FILE_MP4, 
        BIN,
        VIDEOS;    
    }     
}
