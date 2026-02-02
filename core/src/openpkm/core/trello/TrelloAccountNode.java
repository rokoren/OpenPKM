/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloBoard;
import openpkm.trello.TrelloService;
import openpkm.utils.RoundRectIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class TrelloAccountNode extends AbstractNode
{
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/account.png";      
    
    private final TrelloAccount account;
    
    public TrelloAccountNode(TrelloAccount account) 
    {
        super(new TrelloAccountChildren(account), Lookups.fixed(account));
        setName(account.getApiKey());
        setDisplayName(account.getTitle());
        setShortDescription(account.getUsername());
        setIconBaseWithExtension(ICON);
        this.account = account;
    }  
    
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new RemoveTrello(account)
        };
    }  
    
    static final class TrelloAccountChildren extends Children.Keys<TrelloBoard>
    {  
        private TrelloAccount account;
        
        public TrelloAccountChildren(TrelloAccount account)
        {
            this.account = account;
        }  

        protected @Override void addNotify() {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            EventQueue.invokeLater(new Runnable() 
            {
                @Override
                public void run() 
                { 
                    Collection<TrelloBoard> boards = account.getBoards();
                    SortedSet<TrelloBoard> sorted = new TreeSet<TrelloBoard>(TrelloBoard.titleComparator());
                    sorted.addAll(boards);           
                    setKeys(sorted);                      
                }
            });
        }        

        protected @Override void removeNotify() 
        { 
            setKeys(Collections.<TrelloBoard>emptySet());
        }

        @Override
        protected Node[] createNodes(TrelloBoard board) 
        {
            return new Node[] {new TrelloBoardNode(board)};
        }        
    } 
    
    private static final class RemoveTrello extends AbstractAction
    {
        private final TrelloAccount account;

        public RemoveTrello(TrelloAccount account) 
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
                    TrelloService.getDefault().removeAccount(account);
                }
                catch(BackingStoreException e)
                {
                    Exceptions.printStackTrace(e);
                }
            }
        }
    }     

    static final class TrelloBoardNode extends AbstractNode
    {
        private final TrelloBoard board;
        
        public TrelloBoardNode(TrelloBoard board) 
        {
            super(Children.LEAF);
            setName(board.getBoardID());
            setDisplayName(board.getTitle());
            setShortDescription(board.getDescription());
            this.board = board;
        } 
        
        @Override
        public Image getIcon(int type) 
        {
            Icon icon = new RoundRectIcon(16, 16, board.getBackground());
            return ImageUtilities.icon2Image(icon);
        }        
    }     
}
