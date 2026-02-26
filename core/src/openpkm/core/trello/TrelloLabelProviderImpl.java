/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Label;
import java.awt.Color;
import java.awt.Image;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.Icon;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloLabel;
import openpkm.trello.TrelloLabelProvider;
import openpkm.utils.RoundRectIcon;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloLabelProvider.class)
public class TrelloLabelProviderImpl implements TrelloLabelProvider
{
    private static final String PROP_LABEL_ID    = "label.id";
    private static final String PROP_LABEL_NAME  = "label.name";
    private static final String PROP_LABEL_COLOR = "label.color";       
    
    private static final Logger LOG = Logger.getLogger(TrelloLabelProvider.class.getName());     
    
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
        
        public TrelloLabelImpl(Properties props)
        {
            this.props = props;              
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
        public Color getLabelColor() 
        {
            String string = props.getProperty(PROP_LABEL_COLOR);
            if(string != null)
            {
                if(string.equalsIgnoreCase(COLOR_YELLOW))
                {
                    return Color.YELLOW;
                }
                else if(string.equalsIgnoreCase(COLOR_MAGENTA))
                {
                    return Color.MAGENTA;
                }
                else if(string.equalsIgnoreCase(COLOR_BLUE))
                {
                    return Color.BLUE;
                }                     
                else if(string.equalsIgnoreCase(COLOR_RED))
                {
                    return Color.RED;
                }
                else if(string.equalsIgnoreCase(COLOR_GREEN))
                {
                    return Color.GREEN;
                }  
                else if(string.equalsIgnoreCase(COLOR_ORANGE))
                {
                    return Color.ORANGE;
                }
                else if(string.equalsIgnoreCase(COLOR_PINK))
                {
                    return Color.PINK;
                }                 
                else if(string.equalsIgnoreCase(COLOR_BLACK))
                {
                    return Color.BLACK;
                }  
                else if(string.equalsIgnoreCase(COLOR_SKY))
                {
                    return Color.CYAN;
                }
                else if(string.equalsIgnoreCase(COLOR_LIME))
                {
                    return Color.green.brighter();
                }   
                else if(string.equalsIgnoreCase(COLOR_PURPLE))
                {
                    return Color.PINK.darker();
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
            return getLabelID();
        }
        
        @Override
        public String getDisplayName() 
        {
            return getLabelName();
        }
        
        @Override
        public Image getIcon(boolean opened) 
        {
            Color color = getLabelColor();
            if(color != null)
            {
                Icon icon = new RoundRectIcon(14, 14, color);
                return ImageUtilities.icon2Image(icon);
            }
            return null;
        }  
        
        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }        
    }      
}
