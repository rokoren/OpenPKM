/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.Action;

/**
 *
 * @author Rok Koren
 */
public class TrelloActionProviderImpl implements TrelloActionProvider
{
    @Override
    public TrelloAction getAction(Action action) 
    {
        return AbstractTrelloAction.getTrelloAction(action);
    }
}
