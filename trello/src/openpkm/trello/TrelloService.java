/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import java.time.LocalDateTime;
import java.util.List;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;

/**
 *
 * @author Rok Koren
 */
public interface TrelloService 
{
    int STATUS_OK = 200;
    
    List<TrelloBoard> getBoards(TrelloAccount account, Trello trello); 
    List<TrelloList> getLists(TrelloBoard trelloBoard, TrelloListProvider provider, Trello trello);
    TrelloList createList(String boardID, String name, TrelloListProvider provider, TrelloAccount account);
    List<TrelloMember> getMembers(TrelloBoard trelloBoard, TrelloMemberProvider provider, Trello trello);    
    List<TrelloLabel> getLabels(TrelloBoard trelloBoard, TrelloLabelProvider provider, Trello trello);   
    List<TrelloAction> getActions(TrelloBoard board, LocalDateTime after, TrelloActionProvider provider, Trello trello) throws Exception;
    List<Card> getCards(TrelloBoard trelloBoard, Trello trello); 
    String getCardDescription(String cardID, Trello trello);
    String getCommentText(String actionID, Trello trello);
    void setCardDescription(String cardID, String description, TrelloAccount account); 
    int setCardDueComplete(String cardID, boolean complete, TrelloAccount account);
    void setCommentText(String cardID, String actionID, String text, Trello trello);    
    List<TrelloCard> getCards(TrelloList trelloList, TrelloCardProvider provider, Trello trello);
    TrelloCard createCard(String listID, String name, TrelloCardProvider provider, TrelloAccount account);
    TrelloCard createLink(String listID, String url, TrelloCardProvider provider, TrelloAccount account);
    TrelloCard getCard(String cardID, TrelloCardProvider provider, TrelloAccount account) throws UnirestException;
    List<TrelloAttachment> getAttachments(TrelloCard trelloCard, TrelloAttachmentProvider provider, TrelloAccount account);    
    List<TrelloCheckList> getCheckLists(TrelloCard trelloCard, TrelloCheckListProvider provider, TrelloAccount account);
    TrelloCheckList createCheckList(String cardID, String name, TrelloCheckListProvider provider, TrelloAccount account);    
    JSONObject createCheckListIem(String checkListID, String name, TrelloAccount account);
    TrelloAttachment createAttachmentLink(String cardID, String name, String url, TrelloAttachmentProvider provider, TrelloAccount account);
    TrelloComment createComment(String cardID, String text, TrelloActionProvider actionProvider, TrelloCommentProvider commentProvider, TrelloAccount account, Trello trello);
    int deleteComment(String cardID, String actionID, TrelloAccount account);
    int deleteCheckListItem(TrelloCheckList checkList, TrelloCheckListItem item, TrelloAccount account);  
    int setCheckListItemState(TrelloCheckList checkList, TrelloCheckListItem item, TrelloAccount account);   
    int addLabel(TrelloCard card, TrelloLabel label, TrelloAccount account);
    int removeLabel(TrelloCard card, TrelloLabel label, TrelloAccount account);
}
