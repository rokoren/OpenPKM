/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.awt.Color;
import java.awt.Image;
import java.util.Arrays;
import java.util.Optional;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public interface TrelloLabel 
{
    @StaticResource()
    String ICON_YELLOW = "openpkm/trello/resources/tag_yellow.png";   
    
    @StaticResource()
    String ICON_PURPLE = "openpkm/trello/resources/tag_purple.png";  
    
    @StaticResource()
    String ICON_BLUE = "openpkm/trello/resources/tag_blue.png";  
    
    @StaticResource()
    String ICON_RED = "openpkm/trello/resources/tag_red.png";  
    
    @StaticResource()
    String ICON_GREEN = "openpkm/trello/resources/tag_green.png";  
    
    @StaticResource()
    String ICON_ORANGE = "openpkm/trello/resources/tag_orange.png";  

    @StaticResource()
    String ICON_PINK = "openpkm/trello/resources/tag_pink.png";      
    
    String getLabelID();
    String getLabelName();
    Optional<TrelloColor> getLabelColor();
    
    public enum TrelloColor 
    {
        YELLOW("yellow", ICON_YELLOW, Color.YELLOW),
        PURPLE("purple", ICON_PURPLE, Color.MAGENTA),
        BLUE("blue", ICON_BLUE, Color.BLUE),
        RED("red", ICON_RED, Color.RED),        
        GREEN("green", ICON_GREEN, Color.GREEN),
        ORANGE("orange", ICON_ORANGE, Color.ORANGE),
        PINK("pink", ICON_PINK, Color.PINK);

        private final String name;
        private final String icon;
        private final Color color;

        TrelloColor(String name, String icon, Color color) 
        {
            this.name = name;
            this.icon = icon;
            this.color = color;
        }

        public Image getIcon()
        {
            return ImageUtilities.loadImage(icon);
        }        
        
        public Color getColor()
        {
            return color;
        }
        
        @Override
        public String toString() 
        {
            return name;
        }
        
        public static Optional<TrelloColor> get(String string) 
        {
            return Arrays.stream(TrelloColor.values())
                    .filter(color -> color.name.equalsIgnoreCase(string))
                    .findFirst();
        }     
    }        
}
