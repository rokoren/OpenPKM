/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.IconProvider;
import openpkm.base.RemoteDataProvider;
import openpkm.base.TitleProvider;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloComment;
import openpkm.trello.TrelloCommentProvider;
import openpkm.trello.TrelloService;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloCommentProvider.class)
public class TrelloCommentProviderImpl implements TrelloCommentProvider
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
    
    private static final class TrelloCommentImpl implements TrelloComment, RemoteDataProvider, TitleProvider, IconProvider
    {
        private static final Logger LOG = Logger.getLogger(TrelloCommentImpl.class.getName());        
        
        private final PropertyChangeSupport propertyChangeSupport; 
        private final ChangeSupport changeSupport;  
        private final AbstractTrelloAction.CommentCard comment;
        private final Trello trello;
        private final TrelloAccount account;

        private Lookup lkp;                 
        
        public TrelloCommentImpl(AbstractTrelloAction.CommentCard comment, Trello trello, TrelloAccount account) 
        {
            this.comment = comment;
            this.trello = trello;
            this.account = account;
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
            String string = comment.getProperties().getProperty(PROP_DELETED);
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
            comment.getProperties().setProperty(PROP_DELETED, Boolean.toString(isDeleted));
            propertyChangeSupport.firePropertyChange(PROP_DELETED, oldValue, isDeleted);
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
        public String getTitle() 
        {
            return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM).format(comment.getActionDate());
        }

        @Override
        public void setTitle(String title) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Image getIcon() 
        {
            return comment.getIcon(false);
        }
    }      
}
