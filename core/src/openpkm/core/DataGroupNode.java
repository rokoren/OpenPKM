/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionsProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DataGroupProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.DisplayNameProvider.TextFormat;
import openpkm.base.FilterTagsProvider;
import openpkm.base.GoalsGraphProvider;
import openpkm.base.GoalsProvider;
import openpkm.base.IconProvider;
import openpkm.base.KnowledgeGraphProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import openpkm.base.NodeSupport;
import openpkm.base.OpenIconProvider;
import openpkm.base.Source;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.TagsProvider;
import openpkm.base.TopicsProvider;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class DataGroupNode extends AbstractNode implements NodeSupport, ChangeListener
{    
    private static final Logger LOG = Logger.getLogger(DataGroupNode.class.getName());    
    
    private DisplayNameProvider displayNameProvider;
    private IconProvider iconProvider;
    private OpenIconProvider openIconProvider;    
    
    private final DataGroupProvider provider;
    
    public DataGroupNode(DataGroupProvider provider) 
    {
        super(new ChildrenImpl(provider), Lookups.proxy(provider.getProvider()));
        setName(provider.getName());
        this.provider = provider;
    }      

    @Override
    public String getDisplayName() 
    {        
        if(displayNameProvider == null)
        {
            displayNameProvider = provider.getDisplayNameProvider();
            if(displayNameProvider != null)
            {
                if(displayNameProvider instanceof ChangeSupportProvider)
                {
                    ChangeSupportProvider changeSupportProvider = (ChangeSupportProvider)displayNameProvider;
                    changeSupportProvider.addChangeListener(this);                    
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
    public Image getIcon(int type) 
    {
        if(iconProvider == null)
        {
            iconProvider = provider.getIconProvider();
            if(iconProvider != null)
            {
                if(iconProvider instanceof ChangeSupportProvider)
                {
                    ChangeSupportProvider changeSupportProvider = (ChangeSupportProvider)iconProvider;
                    changeSupportProvider.addChangeListener(this);                    
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
                if(openIconProvider instanceof ChangeSupportProvider)
                {
                    ChangeSupportProvider changeSupportProvider = (ChangeSupportProvider)openIconProvider;
                    changeSupportProvider.addChangeListener(this);                    
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
    public Action[] getActions(boolean context) 
    {
        List<Action> actions = new ArrayList();
        ActionsProvider actionsProvider = provider.getActionsProvider();
        if(actionsProvider != null)
        {
            actions.addAll(actionsProvider.getActions());            
        }
        return actions.toArray(new Action[actions.size()]);
    }   
    
    @Override
    public void stateChanged(ChangeEvent evt) 
    {
        if(evt.getSource() == displayNameProvider)
        {
            fireDisplayNameChange(null, displayNameProvider.getDisplayName(TextFormat.PLAIN));
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
    
    private static final class ChildrenImpl extends Children.Keys<DataObject> implements ChangeListener 
    {
        private final DataGroupProvider provider;   
        private final FilterTagsProvider filterTags;
        private final KnowledgeGraphProvider topicProvider;
        private final GoalsGraphProvider goalProvider;

        public ChildrenImpl(DataGroupProvider provider)
        {
            this.provider = provider;   
            provider.addChangeListener(this); 
            filterTags = Lookup.getDefault().lookup(FilterTagsProvider.class);
            if(filterTags != null) 
            {
                filterTags.addChangeListener(this);
            }
            
            topicProvider = provider.getProvider().getLookup().lookup(KnowledgeGraphProvider.class);
            if(topicProvider instanceof ChangeSupportProvider csp)
            {
                csp.addChangeListener(this);
            }
            
            goalProvider = provider.getProvider().getLookup().lookup(GoalsGraphProvider.class);
            if(goalProvider instanceof ChangeSupportProvider csp)
            {
                csp.addChangeListener(this);
            }            
        }  

        @Override
        protected void addNotify() 
        {
            updateKeys();
        }

        private void updateKeys() 
        {  
            SortedSet<DataObject> sorted = new TreeSet<DataObject>(provider.getComparator());
            try
            {
                for(FileObject file : provider.getFiles())
                {
                    DataObject data = null;
                    if(file.isData())
                    {
                        try
                        {
                            data = DataObject.find(file);                    
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.warning(e.getMessage());
                        }
                    }
                    else if(file.isFolder())
                    {
                        data = DataFolder.findFolder(file);
                    }                     

                    if(provider.contains(data))
                    {                        
                        boolean isTag = true;
                        boolean isTopic = true;
                        boolean isGoal = true;
                        
                        if(filterTags != null)
                        {
                            TagsProvider tagsProvider = data.getLookup().lookup(TagsProvider.class);
                            if(tagsProvider != null)
                            {
                                isTag = filterTags.isTag(tagsProvider);
                            }                            
                        }
                        
                        if(topicProvider != null)
                        {
                            SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                            if(sourceProvider != null)
                            {
                                Source source = sourceProvider.getSource();
                                if(source != null)
                                {
                                    TopicsProvider topicsProvider = source.getLookup().lookup(TopicsProvider.class);
                                    if(topicsProvider != null)
                                    {
                                        isTopic = topicProvider.isTopic(topicsProvider);
                                    }                                                 
                                }            
                            }                                                                                    
                        }
                        
                        if(goalProvider != null)
                        {
                            SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                            if(sourceProvider != null)
                            {
                                Source source = sourceProvider.getSource();
                                if(source != null)
                                {
                                    GoalsProvider goalsProvider = source.getLookup().lookup(GoalsProvider.class);
                                    if(goalsProvider != null)
                                    {
                                        isGoal = goalProvider.isGoal(goalsProvider);
                                    }                                                 
                                }            
                            }                                                                                    
                        }                        
                        
                        if(isTag && isTopic && isGoal)                    
                        {
                            sorted.add(data);                  
                        }                        
                    }
                } 
                if(provider.isReversed())
                {
                    sorted = sorted.reversed();
                }
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            } 
            setKeys(sorted); 
            
            /*
            SwingUtilities.invokeLater(() -> 
            {
                SortedSet<OpenPkmDataObject> data = new TreeSet<OpenPkmDataObject>(OpenPkmData.titleComparator());
                data.addAll(getData(provider));     
                setKeys(data);    
            });
            */
        }

        @Override
        protected void removeNotify()
        {
            provider.removeChangeListener(this);  
            
            if(filterTags != null) 
            {
                filterTags.removeChangeListener(this);
            }
            
            if(topicProvider instanceof ChangeSupportProvider csp)
            {
                csp.removeChangeListener(this);
            }   
            
            if(goalProvider instanceof ChangeSupportProvider csp)
            {
                csp.removeChangeListener(this);
            }              
            
            setKeys(Collections.<DataObject>emptySet());
        }

        @Override
        protected Node[] createNodes(DataObject data) 
        {
            return new Node[] {new FilterNode(data.getNodeDelegate())};
        }          

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            updateKeys();
        }                 
    }   
}
