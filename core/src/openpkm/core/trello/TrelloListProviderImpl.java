/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.TList;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.IconProvider;
import openpkm.base.NodePositionProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloList;
import openpkm.trello.TrelloListProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.Children;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloListProvider.class)
public class TrelloListProviderImpl implements TrelloListProvider
{            
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
    
    private static final class TrelloListImpl implements TrelloList, IconProvider, NodePositionProvider
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/application_view_list.png";  
        
        private final Properties props; 
        private final PropertyChangeSupport propertyChangeSupport; 

        private Lookup lkp; 
        
        public TrelloListImpl(Properties props)
        {
            this.props = props;  
            propertyChangeSupport = new PropertyChangeSupport(this); 
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
        public void setListName(String name)
        {
            if(name == null)
            {
                Object oldValue = props.remove(PROP_LIST_NAME);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_LIST_NAME, oldValue, name);
            }
            else
            {
                Object oldValue = props.setProperty(PROP_LIST_NAME, name);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_LIST_NAME, oldValue, name);                
            }            
        }
        
        public void addListNameListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_LIST_NAME, listener);
        }
        
        public void removeListNameListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.removePropertyChangeListener(PROP_LIST_NAME, listener);
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

// TODO IconProvider        
        
        @Override
        public Image getIcon(int type) 
        {
            return ImageUtilities.loadImage(ICON);
        }        
        
// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getListID();
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
            Integer position = getListPosition();
            if(position != null)
            {
                return position.intValue();
            }
            return -1;
        }
    } 
    
    private static final class DisplayNameProviderImpl implements DisplayNameProvider, ChangeSupportProvider, PropertyChangeListener
    {
        private final TrelloListImpl list;
        private final ChangeSupport changeSupport;

        public DisplayNameProviderImpl(TrelloListImpl list) 
        {
            this.list = list;
            list.addListNameListener(this);
            changeSupport = new ChangeSupport(this);
        }                
        
        @Override
        public String getDisplayName(TextFormat format) 
        {
            if(format == TextFormat.PLAIN)
            {
                return list.getListName();
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
}
