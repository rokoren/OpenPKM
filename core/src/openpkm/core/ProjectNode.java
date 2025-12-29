/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.BulletIconProvider;
import openpkm.base.DescriptionProvider;
import openpkm.base.TitleProvider;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class ProjectNode extends AbstractNode implements ChangeListener
{
    private static final Logger LOG = Logger.getLogger(ProjectNode.class.getName());              
    
    private final Project project;  
    
    public ProjectNode(Project project)
    {
        super(Children.LEAF, Lookups.proxy(project));
        ProjectInformation info = ProjectUtils.getInformation(project);
        setName(info.getName());
        this.project = project;
        BulletIconProvider provider = project.getLookup().lookup(BulletIconProvider.class);
        if(provider != null)
        {
            provider.addChangeListener(this);
        }
    } 
    
    @Override
    public String getDisplayName() 
    {
        TitleProvider provider = project.getLookup().lookup(TitleProvider.class);
        if(provider != null)
        {
            return provider.getTitle();
        }        
        ProjectInformation info = ProjectUtils.getInformation(project);
        return info.getDisplayName();
    } 
    
    @Override
    public String getShortDescription()
    {
        DescriptionProvider provider = project.getLookup().lookup(DescriptionProvider.class);
        if(provider != null)
        {
            return provider.getDescription();
        }
        return null;
    }
    
    @Override
    public Image getIcon(int type) 
    {
        ProjectInformation info = ProjectUtils.getInformation(project);
        BulletIconProvider provider = project.getLookup().lookup(BulletIconProvider.class);
        if(provider != null)
        {
            Image bullet = provider.getBullet();
            if(bullet != null)
            {
                return ImageUtilities.mergeImages(ImageUtilities.icon2Image(info.getIcon()), bullet, 8, -4);                
            }
        }        
        return ImageUtilities.icon2Image(info.getIcon());                        
    }    
    
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new OpenProjectAction(project)
        }; 
    }
    
    @Override
    public Action getPreferredAction() 
    {
        return new OpenProjectAction(project);
    }     
    
    @Override
    public void stateChanged(ChangeEvent e) 
    {
        fireIconChange();
    }

    private static final class OpenProjectAction extends AbstractAction 
    {
        private final Project project;

        public OpenProjectAction(Project project) 
        {
            super("Open Project");
            this.project = project;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            TitleProvider provider = project.getLookup().lookup(TitleProvider.class);
            if(provider != null)
            {
                StatusDisplayer.getDefault().setStatusText("Opening: " + provider.getTitle());
            }                          
            Project[] projects = {project};
            OpenProjects.getDefault().open(projects, false, true);
        }          
    }  
}
