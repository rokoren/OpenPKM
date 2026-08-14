/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.utils.Utils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;
import openpkm.base.ProjectManagementProvider;
import openpkm.base.ProjectManagement;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/ProjectManagement",
        id = "openpkm.trello.TrelloBoardAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/trello.png",
        displayName = "#CTL_TrelloBoardAction"
)
@Messages("CTL_TrelloBoardAction=Add Trello Board")
public class TrelloBoardAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(TrelloBoardAction.class.getName());     
    
    private final ProjectManagementProvider provider;

    public TrelloBoardAction(ProjectManagementProvider provider)
    {
        this.provider = provider;
    }   
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {                
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new TrelloBoardWizardPanel1());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) 
        {
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
        wiz.setTitle("Add Trello Board ");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        {
            LocalDateTime now = LocalDateTime.now();
            
            Properties props = Utils.getProperties(wiz);
            String boardID = props.getProperty(TrelloProject.PROP_BOARD_ID);
            if(boardID != null)
            {
                props.setProperty(ProjectManagement.PROP_APP_ID, Utils.getAppID());
                props.setProperty(ProjectManagement.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));  

                try
                {  
                    FileObject projectDirectory = FileUtil.createFolder(provider.getRootDirectory(), boardID);           
                    FileObject projectFolder = FileUtil.createFolder(projectDirectory, TrelloProjectFactory.PROJECT_FOLDER);                   

                    OutputStream os = projectFolder.createAndOpen(TrelloProjectFactory.PROJECT_FILE);
                    props.store(os, "OpenPKM Trello Project"); 
                    os.close(); 

                    StatusDisplayer.getDefault().setStatusText("OpenPKM Trello Project saved: " + boardID); 

                    Project project = ProjectManager.getDefault().findProject(projectDirectory);
                    if(project != null)
                    {
                        ProjectManagement notebook = project.getLookup().lookup(ProjectManagement.class);
                        if(notebook != null)
                        {
                            provider.addProject(notebook);
                            /*
                            Project[] projects = {domain};
                            OpenProjects.getDefault().open(projects, false);   
                            */
                        }
                    }                  
                }
                catch(IOException e) 
                {
                    LOG.warning(e.getMessage());
                }                 
            }                        
        }       
    }     
}
