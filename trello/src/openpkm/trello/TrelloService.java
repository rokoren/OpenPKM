/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.CheckItem;
import com.julienvey.trello.domain.CheckList;
import com.julienvey.trello.domain.Label;
import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import openpkm.utils.DateTimeUtils;
import org.openide.util.ChangeSupport;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Rok Koren
 */
public class TrelloService 
{
    private static final Logger LOG = Logger.getLogger(TrelloService.class.getName());     
    
    private static TrelloService service;    
    
    private final Accounts accounts = new Accounts();
    
    public void addListener(ChangeListener listener)
    {
        accounts.getChangeSupport().addChangeListener(listener);
    }
    
    public void removeListener(ChangeListener listener)
    {
        accounts.getChangeSupport().removeChangeListener(listener);
    }    
    
    public void addAccount(TrelloAccount account) 
    {
        accounts.getAccounts().put(account.getUsername(), account);
        accounts.getChangeSupport().fireChange();
    }

    public void removeAccount(TrelloAccount account) 
    {
        accounts.getAccounts().remove(account.getUsername());
        accounts.getChangeSupport().fireChange();
    }

    public TrelloAccount getAccount(String username) 
    {
        return accounts.getAccounts().get(username);
    }

    public Collection<TrelloAccount> getAccounts() 
    {
        return accounts.getAccounts().values();
    }

    public void store(TrelloAccount account) 
    {
        // Store the account
        Preferences preferences =  account.getPreferences();                
        preferences.put(TrelloAccount.PROPS_USERNAME, account.getUsername());
        preferences.put(TrelloAccount.PROPS_API_KEY, account.getApiKey());
        preferences.put(TrelloAccount.PROPS_TITLE, account.getTitle());
        preferences.put(TrelloAccount.PROPS_ACCESS_TOKEN, account.getAccessToken());
    }
    
    private static final class Accounts
    {
        private Map<String, TrelloAccount> accounts; 
        private ChangeSupport changeSupport;
        
        public synchronized Map<String, TrelloAccount> getAccounts()
        {
            if(accounts == null)
            {
                accounts = new HashMap<>();  
                try
                {
                    load();                    
                }
                catch(BackingStoreException e)
                {
                    LOG.warning(e.getMessage());
                }
            }  
            return accounts;
        }
        
        public ChangeSupport getChangeSupport() 
        {
            if(changeSupport == null)
            {
                changeSupport = new ChangeSupport(this);            
            }
            return changeSupport;
        }        
        
        private void load() throws BackingStoreException
        {
            // Load the accounts list        
            String[] names = TrelloAccount.PREFERENCES.childrenNames();
            for (String name : names)
            {
                Preferences preferences = TrelloAccount.PREFERENCES.node(name);
                TrelloAccount account = getAccount(preferences);
                if(account != null)
                {
                    accounts.put(account.getUsername(), account);                
                }
            }
        } 
        
        private TrelloAccount getAccount(Preferences preferences)
        {
            String username = preferences.get(TrelloAccount.PROPS_USERNAME, null);
            if(username != null)
            {
                String title = preferences.get(TrelloAccount.PROPS_TITLE, "");
                String apiKey = preferences.get(TrelloAccount.PROPS_API_KEY, "");
                String accessToken = preferences.get(TrelloAccount.PROPS_ACCESS_TOKEN, ""); 
                TrelloAccount account = new TrelloAccountImpl(username, apiKey, accessToken);
                account.setTitle(title);                      
                return account;            
            }
            return null;
        }                 
    } 

    public static final class TrelloAccountImpl implements TrelloAccount
    {
        private final String username;
        private final String apiKey;
        private final String accessToken;   

        private String title;     
        private Map<String, TrelloBoard> boards;   

        public TrelloAccountImpl(String username, String apiKey, String accessToken) 
        {
            this.username = username;
            this.apiKey = apiKey;
            this.accessToken = accessToken;  
        }   

