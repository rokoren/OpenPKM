/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import com.google.api.client.util.DateTime;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.DomainsProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.youtube.YouTubeChannel;
import openpkm.youtube.YouTubeProjectWizardPanel1;
import openpkm.youtube.YouTubeProjectWizardPanel2;
import openpkm.youtube.YouTubeService;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "OpenPKM/Domain",
        id = "openpkm.core.YouTubeChannelAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/youtube_channel.png",
        displayName = "#CTL_YouTubeChannelAction"
)
@Messages("CTL_YouTubeChannelAction=Add YouTube Channel")
public class YouTubeChannelAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(YouTubeChannelAction.class.getName());    
    
    private final DomainsProvider provider;

    public YouTubeChannelAction(DomainsProvider provider) 
    {
        this.provider = provider;
    }

    @Override
    public void actionPerformed(ActionEvent evt) 
    {         
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new YouTubeProjectWizardPanel1());
        panels.add(new YouTubeProjectWizardPanel2());
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
        wiz.setTitle("Add YouTube Channel");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            String channelID = (String) wiz.getProperty(YouTubeChannel.PROP_CHANNEL_ID);
            String title = (String) wiz.getProperty(YouTubeChannel.PROP_TITLE);
            String description = (String) wiz.getProperty(YouTubeChannel.PROP_DESCRIPTION);            
            String thumbnail = (String) wiz.getProperty(YouTubeChannel.PROP_THUMBNAIL);
            DateTime publishedAt = (DateTime) wiz.getProperty(YouTubeChannel.PROP_PUBLISHED_AT);
            String customUrl = (String) wiz.getProperty(YouTubeChannel.PROP_CUSTOM_URL);
            String country = (String) wiz.getProperty(YouTubeChannel.PROP_COUNTRY);
            String localizedTitle = (String) wiz.getProperty(YouTubeChannel.PROP_LOCALIZED_TITLE);
            String localizedDescription = (String) wiz.getProperty(YouTubeChannel.PROP_LOCALIZED_DESCRIPTION);
            BigInteger viewCount = (BigInteger) wiz.getProperty(YouTubeChannel.PROP_VIEW_COUNT);
            BigInteger subscriberCount = (BigInteger) wiz.getProperty(YouTubeChannel.PROP_SUBSCRIBER_COUNT);
            BigInteger videoCount = (BigInteger) wiz.getProperty(YouTubeChannel.PROP_VIDEO_COUNT);
            BigInteger commentCount = (BigInteger) wiz.getProperty(YouTubeChannel.PROP_COMMENT_COUNT);    
            String privacyStatus = (String) wiz.getProperty(YouTubeChannel.PROP_PRIVACY_STATUS);
            List<String> topicCategories = (List<String>) wiz.getProperty(YouTubeChannel.PROP_TOPIC_CATEGORIES);
            
            String googleKey = (String) wiz.getProperty(YouTubeService.PROP_GOOGLE_KEY);
            List<Topic> topics = (List<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);            
            
            Properties props = new Properties();
            props.setProperty(YouTubeChannel.PROP_CHANNEL_ID, channelID); 
            props.setProperty(YouTubeChannel.PROP_TITLE, title);       
            props.setProperty(YouTubeChannel.PROP_DESCRIPTION, description);
            props.setProperty(YouTubeChannel.PROP_THUMBNAIL, thumbnail);
            props.setProperty(YouTubeChannel.PROP_PUBLISHED_AT, publishedAt.toStringRfc3339());
            props.setProperty(YouTubeChannel.PROP_CUSTOM_URL, customUrl);
            props.setProperty(YouTubeChannel.PROP_COUNTRY, country);
            props.setProperty(YouTubeChannel.PROP_LOCALIZED_TITLE, localizedTitle);
            props.setProperty(YouTubeChannel.PROP_LOCALIZED_DESCRIPTION, localizedDescription);
            props.setProperty(YouTubeChannel.PROP_VIEW_COUNT, viewCount.toString());
            props.setProperty(YouTubeChannel.PROP_SUBSCRIBER_COUNT, subscriberCount.toString());
            props.setProperty(YouTubeChannel.PROP_VIDEO_COUNT, videoCount.toString());
            if(commentCount != null)
            {
                props.setProperty(YouTubeChannel.PROP_COMMENT_COUNT, commentCount.toString());
            }
            props.setProperty(YouTubeChannel.PROP_PRIVACY_STATUS, privacyStatus);
            if(topicCategories != null)
            {
                StringJoiner joiner = new StringJoiner(",");
                for(String topicCategory : topicCategories)
                {
                    joiner.add(topicCategory);
                }            
                props.setProperty(YouTubeChannel.PROP_TOPIC_CATEGORIES, joiner.toString());                
            }
            
            if(!topics.isEmpty())
            {
                StringJoiner joiner = new StringJoiner(",");
                for(Topic topic : topics)
                {
                    joiner.add(topic.getTopicID());
                }
                props.setProperty(TopicsProvider.PROP_TOPICS, joiner.toString()); 
            }

            try
            {  
                FileObject projectDirectory = FileUtil.createFolder(provider.getRootDirectory(), channelID);           
                FileObject projectFolder = FileUtil.createFolder(projectDirectory, YouTubeChannelProjectFactory.PROJECT_FOLDER);                   

                OutputStream os = projectFolder.createAndOpen(YouTubeChannelProjectFactory.PROJECT_FILE);
                props.store(os, "OpenPKM YouTube Channel Project"); 
                os.close();                

                StatusDisplayer.getDefault().setStatusText("OpenPKM YouTube Channel Project saved: " + title);  
                
                Project project = ProjectManager.getDefault().findProject(projectDirectory);
                if(project != null)
                {
                    Project[] projects = {project};
                    OpenProjects.getDefault().open(projects, false);                             
                }                 
            }
            catch(IOException e) 
            {
                LOG.warning(e.getMessage());
            }                                                          
        } 
    }        
}
