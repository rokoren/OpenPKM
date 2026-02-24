/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.CheckItem;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TitleProvider;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListItem;
import openpkm.trello.TrelloCheckListItemProvider;
import openpkm.trello.TrelloCheckListProvider;
import openpkm.trello.TrelloCheckListsProvider;
import openpkm.trello.TrelloService;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class TrelloCheckListItemProviderImpl implements TrelloCheckListItemProvider
{    
    private static final Logger LOG = Logger.getLogger(TrelloCheckListItemProvider.class.getName()); 
    
    private final TrelloCheckListsProvider provider; 
    private final TrelloCheckList checkList;

    public TrelloCheckListItemProviderImpl(TrelloCheckListsProvider provider, TrelloCheckList checkList) 
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
    public TrelloCheckListsProvider getProvider() 
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
        
        private ChangeSupport changeSupport;
        
        public TrelloCheckListItemImpl(Properties props)
        {
            this.props = props;              
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
                props.remove(PROP_CHECKLIST_ITEM_STATE);
            }
            else
            {
                props.put(PROP_CHECKLIST_ITEM_STATE, state.toString());
            }
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
            return getCheckListItemID();
        }
        
        @Override
        public String getDisplayName() 
        {
            return getCheckListItemName();
        }
        
        @Override
        public Image getIcon(boolean opened) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            State state = getCheckListItemState();
            if(state != null && state == State.COMPLETE)
            {
                return provider.getImage(IconsProvider.ICON.CHECK);
            }
            return provider.getImage(IconsProvider.ICON.UNCHECK);
        }  
        
        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
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
                    String string = provider.getCheckList().getProperties().getProperty(TrelloCheckListProvider.PROP_CHECKLIST_ITEMS);
                    if(string != null)
                    {
                        JSONArray jsons = new JSONArray(string);  
                        int index = getItemIndex(jsons, item.getCheckListItemID());
                        if(index != -1)
                        {
                            jsons.remove(index);
                        }
                        provider.getCheckList().getProperties().setProperty(TrelloCheckListProvider.PROP_CHECKLIST_ITEMS, jsons.toString());
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
            
            String string = provider.getCheckList().getProperties().getProperty(TrelloCheckListProvider.PROP_CHECKLIST_ITEMS);
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
                provider.getCheckList().getProperties().setProperty(TrelloCheckListProvider.PROP_CHECKLIST_ITEMS, jsons.toString());
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
                        item.getChangeSupport().fireChange();
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
