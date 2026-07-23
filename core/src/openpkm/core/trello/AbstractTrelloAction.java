/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.Color;
import java.awt.Image;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.StringTokenizer;
import openpkm.base.DisplayNameProvider;
import openpkm.base.IconProvider;
import openpkm.base.NodeDateTimeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloCardAction;
import openpkm.utils.UserIcon;
import org.openide.nodes.Children;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public abstract class AbstractTrelloAction implements TrelloAction, DisplayNameProvider, IconProvider, NodeDateTimeProvider
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
    
    protected Lookup lkp;  

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
            LocalDateTime utc = LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
            return utc.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();            
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
    public boolean merge(PropertiesProvider provider)
    {
        if(props.equals(provider.getProperties()))       
        {
            return false;
        }
        props.putAll(provider.getProperties());        
        return true;
    }     
  
// TODO DisplayNameProvider

    @Override
    public String getDisplayName(TextFormat format)
    {
        if(format == TextFormat.PLAIN)
        {
            return toString();
        }
        return null;        
    } 

// TODO IconProvider   
    
    @Override
    public Image getIcon(int type) 
    {
        StringTokenizer st = new StringTokenizer(getMemberFullName());
        return new UserIcon(st.nextToken(), st.nextToken(), UserIcon.Type.CIRCLE, Color.ORANGE).getImage();
    }     
    
// TODO NodeProvider         

    @Override
    public String getName() 
    {
        return getActionID();
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
    public LocalDateTime getDateTime()
    {
        return getActionDate();
    }        

    @Override
    public Children getChildren() 
    {
        return Children.LEAF;
    }  
    
    @Override
    public HelpCtx getHelp()
    {
        return HelpCtx.DEFAULT_HELP;
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
    
    public static final class CommentCard extends AbstractTrelloAction
    {        
        public CommentCard(Properties props) 
        {
            super(props);            
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
        public String toString()
        {
            return "Comment card " + getCardName().toUpperCase();
        }
    }     
}
