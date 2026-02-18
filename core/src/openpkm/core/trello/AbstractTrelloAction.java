/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

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
import openpkm.base.NodeDateTimeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloCardAction;
import openpkm.utils.UserIcon;
import org.openide.nodes.Children;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public abstract class AbstractTrelloAction implements TrelloAction, NodeDateTimeProvider
{
    public static final String PROP_ACTION_ID        = "action.id";
    public static final String PROP_ACTION_TYPE      = "action.type";    
    public static final String PROP_ACTION_DATE      = "action.date";
    public static final String PROP_MEMBER_ID        = "member.id";
    public static final String PROP_MEMBER_FULL_NAME = "member.full.name";   
    
    public static final String PROP_CARD_ID         = "card.id";
    public static final String PROP_CARD_NAME       = "card.name";
    public static final String PROP_LIST_ID         = "list.id";       
    public static final String PROP_LIST_NAME       = "list.name"; 
    public static final String PROP_LIST_BEFORE     = "list.before";
    public static final String PROP_LIST_AFTER      = "list.after";      
    public static final String PROP_CHECKLIST_ID    = "checklist.id";    
    public static final String PROP_CHECKLIST_NAME  = "checklist.name"; 
    public static final String PROP_CHECKITEM_ID    = "checkitem.id";
    public static final String PROP_CHECKITEM_NAME  = "checkitem.name";
    public static final String PROP_CHECKITEM_STATE = "checkitem.state";
    public static final String PROP_ATTACHMENT_ID   = "attachment.id";
    public static final String PROP_ATTACHMENT_NAME = "attachment.name";
    public static final String PROP_COMMENT_TEXT    = "comment.text";
    
    public static final String TYPE_CREATE_CARD              = "createCard";
    public static final String TYPE_DELETE_CARD              = "deleteCard";
    public static final String TYPE_COMMENT_CARD             = "commentCard";
    public static final String TYPE_CREATE_LIST              = "createList";
    public static final String TYPE_CREATE_BOARD             = "createBoard";
    public static final String TYPE_CARD_UPDATE              = "updateCard";
    public static final String TYPE_ADD_CHECKLIST_TO_CARD    = "addChecklistToCard";
    public static final String TYPE_ADD_ATTACHMENT_TO_CARD   = "addAttachmentToCard";        
    public static final String TYPE_REMOVE_CHECKLIST_TO_CARD = "removeChecklistFromCard";
    public static final String TYPE_UPDATE_ITEM_STATE        = "updateCheckItemStateOnCard";        

    protected final Properties props;   

    public AbstractTrelloAction(Properties props) 
    {
        this.props = props;
    }

// TODO TrelloAction    
    
    @Override
    public String getActionID()
    {
        return props.getProperty(PROP_ACTION_ID);
    }
    
    @Override
    public String getActionType() 
    {
        return props.getProperty(PROP_ACTION_TYPE);
    }        

    @Override
    public LocalDateTime getActionDate() 
    {
        String string = props.getProperty(PROP_ACTION_DATE);
        if(string != null)
        {
            return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
    } 
    
    @Override
    public String getMemberID()
    {
        return props.getProperty(PROP_MEMBER_ID);
    }
    
    @Override
    public String getMemberFullName()
    {
        return props.getProperty(PROP_MEMBER_FULL_NAME);
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
        props.putAll(provider.getProperties());
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
    public LocalDateTime getDateTime()
    {
        return getActionDate();
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
    
    public static final class CardUpdate extends AbstractTrelloAction implements TrelloCardAction
    {        
        public CardUpdate(Properties props) 
        {
            super(props);
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
        public String getListID()
        {
            return props.getProperty(PROP_LIST_ID);
        }     
        
        public String getListBefore()
        {
            return props.getProperty(PROP_LIST_BEFORE);
        }   
        
        public String getListAfter()
        {
            return props.getProperty(PROP_LIST_AFTER);
        }         
        
        public boolean isListChanged()
        {
            if(getListBefore() != null && getListAfter() != null)
            {
                return getListBefore().equals(getListAfter());
            }
            return false;
        }        
                
        @Override
        public String toString()
        {
            return "Update card " + getCardName().toUpperCase();            
        }
    }
    
    public static final class AddChecklistToCard extends AbstractTrelloAction
    {
        public AddChecklistToCard(Properties props) 
        {
            super(props);
        } 

        public String getChecklistID()
        {
            return props.getProperty(PROP_CHECKLIST_ID);
        }
        
        public String getChecklistName()
        {
            return props.getProperty(PROP_CHECKLIST_NAME);
        }        
        
        public String getCardID()
        {
            return props.getProperty(PROP_CARD_ID);
        }
        
        public String getCardName()
        {
            return props.getProperty(PROP_CARD_NAME);
        }          
        
        @Override
        public String toString()
        {
            return "Add checklist to card " + getCardName().toUpperCase();
        }
    }   
    
    public static final class AddAttachmentToCard extends AbstractTrelloAction
    {
        public AddAttachmentToCard(Properties props) 
        {
            super(props);
        }       
        
        public String getAttachmentID()
        {
            return props.getProperty(PROP_ATTACHMENT_ID);
        }
        
        public String getAttachmentName()
        {
            return props.getProperty(PROP_ATTACHMENT_NAME);
        }        
        
        public String getCardID()
        {
            return props.getProperty(PROP_CARD_ID);
        }
        
        public String getCardName()
        {
            return props.getProperty(PROP_CARD_NAME);
        }          
        
        @Override
        public String toString()
        {
            return "Add attachment to card " + getCardName().toUpperCase();
        }
    }     
    
    public static final class RemoveChecklistFromCard extends AbstractTrelloAction
    {
        public RemoveChecklistFromCard(Properties props) 
        {
            super(props);
        } 

        public String getChecklistID()
        {
            return props.getProperty(PROP_CHECKLIST_ID);
        }
        
        public String getChecklistName()
        {
            return props.getProperty(PROP_CHECKLIST_NAME);
        }          
        
        public String getCardID()
        {
            return props.getProperty(PROP_CARD_ID);
        }
        
        public String getCardName()
        {
            return props.getProperty(PROP_CARD_NAME);
        }         
        
        @Override
        public String toString()
        {
            return "Remove checklist from card " + getCardName().toUpperCase();
        }
    } 

    public static final class CreateBoard extends AbstractTrelloAction
    {
        public CreateBoard(Properties props) 
        {
            super(props);
        } 
                
        @Override
        public String toString()
        {
            return "Create board";
        }
    } 
    
    public static final class CreateCard extends AbstractTrelloAction implements TrelloCardAction
    {        
        public CreateCard(Properties props) 
        {
            super(props);
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
        public String getListID()
        {
            return props.getProperty(PROP_LIST_ID);
        }         
        
        @Override
        public String toString()
        {
            return "Create card " + getCardName().toUpperCase();
        }
    }  
    
    public static final class DeleteCard extends AbstractTrelloAction implements TrelloCardAction
    {
        public DeleteCard(Properties props) 
        {
            super(props);
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
        public String getListID()
        {
            return props.getProperty(PROP_LIST_ID);
        }       
        
        @Override
        public String toString()
        {
            return "Delete card (" + getCardID() + ")";
        }
    }      
    
    public static final class CreateList extends AbstractTrelloAction
    {
        public CreateList(Properties props) 
        {
            super(props);
        } 
        
        public String getListID()
        {
            return props.getProperty(PROP_LIST_ID);
        }   
        
        public String getListName()
        {
            return props.getProperty(PROP_LIST_NAME);
        }         
                
        @Override
        public String toString()
        {
            return "Create list " + getListName().toUpperCase();
        }
    }  
    
    public static final class UpdateCheckListItemState extends AbstractTrelloAction
    {
        public UpdateCheckListItemState(Properties props) 
        {
            super(props);
        } 
        
        public String getChecklistID()
        {
            return props.getProperty(PROP_CHECKLIST_ID);
        }  
        
        public String getCheckitemID()
        {
            return props.getProperty(PROP_CHECKITEM_ID);
        }    
        
        public String getCheckitemName()
        {
            return props.getProperty(PROP_CHECKITEM_NAME);
        }    
        
        public String getCheckitemState()
        {
            return props.getProperty(PROP_CHECKITEM_STATE);
        }         
                
        @Override
        public String toString()
        {
            return "Update checklist item " + getCheckitemName() + " as " + getCheckitemState();
        }        
    }  
    
    public static final class CommentCard extends AbstractTrelloAction implements Source
    {
        private static final Logger LOG = Logger.getLogger(CommentCard.class.getName());        
        
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
             
        @Override
        public String toString()
        {
            return "Comment card " + getCardName().toUpperCase();
        }
    }     
}
