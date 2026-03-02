/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Card;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import openpkm.base.HtmlDisplayNameProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TitleProvider;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardProvider;
import openpkm.youtube.YouTubeVideo;
import openpkm.youtube.YouTubeVideoProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloCardProvider.class)
public class TrelloCardProviderImpl implements TrelloCardProvider
{    
    private static final Logger LOG = Logger.getLogger(TrelloCardProvider.class.getName());  
        
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

    /*
    @Override
    public TrelloCard createCard(Properties props, TrelloCardsProvider provider)
    {
        FileObject root = provider.getRootFolder();
        if(root != null)
        {
            if(isCardLink(props))
            {
                try
                {
                    FileObject file = root.createData(getCardID(props), PropertiesProvider.EXTENSION);
                    OutputStream os = file.getOutputStream();
                    props.store(os, "OpenPKM Trello Card Link"); 
                    os.close();   

                    return getCard(file);
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }              
            }
            else
            {
                try
                {
                    FileObject projectDirectory = FileUtil.createFolder(root, getCardID(props));           
                    FileObject projectFolder = FileUtil.createFolder(projectDirectory, TrelloCardProjectFactory.PROJECT_FOLDER);                   

                    OutputStream os = projectFolder.createAndOpen(TrelloCardProjectFactory.PROJECT_FILE);
                    props.store(os, "OpenPKM Trello Card Project"); 
                    os.close();    

                    return getCard(projectDirectory);
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }         
            }            
        }
        return null;
    }
    */
    
    public static String getCardID(Properties props)
    {
        return props.getProperty(PROP_CARD_ID);      
    }       
    
    private static final class TrelloCardImpl implements TrelloCard, HtmlDisplayNameProvider
    {         
        private final Properties props;
        private final PropertyChangeSupport propertyChangeSupport;
        
        private Lookup lkp;                 
        
        public TrelloCardImpl(Properties props)
        {
            this.props = props;  
            propertyChangeSupport = new PropertyChangeSupport(this);
        }     

        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            { 
                List list = new ArrayList();

                list.add(this);
                if(isCardLink())
                {
                    YouTubeVideoProvider provider = Lookup.getDefault().lookup(YouTubeVideoProvider.class);
                    if(provider != null)
                    {
                        YouTubeVideo video = provider.getVideo(props);
                        if(video != null)
                        {
                            list.add(video);
                        }
                    }
                }

                lkp = Lookups.fixed(list.toArray(new Object[list.size()]));              
            }
            return lkp;
        }         
        
// TODO TrelloCard        
        
        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }         
        
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
        public boolean isDeleted()
        {
            String string = props.getProperty(PROP_DELETED);
            if(string != null)
            {
                return Boolean.parseBoolean(string);
            }
            return false;
        }

        @Override
        public void setDeleted(boolean isDeleted)
        {
            boolean oldValue = isDeleted();
            props.setProperty(PROP_DELETED, Boolean.toString(isDeleted));
            propertyChangeSupport.firePropertyChange(PROP_DELETED, oldValue, isDeleted);
        }  

        @Override
        public void save(OutputStream os, String comments) throws IOException
        {
            props.store(os, comments); 
            LOG.info("Trello Card Properties saved");      
        }  

        @Override
        public String getAccountUsername()
        {
            return props.getProperty(TrelloCardProvider.PROP_ACCOUNT_USERNAME);
        }          
        
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
        public String getCardID() 
        {
            return props.getProperty(PROP_CARD_ID);
        }   

        @Override
        public String getCardName() 
        {
            return props.getProperty(PROP_CARD_NAME);
        }     

        @Override
        public Integer getCardPosition() 
        {
            String string = props.getProperty(PROP_CARD_POSITION);
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
            String string = props.getProperty(PROP_CARD_CLOSED);
            if(string != null)
            {
                return Boolean.parseBoolean(string);
            }
            return null;
        }

        @Override
        public Boolean isCardSubsribed() 
        {
            String string = props.getProperty(PROP_CARD_SUBSCRIBED);
            if(string != null)
            {
                return Boolean.parseBoolean(string);
            }
            return null;
        }

        @Override
        public Boolean isCardPinned() 
        {
            String string = props.getProperty(PROP_CARD_PINNED);
            if(string != null)
            {
                return Boolean.parseBoolean(string);
            }
            return null;
        }

        @Override
        public Boolean isCardDueComplete()
        {
            String string = props.getProperty(PROP_CARD_DUE_COMPLETE);
            if(string != null)
            {
                return Boolean.parseBoolean(string);
            }
            return null;
        }

        @Override
        public Boolean isCardTemplate()
        {
            String string = props.getProperty(PROP_CARD_TEMPLATE);
            if(string != null)
            {
                return Boolean.parseBoolean(string);
            }
            return null;
        }

        @Override
        public LocalDateTime getDateLastActivity() 
        {
            String string = props.getProperty(PROP_CARD_DATE_LAST_ACTIVITY);
            if(string != null)
            {
                return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE);                   
            }                
            return null;
        }

        @Override
        public List<String> getCardLabelsID() 
        {
            String string = props.getProperty(PROP_CARD_LABELS_ID);
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
            Object oldValue = props.remove(TrelloCardProvider.PROP_CARD_LABELS_ID);
        }
        else
        {
            StringJoiner joiner = new StringJoiner(",");
            for(String id : ids)
            {
                joiner.add(id);
            }
            props.setProperty(TrelloCardProvider.PROP_CARD_LABELS_ID, joiner.toString());        
        }
    }         
        
        @Override
        public String getCardRole() 
        {
            return props.getProperty(PROP_CARD_ROLE);
        }          
        
        @Override
        public boolean isCardLink() 
        {
            String cardRole = getCardRole();
            if(cardRole != null)
            {
                return cardRole.equalsIgnoreCase(CARD_ROLE_LINK);
            }
            return false;
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
            propertyChangeSupport.firePropertyChange(PROP_PROPS_ALL, oldValue, props);
        }

// TODO HtmlDisplayNameProvider         
        
        @Override
        public String getHtmlDisplayName()
        {
            TitleProvider provider = getLookup().lookup(TitleProvider.class);
            if(provider == null)
            {
                return null;
            }
            if(isCardDueComplete())
            {
                return "<html><s>" + provider.getTitle() + "</s></html>";
            }
            return "<html><b>" + provider.getTitle() + "</b></html>";
        }
    }        
}
