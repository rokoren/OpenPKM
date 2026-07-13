/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.CheckList;
import java.util.Properties;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCheckListFactory 
{
    String PROP_BOARD_ID           = "board.id";
    String PROP_CARD_ID            = "card.id";    
    String PROP_CHECKLIST_ID       = "checklist.id";
    String PROP_CHECKLIST_NAME     = "checklist.name";        
    String PROP_CHECKLIST_POSITION = "checklist.position";   
    String PROP_CHECKLIST_ITEMS    = "checklist.items";  
    
    TrelloCheckList getCheckList(Properties props);
    TrelloCheckList createCheckList(CheckList checkList);      
}
