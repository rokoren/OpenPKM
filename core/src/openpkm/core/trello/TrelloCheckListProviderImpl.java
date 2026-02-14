/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.CheckList;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import openpkm.base.NodePositionProvider;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TitleProvider;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListItem;
import openpkm.trello.TrelloCheckListItemProvider;
import openpkm.trello.TrelloCheckListProvider;
import openpkm.trello.TrelloCheckListsProvider;
import openpkm.trello.TrelloService;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class TrelloCheckListProviderImpl implements TrelloCheckListProvider
{    
    private static final Logger LOG = Logger.getLogger(TrelloCheckListProvider.class.getName());    
    
    private final TrelloCheckListsProvider provider; 

    public TrelloCheckListProviderImpl(TrelloCheckListsProvider provider) 
    {
        this.provider = provider;
    }    
    
    @Override
    public TrelloCheckList getCheckList(Properties props) 
    {
        return new TrelloCheckListImpl(props, provider);
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
    
    private static final class TrelloCheckListImpl implements TrelloCheckList, NodePositionProvider
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/check_box_list.png";  
        
        private final Properties props; 
        private final TrelloCheckListsProvider provider; 
        
        private Map<String, TrelloCheckListItem> items;
        private ChangeSupport changeSupport;
        
        public TrelloCheckListImpl(Properties props, TrelloCheckListsProvider provider)
        {
            this.props = props; 
            this.provider = provider;
        }         
        
        private synchronized Map<String, TrelloCheckListItem> getItemsById()
        {
            if(items == null)
            {
                items = new HashMap();
                TrelloCheckListItemProvider provider = Lookup.getDefault().lookup(TrelloCheckListItemProvider.class);
                if(provider != null)
                {
                    String string = props.getProperty(PROP_CHECKLIST_ITEMS);
                    if(string != null)
                    {
                        JSONArray jsons = new JSONArray(string);
                        for(int i=0; i<jsons.length(); i++)
                        {
                            JSONObject json = jsons.getJSONObject(i);
                            TrelloCheckListItem item = provider.getCheckListItem(json);
                            items.put(item.getCheckListItemID(), item);
                        }                    
                    }                    
                }
            }
            return items;
        } 
        
        private ChangeSupport getChangeSupport()
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
        
        @Override
        public void addItem(JSONObject json)
        {
            TrelloCheckListItemProvider provider = Lookup.getDefault().lookup(TrelloCheckListItemProvider.class);
            if(provider != null)
            {
                TrelloCheckListItem item = provider.getCheckListItem(json);
                items.put(item.getCheckListItemID(), item); 
                getChangeSupport().fireChange();
            }                                    
        }
        
        @Override
        public void removeItem(String itemID)
        {
            getItemsById().remove(itemID);
            getChangeSupport().fireChange();
        }        
        
        @Override
        public void addChangeListener(ChangeListener listener)
        {
            getChangeSupport().addChangeListener(listener);
        }
        
        @Override
        public void removeChangeListener(ChangeListener listener)
        {
            getChangeSupport().removeChangeListener(listener);
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
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.add(new AddCheckListItem(this, provider));         
            return actions;
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
            checkList.addChangeListener(this);
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
            checkList.removeChangeListener(this);
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
            actions.addAll(provider.getActions());
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
    
    private static final class AddCheckListItem extends AbstractAction
    {          
        private final TrelloCheckList checkList;  
        private final TrelloCheckListsProvider provider; 

        public AddCheckListItem(TrelloCheckList checkList, TrelloCheckListsProvider provider) 
        {
            super("Add Checklist Item");
            this.checkList = checkList;
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            NotifyDescriptor d = new NotifyDescriptor.InputLine("Name:", "Add Checklist Item");
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.OK_OPTION) 
            {
                String name = ((NotifyDescriptor.InputLine) d).getInputText();
                TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
                JSONObject json = service.createCheckListIem(checkList.getCheckListID(), name, provider.getAccount());
                
                String string = checkList.getProperties().getProperty(PROP_CHECKLIST_ITEMS);
                if(string != null)
                {
                    JSONArray jsons = new JSONArray(string);
                    jsons.put(json);
                    checkList.getProperties().setProperty(PROP_CHECKLIST_ITEMS, jsons.toString());
                }                  
                
                FileObject file = provider.getRootFolder().getFileObject(checkList.getCheckListID(), PropertiesProvider.EXTENSION);
                if(file != null)
                {
                    try
                    {
                        OutputStream os = file.getOutputStream();
                        TitleProvider titleProvider = provider.getProvider().getLookup().lookup(TitleProvider.class);
                        checkList.getProperties().store(os, "Updated by Trello project: " + titleProvider.getTitle()); 
                        os.close();
                        LOG.info("Trello checklist saved: " + checkList.getCheckListID()); 
                        checkList.addItem(json);
                    }  
                    catch(FileAlreadyLockedException e)
                    {
                        LOG.warning(e.getMessage());
                    }                             
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }                                                             
                } 
            }
        }
    }     
}
