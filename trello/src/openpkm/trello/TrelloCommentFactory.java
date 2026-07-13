/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.Trello;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCommentFactory 
{
    TrelloComment getComment(TrelloAction action, Trello trello, TrelloAccount account);
}
