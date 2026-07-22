/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.CheckItem;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TitleProvider;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListItem;
import openpkm.trello.TrelloCheckListItem.State;
import openpkm.trello.TrelloCheckListProvider;
import openpkm.trello.TrelloService;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import openpkm.trello.TrelloCheckListFactory;
import openpkm.trello.TrelloCheckListItemProvider;

/**
 *
 * @author Rok Koren
 */
public class TrelloCheckListItemProviderImpl implements TrelloCheckListItemProvider
{    
    private static final Logger LOG = Logger.getLogger(TrelloCheckListItemProvider.class.getName()); 
    
    private final TrelloCheckListProvider provider; 
    private final TrelloCheckList checkList;

    public TrelloCheckListItemProviderImpl(TrelloCheckListProvider provider, TrelloCheckList checkList) 
    {
        this.provider = provider;
        this.checkList = checkList;
    }    
    
    @Override
    public TrelloCheckList getCheckList() 
    {
        return checkList;
    }

    @Override
    public TrelloCheckListProvider getProvider() 
    {
        return provider;
    }    

    @Override
    public TrelloCheckListItem getCheckListItem(JSONObject json) 
    {
        Properties props = new Properties();
        props.setProperty(PROP_CHECKLIST_ITEM_ID, json.getString("id"));
        props.setProperty(PROP_CHECKLIST_ITEM_NAME, json.getString("name"));            
        props.setProperty(PROP_CHECKLIST_ITEM_POSITION, json.getInt("pos") + "");        
        props.setProperty(PROP_CHECKLIST_ITEM_STATE, json.getString("state")); 
        return new TrelloCheckListItemImpl(props);
    }
    
    @Override
    public TrelloCheckListItem createCheckListItem(CheckItem item) 
    {
        Properties props = new Properties();               
        props.setProperty(PROP_CHECKLIST_ITEM_ID, item.getId());
        props.setProperty(PROP_CHECKLIST_ITEM_NAME, item.getName());          
        props.setProperty(PROP_CHECKLIST_ITEM_POSITION, item.getPos() + ""); 
        props.setProperty(PROP_CHECKLIST_ITEM_STATE, item.getState()); 
        return new TrelloCheckListItemImpl(props);
    } 
    
    private final class TrelloCheckListItemImpl implements TrelloCheckListItem
    {          
        private final Properties props; 
        private final PropertyChangeSupport propertyChangeSupport;        
        
        private Lookup lkp; 
        private SourceState state;  
        
        public TrelloCheckListItemImpl(Properties props)
        {
            this.props = props; 
            propertyChangeSupport = new PropertyChangeSupport(this);
        }     

// TODO TrelloCheckListItem        
        
        @Override
        public String getCheckListItemID() 
        {
            return props.getProperty(PROP_CHECKLIST_ITEM_ID);
        } 
        
        @Override
        public String getCheckListItemName() 
        {
            return props.getProperty(PROP_CHECKLIST_ITEM_NAME);
        } 
        
