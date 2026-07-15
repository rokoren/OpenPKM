/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/contextAction.java to edit this template
 */
package openpkm.core.youtube;

import openpkm.youtube.YouTubeWizardPanel1;
import com.google.api.client.util.DateTime;
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
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.FileTypeProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.core.TopicWizardPanel;
import openpkm.reference.Reference;
import openpkm.utils.Utils;
import openpkm.youtube.YouTubeVideoProvider;
import openpkm.youtube.YouTubeVideo;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle.Messages;
import openpkm.youtube.YouTubeVideoFactory;

@ActionID(
        category = "OpenPKM/Video",
        id = "openpkm.core.youtube.YouTubeVideoAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/youtube_video.png",
        displayName = "#CTL_YouTubeVideoAction"
)
@Messages("CTL_YouTubeVideoAction=Add YouTube Video")
public final class YouTubeVideoAction implements ActionListener 
{
    private static final Logger LOG = Logger.getLogger(YouTubeVideoAction.class.getName());      
    
    private final YouTubeVideoProvider provider;

    public YouTubeVideoAction(YouTubeVideoProvider provider) 
    {
        this.provider = provider;
    }

    @Override
    public void actionPerformed(ActionEvent ev) 
    {        
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new YouTubeWizardPanel1());
        panels.add(new TopicWizardPanel());
        /*
        panels.add(new YouTubeWizardPanel2());
        panels.add(new YouTubeWizardPanel3());
        */
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
        wiz.setTitle("Add YouTube Video");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
            
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
            VisibilityProvider.Modifier visibiltyModifier = (VisibilityProvider.Modifier) wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);       
            String videoID = (String) wiz.getProperty(YouTubeVideo.PROP_VIDEO_ID);
            String title = (String) wiz.getProperty(YouTubeVideo.PROP_VIDEO_TITLE);
            DateTime publishedAt = (DateTime) wiz.getProperty(YouTubeVideo.PROP_PUBLISHED_AT);
            String duration = (String) wiz.getProperty(YouTubeVideo.PROP_DURATION);
            String caption = (String) wiz.getProperty(YouTubeVideo.PROP_CAPTION);
            String desc = (String) wiz.getProperty(YouTubeVideo.PROP_DESCRIPTION);
            String channelID = (String) wiz.getProperty(YouTubeVideo.PROP_CHANNEL_ID);
            String channelTitle = (String) wiz.getProperty(YouTubeVideo.PROP_CHANNEL_TITLE);
            String defaultLanguage = (String) wiz.getProperty(YouTubeVideo.PROP_DEFAULT_LANGUAGE);
            String defaultAudioLanguage = (String) wiz.getProperty(YouTubeVideo.PROP_DEFAULT_AUDIO_LANGUAGE);
            String liveBroadcastContent = (String) wiz.getProperty(YouTubeVideo.PROP_LIVEBROADCAST_CONTENT);
            String thumbnailDefault = (String) wiz.getProperty(YouTubeVideo.PROP_THUMBNAIL_DEFAULT);
            String thumbnailMedium = (String) wiz.getProperty(YouTubeVideo.PROP_THUMBNAIL_MEDIUM);
            String thumbnailHigh = (String) wiz.getProperty(YouTubeVideo.PROP_THUMBNAIL_HIGH);
            String thumbnailStandard = (String) wiz.getProperty(YouTubeVideo.PROP_THUMBNAIL_STANDARD);
            List<String> tags = (List<String>) wiz.getProperty(TagsProvider.PROP_TAGS);
            Set<Topic> topics = (Set<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);
            List<String> youTubeTags = (List<String>) wiz.getProperty(YouTubeVideo.PROP_YOUTUBE_TAGS);        

            Properties props = new Properties();
            props.setProperty(Reference.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));             
            props.setProperty(YouTubeVideo.PROP_APP_ID, Utils.getAppID());
            props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, visibiltyModifier.toString());                 
            props.setProperty(YouTubeVideo.PROP_VIDEO_ID, videoID);
            props.setProperty(YouTubeVideo.PROP_VIDEO_TITLE, title);
            props.setProperty(YouTubeVideo.PROP_CHANNEL_ID, channelID); 
            props.setProperty(YouTubeVideo.PROP_CHANNEL_TITLE, channelTitle);
            if (publishedAt != null)
            {
                props.setProperty(YouTubeVideo.PROP_PUBLISHED_AT, publishedAt.toStringRfc3339());  
            }
            if (duration != null)
            {
                props.setProperty(YouTubeVideo.PROP_DURATION, duration);  
            }
            if (desc != null)
            {
                props.setProperty(YouTubeVideo.PROP_DESCRIPTION, desc);            
            }
            if (defaultLanguage != null)
            {
                props.setProperty(YouTubeVideo.PROP_DEFAULT_LANGUAGE, defaultLanguage);
            }   
            if (defaultAudioLanguage != null)
            {
                props.setProperty(YouTubeVideo.PROP_DEFAULT_AUDIO_LANGUAGE, defaultAudioLanguage);
            }  
            if (liveBroadcastContent != null)
            {
                props.setProperty(YouTubeVideo.PROP_LIVEBROADCAST_CONTENT, liveBroadcastContent);
            }  
            if(caption != null)
            {
                props.setProperty(YouTubeVideo.PROP_CAPTION, caption);
            }
            if(thumbnailDefault != null)
            {
                props.setProperty(YouTubeVideo.PROP_THUMBNAIL_DEFAULT, thumbnailDefault);
            }
            if(thumbnailHigh != null)
            {
                props.setProperty(YouTubeVideo.PROP_THUMBNAIL_HIGH, thumbnailHigh);
            } 
            if(thumbnailMedium != null)
            {
                props.setProperty(YouTubeVideo.PROP_THUMBNAIL_MEDIUM, thumbnailMedium);
            }
            if(thumbnailStandard != null)
            {
                props.setProperty(YouTubeVideo.PROP_THUMBNAIL_STANDARD, thumbnailStandard);
            }
            if(tags != null)
            {
                StringJoiner joiner = new StringJoiner(",");
                for(String tag : tags)
                {
                    joiner.add(tag);
                }
                props.setProperty(TagsProvider.PROP_TAGS, joiner.toString());
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
            
            if(youTubeTags != null)
            {
                StringJoiner joiner = new StringJoiner(",");
                for(String tag : youTubeTags)
                {
                    joiner.add(tag);
                }
                props.setProperty(YouTubeVideo.PROP_YOUTUBE_TAGS, joiner.toString());
            }        

            FileObject root = provider.getRootFolder();
            if(root != null)
            {
                YouTubeVideo video = provider.getFactory().getVideo(props, YouTubeVideoFactory.Type.STANDARD);
                try
                {
                    FileObject file = provider.createData(video, fileType); 
                    OutputStream os = root.createAndOpen(video.getVideoID() + "." + PropertiesProvider.EXTENSION);  
                    provider.getFactory().save(video, os, "New YouTube Video Created by Wizard");
                    os.close();  

                    StatusDisplayer.getDefault().setStatusText("YouTube video saved with title: " + title);                         

                    NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to open YouTube video in editor?", title, NotifyDescriptor.YES_NO_OPTION);
                    if(DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION)
                    {
                        try
                        {
                            DataObject data = DataObject.find(file);
                            OpenCookie open = data.getCookie(OpenCookie.class);
                            open.open();                            
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.warning(e.getMessage());
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
