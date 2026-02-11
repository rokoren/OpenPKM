/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.CheckItem;
import java.awt.Image;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.Action;
import kong.unirest.json.JSONObject;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloCheckListItem;
import openpkm.trello.TrelloCheckListItemProvider;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloCheckListItemProvider.class)
public class TrelloCheckListItemProviderImpl implements TrelloCheckListItemProvider
{    
    private static final Logger LOG = Logger.getLogger(TrelloCheckListItemProvider.class.getName());    

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
    
    private static final class TrelloCheckListItemImpl implements TrelloCheckListItem
    {         
        private final Properties props;     
        
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
        public List<Action> getActions() 
        {       
            return Collections.EMPTY_LIST;
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
    }      
}