        private synchronized Map<String, TrelloBoard> getMap()
        {
            if(boards == null)
            {
                boards = new HashMap<>();  
                List<TrelloBoard> list = TrelloService.getBoards(this);
                for (TrelloBoard board : list)
                {
                    boards.put(board.getBoardID(), board);
                    LOG.info("Board: " + board.getTitle() + ", Workspace ID: " + board.getWorkspaceID()); 
                }            
            }  
            return boards;
        }    

        @Override
        public Collection<TrelloBoard> getBoards()
        {
            return Collections.unmodifiableCollection(getMap().values());
        }  

        @Override
        public TrelloBoard getBoard(String boardID)
        {      
            return getMap().get(boardID);
        }

        @Override
        public String getUsername() 
        {
            return username;
        }

        @Override
        public String getTitle()
        {
            return title;
        }

        @Override
        public void setTitle(String title) 
        {
            this.title = title;
        }   

        @Override
        public String getApiKey() 
        {
            return apiKey;
        }

        @Override
        public String getAccessToken() 
        {
            return accessToken;
        }

        @Override
        public Preferences getPreferences() 
        {
            return PREFERENCES.node(username);
        }         
    }
    
    private static final class TrelloBoardImpl implements TrelloBoard
    {
        private static final Color DEFAULT_BACKGROUND = Color.MAGENTA;

        private static final RequestProcessor RP = new RequestProcessor(TrelloBoard.class);             

        private final TrelloAccount account;
        private final Board board;

        private Color background;           

        public TrelloBoardImpl(TrelloAccount account, Board board) 
        {
            this.account = account;
            this.board = board;       
        }

        @Override
        public RequestProcessor getRequestProcessor()
        {
            return RP;
        }      

        @Override
        public TrelloAccount getAccount() 
        {
            return account;
        }      

        @Override
        public String getBoardID() 
        {
            return board.getId();
        }

        @Override
        public String getUrl()
        {
            return board.getUrl();
        }

        @Override
        public String getTitle() 
        {
            return board.getName();
        }

        @Override
        public void setTitle(String title) 
        {
            board.setName(title);
        }

        @Override
        public String getDescription() 
        {
            return board.getDesc();
        }

        @Override
        public void setDescription(String desc) 
        {
            board.setDesc(desc);
        }

        @Override
        public String getWorkspaceID()
        {
            return board.getIdOrganization();
        }

        @Override
        public Color getBackground()
        {
            if(background == null)
            {
                return DEFAULT_BACKGROUND;
            }
            return background;
        }

        @Override
        public void setBackground(Color color) 
        {
            background = color;
        }    

        @Override
        public String toString()
        {
            return getTitle();
        }         
    } 
    
    private static final class TrelloCardImpl implements TrelloCard
    {
        private final Card card; 

        private boolean dueComplete;

        public TrelloCardImpl(Card card) 
        {
            this.card = card;
        }      

        @Override
        public String getBoardID() 
        {
            return card.getIdBoard();
        }    

        @Override
        public String getListID() 
        {
            return card.getIdList();
        }

        @Override
        public String getCardID() 
        {
            return card.getId();
        }

        @Override
        public String getTitle() 
        {
            return card.getName();
        }

        @Override
        public String getDescription()
        {
            return card.getDesc();
        }

        @Override
        public int getPosition() 
        {
            return card.getPos();
        }  

        @Override
        public boolean isClosed()
        {
            return card.isClosed();
        }

        @Override
        public boolean isSubscribed()
        {
            return card.isSubscribed();
        }

        @Override
        public boolean isDueComplete()
        {
            return dueComplete;
        }

        @Override
        public void setDueComplete(boolean complete)
        {
            dueComplete = complete;
        }

        @Override
        public LocalDate getDueDate()
        {
            Date date = card.getDue();
            if(date != null)
            {
                return DateTimeUtils.convertToLocalDate(date);
            }
            return null;
        }

        @Override
        public List<TrelloLabel> getLabels()
        {
            List<TrelloLabel> list = new ArrayList();
            for(Label label : card.getLabels())
            {
                TrelloLabel trelloLabel = new TrelloLabelImpl(label);
                list.add(trelloLabel);
            }
            return list;
        }

        @Override
        public void delete()
        {
            card.delete();
        }
    } 
    
