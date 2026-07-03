/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionsProvider;
import openpkm.base.BulletIconProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.DisplayNameProvider.TextFormat;
import openpkm.base.IconProvider;
import openpkm.base.OpenIconProvider;
import openpkm.base.ShortDescriptionProvider;
import openpkm.base.Source;
import openpkm.base.SourceProviderWrapper;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public class MarkdownDataNode extends DataNode implements ChangeListener
{
    private static final Logger LOG = Logger.getLogger(MarkdownDataNode.class.getName());    
    
    private SourceProviderWrapper sourceProvider;
    private DisplayNameProvider displayNameProvider;
    private ShortDescriptionProvider shortDescriptionProvider;
    private IconProvider iconProvider;
    private OpenIconProvider openIconProvider;
    private BulletIconProvider bulletIconProvider;
    
    public MarkdownDataNode(MarkdownDataObject data) 
    {
        super(data, Children.LEAF, data.getLookup());    
    }
    
    private SourceProviderWrapper getSourceProvider()
    {
        if(sourceProvider == null)
        {
            sourceProvider = getLookup().lookup(SourceProviderWrapper.class);
            if(sourceProvider != null)
            {
                sourceProvider.addListener(this);
            }            
        }
        return sourceProvider;
    }
    
    private DisplayNameProvider getDisplayNameProvider()
    {
        if(displayNameProvider == null)
        {
            if(getSourceProvider() != null)
            {
                Source source = getSourceProvider().getSource();
                if(source != null)
                {
                    displayNameProvider = source.getLookup().lookup(DisplayNameProvider.class);
                    if(displayNameProvider != null)
                    {
                        if(displayNameProvider instanceof ChangeSupportProvider provider)
                        {
                            provider.addChangeListener(this);                    
                        }                                             
                    } 
                }            
            }                       
        } 
        return displayNameProvider;         
    }  
    
    private IconProvider getIconProvider()
    {
        if(iconProvider == null)
        {
            if(getSourceProvider() != null)
            {
                Source source = getSourceProvider().getSource();
                if(source != null)
                {
                    iconProvider = source.getLookup().lookup(IconProvider.class);
                    if(iconProvider != null)
                    {
                        if(iconProvider instanceof ChangeSupportProvider provider)
                        {
                            provider.addChangeListener(this);                    
                        }                                             
                    } 
                }            
            }                       
        } 
        return iconProvider;         
    }    
    
    private BulletIconProvider getBulletIconProvider()
    {
        if(bulletIconProvider == null)
        {
            if(getSourceProvider() != null)
            {
                Source source = getSourceProvider().getSource();
                if(source != null)
                {
                    bulletIconProvider = source.getLookup().lookup(BulletIconProvider.class);
                    if(bulletIconProvider != null)
                    {
                        bulletIconProvider.addChangeListener(this);                                            
                    } 
                }            
            }                       
        } 
        return bulletIconProvider;         
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
        
        MarkdownDataObject data = (MarkdownDataObject)getDataObject();  
        //actions.add(new PdfAction(data));            
        
        return actions.toArray(new Action[actions.size()]);
    }    
    
    @Override
    public Image getIcon(int type) 
    {
        if(getIconProvider() != null)
        {
            if(getBulletIconProvider() != null)
            {
                Image bullet = bulletIconProvider.getBullet();
                if(bullet != null)
                {
                    return ImageUtilities.mergeImages(iconProvider.getIcon(type), bullet, 7, -4);                
                }                  
            }
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
        return getIcon(type);
    }      
    
    @Override
    public String getDisplayName() 
    {        
        DisplayNameProvider provider = getDisplayNameProvider();
        if(provider != null)
        {
            return displayNameProvider.getDisplayName(TextFormat.PLAIN); 
        } 
        return super.getDisplayName();
    }  
    
    @Override
    public String getHtmlDisplayName() 
    {
        DisplayNameProvider provider = getDisplayNameProvider();
        if(provider != null)
        {
            return displayNameProvider.getDisplayName(TextFormat.HTML); 
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
            fireDisplayNameChange(null, displayNameProvider.getDisplayName(TextFormat.PLAIN));
        }
        else if(evt.getSource() == shortDescriptionProvider)
        {
            fireShortDescriptionChange(null, shortDescriptionProvider.getShortDescription());
        }
        else if(evt.getSource() == iconProvider || evt.getSource() == bulletIconProvider)
        {
            fireIconChange();
        }         
        else if(evt.getSource() == openIconProvider)
        {
            fireOpenedIconChange();
        }   
        else if(evt.getSource() == sourceProvider)
        {
            String displayName = getDisplayName();
            displayNameProvider = null;
            iconProvider = null;
            bulletIconProvider = null;
            openIconProvider = null;
            shortDescriptionProvider = null;
            fireDisplayNameChange(displayName, getDisplayName());
        }         
    }    
}
