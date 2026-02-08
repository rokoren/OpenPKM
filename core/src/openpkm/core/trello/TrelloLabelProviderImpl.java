/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Label;
import java.awt.Color;
import java.awt.Image;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.Action;
import openpkm.base.IconsProvider;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloLabel;
import openpkm.trello.TrelloLabelProvider;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
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
    
    private static final class TrelloLabelImpl implements TrelloLabel, NodeProvider, PropertiesProvider
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
                if(string.equalsIgnoreCase("yellow"))
                {
                    return Color.YELLOW;
                }
                else if(string.equalsIgnoreCase("magenta"))
                {
                    return Color.MAGENTA;
                }
                else if(string.equalsIgnoreCase("blue"))
                {
                    return Color.BLUE;
                }                     
                else if(string.equalsIgnoreCase("red"))
                {
                    return Color.RED;
                }
                else if(string.equalsIgnoreCase("green"))
                {
                    return Color.GREEN;
                }  
                else if(string.equalsIgnoreCase("orange"))
                {
                    return Color.ORANGE;
                }
                else if(string.equalsIgnoreCase("pink"))
                {
                    return Color.PINK;
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
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            Color color = getLabelColor();
            if(color != null)
            {
                if(color == Color.YELLOW)
                {
                    return provider.getImage(IconsProvider.ICON.TAG_YELLOW);
                }
                else if(color == Color.MAGENTA)
                {
                    throw new UnsupportedOperationException("Icon not found");
                } 
                else if(color == Color.BLUE)
                {
                    return provider.getImage(IconsProvider.ICON.TAG_BLUE);
                }  
                else if(color == Color.RED)
                {
                    return provider.getImage(IconsProvider.ICON.TAG_RED);
                }  
                else if(color == Color.GREEN)
                {
                    return provider.getImage(IconsProvider.ICON.TAG_GREEN);
                }  
                else if(color == Color.ORANGE)
                {
                    return provider.getImage(IconsProvider.ICON.TAG_ORANGE);
                }      
                else if(color == Color.PINK)
                {
                    return provider.getImage(IconsProvider.ICON.TAG_PINK);
                }                 
            }
            return null;
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
    }      
}
