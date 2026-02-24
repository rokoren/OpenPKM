/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.CheckList;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import openpkm.base.ActionsProvider;
import openpkm.base.NodePositionProvider;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListItem;
import openpkm.trello.TrelloCheckListItemProvider;
import openpkm.trello.TrelloCheckListProvider;
import openpkm.trello.TrelloCheckListsProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class TrelloCheckListProviderImpl implements TrelloCheckListProvider
{    
    private static final Logger LOG = Logger.getLogger(TrelloCheckListProvider.class.getName()); 
    
    private final TrelloCheckListsProvider checkListsProvider;

    public TrelloCheckListProviderImpl(TrelloCheckListsProvider checkListsProvider) 
    {
        this.checkListsProvider = checkListsProvider;
    }       
    
    @Override
    public TrelloCheckList getCheckList(Properties props) 
    {
        return new TrelloCheckListImpl(props, checkListsProvider);
    }
    
    @Override
    public TrelloCheckList createCheckList(CheckList checkList) 
    {
        Properties props = new Properties(); 
        props.setProperty(PROP_BOARD_ID, checkList.getIdBoard());        
        props.setProperty(PROP_CARD_ID, checkList.getIdCard());        
        props.setProperty(PROP_CHECKLIST_ID, checkList.getId());
        props.setProperty(PROP_CHECKLIST_NAME, checkList.getName());          
        props.setProperty(PROP_CHECKLIST_POSITION, checkList.getPos() + ""); 
        return getCheckList(props);
    } 
    
    private static final class TrelloCheckListImpl implements TrelloCheckList
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/check_box_list.png";  
        
        private final TrelloCheckListItemProvider checkListItemProvider;        
        private final Properties props; 
        
        private ChangeSupport changeSupport;
        
        public TrelloCheckListImpl(Properties props, TrelloCheckListsProvider checkListsProvider)
        {
            this.props = props; 
            checkListItemProvider = new TrelloCheckListItemProviderImpl(checkListsProvider, this);
        }         
        
        private synchronized Map<String, TrelloCheckListItem> getItemsById()
        {
            Map<String, TrelloCheckListItem> items = new HashMap();
            String string = props.getProperty(PROP_CHECKLIST_ITEMS);
            if(string != null)
            {
                JSONArray jsons = new JSONArray(string);
                for(int i=0; i<jsons.length(); i++)
                {
                    JSONObject json = jsons.getJSONObject(i);
                    TrelloCheckListItem item = checkListItemProvider.getCheckListItem(json);
                    items.put(item.getCheckListItemID(), item);
                }                    
            } 
            return items;
        } 
        
        @Override
        public ChangeSupport getChangeSupport()
        {
            if(changeSupport == null)
            {
                changeSupport = new ChangeSupport(this);
            }
            return changeSupport;
        }

// TODO TrelloCheckList        

        @Override
        public String getBoardID() 
        {
            return props.getProperty(PROP_BOARD_ID);
        } 

        @Override
        public String getCardID() 
        {
            return props.getProperty(PROP_CARD_ID);
        }
        
        @Override
        public String getCheckListID() 
        {
            return props.getProperty(PROP_CHECKLIST_ID);
        } 
        
        @Override
        public String getCheckListName() 
        {
            return props.getProperty(PROP_CHECKLIST_NAME);
        }         
                
        @Override
        public Integer getCheckListPosition() 
        {
            String string = props.getProperty(PROP_CHECKLIST_POSITION);
            if(string != null)
            {
                try
                {
                    return Integer.parseInt(string);
                }
                catch(NumberFormatException e)
                {
                    LOG.warning(e.getMessage());
                }
            }
            return null;
        } 
        
        @Override
        public Collection<TrelloCheckListItem> getItems()
        {
            return Collections.unmodifiableCollection(getItemsById().values());
        }              
        
// TODO PropertiesProvider        
        
        @Override
        public Properties getProperties()
        {
            return props;
        }   
        
        @Override
        public void merge(PropertiesProvider provider)
        {
            props.putAll(provider.getProperties());
        }        

// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getCheckListID();
        }
        
        @Override
        public String getDisplayName() 
        {
            return getCheckListName();
        }
        
        @Override
        public Image getIcon(boolean opened) 
        {
            return ImageUtilities.loadImage(ICON);
        }  
        
        @Override
        public Children getChildren() 
        {
            return new ChildrenImpl(this);
        }        

        @Override
        public int getPosition() 
        {
            Integer position = getCheckListPosition();
            if(position != null)
            {
                return position.intValue();
            }
            return -1;
        }
    }  
    
    static final class ChildrenImpl extends Children.Keys<NodePositionProvider> implements ChangeListener
    {
        private final TrelloCheckList checkList;

        public ChildrenImpl(TrelloCheckList checkList)
        {
            this.checkList = checkList;  
            checkList.getChangeSupport().addChangeListener(this);
        }  

        protected @Override void addNotify() 
        {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            SortedSet<NodePositionProvider> sorted = new TreeSet<NodePositionProvider>(NodePositionProvider.positionComparator());
            sorted.addAll(checkList.getItems());
            setKeys(sorted);             
            /*
            EventQueue.invokeLater(new Runnable() 
            {
                public void run()
                {                                                
                    SortedSet<NodePositionProvider> sorted = new TreeSet<NodePositionProvider>(NodePositionProvider.positionComparator());
                    sorted.addAll(checkList.getItems());
                    setKeys(sorted);  
                }
            });
            */
        }        

        @Override
        protected void removeNotify() 
        {
            checkList.getChangeSupport().removeChangeListener(this);
            setKeys(Collections.<NodePositionProvider>emptySet());
        }

        @Override
        protected Node[] createNodes(NodePositionProvider provider) 
        {
            return new Node[] {new ItemNode(provider)};
        }          

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            updateKeys();
        }
    }     

    private static final class ItemNode extends AbstractNode
    {
        private final NodeProvider provider;

        public ItemNode(NodeProvider provider) 
        {
            super(provider.getChildren(), Lookups.singleton(provider));
            setName(provider.getName());
            setDisplayName(provider.getDisplayName());
            this.provider = provider;
        } 
        
        @Override    
        public Action[] getActions(boolean context) 
        {
            List<Action> actions = new ArrayList();
            if(provider instanceof ActionsProvider)
            {
                ActionsProvider actionsProvider = (ActionsProvider)provider;
                actions.addAll(actionsProvider.getActions());                
            }
            return actions.toArray(new Action[actions.size()]);
        }

        @Override    
        public Image getIcon(int type)     
        {
            return provider.getIcon(false);
        }

        @Override
        public Image getOpenedIcon(int type) 
        {
            return provider.getIcon(true);
        }        
    }    
}
