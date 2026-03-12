/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionsProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.DisplayNameProvider.TextFormat;
import openpkm.base.IconProvider;
import openpkm.base.NodeProvider;
import openpkm.base.NodeSupport;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import openpkm.base.SourceGroupProvider;
import openpkm.base.NodeActionsProvider;
import openpkm.base.OpenIconProvider;
import openpkm.base.ShortDescriptionProvider;

/**
 *
 * @author Rok Koren
 */
public class SourceGroupNode extends AbstractNode implements NodeSupport
{
    private static final Logger LOG = Logger.getLogger(SourceGroupNode.class.getName());    
    
    private final SourceGroupProvider provider;
    
    public SourceGroupNode(SourceGroupProvider provider) 
    {
        super(new ChildrenImpl(provider), Lookups.proxy(provider.getProvider()));
        setName(provider.getName());
        setDisplayName(provider.getDisplayName());
        this.provider = provider;
    }      

    @Override    
    public Action[] getActions(boolean context) 
    {
        List<Action> actions = new ArrayList();
        actions.addAll(provider.getActions());
        return actions.toArray(new Action[actions.size()]);
    }

    private Image getIcon(boolean opened) 
    {
        return provider.getIcon(true, opened);
    } 
    
    @Override    
    public Image getIcon(int type)     
    {
        return getIcon(false);
    }

    @Override
    public Image getOpenedIcon(int type) 
    {
        return getIcon(true);
    }     
    
    @Override
    public Node findNode(Node root, Object target) 
    {
        FileObject file = (FileObject)target;
        return root.getChildren().findChild(file.getName());
        /*
        LOG.info("Finding node: " + target.toString());
        
        for(Node node : root.getChildren().getNodes())
        {
            DataObject data = node.getLookup().lookup(DataObject.class);                                          
            if(data instanceof OpenPkmDataObject && data.getPrimaryFile().equals(target))
            {
                OpenPkmDataObject opdo = (OpenPkmDataObject)data;
                LOG.info("Data found: " + opdo.getDataID());
                LOG.info("Node found: " + node.getName());
                return node;
            }
        }           
        return null;
        */
    } 
    
    private static final class ChildrenImpl extends Children.Keys<NodeProvider> implements ChangeListener 
    {
        private final SourceGroupProvider provider;  
        
        private NodeActionsProvider ap;

        public ChildrenImpl(SourceGroupProvider provider)
        {
            this.provider = provider;   
            provider.addChangeListener(this);             
        }  

        @Override
        protected void addNotify() 
        {
            updateKeys();
        }

        private void updateKeys() 
        {
            setKeys(provider.getNodes()); 
        }

        @Override
        protected void removeNotify()
        {
            provider.removeChangeListener(this);            
            setKeys(Collections.<NodeProvider>emptySet());
        }

        @Override
        protected Node[] createNodes(NodeProvider nodeProvider) 
        {
            if(provider instanceof NodeActionsProvider)
            {
                NodeActionsProvider actionsProvider = (NodeActionsProvider)provider;
                return new Node[] {new ProviderNode(nodeProvider, actionsProvider.getActions(nodeProvider))};                
            }            
            return new Node[] {new ProviderNode(nodeProvider, Collections.EMPTY_LIST)};
        }          

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            updateKeys();
        }                 
    }  
    
    private static final class ProviderNode extends AbstractNode implements ChangeListener
    {        
        private DisplayNameProvider displayNameProvider;
        private ShortDescriptionProvider shortDescriptionProvider;
        private IconProvider iconProvider;
        private OpenIconProvider openIconProvider;  
        
        private final List<Action> actions;        

        public ProviderNode(NodeProvider provider, List<Action> actions) 
        {
            super(provider.getChildren(), provider.getLookup());
            setName(provider.getName());
            this.actions = actions;
        } 
        
        @Override    
        public Action[] getActions(boolean context) 
        {
            List<Action> list = new ArrayList<>();
            list.addAll(actions);
            
            ActionsProvider provider = getLookup().lookup(ActionsProvider.class);
            if(provider != null)
            {
                actions.addAll(provider.getActions());
            }
                                   
            return list.toArray(new Action[list.size()]);
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
                    return displayNameProvider.getDisplayName(TextFormat.PLAIN);                
                }
            } 
            else
            {
                return displayNameProvider.getDisplayName(TextFormat.PLAIN);             
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
                    return displayNameProvider.getDisplayName(TextFormat.HTML);                
                }
            } 
            else
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
}
