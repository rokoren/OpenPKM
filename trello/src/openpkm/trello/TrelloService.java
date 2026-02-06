/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.List;

/**
 *
 * @author Rok Koren
 */
public interface TrelloService 
{
    List<TrelloBoard> getBoards(TrelloAccount account);    
}
