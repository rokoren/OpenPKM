/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.time.LocalDate;
import java.util.Collection;

/**
 *
 * @author Rok Koren
 */
public interface TrelloList 
{
    String getBoardID();  
    String getListID();
    String getListName();
    Integer getListPosition();   
    Collection<TrelloCard> getCards();  
    TrelloCard getCard(String cardID);
    TrelloCard addLink(String url);      
    TrelloCard addCard(String title, String desc, LocalDate dueDate, int position);  
    void deleteCard(String cardID);    
}
