/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.raindrop;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import openpkm.base.DescriptionProvider;
import openpkm.base.TitleProvider;
import openpkm.utils.DescriptionWizardPanel;
import openpkm.utils.RootProjectWizardPanel1;
import openpkm.utils.Utils;
import openpkm.neo4j.Neo4jInstance;
import openpkm.raindrop.RaindropAccount;
import openpkm.raindrop.RaindropCollection;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public class RaindropCollectionNode extends AbstractNode
{
    @StaticResource()
    public static final String BANNER = "openpkm/core/resources/raindrop128.png";  
    
    private static final Logger LOG = Logger.getLogger(RaindropCollectionNode.class.getName());      
    
    private final RaindropAccount account;
    private final RaindropCollection collection;
    
    public RaindropCollectionNode(RaindropAccount account, RaindropCollection collection) 
    {
        super(Children.LEAF);
        setName(collection.getCollectionID() + "");
        setDisplayName(collection.getTitle());
        setShortDescription(collection.getDescription());
        this.account = account;
        this.collection = collection;
    }  
    
    private Image getIcon(boolean opened)
    {
        try 
        {
            BufferedImage image = collection.getImage();
            if(image != null)
            {
                return Utils.resizeImage(image, 16, 16);
            }
        } 
        catch (IOException e) 
        {
            LOG.warning(e.getMessage());
        }
        return Utils.getTreeFolderIcon(opened);        
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
    public Action[] getActions(boolean context) 
    {
        if(collection.isPublic())
        {
            return new Action[]
            {
                new NewProjectAction()
            };            
        }
        return new Action[0];
    }
    
    private final class NewProjectAction extends AbstractAction
    {
        public NewProjectAction() 
        {
            super("New OpenPKM Raindrop Project");
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new RootProjectWizardPanel1());
            panels.add(new DescriptionWizardPanel());
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
            WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle("New OpenPKM Root Project");
            wiz.putProperty("WizardPanel_image", Utils.getWizardImage(ImageUtilities.loadImage(BANNER), 128, 128));            
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {
                String title = (String)wiz.getProperty(TitleProvider.PROP_TITLE);                   
                Neo4jInstance neo4j = (Neo4jInstance)wiz.getProperty("neo4j");
                String description = (String)wiz.getProperty(DescriptionProvider.PROP_DESCRIPTION);   
                String location = (String)wiz.getProperty("location");                  

                Properties props = new Properties();
                props.setProperty(RaindropProject.PROP_TITLE, title);     
                props.setProperty(RaindropProject.PROP_NEO4J_INSTANCE_ID, neo4j.getInstanceID()); 
                props.setProperty(RaindropProject.PROP_DESCRIPTION, description);
                props.setProperty(RaindropProject.PROP_RAINDROP_USER_ID, account.getUser().getUserID() + "");   
                props.setProperty(RaindropProject.PROP_RAINDROP_COLLECTION_ID, collection.getCollectionID() + ""); 
                props.setProperty(RaindropProject.PROP_RAINDROP_COLLECTION_ROOT, Boolean.TRUE.toString()); 
                
                try
                {             
                    File projectDirectory = new File(location, title.toLowerCase());
                    projectDirectory.mkdir();            
                    File projectFolder = new File(projectDirectory, RaindropProjectFactory.PROJECT_FOLDER);
                    projectFolder.mkdir();
                    File projectFile = new File(projectFolder, RaindropProjectFactory.PROJECT_FILE);                       

                    OutputStream os = new FileOutputStream(projectFile);
                    props.store(os, "OpenPKM Root Project"); 
                    os.close();
                    
                    Project project = ProjectManager.getDefault().findProject(FileUtil.toFileObject(projectDirectory));
                    if(project != null)
                    {
                        Project[] projects = {project};
                        StatusDisplayer.getDefault().setStatusText("Opening OpenPKM Root Project: " + title);
                        OpenProjects.getDefault().open(projects, false);                             
                    }               
                }
                catch (IOException e) 
                {
                    LOG.warning(e.getMessage());
                }                 
            }
        }         
    }    
}
