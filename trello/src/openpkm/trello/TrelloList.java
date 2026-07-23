/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import openpkm.base.NodePositionProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.StateSupport;

/**
 *
 * @author Rok Koren
 */
public interface TrelloList extends StateSupport, PropertiesProvider, NodePositionProvider
{
    String getBoardID();  
    String getListID();
    String getListName();
    void setListName(String name);
    Integer getListPosition();      
}
