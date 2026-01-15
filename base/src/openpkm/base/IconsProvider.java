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
        HOME_PAGE,
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
        RSS_CHANNEL,
        WATCH_LATER,
        BULLET_RED,
        BULLET_GREEN,
        BULLET_BLUE,        
        BULLET_BELL,
        BULLET_STAR,
        VIDEOS;    
    }     
}
