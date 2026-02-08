/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Action;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloActionProvider;
import openpkm.utils.DateTimeUtils;
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
            return new AbstractTrelloAction.CommentCard(props);
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
            props.setProperty(AbstractTrelloAction.PROP_LIST_BEFORE, action.getData().getListBefore().getId());
            props.setProperty(AbstractTrelloAction.PROP_LIST_AFTER, action.getData().getListAfter().getId());
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
}
