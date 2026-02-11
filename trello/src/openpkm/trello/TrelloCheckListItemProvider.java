/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.CheckItem;
import kong.unirest.json.JSONObject;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCheckListItemProvider 
{
    String PROP_CHECKLIST_ITEM_ID       = "checklist.item.id";
    String PROP_CHECKLIST_ITEM_NAME     = "checklist.item.name";        
    String PROP_CHECKLIST_ITEM_POSITION = "checklist.item.position"; 
    String PROP_CHECKLIST_ITEM_STATE    = "checklist.item.state";     
    
    TrelloCheckListItem getCheckListItem(JSONObject json);
    TrelloCheckListItem createCheckListItem(CheckItem item);     
}
