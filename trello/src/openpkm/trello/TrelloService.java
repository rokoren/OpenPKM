/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.Trello;
import java.util.List;

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
}
