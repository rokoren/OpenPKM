/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardFactory;
import openpkm.trello.TrelloCardLink;
import openpkm.youtube.YouTubeVideo;
import openpkm.youtube.YouTubeVideoFactory;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import openpkm.trello.TrelloCardProvider;

/**
 *
 * @author rok
 */
public class TrelloCardImpl implements TrelloCardLink, PropertyChangeListener
{
    private static final Logger LOG = Logger.getLogger(TrelloCard.class.getName());   
    
    private final Properties props;
    private final PropertyChangeSupport propertyChangeSupport;

    private Lookup lkp;  
    private SourceState state;        

    public TrelloCardImpl(Properties props)
    {
        this.props = props;  
        propertyChangeSupport = new PropertyChangeSupport(this);
        propertyChangeSupport.addPropertyChangeListener(this);
    }     

    @Override
    public Lookup getLookup() 
    {
        if (lkp == null) 
        { 
            List list = new ArrayList();

            list.add(this);
            list.add(new DisplayNameProviderImpl());
            list.add(new CardComplete());
            Lookup lookup = Lookups.fixed(list.toArray(new Object[list.size()]));  

            YouTubeVideoFactory provider = Lookup.getDefault().lookup(YouTubeVideoFactory.class);
            if(provider != null)
            {
                YouTubeVideo video = provider.getVideo(props, YouTubeVideoFactory.Type.BASIC);
                if(video != null)
                {
                    lkp = new ProxyLookup(lookup, Lookups.proxy(video));
                }
            }
        }
        return lkp;
    }         

    // TODO TrelloCard                

    @Override
    public String getAppID() 
    {
        return props.getProperty(PROP_APP_ID);
    }   

    @Override
    public LocalDateTime getTimeCreated() 
    {
        String string = props.getProperty(PROP_TIME_CREATED);
        if(string != null)
        {
            return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
    }  

    @Override
    public String getSourceID()
    {
        return getCardID();
    }

    @Override
    public boolean isModified() 
    {
        return state == SourceState.MODIFIED;
    }

    @Override
    public void markModified()
    {
        SourceState oldValue = state;
        state = SourceState.MODIFIED;
        propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
    }   
    
    @Override
    public boolean isDeleted() 
    {
        return state == SourceState.DELETED;
    }    

    @Override
    public void notifyDeleted()
    {
        SourceState oldValue = state;
        state = SourceState.DELETED;
        propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
    }  

    @Override
    public String getAccountUsername()
    {
        return props.getProperty(TrelloCardFactory.PROP_ACCOUNT_USERNAME);
    }          

    @Override
    public String getBoardID() 
    {
        return props.getProperty(TrelloCardFactory.PROP_BOARD_ID);
    }    

    @Override
    public String getListID() 
    {
        return props.getProperty(TrelloCardFactory.PROP_LIST_ID);
    } 

    @Override
    public String getCardID() 
    {
        return props.getProperty(TrelloCardFactory.PROP_CARD_ID);
    }   

    @Override
    public String getCardName() 
    {
        return props.getProperty(TrelloCardFactory.PROP_CARD_NAME);
    } 

    @Override
    public void setCardName(String name)
    {
        if(name == null)
        {
            Object oldValue = props.remove(TrelloCardFactory.PROP_CARD_NAME);
            propertyChangeSupport.firePropertyChange(TrelloCardFactory.PROP_CARD_NAME, oldValue, name);
        }
        else
        {
            Object oldValue = props.setProperty(TrelloCardFactory.PROP_CARD_NAME, name);
            propertyChangeSupport.firePropertyChange(TrelloCardFactory.PROP_CARD_NAME, oldValue, name);            
        }
    } 

    public void addCardNameListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(TrelloCardFactory.PROP_CARD_NAME, listener);
    }

