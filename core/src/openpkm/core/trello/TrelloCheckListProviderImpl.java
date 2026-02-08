/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.CheckList;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.Action;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloCheckListProvider.class)
public class TrelloCheckListProviderImpl implements TrelloCheckListProvider
{
    private static final String PROP_CHECKLIST_ID       = "checklist.id";
    private static final String PROP_CHECKLIST_NAME     = "checklist.name";        
    private static final String PROP_CHECKLIST_POSITION = "checklist.position"; 
    
    private static final Logger LOG = Logger.getLogger(TrelloCheckListProvider.class.getName());    

    @Override
    public TrelloCheckList getCheckList(Properties props) 
    {
        return new TrelloCheckListImpl(props);
    }
    
    @Override
    public TrelloCheckList createCheckList(CheckList checkList) 
    {
        Properties props = new Properties();               
        props.setProperty(PROP_CHECKLIST_ID, checkList.getId());
        props.setProperty(PROP_CHECKLIST_NAME, checkList.getName());          
        props.setProperty(PROP_CHECKLIST_POSITION, checkList.getPos() + ""); 
        return getCheckList(props);
    } 
    
    private static final class TrelloCheckListImpl implements TrelloCheckList, NodeProvider, PropertiesProvider
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/date_task.png";  
        
        private final Properties props;     
        
        public TrelloCheckListImpl(Properties props)
        {
            this.props = props;              
        }     

// TODO TrelloCheckList        
        
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
        
// TODO PropertiesProvider        
        
        @Override
        public Properties getProperties()
        {
            return props;
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
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Trello/Checklist"));         
            return actions;
        } 
        
        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }        
    }     
}
