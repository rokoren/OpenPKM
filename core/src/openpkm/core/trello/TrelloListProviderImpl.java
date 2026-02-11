/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.TList;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.Action;
import openpkm.base.NodePositionProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloList;
import openpkm.trello.TrelloListProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloListProvider.class)
public class TrelloListProviderImpl implements TrelloListProvider
{        
    private static final String PROP_BOARD_ID      = "board.id";
    private static final String PROP_LIST_ID       = "list.id";
    private static final String PROP_LIST_NAME     = "list.name";    
    private static final String PROP_LIST_POSITION = "list.position";     
    
    private static final Logger LOG = Logger.getLogger(TrelloListProvider.class.getName());    

    @Override
    public TrelloList getList(Properties props) 
    {
        return new TrelloListImpl(props);
    }
    
    @Override
    public TrelloList createList(TList list) 
    {
        Properties props = new Properties();
        props.setProperty(PROP_BOARD_ID, list.getIdBoard());
        props.setProperty(PROP_LIST_ID, list.getId());
        props.setProperty(PROP_LIST_NAME, list.getName());
        props.setProperty(PROP_LIST_POSITION, list.getPos() + "");          
        return new TrelloListImpl(props);
    } 
    
    private static final class TrelloListImpl implements TrelloList, NodePositionProvider
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/application_view_list.png";  
        
        private final Properties props;                 
        
        public TrelloListImpl(Properties props)
        {
            this.props = props;              
        }     

// TODO TrelloList        
        
        @Override
        public String getBoardID() 
        {
            return props.getProperty(PROP_BOARD_ID);
        } 
        
        @Override
        public String getListID() 
        {
            return props.getProperty(PROP_LIST_ID);
        }         
                
        @Override
        public String getListName() 
        {
            return props.getProperty(PROP_LIST_NAME);
        }

        @Override
        public Integer getListPosition() 
        {
            String string = props.getProperty(PROP_LIST_POSITION);
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
            return getListID();
        }
        
        @Override
        public String getDisplayName() 
        {
            return getListName();
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
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Trello/Card"));         
            return actions;
        } 
        
        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }        

        @Override
        public int getPosition() 
        {
            Integer position = getListPosition();
            if(position != null)
            {
                return position.intValue();
            }
            return -1;
        }
    } 
}
