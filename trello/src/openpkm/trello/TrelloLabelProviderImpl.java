/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.Label;
import java.awt.Image;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloLabel.TrelloColor;
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
        public Optional<TrelloColor> getLabelColor() 
        {
            String string = props.getProperty(PROP_LABEL_COLOR);
            if(string != null)
            {
                return TrelloLabel.TrelloColor.get(string);
            }
            return Optional.empty();
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
            Optional<TrelloColor> color = getLabelColor();
            if(color.isPresent())
            {
                color.get().getIcon();
            }
            return null;
        }        
    }      
}
