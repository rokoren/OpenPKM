/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.Label;
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
}
