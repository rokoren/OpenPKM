/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.TList;
import java.awt.Image;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloListProvider.class)
public class TrelloListProviderImpl implements TrelloListProvider
{        
    private static final String PROP_BOARD_ID      = "board.id";
    private static final String PROP_LIST_ID       = "list.id";
    private static final String PROP_LIST_NAME     = "list.name";    
    private static final String PROP_LIST_POSITION = "list.position";     
    
    private static final Logger LOG = Logger.getLogger(TrelloListProvider.class.getName());    

    @Override
    public TrelloList getList(Properties props) 
    {
        return new TrelloListImpl(props);
    }
    
    @Override
    public TrelloList createList(TList list) 
    {
        Properties props = new Properties();
        props.setProperty(PROP_BOARD_ID, list.getIdBoard());
        props.setProperty(PROP_LIST_ID, list.getId());
        props.setProperty(PROP_LIST_NAME, list.getName());
        props.setProperty(PROP_LIST_POSITION, list.getPos() + "");          
        return new TrelloListImpl(props);
    } 
    
    private static final class TrelloListImpl implements TrelloList, NodeProvider, PropertiesProvider
    { 
        @StaticResource()
        private static final String ICON = "openpkm/trello/resources/application_view_list.png";  
        
        private final Properties props; 
        
        private Map<String, TrelloCard> cards;         
        
        public TrelloListImpl(Properties props)
        {
            this.props = props;              
        }     

// TODO TrelloList        
        
        @Override
        public String getBoardID() 
        {
            return props.getProperty(PROP_BOARD_ID);
        } 
        
        @Override
        public String getListID() 
        {
            return props.getProperty(PROP_LIST_ID);
        }         
                
        @Override
        public String getListName() 
        {
            return props.getProperty(PROP_LIST_ID);
        }

        @Override
        public Integer getListPosition() 
        {
            String string = props.getProperty(PROP_LIST_POSITION);
            if(string != null)
            {
                try
                {
                    return Integer.parseInt(string);                    
                }
                catch(NumberFormatException e)
                {
                    LOG.warning(e.getMessage());
                }
            }
            return null;
        }
        
// TODO PropertiesProvider        
        
        @Override
        public Properties getProperties()
        {
            return props;
        }                  

// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getListID();
        }
        
        @Override
        public String getDisplayName() 
        {
            return getListName();
        }
        
        @Override
        public Image getIcon(boolean opened) 
        {
            return ImageUtilities.loadImage(ICON);
        }         

        private synchronized Map<String, TrelloCard> getCardsMap()
        {
            if (cards == null)
            {
                cards = new HashMap<>();
                List<TrelloCard> list = TrelloService.getCards(this);
                for (TrelloCard trelloCard : list)
                {
                    cards.put(trelloCard.getCardID(), trelloCard);
                }  
            }
            return cards;
        }      

        @Override
        public Collection<TrelloCard> getCards()
        {
            return getCardsMap().values();
        }  

        @Override
        public TrelloCard getCard(String cardID)
        {      
            return getCardsMap().get(cardID);
        } 

        @Override
        public TrelloCard addLink(String url)
        {
            try
            {
                Card card = TrelloService.createLink(getListID(), url);
                TrelloCard trelloCard = new TrelloCardImpl(card);
                getCardsMap().put(trelloCard.getCardID(), trelloCard);
                return trelloCard;    
            }
            catch(UnirestException e)
            {
                LOG.warning(e.getMessage());
            }
            return null;
        }   

        @Override
        public TrelloCard addCard(String title, String desc, LocalDate dueDate, int position)
        {
            Card card = new Card();
            card.setName(title);
            if(desc != null)
            {
                card.setDesc(desc);            
            }
            card.setIdList(getListID());
            card.setIdBoard(getBoardID());
            card.setPos(position);
            if(dueDate != null)
            {
                card.setDue(Date.from(dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            Card card1 = TrelloService.createCard(getListID(), card);
            TrelloCard trelloCard = new TrelloCardImpl(card1);
            getCardsMap().put(trelloCard.getCardID(), trelloCard);
            return trelloCard;
        } 

        @Override    
        public void deleteCard(String cardID)
        {
            TrelloCard card = getCardsMap().remove(cardID);
            if(card != null)
            {
                card.delete();
            }
        }
    }     
}
