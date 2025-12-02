/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/contextAction.java to edit this template
 */
package openpkm.core;

import openpkm.youtube.YouTubeDownloadWizardPanel;
import openpkm.youtube.YouTubeWizardPanel1;
import openpkm.youtube.YouTubeWizardPanel3;
import openpkm.youtube.YouTubeWizardPanel2;
import com.github.felipeucelli.javatube.Youtube;
import com.google.api.client.util.DateTime;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.FileTypeIndependent;
import openpkm.base.FileTypeProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.Reference;
import openpkm.base.ReferenceProvider;
import openpkm.base.ReferenceSourceProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.base.Video;
import openpkm.base.VisibilityProvider;
import openpkm.youtube.YouTubeDownloadWizardPanel.DownloadType;
import openpkm.utils.Utils;
import openpkm.youtube.YouTubeSourceProvider;
import openpkm.youtube.YouTubeVideo;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "OpenPKM/Video",
        id = "openpkm.core.YouTubeVideoAction"
)
@ActionRegistration(
        iconBase = "openpkm/youtube/resources/logo.png",
        displayName = "#CTL_YouTubeVideoAction"
)
@Messages("CTL_YouTubeVideoAction=Add YouTube Video")
public final class YouTubeVideoAction implements ActionListener 
{
    private static final Logger LOG = Logger.getLogger(YouTubeVideoAction.class.getName());  
    
    private static final RequestProcessor RP = new RequestProcessor("YouTubeDownload", 10);    
    
    private final YouTubeSourceProvider provider;

    public YouTubeVideoAction(YouTubeSourceProvider provider) 
    {
        this.provider = provider;
    }

    @Override
    public void actionPerformed(ActionEvent ev) 
    {
        ReferenceSourceProvider referenceProvider = provider.getProvider().getLookup().lookup(ReferenceSourceProvider.class); 
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new YouTubeWizardPanel1());
        if(referenceProvider != null)
        {
            panels.add(new YouTubeDownloadWizardPanel());
        }
        panels.add(new YouTubeWizardPanel2());
        panels.add(new YouTubeWizardPanel3());
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
        showWizard(provider, referenceProvider, wiz);
    }
    
    public static void showWizard(YouTubeSourceProvider provider, ReferenceSourceProvider referenceProvider, WizardDescriptor wiz)
    {
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        {             
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
            VisibilityProvider.Modifier visibiltyModifier = (VisibilityProvider.Modifier) wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);       
            String videoID = (String) wiz.getProperty(YouTubeVideo.PROP_VIDEO_ID);
            DownloadType downloadType = (DownloadType) wiz.getProperty(YouTubeDownloadWizardPanel.PROP_DOWNLOAD_TYPE);
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
            List<Topic> topics = (List<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);
            List<String> youTubeTags = (List<String>) wiz.getProperty(YouTubeVideo.PROP_YOUTUBE_TAGS);        

            Properties props = new Properties(); 
            props.setProperty(YouTubeVideo.PROP_APP_ID, Utils.getAppID());
            props.setProperty(FileTypeIndependent.PROP_DATA_FILE_EXTENSION, fileType.getExtension());
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

            if(downloadType != null && downloadType != DownloadType.NONE)
            {
                Video.Resolution resolution = (Video.Resolution) wiz.getProperty(YouTubeDownloadWizardPanel.PROP_DOWNLOAD_RESOLUTION);
                props.setProperty(ReferenceProvider.PROP_TYPE, ReferenceProvider.Type.VIDEO.getName());                    
                props.setProperty(TitleProvider.PROP_TITLE, title);                     
                props.setProperty(Reference.PROP_FILE_NAME, videoID); 
                props.setProperty(Reference.PROP_FILE_EXT, "mp4");
                props.setProperty(Reference.PROP_FILE_PATH, videoID + ".mp4");      
                
                YouTubeDownload downloader = new YouTubeDownload(props, referenceProvider, resolution, videoID, title, fileType);
                RP.post(downloader);                
            }  
            else
            {
                FileObject folder = provider.getRootFolder();
                if(folder != null)
                {
                    try
                    { 
                        OutputStream os = folder.createAndOpen(videoID + "." + PropertiesProvider.EXTENSION);
                        props.store(os, "New YouTube Video Created by Wizard"); 
                        os.close();                           
                        StatusDisplayer.getDefault().setStatusText("YouTube video saved with title: " + title);             
                    }
                    catch(IOException e) 
                    {
                        LOG.warning(e.getMessage());
                    }                     
                }                
            }                                             
        }          
    }    

    private static class YouTubeDownload extends Thread
    {
        private static final Logger LOG = Logger.getLogger(YouTubeDownload.class.getName());         
        
        private final Properties props;
        private final ReferenceSourceProvider provider;
        private final Video.Resolution resolution;
        private final String videoID;
        private final String title;
        private final FileTypeProvider fileType;

        public YouTubeDownload(Properties props, ReferenceSourceProvider provider, Video.Resolution resolution, String videoID, String title, FileTypeProvider fileType) 
        {
            this.props = props;
            this.provider = provider;
            this.resolution = resolution;
            this.videoID = videoID;
            this.title = title;
            this.fileType = fileType;
        }
        
        @Override
        public void run()
        {                     
            ProgressHandle progress = ProgressHandle.createHandle("Downloading YouTube Video");
            progress.start(); 
            
            try
            {
                Youtube youtube = new Youtube("https://www.youtube.com/watch?v=" + videoID);
                if(resolution == Video.Resolution.LOWEST || resolution == Video.Resolution.LOW)
                {
                    youtube.streams().getLowestResolution().download(AbstractFilesProvider.VIDEOS.getDirectory().getPath() + File.separator, videoID);                                                                                        
                }
                else
                {
                    youtube.streams().getHighestResolution().download(AbstractFilesProvider.VIDEOS.getDirectory().getPath() + File.separator, videoID);                                                                                        
                }
                
                FileObject folder = provider.getRootFolder();
                if(folder != null)
                {
                    OutputStream os = folder.createAndOpen(videoID + "." + PropertiesProvider.EXTENSION);
                    props.store(os, "New YouTube Video Created by Wizard"); 
                    os.close();                           
                    StatusDisplayer.getDefault().setStatusText("YouTube video saved with title: " + title);                    
                }                                 
            }
            catch(Exception e)
            {
                LOG.warning(e.getMessage());
            }
            finally
            {
                progress.finish();                 
            }                                          
        }
    }    
}
