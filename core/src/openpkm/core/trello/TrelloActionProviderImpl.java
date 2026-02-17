/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Action;
import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.NodeProvider;
import openpkm.base.Source;
import openpkm.trello.AbstractTrelloAction;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloActionProvider;
import openpkm.utils.DateTimeUtils;
import openpkm.utils.UserIcon;
import org.openide.nodes.Children;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloActionProvider.class)
public class TrelloActionProviderImpl implements TrelloActionProvider
{    
    private static final Logger LOG = Logger.getLogger(TrelloActionProvider.class.getName());    

    @Override
    public TrelloAction getAction(Properties props) 
    {
        String actionType = props.getProperty(AbstractTrelloAction.PROP_ACTION_TYPE);
        if(actionType.equals(AbstractTrelloAction.TYPE_CARD_UPDATE))
        {
            return new AbstractTrelloAction.CardUpdate(props);
        }
        else if(actionType.equals(AbstractTrelloAction.TYPE_ADD_CHECKLIST_TO_CARD))
        {
            return new AbstractTrelloAction.AddChecklistToCard(props);
        }
        else if(actionType.equals(AbstractTrelloAction.TYPE_ADD_ATTACHMENT_TO_CARD))
        {
            return new AbstractTrelloAction.AddAttachmentToCard(props);
        }        
        else if(actionType.equals(AbstractTrelloAction.TYPE_REMOVE_CHECKLIST_TO_CARD))
        {
            return new AbstractTrelloAction.RemoveChecklistFromCard(props);
        }   
        else if(actionType.equals(AbstractTrelloAction.TYPE_CREATE_BOARD))
        {
            return new AbstractTrelloAction.CreateBoard(props);
        }  
        else if(actionType.equals(AbstractTrelloAction.TYPE_CREATE_CARD))
        {
            return new AbstractTrelloAction.CreateCard(props);
        } 
        else if(actionType.equals(AbstractTrelloAction.TYPE_DELETE_CARD))
        {
            return new AbstractTrelloAction.DeleteCard(props);
        }         
        else if(actionType.equals(AbstractTrelloAction.TYPE_CREATE_LIST))
        {
            return new AbstractTrelloAction.CreateList(props);
        } 
        else if(actionType.equals(AbstractTrelloAction.TYPE_COMMENT_CARD))
        {
            return new CommentCard(props);
        }    
        else if(actionType.equals(AbstractTrelloAction.TYPE_UPDATE_ITEM_STATE))
        {
            return new AbstractTrelloAction.UpdateCheckListItemState(props);
        }          
        return null;    
    }
    
