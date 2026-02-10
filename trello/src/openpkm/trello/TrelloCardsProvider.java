/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;
import openpkm.base.SourceProvider;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCardsProvider extends SourceProvider
{
    TrelloCardProvider getCardProvider();   
    Collection<TrelloCard> getCards();     
}
