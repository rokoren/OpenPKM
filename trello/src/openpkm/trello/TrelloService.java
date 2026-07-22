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
    List<TrelloList> getLists(TrelloBoard trelloBoard, TrelloListFactory provider, Trello trello);
    TrelloList createList(String boardID, String name, TrelloListFactory provider, TrelloAccount account);
    List<TrelloMember> getMembers(TrelloBoard trelloBoard, TrelloMemberFactory provider, Trello trello);    
    List<TrelloLabel> getLabels(TrelloBoard trelloBoard, TrelloLabelFactory factory, Trello trello);   
    List<TrelloAction> getActions(TrelloBoard board, LocalDateTime after, TrelloActionFactory factory, Trello trello) throws Exception;
    List<Card> getCards(TrelloBoard trelloBoard, Trello trello); 
    String getCardDescription(String cardID, Trello trello);
    String getCommentText(String actionID, Trello trello);
    void setCardDescription(String cardID, String description, TrelloAccount account); 
    int setCardDueComplete(String cardID, boolean complete, TrelloAccount account);
    void setCommentText(String cardID, String actionID, String text, Trello trello);    
    List<TrelloCard> getCards(TrelloList trelloList, TrelloCardFactory factory, Trello trello);
    TrelloCard createCard(String listID, String name, TrelloCardFactory factory, TrelloAccount account);
    TrelloCardLink createLink(String listID, String url, TrelloCardFactory factory, TrelloAccount account);
    TrelloCard getCard(String cardID, TrelloCardFactory factory, TrelloAccount account) throws UnirestException;
    List<TrelloAttachment> getAttachments(TrelloCard trelloCard, TrelloAttachmentFactory factory, TrelloAccount account);    
    List<TrelloCheckList> getCheckLists(TrelloCard trelloCard, TrelloCheckListFactory factory, TrelloAccount account);
    TrelloCheckList createCheckList(String cardID, String name, TrelloCheckListFactory factory, TrelloAccount account);    
    JSONObject createCheckListIem(String checkListID, String name, TrelloAccount account);
    TrelloAttachment createAttachmentLink(String cardID, String name, String url, TrelloAttachmentFactory factory, TrelloAccount account);
    TrelloComment createComment(String cardID, String text, TrelloActionFactory actionFactory, TrelloCommentFactory commentFactory, TrelloAccount account, Trello trello);
    int deleteComment(String cardID, String actionID, TrelloAccount account);
    int deleteCheckListItem(TrelloCheckList checkList, TrelloCheckListItem item, TrelloAccount account);  
    int setCheckListItemState(TrelloCheckList checkList, TrelloCheckListItem item, TrelloAccount account);   
    int addLabel(TrelloCard card, TrelloLabel label, TrelloAccount account);
    int removeLabel(TrelloCard card, TrelloLabel label, TrelloAccount account);
}
