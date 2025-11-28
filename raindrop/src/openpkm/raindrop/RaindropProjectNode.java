/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.DescriptionProvider;
import openpkm.base.IconProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Rok Koren
 */
public class RaindropProjectNode extends FilterNode implements ChangeListener, PropertyChangeListener
{
    private static final Logger LOG = Logger.getLogger(RaindropProjectNode.class.getName());          
    
    final RaindropProject project;

    public RaindropProjectNode(Node node, RaindropProject project) throws DataObjectNotFoundException {
        super(node, NodeFactorySupport.createCompositeChildren(
                project,
                "Projects/openpkm-project/Nodes"),
                new ProxyLookup(
                        new Lookup[]{
                            Lookups.singleton(project),
                            node.getLookup()
                        }));
        this.project = project;        
        project.addPropertyChangeListener(this);
        
        IconProvider iconProvider = project.getLookup().lookup(IconProvider.class);
        if(iconProvider != null)
        {
            iconProvider.addChangeListener(this);
        }  
        
        KnowledgeGraphProvider knowledgeGraphProvider = project.getLookup().lookup(KnowledgeGraphProvider.class); 
        if(knowledgeGraphProvider != null)
        {
            knowledgeGraphProvider.addChangeListener(this);
        }          
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{
            CommonProjectActions.newFileAction(),
            CommonProjectActions.copyProjectAction(),
            CommonProjectActions.deleteProjectAction(),
            CommonProjectActions.customizeProjectAction(),
            CommonProjectActions.closeProjectAction()
        };
    }

    @Override
    public Image getIcon(int type) 
    {
        IconProvider provider = project.getLookup().lookup(IconProvider.class);
        if(provider != null)
        {
            return provider.getIcon();
        }
        return ImageUtilities.loadImage(Raindrop.ICON);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getDisplayName() 
    {
        KnowledgeGraphProvider provider = project.getLookup().lookup(KnowledgeGraphProvider.class);
        if(provider != null)
        {
            Collection<Topic> topics = provider.getSelectedTopics();
            if(!topics.isEmpty())
            {
                StringJoiner joiner = new StringJoiner(", ");
                Iterator<Topic> iterator = topics.iterator();
                while(iterator.hasNext())
                {
                    joiner.add(iterator.next().getName());
                } 
                
                LOG.info("Node display name: " + project.getTitle() + " [" + joiner.toString() + "]");
                
                return project.getTitle() + " [" + joiner.toString() + "]";                
            }
        }
        return project.getTitle();
    } 

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if(evt.getPropertyName().equals(TitleProvider.PROP_TITLE))
        {
            fireDisplayNameChange((String)evt.getOldValue(), (String)evt.getNewValue());
        }
        else if(evt.getPropertyName().equals(DescriptionProvider.PROP_DESCRIPTION))
        {
            fireShortDescriptionChange((String)evt.getOldValue(), (String)evt.getNewValue());
        }              
    }      
    
    @Override
    public void stateChanged(ChangeEvent e) 
    {
        if(e.getSource() instanceof KnowledgeGraphProvider)
        {
            fireDisplayNameChange(null, getDisplayName());              
        }
        else
        {
            fireIconChange();            
        }                       
    }     
}
