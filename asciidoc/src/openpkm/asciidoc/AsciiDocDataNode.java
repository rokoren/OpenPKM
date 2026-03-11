/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionsProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.IconProvider;
import openpkm.base.OpenIconProvider;
import openpkm.base.ShortDescriptionProvider;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

/**
 *
 * @author Rok Koren
 */
public class AsciiDocDataNode extends DataNode implements ChangeListener
{
    private static final Logger LOG = Logger.getLogger(AsciiDocDataNode.class.getName());    
    
    private DisplayNameProvider displayNameProvider;
    private ShortDescriptionProvider shortDescriptionProvider;
    private IconProvider iconProvider;
    private OpenIconProvider openIconProvider;    
    
    public AsciiDocDataNode(AsciiDocDataObject data) 
    {
        super(data, Children.LEAF, data.getLookup());
    }
    
    @Override    
    public Action[] getActions(boolean context) 
    {
        List<Action> actions = new ArrayList();         
        
        for(Action action : super.getActions(context))
        {
            actions.add(action);
        }
        
        ActionsProvider provider = getLookup().lookup(ActionsProvider.class);
        if(provider != null)
        {
            actions.addAll(provider.getActions());
        }
        
        AsciiDocDataObject data = (AsciiDocDataObject)getDataObject();  
        //actions.add(new PdfAction(data));            
        
        return actions.toArray(new Action[actions.size()]);
    }    
    
    @Override
    public Image getIcon(int type) 
    {
        if(iconProvider == null)
        {
            iconProvider = getLookup().lookup(IconProvider.class);
            if(iconProvider != null)
            {
                if(iconProvider instanceof ChangeSupportProvider provider)
                {
                    provider.addChangeListener(this);                    
                }
                return iconProvider.getIcon(type);                
            }
        } 
        else
        {
            return iconProvider.getIcon(type);             
        }                  
        return super.getIcon(type);
    }   
    
    @Override
    public Image getOpenedIcon(int type) 
    {
        if(openIconProvider == null)
        {
            openIconProvider = getLookup().lookup(OpenIconProvider.class);
            if(openIconProvider != null)
            {
                if(openIconProvider instanceof ChangeSupportProvider provider)
                {
                    provider.addChangeListener(this);                    
                }
                return openIconProvider.getOpenedIcon(type);                
            }
        } 
        else
        {
            return openIconProvider.getOpenedIcon(type);             
        }         
        return super.getOpenedIcon(type);
    }     
    
    @Override
    public String getDisplayName() 
    {
        if(displayNameProvider == null)
        {
            displayNameProvider = getLookup().lookup(DisplayNameProvider.class);
            if(displayNameProvider != null)
            {
                if(displayNameProvider instanceof ChangeSupportProvider provider)
                {
                    provider.addChangeListener(this);                    
                }                                
                return displayNameProvider.getDisplayName(false);                
            }
        } 
        else
        {
            return displayNameProvider.getDisplayName(false);             
        }
        return super.getDisplayName();
    }     

    @Override
    public String getHtmlDisplayName() 
    {
        if(displayNameProvider == null)
        {
            displayNameProvider = getLookup().lookup(DisplayNameProvider.class);
            if(displayNameProvider != null)
            {
                if(displayNameProvider instanceof ChangeSupportProvider provider)
                {
                    provider.addChangeListener(this);                    
                }                                
                return displayNameProvider.getDisplayName(true);                
            }
        } 
        else
        {
            return displayNameProvider.getDisplayName(true);             
        } 
        return super.getHtmlDisplayName();
    }    
    
    @Override
    public String getShortDescription()
    {
        if(shortDescriptionProvider == null)
        {
            shortDescriptionProvider = getLookup().lookup(ShortDescriptionProvider.class);
            if(shortDescriptionProvider != null)
            {
                if(shortDescriptionProvider instanceof ChangeSupportProvider provider)
                {
                    provider.addChangeListener(this);                    
                }                                  
                return shortDescriptionProvider.getShortDescription();                
            }
        } 
        else
        {
            return shortDescriptionProvider.getShortDescription();             
        }
        return super.getShortDescription();       
    }     

    @Override
    public void stateChanged(ChangeEvent evt) 
    {
        if(evt.getSource() == displayNameProvider)
        {
            fireDisplayNameChange(null, displayNameProvider.getDisplayName(false));
        }
        else if(evt.getSource() == shortDescriptionProvider)
        {
            fireShortDescriptionChange(null, shortDescriptionProvider.getShortDescription());
        }
        else if(evt.getSource() == iconProvider)
        {
            fireIconChange();
        }         
        else if(evt.getSource() == openIconProvider)
        {
            fireOpenedIconChange();
        }        
    }  
}
