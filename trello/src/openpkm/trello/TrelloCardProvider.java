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
public interface TrelloCardProvider extends SourceProvider<TrelloCard>
{   
    Collection<TrelloCard> getCards();   
    void createLink(TrelloList list, String url);
    void createCard(TrelloList list, String name);
    TrelloCardFactory getFactory();    
    TrelloAccount getAccount();
}
