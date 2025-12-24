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

    private String getResource(ICON icon)
    {
        switch(icon)
        {
            case ICON.DOMAINS:
            return ICON_DOMAINS;
            case ICON.NOTES:
            return ICON_NOTES;   
            case ICON.BOOKS:
            return ICON_BOOKS;    
            case ICON.ARTICLES:
            return ICON_ARTICLES;  
            case ICON.DOCUMENTS:
            return ICON_DOCUMENTS; 
            case ICON.LINKS:
            return ICON_LINKS;   
            case ICON.PICTURES:
            return ICON_PICTURES;   
            case ICON.VIDEOS:
            return ICON_VIDEOS;             
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
