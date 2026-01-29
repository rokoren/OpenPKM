/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.Action;
import java.util.Date;

/**
 *
 * @author Rok Koren
 */
public abstract class AbstractTrelloAction implements TrelloAction
{
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

    protected final Action action;   

    public AbstractTrelloAction(Action action) 
    {
        this.action = action;
    }

    @Override
    public String getActionID()
    {
        return action.getId();
    }
    
    @Override
    public String getType() 
    {
        return action.getType();
    }        

    @Override
    public Date getDate() 
    {
        return action.getDate();
    } 
    
    @Override
    public String getMemberID()
    {
        return action.getIdMemberCreator();
    }
    
    public static AbstractTrelloAction getTrelloAction(Action action)
    {
        if(action.getType().equalsIgnoreCase(TYPE_CARD_UPDATE))
        {
            return new CardUpdate(action);
        }
        else if(action.getType().equalsIgnoreCase(TYPE_ADD_CHECKLIST_TO_CARD))
        {
            return new AddChecklistToCard(action);
        }
        else if(action.getType().equalsIgnoreCase(TYPE_ADD_ATTACHMENT_TO_CARD))
        {
            return new AddAttachmentToCard(action);
        }        
        else if(action.getType().equalsIgnoreCase(TYPE_REMOVE_CHECKLIST_TO_CARD))
        {
            return new RemoveChecklistFromCard(action);
        }   
        else if(action.getType().equalsIgnoreCase(TYPE_CREATE_BOARD))
        {
            return new CreateBoard(action);
        }  
        else if(action.getType().equalsIgnoreCase(TYPE_CREATE_CARD))
        {
            return new CreateCard(action);
        } 
        else if(action.getType().equalsIgnoreCase(TYPE_DELETE_CARD))
        {
            return new DeleteCard(action);
        }         
        else if(action.getType().equalsIgnoreCase(TYPE_CREATE_LIST))
        {
            return new CreateList(action);
        } 
        else if(action.getType().equalsIgnoreCase(TYPE_COMMENT_CARD))
        {
            return new CommentCard(action);
        }    
        else if(action.getType().equalsIgnoreCase(TYPE_UPDATE_ITEM_STATE))
        {
            return new UpdateCheckListItemState(action);
        }          
        return null;        
    }
    
    public static final class CardUpdate extends AbstractTrelloAction implements TrelloCardAction
    {
        public CardUpdate(Action action) 
        {
            super(action);
        } 
        
        @Override
        public String getCardID()
        {
            return action.getData().getCard().getId();
        }
        
        public String getListID()
        {
            return action.getData().getList().getId();
        }       
        
        public String getDesc()
        {
            return action.getData().getCard().getDesc();
        }        
        
        public boolean isListChanged()
        {
            if(action.getData().getListBefore() != null && action.getData().getListAfter() != null)
            {
                if(action.getData().getListBefore().getId() != action.getData().getListAfter().getId())
                {
                    return true;
                }                
            }
            return false;
        }        
        
        public boolean isDescriptionChanged()
        {
            if(action.getData().getCard().getDesc() != null && action.getData().getOld().getDesc() != null)
            {
                if(!action.getData().getCard().getDesc().equals(action.getData().getOld().getDesc()))
                {
                    return true;
                }                
            }
            return false;
        }
                
        @Override
        public String toString()
        {
            return "Update card " + action.getData().getCard().getName().toUpperCase();            
        }
    }
    
    private static final class AddChecklistToCard extends AbstractTrelloAction
    {
        public AddChecklistToCard(Action action) 
        {
            super(action);
        }       
        
        @Override
        public String toString()
        {
            return "Add checklist to card " + action.getData().getCard().getName().toUpperCase();
        }
    }   
    
    private static final class AddAttachmentToCard extends AbstractTrelloAction
    {
        public AddAttachmentToCard(Action action) 
        {
            super(action);
        }       
        
        @Override
        public String toString()
        {
            return "Add attachment to card " + action.getData().getCard().getName().toUpperCase();
        }
    }     
    
    private static final class RemoveChecklistFromCard extends AbstractTrelloAction
    {
        public RemoveChecklistFromCard(Action action) 
        {
            super(action);
        }         
        
        @Override
        public String toString()
        {
            return "Remove checklist from card " + action.getData().getCard().getName().toUpperCase();
        }
    } 

    private static final class CreateBoard extends AbstractTrelloAction
    {
        public CreateBoard(Action action) 
        {
            super(action);
        } 
                
        @Override
        public String toString()
        {
            return "Create board";
        }
    } 
    
    public static final class CreateCard extends AbstractTrelloAction implements TrelloCardAction
    {
        public CreateCard(Action action) 
        {
            super(action);
        } 

        @Override
        public String getCardID()
        {
            return action.getData().getCard().getId();
        }
        
        public String getListID()
        {
            return action.getData().getList().getId();
        }        
        
        @Override
        public String toString()
        {
            return "Create card " + action.getData().getCard().getName().toUpperCase();
        }
    }  
    
    public static final class DeleteCard extends AbstractTrelloAction implements TrelloCardAction
    {
        public DeleteCard(Action action) 
        {
            super(action);
        } 

        @Override
        public String getCardID()
        {
            return action.getData().getCard().getId();
        }
        
        public String getListID()
        {
            return action.getData().getList().getId();
        }        
        
        @Override
        public String toString()
        {
            return "Delete card (" + action.getData().getCard().getId() + ")";
        }
    }      
    
    private static final class CreateList extends AbstractTrelloAction
    {
        public CreateList(Action action) 
        {
            super(action);
        } 
                
        @Override
        public String toString()
        {
            return "Create list " + action.getData().getList().getName().toUpperCase();
        }
    }  
    
    private static final class UpdateCheckListItemState extends AbstractTrelloAction
    {
        public UpdateCheckListItemState(Action action) 
        {
            super(action);
        } 
                
        @Override
        public String toString()
        {
            return "Update checklist item " + action.getData().getCheckItem().getName() + " as " + action.getData().getCheckItem().getState();
        }        
    }
    
    public static final class CommentCard extends AbstractTrelloAction
    {
        public CommentCard(Action action) 
        {
            super(action);
        } 
        
        public String getCardID()
        {
            return action.getData().getCard().getId();
        }
        
        public String getListID()
        {
            return action.getData().getList().getId();
        }         
        
        public String getText() 
        {
            return action.getData().getText();
        }  
             
        @Override
        public String toString()
        {
            return "Comment card " + action.getData().getCard().getName().toUpperCase();
        }
    }      
}
