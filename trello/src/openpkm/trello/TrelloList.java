/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

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
}
