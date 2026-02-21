/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;
import openpkm.base.NodePositionProvider;
import openpkm.base.PropertiesProvider;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCheckList extends PropertiesProvider, NodePositionProvider
{
    String getBoardID();
    String getCardID();
    String getCheckListID();
    String getCheckListName();
    Integer getCheckListPosition(); 
    Collection<TrelloCheckListItem> getItems();
    ChangeSupport getChangeSupport();
}
