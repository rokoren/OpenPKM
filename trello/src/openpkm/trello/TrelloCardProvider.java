/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.Card;
import java.util.Properties;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCardProvider 
{
    String CARD_ROLE_LINK = "link";
    
    String PROP_APP_ID           = "app.id";        
    String PROP_TIME_CREATED     = "time.created"; 
    String PROP_ACCOUNT_USERNAME = "account.username";    
    String PROP_BOARD_ID         = "board.id";
    String PROP_LIST_ID          = "list.id";
    String PROP_CARD_ID          = "card.id";
    String PROP_CARD_NAME        = "card.name";
    String PROP_CARD_DESCRIPTION = "card.description";
    String PROP_CARD_POSITION    = "card.position"; 
    String PROP_CARD_ROLE        = "card.role"; 
    String PROP_CARD_CLOSED      = "card.closed"; 

    TrelloCard getCard(Properties props);  
    TrelloCard createCard(Card card);      
}
