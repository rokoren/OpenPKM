/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Label;
import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.IconProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloLabel;
import openpkm.utils.RoundRectIcon;
import org.openide.nodes.Children;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import openpkm.trello.TrelloLabelFactory;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloLabelFactory.class)
public class TrelloLabelFactoryImpl implements TrelloLabelFactory
{
    private static final String PROP_LABEL_ID    = "label.id";
    private static final String PROP_LABEL_NAME  = "label.name";
    private static final String PROP_LABEL_COLOR = "label.color";       
    
    private static final Logger LOG = Logger.getLogger(TrelloLabelFactory.class.getName());     
    
    @Override
    public TrelloLabel getLabel(Properties props) 
    {
        return new TrelloLabelImpl(props);
    }
    
    @Override
    public TrelloLabel createLabel(Label label) 
    {
        Properties props = new Properties();
        props.setProperty(PROP_LABEL_ID, label.getId());
        props.setProperty(PROP_LABEL_NAME, label.getName());
        props.setProperty(PROP_LABEL_COLOR, label.getColor());            
        return new TrelloLabelImpl(props);
    }    
    
    private static final class TrelloLabelImpl implements TrelloLabel
    {        
        private final Properties props;
        private final PropertyChangeSupport propertyChangeSupport; 
        
        private Lookup lkp;  
        private State state; 
        
        public TrelloLabelImpl(Properties props)
        {
            this.props = props;
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        
// TODO TrelloLabel

        @Override
        public String getLabelID() 
        {
            return props.getProperty(PROP_LABEL_ID);
        }
        
        @Override
        public String getLabelName() 
        {
            return props.getProperty(PROP_LABEL_NAME);
        }   
        
        @Override
        public void setLabelName(String name)
        {
            if(name == null)
            {
                Object oldValue = props.remove(PROP_LABEL_NAME);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_LABEL_NAME, oldValue, name);
            }
            else
            {
                Object oldValue = props.setProperty(PROP_LABEL_NAME, name);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_LABEL_NAME, oldValue, name);                
            }            
        }
        
        public void addLabelNameListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_LABEL_NAME, listener);
        }
        
        public void removeLabelNameListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.removePropertyChangeListener(PROP_LABEL_NAME, listener);
        }                  
        
        @Override
        public String getLabelColor() 
        {
            return props.getProperty(PROP_LABEL_COLOR);
        } 
        
        @Override
        public void setLabelColor(String color)
        {
            if(color == null)
            {
                Object oldValue = props.remove(PROP_LABEL_COLOR);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_LABEL_COLOR, oldValue, color);
            }
            else
            {
                Object oldValue = props.setProperty(PROP_LABEL_COLOR, color);
                if(oldValue != null)
                {
                    oldValue = oldValue.toString();
                }
                propertyChangeSupport.firePropertyChange(PROP_LABEL_COLOR, oldValue, color);                
            }            
        }
        
        public void addLabelColorListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_LABEL_COLOR, listener);
        }
        
        public void removeLabelColorListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.removePropertyChangeListener(PROP_LABEL_COLOR, listener);
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

// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getLabelID();
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
    }
    
    private static final class DisplayNameProviderImpl implements DisplayNameProvider, ChangeSupportProvider, PropertyChangeListener
    {
        private final TrelloLabelImpl label;
        private final ChangeSupport changeSupport;

        public DisplayNameProviderImpl(TrelloLabelImpl label) 
        {
            this.label = label;
            label.addLabelNameListener(this);
            changeSupport = new ChangeSupport(this);
        }                
        
        @Override
        public String getDisplayName(TextFormat format) 
        {
            if(format == TextFormat.PLAIN)
            {
                return label.getLabelName();
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
        private final TrelloLabelImpl label;
        private final ChangeSupport changeSupport;

        public IconProviderImpl(TrelloLabelImpl label) 
        {
            this.label = label;
            label.addLabelColorListener(this);
            changeSupport = new ChangeSupport(this);
        }                
        
        @Override
        public Image getIcon(int type) 
        {
            String name = label.getLabelColor();
            if(name != null)
            {
                Color color = TrelloLabelFactory.getColor(name);
                if(color != null)
                {
                    Icon icon = new RoundRectIcon(14, 14, color);
                    return ImageUtilities.icon2Image(icon);
                }                
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