    public void removeCardNameListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(TrelloCardFactory.PROP_CARD_NAME, listener);
    }        

    @Override
    public Integer getCardPosition() 
    {
        String string = props.getProperty(TrelloCardFactory.PROP_CARD_POSITION);
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

    @Override
    public Boolean isCardClosed()
    {
        String string = props.getProperty(TrelloCardFactory.PROP_CARD_CLOSED);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    }

    @Override
    public Boolean isCardSubsribed() 
    {
        String string = props.getProperty(TrelloCardFactory.PROP_CARD_SUBSCRIBED);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    }

    @Override
    public Boolean isCardPinned() 
    {
        String string = props.getProperty(TrelloCardFactory.PROP_CARD_PINNED);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    }

    @Override
    public Boolean isCardDueComplete()
    {
        String string = props.getProperty(TrelloCardFactory.PROP_CARD_DUE_COMPLETE);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    }

    @Override
    public void setCardDueComplete(Boolean complete)
    {
        if(complete == null)
        {
            Object oldValue = props.remove(TrelloCardFactory.PROP_CARD_DUE_COMPLETE);
            if(oldValue != null)
            {
                oldValue = Boolean.parseBoolean(oldValue.toString());
            }
            propertyChangeSupport.firePropertyChange(TrelloCardFactory.PROP_CARD_DUE_COMPLETE, oldValue, complete);
        }
        else
        {
            Object oldValue = props.setProperty(TrelloCardFactory.PROP_CARD_DUE_COMPLETE, complete.toString());
            if(oldValue != null)
            {
                oldValue = Boolean.parseBoolean(oldValue.toString());
            }
            propertyChangeSupport.firePropertyChange(TrelloCardFactory.PROP_CARD_DUE_COMPLETE, oldValue, complete);                
        }
    }

    public void addCardDueCompleteListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(TrelloCardFactory.PROP_CARD_DUE_COMPLETE, listener);
    }

    public void removeCardDueCompleteListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(TrelloCardFactory.PROP_CARD_DUE_COMPLETE, listener);
    }

    @Override
    public Boolean isCardTemplate()
    {
        String string = props.getProperty(TrelloCardFactory.PROP_CARD_TEMPLATE);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    }

    @Override
    public LocalDateTime getDateLastActivity() 
    {
        String string = props.getProperty(TrelloCardFactory.PROP_CARD_DATE_LAST_ACTIVITY);
        if(string != null)
        {
            return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE);                   
        }                
        return null;
    }

    @Override
    public List<String> getCardLabelsID() 
    {
        String string = props.getProperty(TrelloCardFactory.PROP_CARD_LABELS_ID);
        if(string != null)
        {
            return List.of(string.split(","));                   
        }                
        return null;
    }

    @Override
    public void setCardLabelsID(List<String> ids) 
    {
        if(ids == null)
        {
            Object oldValue = props.remove(TrelloCardFactory.PROP_CARD_LABELS_ID);
        }
        else
        {
            StringJoiner joiner = new StringJoiner(",");
            for(String id : ids)
            {
                joiner.add(id);
            }
            props.setProperty(TrelloCardFactory.PROP_CARD_LABELS_ID, joiner.toString());        
        }
    }         

    @Override
    public String getCardRole() 
    {
        return props.getProperty(TrelloCardFactory.PROP_CARD_ROLE);
    }                 

    // TODO PropertiesProvider        

    @Override
    public Properties getProperties()
    {
        return props;
    }  

    @Override
    public void merge(PropertiesProvider provider)
    {
        Object oldValue = props.clone();
        props.putAll(provider.getProperties());
        //propertyChangeSupport.firePropertyChange(PROP_PROPS_ALL, oldValue, props);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) 
    {
        markModified();
    }

    private final class DisplayNameProviderImpl implements DisplayNameProvider, ChangeSupportProvider, PropertyChangeListener
    {
        private final ChangeSupport changeSupport;

        public DisplayNameProviderImpl() 
        {
            addCardNameListener(this);
            addCardDueCompleteListener(this);
            changeSupport = new ChangeSupport(this);
        }                
        
        @Override
        public String getDisplayName(TextFormat format) 
        {
            String displayName = null;
            YouTubeVideo video = getLookup().lookup(YouTubeVideo.class);
            if(video != null)
            {
                displayName = video.getVideoTitle();
            }
            else
            {
                displayName = getCardName();
            }
            
            if(format == TextFormat.PLAIN)
            {
                return displayName;
            }
            else if(format == TextFormat.HTML)
            {
                if(isCardDueComplete())
                {
                    return "<html><s>" + displayName + "</s></html>";
                }
                return "<html><b>" + displayName + "</b></html>";                
            }
            return null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            changeSupport.fireChange();
        }

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener)
        {
            changeSupport.removeChangeListener(listener);
        }        
    }
    
    private final class CardComplete implements ActionProvider<TrelloCardProvider>
    {
        @Override
        public Action getAction(TrelloCardProvider provider) 
        {
            return new TrelloCardActionsProvider.CardComplete(TrelloCardImpl.this, provider.getAccount());
        }        
    }   
}
