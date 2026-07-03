/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

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
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.DescriptionProvider;
import openpkm.base.Domain;
import openpkm.base.DomainsProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
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

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/Domain",
        id = "openpkm.core.LinkedInAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/home_page.png",
        displayName = "#CTL_LinkedInAction"
)
@Messages("CTL_LinkedInAction=Add LinkedIn")
public class LinkedInAction implements ActionListener
{    
    private static final Logger LOG = Logger.getLogger(LinkedInAction.class.getName());  
             
    private final DomainsProvider provider;

    public LinkedInAction(DomainsProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new LinkedInWizardPanel1());
        panels.add(new LinkedInWizardPanel2());
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
        wiz.setTitle("Add LinkedIn");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
            
            String username = (String) wiz.getProperty(LinkedInProject.PROP_USER_NAME);
            String title = (String) wiz.getProperty(TitleProvider.PROP_TITLE);
            String description = (String) wiz.getProperty(DescriptionProvider.PROP_DESCRIPTION);              
            List<Topic> topics = (List<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);                  
                       
            Properties props = new Properties();
            props.setProperty(Domain.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));            
            props.setProperty(LinkedInProject.PROP_USER_NAME, username);
            props.setProperty(TitleProvider.PROP_TITLE, title);       
            props.setProperty(DescriptionProvider.PROP_DESCRIPTION, description);   
            
            if(topics != null)
            {
                KnowledgeGraphProvider knowledgeGraphProvider = provider.getProvider().getLookup().lookup(KnowledgeGraphProvider.class);
                if(knowledgeGraphProvider != null)
                {
                    StringJoiner joiner = new StringJoiner(",");
                    for(Topic topic : topics)
                    {
                        joiner.add(knowledgeGraphProvider.getTreeID(topic));
                    }
                    props.setProperty(TopicsProvider.PROP_TOPICS, joiner.toString());                    
                }
            }  

            try
            {  
                FileObject projectDirectory = FileUtil.createFolder(provider.getRootDirectory(), username);           
                FileObject projectFolder = FileUtil.createFolder(projectDirectory, LinkedInProjectFactory.PROJECT_FOLDER);                   

                OutputStream os = projectFolder.createAndOpen(LinkedInProjectFactory.PROJECT_FILE);
                props.store(os, "OpenPKM LinkedIn Project"); 
                os.close(); 
                                
                StatusDisplayer.getDefault().setStatusText("OpenPKM LinkedIn Project saved: " + title); 

                Project project = ProjectManager.getDefault().findProject(projectDirectory);
                if(project != null)
                {
                    Domain domain = project.getLookup().lookup(Domain.class);
                    if(domain != null)
                    {
                        provider.addDomain(domain);
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
