/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;
import javax.swing.event.ChangeListener;
import kong.unirest.json.JSONObject;
import openpkm.base.PropertiesProvider;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCheckList extends PropertiesProvider
{
    String getBoardID();
    String getCardID();
    String getCheckListID();
    String getCheckListName();
    Integer getCheckListPosition(); 
    Collection<TrelloCheckListItem> getItems();
    void addItem(JSONObject json);
    void removeItem(String itemID);
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);
}
