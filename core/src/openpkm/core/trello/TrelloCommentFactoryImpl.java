/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import java.awt.Image;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Logger;
import openpkm.base.DisplayNameProvider;
import openpkm.base.IconProvider;
import openpkm.base.RemoteDataProvider;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloComment;
import openpkm.trello.TrelloService;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import openpkm.trello.TrelloCommentFactory;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloCommentFactory.class)
public class TrelloCommentFactoryImpl implements TrelloCommentFactory
{
    @Override
    public TrelloComment getComment(TrelloAction action, Trello trello, TrelloAccount account) 
    {
        if(action instanceof AbstractTrelloAction.CommentCard comment)
        {
            return new TrelloCommentImpl(comment, trello, account);
        }
        return null;
    }  
    
    private static final class TrelloCommentImpl implements TrelloComment, RemoteDataProvider, DisplayNameProvider, IconProvider
    {
        private static final Logger LOG = Logger.getLogger(TrelloCommentImpl.class.getName());        
        
        private final PropertyChangeSupport propertyChangeSupport; 
        private final AbstractTrelloAction.CommentCard comment;
        private final Trello trello;
        private final TrelloAccount account;

        private Lookup lkp;    
        private SourceState state;        
        
        public TrelloCommentImpl(AbstractTrelloAction.CommentCard comment, Trello trello, TrelloAccount account) 
        {
            this.comment = comment;
            this.trello = trello;
            this.account = account;
            propertyChangeSupport = new PropertyChangeSupport(this);                       
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
        public SourceState getState()
        {
            return state;
        }

        @Override
        public void markModified()
        {
            SourceState oldValue = getState();
            state = SourceState.MODIFIED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }   

        @Override
        public void notifyDeleted()
        {
            SourceState oldValue = getState();
            state = SourceState.DELETED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        } 
        
        @Override
        public String getAppID()
        {
            return comment.getProperties().getProperty(PROP_APP_ID);
        }         
        
        @Override
        public LocalDateTime getTimeCreated() 
        {
            String created = comment.getProperties().getProperty(PROP_TIME_CREATED);
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
        
        @Override
        public String getActionID()
        {
            return comment.getActionID();
        }        
        
        @Override
        public String getCardID()
        {
            return comment.getCardID();
        }
        
        @Override
        public String getText()
        {
            return comment.getCommentText();
        }
        
        @Override
        public LocalDateTime getDate() 
        {
            return comment.getActionDate();
        }        
        
        @Override
        public void save(OutputStream os, String comments) throws IOException
        {
            comment.getProperties().store(os, comments); 
            LOG.info("Trello comment saved");
        } 
      
// RemoteDataProvider        
        
        @Override
        public String pull() 
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            return service.getCommentText(comment.getActionID(), trello);
        }

        @Override
        public void push(String data) 
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            service.setCommentText(getCardID(), getActionID(), data, trello);
        }  
        
        @Override
        public void delete() 
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            int status = service.deleteComment(getCardID(), getActionID(), account);            
            LOG.info("Delete Comment status: " + status);
        }        

        @Override
        public String getDisplayName(TextFormat format)
        {
            if(format == TextFormat.PLAIN)
            {
                return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM).format(comment.getActionDate());
            }
            return null;
        }
        
        @Override
        public Image getIcon(int type) 
        {
            return comment.getIcon(type);
        }
    }      
}