        @Override
        public void setCheckListItemName(String name)
        {
            if(name == null)
            {
                Object oldValue = props.remove(PROP_CHECKLIST_ITEM_NAME);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_CHECKLIST_ITEM_NAME, oldValue, name);
            }
            else
            {
                Object oldValue = props.setProperty(PROP_CHECKLIST_ITEM_NAME, name);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_CHECKLIST_ITEM_NAME, oldValue, name);                
            }
        }

        public void addCheckListItemNameListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_CHECKLIST_ITEM_NAME, listener);
        }
        
        public void removeCheckListItemNameListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.removePropertyChangeListener(PROP_CHECKLIST_ITEM_NAME, listener);
        }        
                
        @Override
        public Integer getCheckListItemPosition() 
        {
            String string = props.getProperty(PROP_CHECKLIST_ITEM_POSITION);
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
        public State getCheckListItemState() 
        {
            String string = props.getProperty(PROP_CHECKLIST_ITEM_STATE);
            if(string != null)
            {
                Optional<State> state = State.get(string);
                if(state.isPresent())
                {
                    return state.get();
                }
            }
            return State.INCOMPLETE;
        }  
        
        @Override
        public void setCheckListItemState(State state) 
        {
            if(state == null)
            {
                Object oldValue = props.remove(PROP_CHECKLIST_ITEM_STATE);
                if(oldValue != null)
                {
                    oldValue = State.get(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_CHECKLIST_ITEM_STATE, oldValue, state);                
            }
            else
            {
                Object oldValue = props.setProperty(PROP_CHECKLIST_ITEM_STATE, state.toString());
                if(oldValue != null)
                {
                    oldValue = State.get(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_CHECKLIST_ITEM_STATE, oldValue, state);  
            }
        }
        
        public void addCheckListItemStateListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_CHECKLIST_ITEM_STATE, listener);
        }
        
        public void removeCheckListItemStateListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.removePropertyChangeListener(PROP_CHECKLIST_ITEM_STATE, listener);
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
        
        @Override
        public boolean isModified() 
        {
            return state == SourceState.MODIFIED;
        }

        @Override
        public void markModified()
        {
            SourceState oldValue = state;
            state = SourceState.MODIFIED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }   

        @Override
        public boolean isDeleted() 
        {
            return state == SourceState.DELETED;
        }         
        
        @Override
        public void notifyDeleted()
        {
            SourceState oldValue = state;
            state = SourceState.DELETED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }         

// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getCheckListItemID();
        } 
        
        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            {
                lkp = Lookups.fixed(this, new DisplayNameProviderImpl(this), new IconProviderImpl(this));              
            }
            return lkp;
        }         
        
        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }  
        
        @Override
        public HelpCtx getHelp()
        {
            return HelpCtx.DEFAULT_HELP;
        }         
        
        @Override
        public int getPosition() 
        {
            Integer position = getCheckListItemPosition();
            if(position != null)
            {
                return position.intValue();
            }
            return -1;
        }        

        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList<>();
            actions.add(new CheckUncheckAction(this, TrelloCheckListItemProviderImpl.this));
            actions.add(new DeleteCheckListItem(this, TrelloCheckListItemProviderImpl.this));
            return actions;
        }

        @Override
        public Action getPreferredAction() 
        {
            return new CheckUncheckAction(this, TrelloCheckListItemProviderImpl.this);
        }
    } 
    
    private static final class DisplayNameProviderImpl implements DisplayNameProvider, ChangeSupportProvider, PropertyChangeListener
    {
        private final TrelloCheckListItemImpl item;
        private final ChangeSupport changeSupport;

        public DisplayNameProviderImpl(TrelloCheckListItemImpl item) 
        {
            this.item = item;
            item.addCheckListItemNameListener(this);
            changeSupport = new ChangeSupport(this);
        }                
        
        @Override
        public String getDisplayName(TextFormat format) 
        {
            if(format == TextFormat.PLAIN)
            {
                return item.getCheckListItemName();
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
    
    private static final class IconProviderImpl implements IconProvider, ChangeSupportProvider, PropertyChangeListener
    {
        private final TrelloCheckListItemImpl item;
        private final ChangeSupport changeSupport;

        public IconProviderImpl(TrelloCheckListItemImpl item) 
        {
            this.item = item;
            item.addCheckListItemStateListener(this);
            changeSupport = new ChangeSupport(this);
        }                
        
        @Override
        public Image getIcon(int type) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            State state = item.getCheckListItemState();
            if(state != null && state == State.COMPLETE)
            {
                return provider.getImage(IconsProvider.ICON.CHECK);
            }
            return provider.getImage(IconsProvider.ICON.UNCHECK);
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
    
    private static final class DeleteCheckListItem extends AbstractAction
    {          
        private final TrelloCheckListItem item;   
        private final TrelloCheckListItemProvider provider; 

        public DeleteCheckListItem(TrelloCheckListItem item, TrelloCheckListItemProvider provider) 
        {
            super("Delete");
            this.item = item;
            this.provider = provider;
        }
        
        private int getItemIndex(JSONArray jsons, String itemID)
        {
            for(int i=0; i<jsons.length(); i++)
            {
                JSONObject json = jsons.getJSONObject(i);
                TrelloCheckListItem item = provider.getCheckListItem(json);
                if(item.getCheckListItemID().equals(itemID))
                {
                    return i;
                }
            }           
            return -1;           
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        { 
            NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to delete: " + item.getCheckListItemName(), "Delete Checklist Item", NotifyDescriptor.YES_NO_OPTION);
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.YES_OPTION) 
            {
                TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
                int status = service.deleteCheckListItem(provider.getCheckList(), item, provider.getProvider().getAccount());
                if(status == TrelloService.STATUS_OK)
                {
                    String string = provider.getCheckList().getProperties().getProperty(TrelloCheckListFactory.PROP_CHECKLIST_ITEMS);
                    if(string != null)
                    {
                        JSONArray jsons = new JSONArray(string);  
                        int index = getItemIndex(jsons, item.getCheckListItemID());
                        if(index != -1)
                        {
                            jsons.remove(index);
                        }
                        provider.getCheckList().getProperties().setProperty(TrelloCheckListFactory.PROP_CHECKLIST_ITEMS, jsons.toString());
                    }                  

                    FileObject file = provider.getProvider().getRootFolder().getFileObject(provider.getCheckList().getCheckListID(), PropertiesProvider.EXTENSION);
                    if(file != null)
                    {
                        try
                        {
                            OutputStream os = file.getOutputStream();
                            TitleProvider titleProvider = provider.getProvider().getProvider().getLookup().lookup(TitleProvider.class);
                            provider.getCheckList().getProperties().store(os, "Updated by Trello project: " + titleProvider.getTitle()); 
                            os.close();
                            provider.getCheckList().getChangeSupport().fireChange();
                            LOG.info("Trello checklist saved: " + provider.getCheckList().getCheckListID()); 
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
    
    private static final class CheckUncheckAction extends AbstractAction
    {  
        private static final String ACTION_NAME_CHECK   = "Check";
        private static final String ACTION_NAME_UNCHECK = "Uncheck";
        
        private final TrelloCheckListItem item;   
        private final TrelloCheckListItemProvider provider; 

        public CheckUncheckAction(TrelloCheckListItem item, TrelloCheckListItemProvider provider) 
        {
            super(getActionName(item));
            this.item = item;
            this.provider = provider;
        }
        
        private static String getActionName(TrelloCheckListItem item)
        {
            if(item.getCheckListItemState() == TrelloCheckListItem.State.INCOMPLETE)
            {
                return ACTION_NAME_CHECK;
            }
            else if(item.getCheckListItemState() == TrelloCheckListItem.State.COMPLETE)
            {
                return ACTION_NAME_UNCHECK;
            }
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        { 
            if(item.getCheckListItemState() == TrelloCheckListItem.State.INCOMPLETE)
            {
                item.setCheckListItemState(TrelloCheckListItem.State.COMPLETE);
            }   
            else if(item.getCheckListItemState() == TrelloCheckListItem.State.COMPLETE)
            {
                item.setCheckListItemState(TrelloCheckListItem.State.INCOMPLETE);
            }             
            
            String string = provider.getCheckList().getProperties().getProperty(TrelloCheckListFactory.PROP_CHECKLIST_ITEMS);
            if(string != null)
            {
                JSONArray jsons = new JSONArray(string);  
                for(int i=0; i<jsons.length(); i++)
                {
                    JSONObject json = jsons.getJSONObject(i);
                    if(item.getCheckListItemID().equals(provider.getCheckListItem(json).getCheckListItemID()))
                    {                                               
                        json.put("state", item.getCheckListItemState().toString());
                    }
                } 
                provider.getCheckList().getProperties().setProperty(TrelloCheckListFactory.PROP_CHECKLIST_ITEMS, jsons.toString());
            }               
            
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            int status = service.setCheckListItemState(provider.getCheckList(), item, provider.getProvider().getAccount());
            if(status == TrelloService.STATUS_OK)
            {               
                FileObject file = provider.getProvider().getRootFolder().getFileObject(provider.getCheckList().getCheckListID(), PropertiesProvider.EXTENSION);
                if(file != null)
                {
                    try
                    {
                        OutputStream os = file.getOutputStream();
                        TitleProvider titleProvider = provider.getProvider().getProvider().getLookup().lookup(TitleProvider.class);
                        provider.getCheckList().getProperties().store(os, "Updated by Trello project: " + titleProvider.getTitle()); 
                        os.close();
                        LOG.info("Trello checklist saved: " + provider.getCheckList().getCheckListID()); 
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
