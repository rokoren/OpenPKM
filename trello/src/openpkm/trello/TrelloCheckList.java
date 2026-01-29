/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCheckList 
{
    String getCardID();
    String getCheckListID();
    String getTitle();
    int getPosition();
    Collection<TrelloCheckListItem> getItems();
    TrelloCheckListItem getItem(String itemID);
    TrelloCheckListItem addItem(String title, int position);     
}
