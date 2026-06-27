/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionsProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.DisplayNameProvider.TextFormat;
import openpkm.base.IconProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.OpenIconProvider;
import openpkm.base.ShortDescriptionProvider;
import openpkm.base.Topic;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author rokor
 */
public class OpenProjectNode extends FilterNode implements ChangeListener
{
    private static final Logger LOG = Logger.getLogger(OpenProjectNode.class.getName());    
    
    private DisplayNameProvider displayNameProvider;
    private ShortDescriptionProvider shortDescriptionProvider;
    private IconProvider iconProvider;
    private OpenIconProvider openIconProvider;  
    private KnowledgeGraphProvider topicProvider;
    
    private final Project project;
    
    public OpenProjectNode(Node node, Project project) 
    {  
        super(node, NodeFactorySupport.createCompositeChildren(
                project,
                "Projects/openpkm-project/Nodes"),
                new ProxyLookup(
                        new Lookup[]{
                            Lookups.singleton(project),
                            node.getLookup()
                        }));  
        ProjectInformation info = ProjectUtils.getInformation(project);
        setName(info.getName());
        this.project = project;       
    }

    private String getDisplayName(TextFormat format)
    {
        if(displayNameProvider == null)
        {
            displayNameProvider = project.getLookup().lookup(DisplayNameProvider.class);
            if(displayNameProvider != null)
            {
                if(displayNameProvider instanceof ChangeSupportProvider provider)
                {
                    provider.addChangeListener(this);                    
                }                                
                return displayNameProvider.getDisplayName(format);                
            }
        } 
        else
        {
            return displayNameProvider.getDisplayName(format);             
        }
        return super.getDisplayName();        
    }

    private String getTopicsName(Collection<Topic> topics)
    {
        if(!topics.isEmpty())
        {
            StringJoiner joiner = new StringJoiner(", ");
            Iterator<Topic> iterator = topics.iterator();
            while(iterator.hasNext())
            {
                joiner.add(iterator.next().getName());
            } 

            LOG.info("Topics: " + joiner.toString());

            return joiner.toString();                
        }
        return null;
    }
    
    private String getTopicsName()
    {
        if(topicProvider == null)
        {
            topicProvider = project.getLookup().lookup(KnowledgeGraphProvider.class);
            if(topicProvider != null)
            {                
                if(topicProvider instanceof ChangeSupportProvider provider)
                {
                    provider.addChangeListener(this);                    
                }                                   
                return getTopicsName(topicProvider.getSelectedTopics());
            }             
        } 
        else
        {
            return getTopicsName(topicProvider.getSelectedTopics());
        }
        return null;
    }
    
    @Override
    public String getDisplayName() 
    {
        String topicsName = getTopicsName();
        if(topicsName != null)
        {
            return getDisplayName(TextFormat.PLAIN) + " [" + topicsName + "]";      
        }
        return getDisplayName(TextFormat.PLAIN);
    } 
    
    @Override
    public String getShortDescription()
    {
        if(shortDescriptionProvider == null)
        {
            shortDescriptionProvider = project.getLookup().lookup(ShortDescriptionProvider.class);
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
    public Image getIcon(int type) 
    {
        if(iconProvider == null)
        {
            iconProvider = project.getLookup().lookup(IconProvider.class);
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
            openIconProvider = project.getLookup().lookup(OpenIconProvider.class);
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
    public Action[] getActions(boolean arg0) 
    {
        ActionsProvider provider = project.getLookup().lookup(ActionsProvider.class);
        if(provider == null)
        {
            return new Action[]{
                CommonProjectActions.newFileAction(),
                CommonProjectActions.moveProjectAction(),
                CommonProjectActions.copyProjectAction(),
                CommonProjectActions.deleteProjectAction(),
                CommonProjectActions.customizeProjectAction(),
                CommonProjectActions.closeProjectAction()
            };            
        }
        List<Action> actions = new ArrayList();
        actions.add(CommonProjectActions.deleteProjectAction()); 
        actions.add(CommonProjectActions.customizeProjectAction()); 
        actions.add(CommonProjectActions.closeProjectAction()); 
        actions.addAll(provider.getActions());
        return actions.toArray(new Action[actions.size()]);
    } 
    
    @Override
    public Action getPreferredAction() 
    {
        TopComponentProvider provider = project.getLookup().lookup(TopComponentProvider.class);
        if(provider != null)
        {
            return new OpenTopComponentAction(provider);
        }
        return null;
    }     

    @Override
    public void stateChanged(ChangeEvent evt) 
    {
        if(evt.getSource() == displayNameProvider || evt.getSource() == topicProvider)
        {
            fireDisplayNameChange(null, getDisplayName());
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

    private static final class OpenTopComponentAction extends AbstractAction 
    {
        private final TopComponentProvider provider;

        public OpenTopComponentAction(TopComponentProvider provider) 
        {
            super("Open in browser");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            /*
            SwingUtilities.invokeLater(() -> 
            {
                getTopComponent().open();   
            });
            */
            provider.getTopComponent().open();
            provider.getTopComponent().requestActive();            
        }          
    }      
}