    @Override
    public TrelloAction createAction(Action action) 
    {
        LocalDateTime actionDate = DateTimeUtils.convertToLocalDateTime(action.getDate());
        Properties props = new Properties();
        props.setProperty(AbstractTrelloAction.PROP_ACTION_ID, action.getId());
        props.setProperty(AbstractTrelloAction.PROP_ACTION_TYPE, action.getType());
        props.setProperty(AbstractTrelloAction.PROP_ACTION_DATE, actionDate.format(DateTimeFormatter.ISO_DATE_TIME));   
        props.setProperty(AbstractTrelloAction.PROP_MEMBER_ID, action.getIdMemberCreator());
        props.setProperty(AbstractTrelloAction.PROP_MEMBER_FULL_NAME, action.getMemberCreator().getFullName());
        
        if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_CARD_UPDATE))
        {
            props.setProperty(AbstractTrelloAction.PROP_CARD_ID, action.getData().getCard().getId());
            props.setProperty(AbstractTrelloAction.PROP_CARD_NAME, action.getData().getCard().getName());
            props.setProperty(AbstractTrelloAction.PROP_LIST_ID, action.getData().getList().getId());
            if(action.getData().getListBefore() != null)
            {
                props.setProperty(AbstractTrelloAction.PROP_LIST_BEFORE, action.getData().getListBefore().getId());                 
            }
            if(action.getData().getListAfter() != null)
            {
                props.setProperty(AbstractTrelloAction.PROP_LIST_AFTER, action.getData().getListAfter().getId());
            }
        }
        else if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_ADD_CHECKLIST_TO_CARD))
        {
            props.setProperty(AbstractTrelloAction.PROP_CARD_ID, action.getData().getCard().getId());
            props.setProperty(AbstractTrelloAction.PROP_CARD_NAME, action.getData().getCard().getName());            
            props.setProperty(AbstractTrelloAction.PROP_CHECKLIST_ID, action.getData().getChecklist().getId());
            props.setProperty(AbstractTrelloAction.PROP_CHECKLIST_NAME, action.getData().getChecklist().getName());
        }
        else if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_ADD_ATTACHMENT_TO_CARD))
        {
            props.setProperty(AbstractTrelloAction.PROP_CARD_ID, action.getData().getCard().getId());
            props.setProperty(AbstractTrelloAction.PROP_CARD_NAME, action.getData().getCard().getName());  
            props.setProperty(AbstractTrelloAction.PROP_ATTACHMENT_ID, action.getData().getAttachment().getId());  
            props.setProperty(AbstractTrelloAction.PROP_ATTACHMENT_NAME, action.getData().getAttachment().getName());
        }        
        else if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_REMOVE_CHECKLIST_TO_CARD))
        {
            props.setProperty(AbstractTrelloAction.PROP_CARD_ID, action.getData().getCard().getId());
            props.setProperty(AbstractTrelloAction.PROP_CARD_NAME, action.getData().getCard().getName());            
            props.setProperty(AbstractTrelloAction.PROP_CHECKLIST_ID, action.getData().getChecklist().getId());
            props.setProperty(AbstractTrelloAction.PROP_CHECKLIST_NAME, action.getData().getChecklist().getName());
        }   
        else if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_CREATE_BOARD))
        {
        }  
        else if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_CREATE_CARD))
        {
            props.setProperty(AbstractTrelloAction.PROP_CARD_ID, action.getData().getCard().getId());
            props.setProperty(AbstractTrelloAction.PROP_CARD_NAME, action.getData().getCard().getName());  
            props.setProperty(AbstractTrelloAction.PROP_LIST_ID, action.getData().getList().getId());
        } 
        else if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_DELETE_CARD))
        {
            props.setProperty(AbstractTrelloAction.PROP_CARD_ID, action.getData().getCard().getId());
            props.setProperty(AbstractTrelloAction.PROP_CARD_NAME, action.getData().getCard().getName());  
            props.setProperty(AbstractTrelloAction.PROP_LIST_ID, action.getData().getList().getId());
        }         
        else if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_CREATE_LIST))
        {
            props.setProperty(AbstractTrelloAction.PROP_LIST_ID, action.getData().getList().getId());
            props.setProperty(AbstractTrelloAction.PROP_LIST_NAME, action.getData().getList().getName()); 
        } 
        else if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_COMMENT_CARD))
        {
            props.setProperty(AbstractTrelloAction.PROP_CARD_ID, action.getData().getCard().getId());
            props.setProperty(AbstractTrelloAction.PROP_CARD_NAME, action.getData().getCard().getName());  
            props.setProperty(AbstractTrelloAction.PROP_LIST_ID, action.getData().getList().getId());
            props.setProperty(AbstractTrelloAction.PROP_COMMENT_TEXT, action.getData().getText());
        }    
        else if(action.getType().equalsIgnoreCase(AbstractTrelloAction.TYPE_UPDATE_ITEM_STATE))
        {
            props.setProperty(AbstractTrelloAction.PROP_CHECKLIST_ID, action.getData().getChecklist().getId());
            props.setProperty(AbstractTrelloAction.PROP_CHECKITEM_ID, action.getData().getCheckItem().getId());
            props.setProperty(AbstractTrelloAction.PROP_CHECKITEM_NAME, action.getData().getCheckItem().getName());
            props.setProperty(AbstractTrelloAction.PROP_CHECKITEM_STATE, action.getData().getCheckItem().getState());
        }        
        
        return getAction(props);
    }  
    
    public static final class CommentCard extends AbstractTrelloAction implements Source, NodeProvider
    {
        private final PropertyChangeSupport propertyChangeSupport;
        private final ChangeSupport changeSupport;  

        private Lookup lkp;         
        private boolean isDeleted;          
        
        public CommentCard(Properties props) 
        {
            super(props);
            propertyChangeSupport = new PropertyChangeSupport(this);
            changeSupport = new ChangeSupport(this);              
        } 
        
        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            { 
                lkp = Lookups.fixed(this);              
            }
            return lkp;
        }        
        
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
        public void addChangeListener(ChangeListener listener)
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener)
        {
            changeSupport.removeChangeListener(listener);
        } 
        
        @Override
        public boolean isDeleted()
        {
            return isDeleted;
        }

        @Override
        public void setDeleted()
        {
            isDeleted = true;
            changeSupport.fireChange();
        } 
        
        @Override
        public String getAppID()
        {
            return props.getProperty(PROP_APP_ID);
        }         
        
        @Override
        public LocalDateTime getTimeCreated() 
        {
            String created = props.getProperty(PROP_TIME_CREATED);
            if(created != null)
            {
                return LocalDateTime.parse(created, DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;
        }  
        
        @Override
        public String getSourceID()
        {
            return getActionID();
        }        
        
        public String getCardID()
        {
            return props.getProperty(PROP_CARD_ID);
        }
        
        public String getCardName()
        {
            return props.getProperty(PROP_CARD_NAME);
        }        
        
        public String getListID()
        {
            return props.getProperty(PROP_LIST_ID);
        }        
        
        public String getCommentText() 
        {
            return props.getProperty(PROP_COMMENT_TEXT);
        } 
        
        @Override
        public void save(OutputStream os, String comments) throws IOException
        {
            props.store(os, comments); 
            LOG.info("Trello comment saved");
        }         
        
// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getActionID();
        }

        @Override
        public String getDisplayName()
        {
            return toString();
        }        

        @Override
        public Image getIcon(boolean opened) 
        {
            StringTokenizer st = new StringTokenizer(getMemberFullName());
            return new UserIcon(st.nextToken(), st.nextToken(), UserIcon.Type.CIRCLE, Color.ORANGE).getImage();
        } 
        
        @Override
        public List<javax.swing.Action> getActions() 
        {       
            return Collections.EMPTY_LIST;
        }         

        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }          
             
        @Override
        public String toString()
        {
            return "Comment card " + getCardName().toUpperCase();
        }
    }     
}
