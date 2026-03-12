/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.Label;
import java.awt.Color;
import java.util.Properties;

/**
 *
 * @author Rok Koren
 */
public interface TrelloLabelProvider 
{
    String COLOR_YELLOW  = "yellow";
    String COLOR_MAGENTA = "magenta";
    String COLOR_BLUE    = "blue";    
    String COLOR_GREEN   = "green";
    String COLOR_RED     = "red";
    String COLOR_ORANGE  = "orange";    
    String COLOR_PINK    = "pink";         
    String COLOR_LIME    = "lime";
    String COLOR_SKY     = "sky";    
    String COLOR_BLACK   = "black";   
    String COLOR_PURPLE  = "purple";     
    
    TrelloLabel getLabel(Properties props);
    TrelloLabel createLabel(Label label);
    
    public static Color getColor(String name)
    {
        if(name.equalsIgnoreCase(COLOR_YELLOW))
        {
            return Color.YELLOW;
        }
        else if(name.equalsIgnoreCase(COLOR_MAGENTA))
        {
            return Color.MAGENTA;
        }
        else if(name.equalsIgnoreCase(COLOR_BLUE))
        {
            return Color.BLUE;
        }                     
        else if(name.equalsIgnoreCase(COLOR_RED))
        {
            return Color.RED;
        }
        else if(name.equalsIgnoreCase(COLOR_GREEN))
        {
            return Color.GREEN;
        }  
        else if(name.equalsIgnoreCase(COLOR_ORANGE))
        {
            return Color.ORANGE;
        }
        else if(name.equalsIgnoreCase(COLOR_PINK))
        {
            return Color.PINK;
        }                 
        else if(name.equalsIgnoreCase(COLOR_BLACK))
        {
            return Color.BLACK;
        }  
        else if(name.equalsIgnoreCase(COLOR_SKY))
        {
            return Color.CYAN;
        }
        else if(name.equalsIgnoreCase(COLOR_LIME))
        {
            return Color.green.brighter();
        }   
        else if(name.equalsIgnoreCase(COLOR_PURPLE))
        {
            return Color.PINK.darker();
        }  
        return null;            
    }      
}
