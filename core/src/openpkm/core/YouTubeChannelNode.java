/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.IconProvider;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
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
public class YouTubeChannelNode extends FilterNode implements ChangeListener, PropertyChangeListener
{
    private final YouTubeChannelProject project;
    
    public YouTubeChannelNode(Node node, YouTubeChannelProject project) 
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
        project.addPropertyChangeListener(this);
        
        IconProvider iconProvider = project.getLookup().lookup(IconProvider.class);
        if(iconProvider != null)
        {
            iconProvider.addChangeListener(this);
        }         
    }

    @Override
    public String getDisplayName() 
    {
        return project.getTitle();
    } 
    
    @Override
    public String getShortDescription()
    {
        return project.getDescription();
    }    
    
    @Override
    public Image getIcon(int type) 
    {
        IconProvider provider = project.getLookup().lookup(IconProvider.class);
        if(provider != null)
        {
            return provider.getIcon();
        }
        
        ProjectInformation info = ProjectUtils.getInformation(project);        
        return ImageUtilities.icon2Image(info.getIcon());
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
    
    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{
            CommonProjectActions.newFileAction(),
            CommonProjectActions.moveProjectAction(),
            CommonProjectActions.copyProjectAction(),
            CommonProjectActions.deleteProjectAction(),
            CommonProjectActions.customizeProjectAction(),
            CommonProjectActions.closeProjectAction()            
        };
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
    public void stateChanged(ChangeEvent e) 
    {
        fireIconChange();                        
    }     
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if(evt.getPropertyName().equals(YouTubeChannelProject.PROP_TITLE))
        {
            fireDisplayNameChange((String)evt.getOldValue(), (String)evt.getNewValue());
        }
        else if(evt.getPropertyName().equals(YouTubeChannelProject.PROP_DESCRIPTION))
        {
            fireShortDescriptionChange((String)evt.getOldValue(), (String)evt.getNewValue());
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
