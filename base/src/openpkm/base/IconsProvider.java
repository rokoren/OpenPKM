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
        NOTES,
        ARTICLES,
        BOOKS,
        DOCUMENTS,
        LINKS,
        PICTURES,
        VIDEOS;    
    }     
}
