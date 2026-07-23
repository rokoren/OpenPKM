/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.CheckList;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.DisplayNameProvider.TextFormat;
import openpkm.base.IconProvider;
import openpkm.base.NodePositionProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.StateSupport.State;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListItem;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import openpkm.trello.TrelloCheckListFactory;
import openpkm.trello.TrelloCheckListItemProvider;
import openpkm.trello.TrelloCheckListProvider;

/**
 *
 * @author Rok Koren
 */
public class TrelloCheckListFactorympl implements TrelloCheckListFactory
{    
    private static final Logger LOG = Logger.getLogger(TrelloCheckListFactory.class.getName());  
    
    private final TrelloCheckListProvider provider;

    public TrelloCheckListFactorympl(TrelloCheckListProvider provider) {
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
    
    private static final class TrelloCheckListImpl implements TrelloCheckList, IconProvider
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/check_box_list.png";  
        
        private final TrelloCheckListItemProvider itemProvider;        
        private final Properties props; 
        private final PropertyChangeSupport propertyChangeSupport;           
        
        private Lookup lkp; 
        private State state; 
        private ChangeSupport changeSupport;
        
        public TrelloCheckListImpl(Properties props, TrelloCheckListProvider provider)
        {
            this.props = props; 
            propertyChangeSupport = new PropertyChangeSupport(this);
            itemProvider = new TrelloCheckListItemProviderImpl(provider, this);
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
                    TrelloCheckListItem item = itemProvider.getCheckListItem(json);
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
        public void setCheckListName(String name)
        {
            if(name == null)
            {
                Object oldValue = props.remove(PROP_CHECKLIST_NAME);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_CHECKLIST_NAME, oldValue, name);
            }
            else
            {
                Object oldValue = props.setProperty(PROP_CHECKLIST_NAME, name);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_CHECKLIST_NAME, oldValue, name);                
            }            
        }
        
        public void addCheckListNameListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_CHECKLIST_NAME, listener);
        }
        
        public void removeCheckListNameListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.removePropertyChangeListener(PROP_CHECKLIST_NAME, listener);
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
        public boolean merge(PropertiesProvider provider)
        {
            if(props.equals(provider.getProperties()))       
            {
                return false;
            }
            props.putAll(provider.getProperties());        
            return true;
        }  
        
        @Override
        public boolean isModified() 
        {
            return state == State.MODIFIED;
        }

        @Override
        public void markModified()
        {
            State oldValue = state;
            state = State.MODIFIED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }   

        @Override
        public boolean isDeleted() 
        {
            return state == State.DELETED;
        }    

        @Override
        public void notifyDeleted()
        {
            State oldValue = state;
            state = State.DELETED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }         

// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getCheckListID();
        }
        
        @Override
        public Image getIcon(int type) 
        {
            return ImageUtilities.loadImage(ICON);
        }  
        
        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            {
                lkp = Lookups.fixed(this, new DisplayNameProviderImpl(this));              
            }
            return lkp;
        }         
        
        @Override
        public Children getChildren() 
        {
            return new ChildrenImpl(this);
        }  
        
        @Override
        public HelpCtx getHelp()
        {
            return HelpCtx.DEFAULT_HELP;
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
    
    private static final class DisplayNameProviderImpl implements DisplayNameProvider, ChangeSupportProvider, PropertyChangeListener
    {
        private final TrelloCheckListImpl checkList;
        private final ChangeSupport changeSupport;

        public DisplayNameProviderImpl(TrelloCheckListImpl checkList) 
        {
            this.checkList = checkList;
            checkList.addCheckListNameListener(this);
            changeSupport = new ChangeSupport(this);
        }                
        
        @Override
        public String getDisplayName(TextFormat format) 
        {
            if(format == TextFormat.PLAIN)
            {
                return checkList.getCheckListName();
            }
            return null;        
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            changeSupport.fireChange();
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
    }     
    
    static final class ChildrenImpl extends Children.Keys<TrelloCheckListItem> implements ChangeListener
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
            SortedSet<TrelloCheckListItem> sorted = new TreeSet<TrelloCheckListItem>(NodePositionProvider.positionComparator());
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
            setKeys(Collections.<TrelloCheckListItem>emptySet());
        }

        @Override
        protected Node[] createNodes(TrelloCheckListItem item) 
        {
            return new Node[] {new ItemNode(item)};
        }          

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            updateKeys();
        }
    }     

    private static final class ItemNode extends AbstractNode implements ChangeListener
    {
        private DisplayNameProvider displayNameProvider;
        private IconProvider iconProvider;
        
        private final TrelloCheckListItem item;

        public ItemNode(TrelloCheckListItem item) 
        {
            super(item.getChildren(), item.getLookup());
            setName(item.getName());
            this.item = item;
        } 
        
        @Override
        public Action getPreferredAction()
        {
            return item.getPreferredAction();
        }
        
        @Override    
        public Action[] getActions(boolean context) 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(item.getActions());
            return actions.toArray(new Action[actions.size()]);
        }
        
        @Override
        public String getDisplayName() 
        {        
            if(displayNameProvider == null)
            {
                displayNameProvider = getLookup().lookup(DisplayNameProvider.class);
                if(displayNameProvider != null)
                {
                    if(displayNameProvider instanceof ChangeSupportProvider provider)
                    {
                        provider.addChangeListener(this);                    
                    }                                
                    return displayNameProvider.getDisplayName(TextFormat.PLAIN);                
                }
            } 
            else
            {
                return displayNameProvider.getDisplayName(TextFormat.PLAIN);             
            }
            return super.getDisplayName();
        }          

        @Override
        public Image getIcon(int type) 
        {
            if(iconProvider == null)
            {
                iconProvider = getLookup().lookup(IconProvider.class);
                if(iconProvider != null)
                {
                    if(iconProvider instanceof ChangeSupportProvider provider)
                    {
                        provider.addChangeListener(this);                    
                    }
                    return iconProvider.getIcon(type);                
                }
            } 
            else
            {
                return iconProvider.getIcon(type);             
            }                  
            return super.getIcon(type);
        }        

        @Override
        public void stateChanged(ChangeEvent evt) 
        {
            if(evt.getSource() == displayNameProvider)
            {
                fireDisplayNameChange(null, displayNameProvider.getDisplayName(TextFormat.PLAIN));
            }
            else if(evt.getSource() == iconProvider)
            {
                fireIconChange();
            }         
        }
    }    
}
