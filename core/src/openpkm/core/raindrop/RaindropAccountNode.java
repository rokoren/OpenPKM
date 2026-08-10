/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.raindrop;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import openpkm.raindrop.RaindropAccount;
import openpkm.raindrop.RaindropCollection;
import openpkm.raindrop.RaindropService;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class RaindropAccountNode extends AbstractNode
{
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/account.png"; 
    
    private static final Logger LOG = Logger.getLogger(RaindropAccountNode.class.getName());      
    
    private final RaindropAccount account;
    
    public RaindropAccountNode(RaindropAccount account) 
    {
        super(new RaindropAccountChildren(account), Lookups.fixed(account));
        setName(account.getUser().getUserID() + "");
        setDisplayName(account.getTitle());
        int usedSpace = account.getUser().getFilesUsed() / 1000000;
        int totalSpace = account.getUser().getFilesSize() / 1000000;        
        setShortDescription("Used space: " + usedSpace + "MB of " + totalSpace + "MB");
        setIconBaseWithExtension(ICON);
        this.account = account;
    }   
    
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new RemoveRaindrop(account)
        };
    }     
    
    static final class RaindropAccountChildren extends Children.Keys<RaindropCollection>
    {  
        private RaindropAccount account;
        
        public RaindropAccountChildren(RaindropAccount account)
        {
            this.account = account;
        }  

        @Override
        protected void addNotify() {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            EventQueue.invokeLater(new Runnable() 
            {
                @Override
                public void run() 
                { 
                    Collection<RaindropCollection> collections = account.getRootCollections();
                    SortedSet<RaindropCollection> sorted = new TreeSet<RaindropCollection>(RaindropCollection.titleComparator());
                    sorted.addAll(collections);                    
                    setKeys(sorted);                      
                }
            });
        }        

        protected @Override void removeNotify() 
        {                      
            setKeys(Collections.<RaindropCollection>emptySet());
        }

        @Override
        protected Node[] createNodes(RaindropCollection collection) 
        {
            return new Node[] {new RaindropCollectionNode(account, collection)};
        }        
    } 
    
    private static final class RemoveRaindrop extends AbstractAction
    {
        private final RaindropAccount account;

        public RemoveRaindrop(RaindropAccount account) 
        {
            super("Remove Account");
            this.account = account;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            NotifyDescriptor d = new NotifyDescriptor(
                    "Do you want to remove account?", // message
                    account.getTitle(), // title
                    NotifyDescriptor.YES_NO_OPTION, // option type
                    NotifyDescriptor.QUESTION_MESSAGE, // message type
                    null, // custom buttons (as Object[])
                    null); // default value
            if(DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION)
            {
                try
                {
                    account.getPreferences().removeNode(); 
                    account.getPreferences().flush();
                    RaindropService.getDefault().removeAccount(account);
                }
                catch(BackingStoreException e)
                {
                    LOG.warning(e.getMessage());
                }
            }
        }
    }     
}
