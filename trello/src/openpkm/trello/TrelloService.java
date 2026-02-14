/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.Trello;
import java.util.List;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;

/**
 *
 * @author Rok Koren
 */
public interface TrelloService 
{
    List<TrelloBoard> getBoards(TrelloAccount account, Trello trello); 
    List<TrelloList> getLists(TrelloBoard trelloBoard, TrelloListProvider provider, Trello trello);
    List<TrelloMember> getMembers(TrelloBoard trelloBoard, TrelloMemberProvider provider, Trello trello);    
    List<TrelloLabel> getLabels(TrelloBoard trelloBoard, TrelloLabelProvider provider, Trello trello);          
    List<String> getCardsID(TrelloBoard trelloBoard, Trello trello); 
    String getCardDescription(String cardID, Trello trello);
    void setCardDescription(String cardID, String description, TrelloAccount account);    
    List<TrelloCard> getCards(TrelloList trelloList, TrelloCardProvider provider, Trello trello);  
    TrelloCard getCard(String cardID, TrelloCardProvider provider, TrelloAccount account) throws UnirestException;
    List<TrelloAttachment> getAttachments(TrelloCard trelloCard, TrelloAttachmentProvider provider, Trello trello);    
    List<TrelloCheckList> getCheckLists(TrelloCard trelloCard, TrelloCheckListProvider provider, TrelloAccount account);
    TrelloCheckList createCheckList(String cardID, String name, TrelloCheckListProvider provider, TrelloAccount account);
    JSONObject createCheckListIem(String checkListID, String name, TrelloAccount account);
    TrelloAttachment createAttachmentLink(String cardID, String name, String url, TrelloAttachmentProvider provider, TrelloAccount account);
}