    private static final class TrelloCheckListImpl implements TrelloCheckList
    {
        private final CheckList checkList;

        private Map<String, TrelloCheckListItem> items = new HashMap<>();;

        public TrelloCheckListImpl(CheckList checkList) 
        {
            this.checkList = checkList;
            for(CheckItem item : checkList.getCheckItems())
            {
                TrelloCheckListItem trelloCheckListItem = new TrelloCheckListItemImpl(this, item);            
                items.put(trelloCheckListItem.getCheckListItemID(), trelloCheckListItem);
            }        
        }

        @Override
        public String getCardID() 
        {
            return checkList.getIdCard();
        }

        @Override
        public String getCheckListID()
        {
            return checkList.getId();
        }   

        @Override
        public String getTitle() 
        {
            return checkList.getName();
        }

        @Override
        public int getPosition() 
        {
            return checkList.getPos();
        }

        @Override
        public Collection<TrelloCheckListItem> getItems() 
        {
            return items.values();
        }

        @Override
        public TrelloCheckListItem getItem(String itemID) 
        {
            return items.get(itemID);
        }

        @Override
        public TrelloCheckListItem addItem(String title, int position)
        {
            CheckItem item = new CheckItem();
            item.setId(System.currentTimeMillis() + "");
            item.setName(title);
            item.setState(TrelloCheckListItem.State.INCOMPLETE.toString().toLowerCase());
            item.setPos(position);
            CheckItem item1 = TrelloService.createCheckListItem(getCheckListID(), item);
            TrelloCheckListItem trelloItem = new TrelloCheckListItemImpl(this, item1);
            items.put(trelloItem.getCheckListItemID(), trelloItem);
            return trelloItem;
        }   
    } 
    
    private static final class TrelloCheckListItemImpl implements TrelloCheckListItem
    {
        private final TrelloCheckList checkList;
        private final CheckItem item;

        public TrelloCheckListItemImpl(TrelloCheckList checkList, CheckItem item) 
        {
            this.checkList = checkList;
            this.item = item;
        }

        @Override
        public TrelloCheckList getCheckList() 
        {
            return checkList;
        }

        @Override
        public String getCheckListItemID() 
        {
            return item.getId();
        }

        @Override
        public String getTitle() 
        {
            return item.getName();
        }

        @Override
        public int getPosition() 
        {
            return item.getPos();
        }  

        @Override
        public State getState() 
        {
            Optional<State> optional = State.get(item.getState());
            if(optional.isPresent())
            {
                return optional.get();
            }
            return null;
        }

        @Override
        public void setState(State state)
        {
            item.setState(state.toString().toLowerCase());
        }    
    } 
    
    private static final class TrelloAttachmentImpl implements TrelloAttachment
    {
        private final Attachment attachment;

        public TrelloAttachmentImpl(Attachment attachment) 
        {
            this.attachment = attachment;
        }

        @Override
        public String getAttachmentID() 
        {
            return attachment.getId();
        }   

        @Override
        public String getUrl() 
        {
            return attachment.getUrl();
        }

        @Override
        public String getName() 
        {
            return attachment.getName();
        }   
    }       
    
    private static CheckItem getCheckListItem(CheckList checkList, int position)
    {
        for(CheckItem item : checkList.getCheckItems())
        {
            if(item.getPos() == position)
            {
                return item;
            }
        }
        return null;
    } 
    
    public static final CheckList getCheckList(String boardID, String cardID, String name, int position)
    {
        CheckList checkList = new CheckList();
        checkList.setName(name);
        checkList.setIdCard(cardID);
        checkList.setPos(position);
        checkList.setIdBoard(boardID); 
        return checkList;
    }
    
    public static final CheckItem getCheckListItem(String name, int position)
    {
        CheckItem item = new CheckItem();
        item.setId(System.currentTimeMillis() + "");
        item.setName(name);
        item.setState(TrelloCheckListItem.State.INCOMPLETE.toString().toLowerCase());
        item.setPos(position);
        return item;
    }     
    
    public static synchronized TrelloService getDefault()
    {
        if(service == null)
        {
            service = new TrelloService();
        }
        return service;
    }       
}
