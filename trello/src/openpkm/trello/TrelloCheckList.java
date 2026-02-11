/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import openpkm.base.PropertiesProvider;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCheckList extends PropertiesProvider
{
    String getCheckListID();
    String getCheckListName();
    Integer getCheckListPosition();   
}
