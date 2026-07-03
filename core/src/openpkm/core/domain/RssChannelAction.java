/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndFeed;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import openpkm.rss.RssChannel;
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
        id = "openpkm.core.domain.RssChannelAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/feed.png",
        displayName = "#CTL_RssChannelAction"
)
@Messages("CTL_RssChannelAction=Add RSS Channel")
public class RssChannelAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(RssChannelAction.class.getName());     
    
    private final DomainsProvider provider;

    public RssChannelAction(DomainsProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new RssChannelWizardPanel1());
        panels.add(new RssChannelWizardPanel2());
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
        wiz.setTitle("Add RSS Channel");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
            
            String rssUrl = (String) wiz.getProperty(RssChannel.PROP_RSS_URL);
            String rssFile = (String) wiz.getProperty(RssChannelProject.PROP_RSS_FILE);
            String title = (String) wiz.getProperty(TitleProvider.PROP_TITLE);
            String description = (String) wiz.getProperty(DescriptionProvider.PROP_DESCRIPTION);  
            List<Topic> topics = (List<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);  
            SyndFeed feed = (SyndFeed) wiz.getProperty("feed");                           
            
         
                                                                                             
            Properties props = new Properties();
            props.setProperty(RssChannel.PROP_RSS_URL, rssUrl); 
            props.setProperty(RssChannelProject.PROP_RSS_FILE, rssFile);
            props.setProperty(Domain.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
            props.setProperty(RssChannel.PROP_TITLE, title);       
            props.setProperty(RssChannel.PROP_DESCRIPTION, description);            
            props.setProperty(RssChannel.PROP_LINK, feed.getLink());
            if(feed.getUri() != null)
            {
                props.setProperty(RssChannel.PROP_URI, feed.getUri());                
            }
            props.setProperty(RssChannel.PROP_AUTHOR, feed.getAuthor());
            props.setProperty(RssChannel.PROP_COPYRIGHT, feed.getCopyright());
            props.setProperty(RssChannel.PROP_GENERATOR, feed.getGenerator());
            if(feed.getLanguage() != null)
            {
                props.setProperty(RssChannel.PROP_LANGUAGE, feed.getLanguage());                
            }
            props.setProperty(RssChannel.PROP_MANAGING_EDITOR, feed.getManagingEditor());
            props.setProperty(RssChannel.PROP_IMAGE, feed.getImage().getUrl());
            if(feed.getIcon() != null)
            {
                props.setProperty(RssChannel.PROP_ICON, feed.getIcon().getUrl());                            
            }

            if(feed.getCategories() != null)
            {
                StringJoiner joiner = new StringJoiner(",");
                for(SyndCategory category : feed.getCategories())
                {
                    joiner.add(category.getLabel());
                }
                props.setProperty(RssChannel.PROP_CATEGORY, joiner.toString());                 
            }
            
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
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(rssUrl.getBytes(StandardCharsets.UTF_8));                                  
                StringBuilder hex = new StringBuilder();
                for (byte b : hash) {
                    hex.append(String.format("%02x", b));
                }
                String domainID = hex.toString();                 
                
                FileObject projectDirectory = FileUtil.createFolder(provider.getRootDirectory(), domainID);           
                FileObject projectFolder = FileUtil.createFolder(projectDirectory, RssChannelProjectFactory.PROJECT_FOLDER);                   

                OutputStream os = projectFolder.createAndOpen(RssChannelProjectFactory.PROJECT_FILE);
                props.store(os, "OpenPKM RSS Channel Project"); 
                os.close(); 
                                
                StatusDisplayer.getDefault().setStatusText("OpenPKM RSS Channel Project saved: " + title); 

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
            catch(NoSuchAlgorithmException e) 
            {
                LOG.warning(e.getMessage());
            }            
        }        
    } 
}
