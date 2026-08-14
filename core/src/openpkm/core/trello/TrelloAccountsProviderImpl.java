/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAccountsProvider;
import openpkm.trello.TrelloBoard;
import openpkm.trello.TrelloInbox;
import openpkm.trello.TrelloService;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloAccountsProvider.class)
public class TrelloAccountsProviderImpl implements TrelloAccountsProvider
{
    private static final Logger LOG = Logger.getLogger(TrelloAccountsProvider.class.getName());       
    
    private final Accounts accounts = new Accounts();
    
    @Override
    public void addListener(ChangeListener listener)
    {
        accounts.getChangeSupport().addChangeListener(listener);
    }
    
    @Override
    public void removeListener(ChangeListener listener)
    {
        accounts.getChangeSupport().removeChangeListener(listener);
    }    
    
    @Override
    public void addAccount(TrelloAccount account) 
    {
        accounts.getAccounts().put(account.getUsername(), account);
        accounts.getChangeSupport().fireChange();
    }

    @Override
    public void removeAccount(TrelloAccount account) 
    {
        accounts.getAccounts().remove(account.getUsername());
        accounts.getChangeSupport().fireChange();
    }

    @Override
    public TrelloAccount getAccount(String username) 
    {
        return accounts.getAccounts().get(username);
    }

    @Override
    public Collection<TrelloAccount> getAccounts() 
    {
        return Collections.unmodifiableCollection(accounts.getAccounts().values());
    }

    @Override
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
        private TrelloInbox inbox;
        private Map<String, TrelloBoard> boards;   

        public TrelloAccountImpl(String username, String apiKey, String accessToken) 
        {
            this.username = username;
            this.apiKey = apiKey;
            this.accessToken = accessToken;
        } 
        
        @Override
        public TrelloInbox getInbox()
        {
            if(inbox == null)
            {
                TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
                if(service != null)
                {
                    inbox = service.getInbox(this);
                }                
            }
            return inbox;
        }

        private synchronized Map<String, TrelloBoard> getMap()
        {            
            if(boards == null)
            {
                boards = new HashMap<>();   
                TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
                if(service != null)
                {
                    Trello trello = new TrelloImpl(getApiKey(), getAccessToken(), new JDKTrelloHttpClient());                      
                    List<TrelloBoard> list = service.getBoards(this, trello);
                    for (TrelloBoard board : list)
                    {
                        boards.put(board.getBoardID(), board);
                        LOG.info("Board: " + board.getBoardName() + ", Workspace ID: " + board.getWorkspaceID()); 
                    }                     
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
}
