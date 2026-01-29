/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Rok Koren
 */
public class RaindropService 
{
    private static final Logger LOG = Logger.getLogger(RaindropService.class.getName());     
    
    private static RaindropService service;
    
    private final ChangeSupport cs; 
    private final Accounts accounts;

    public RaindropService() 
    {
        accounts = new Accounts();
        cs = new ChangeSupport(this); 
    }        
    
    public void addAccount(RaindropAccount account)
    {
        accounts.getAccounts().put(account.getUser().getUserID(), account);
        cs.fireChange();
    }
    
    public void removeAccount(RaindropAccount account)
    {
        accounts.getAccounts().remove(account.getUser().getUserID());
        cs.fireChange();
    }
    
    public RaindropAccount getAccount(int userID) 
    {
        return accounts.getAccounts().get(userID);
    }  
    
    public Collection<RaindropAccount> getAccounts()
    {       
        return accounts.getAccounts().values();
    }
    
    public void store(RaindropAccount account) 
    {
        // Store the account
        Preferences preferences =  account.getPreferences();                
        preferences.put(RaindropAccount.PROP_TITLE, account.getTitle());
        preferences.put(RaindropAccount.PROP_TOKEN, account.getToken());
    }

    public void addChangeListener(ChangeListener listener) 
    {
        cs.addChangeListener(listener);
    }  
    
    public void removeChangeListener(ChangeListener listener) 
    {
        cs.addChangeListener(listener);
    }    
    
    private static final class Accounts
    {
        private Map<Integer, RaindropAccount> accounts;  
        
        public synchronized Map<Integer, RaindropAccount> getAccounts()
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
        
        private void load() throws BackingStoreException
        {
            // Load the accounts list        
            String[] names = RaindropAccount.PREFERENCES.childrenNames();
            for (String name : names)
            {
                Preferences preferences = RaindropAccount.PREFERENCES.node(name);
                RaindropAccount account = getAccount(preferences);
                if(account != null)
                {
                    accounts.put(account.getUser().getUserID(), account);                
                }
            }
        } 

        private RaindropAccount getAccount(Preferences preferences)
        {
            String title = preferences.get(RaindropAccount.PROP_TITLE, "");
            String token = preferences.get(RaindropAccount.PROP_TOKEN, "");                        
            RaindropUser user = RaindropUtils.getUser(token, title);
            if(user != null)
            {
                RaindropAccount account = new RaindropAccount(token, user);
                account.setTitle(title);
                return account;            
            }
            return null;
        }        
    }    
    
    public static synchronized RaindropService getDefault()
    {
        if(service == null)
        {
            service = new RaindropService();
        }
        return service;
    }    
}
