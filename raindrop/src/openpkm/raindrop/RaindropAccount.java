/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Rok Koren
 */
public class RaindropAccount 
{
    public static final String PROP_TITLE = "title";     
    public static final String PROP_TOKEN = "token";     
    
    public static final String ACCOUNT_TYPE_FREE = "Free";
    public static final String ACCOUNT_TYPE_PRO  = "PRO";        
    
    public static final Preferences PREFERENCES = NbPreferences.forModule(RaindropAccount.class);          
    
    private final String token;    
    private final RaindropUser user;
    
    private String title;
    
    private RootCollections rootCollections;
    private ChildrenCollections childrenCollections;       
    
    public RaindropAccount(String token, RaindropUser user) 
    {
        this.token = token;
        this.user = user;
        rootCollections = new RootCollections(this);
        childrenCollections = new ChildrenCollections(this);
    }  
    
    public Collection<RaindropCollection> getRootCollections()
    {
        return rootCollections.getCollections().values();
    }
    
    public RaindropChildrenCollection getChildrenCollection(int id) throws MalformedURLException, ProtocolException, IOException
    {               
        return childrenCollections.getCollections().get(id);
    }      
    
    public List<RaindropChildrenCollection> getChildrenCollections(int parentID) throws MalformedURLException, ProtocolException, IOException
    { 
        List<RaindropChildrenCollection> list = new ArrayList<>();
        for (RaindropChildrenCollection collection : childrenCollections.getCollections().values())
        {
            if(collection.getParentID() == parentID)
            {
                list.add(collection);
            }
        }
        return list;
    }
    
    public RaindropCollection getRootCollection(int id) throws MalformedURLException, ProtocolException, IOException
    {             
        return rootCollections.getCollections().get(id);
    }  
    
    public RaindropCollection getCollection(int id) throws MalformedURLException, ProtocolException, IOException
    {
        if(rootCollections.getCollections().containsKey(id))
        {
            return rootCollections.getCollections().get(id);
        } 
        if(childrenCollections.getCollections().containsKey(id))
        {
            return childrenCollections.getCollections().get(id);
        }
        return null;
    }      
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public RaindropUser getUser()
    {
        return user;
    }

    public Preferences getPreferences() 
    {
        return PREFERENCES.node(user.getUserID() + "");
    }
    
    public static String getAccountType(boolean pro) 
    {
        if (pro)
        {
            return ACCOUNT_TYPE_PRO;
        }
        return ACCOUNT_TYPE_FREE;
    }   
    
    private static final class RootCollections
    {
        private final RaindropAccount account;
                
        private Map<Integer, RaindropCollection> collections; 

        public RootCollections(RaindropAccount account) 
        {
            this.account = account;
        }                
        
        public Map<Integer, RaindropCollection> getCollections()
        {
            if(collections == null)
            {
                collections = new HashMap<>();
                List<RaindropCollection> list = RaindropUtils.getRootCollections(account);
                for (RaindropCollection collection : list)
                {
                    collections.put(collection.getCollectionID(), collection);
                } 
            }  
            return collections;
        }        
    }   
    
    private static final class ChildrenCollections
    {
        private final RaindropAccount account;
                
        private Map<Integer, RaindropChildrenCollection> collections;

        public ChildrenCollections(RaindropAccount account) 
        {
            this.account = account;
        }                
        
        public Map<Integer, RaindropChildrenCollection> getCollections()
        {
            if(collections == null)
            {
                collections = new HashMap<>();
                for (RaindropChildrenCollection collection : RaindropUtils.getChildrenCollections(account))
                {
                    collections.put(collection.getCollectionID(), collection);
                }
            }  
            return collections;
        }        
    }
    
    public static Comparator<RaindropAccount> titleComparator() 
    {
        return new Comparator<RaindropAccount>() 
        {
            @Override
            public int compare(RaindropAccount account1, RaindropAccount account2) 
            {
                String title1 = account1.getTitle();
                String title2 = account2.getTitle();
                return title1.compareTo(title2);
            }
        };
    }     
}
