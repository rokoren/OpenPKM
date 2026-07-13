/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Card;
import java.util.Properties;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloCardFactory.class)
public class TrelloCardFactoryImpl implements TrelloCardFactory
{            
    @Override
    public TrelloCard getCard(Properties props)
    {
        return new TrelloCardImpl(props);
    }
    
    @Override
    public TrelloCard createCard(Card card) 
    {
        Properties props = new Properties();
        props.setProperty(PROP_CARD_ID, card.getId());
        props.setProperty(PROP_BOARD_ID, card.getIdBoard());
        props.setProperty(PROP_LIST_ID, card.getIdList());            
        props.setProperty(PROP_CARD_NAME, card.getName());
        props.setProperty(PROP_CARD_DESCRIPTION, card.getDesc());
        props.setProperty(PROP_CARD_POSITION, card.getPos() + "");
        props.setProperty(PROP_CARD_CLOSED, Boolean.toString(card.isClosed()));
        props.setProperty(PROP_CARD_SUBSCRIBED, Boolean.toString(card.isSubscribed()));
        return getCard(props);
    }     
}
