/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.youtube;

import com.google.api.client.util.DateTime;
import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import javax.swing.event.ChangeListener;
import openpkm.base.TagsProvider;
import openpkm.youtube.YouTubeVideo;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author Rok Koren
 */
public class YouTubeWizardPanel2 implements WizardDescriptor.FinishablePanel<WizardDescriptor>
{
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private YouTubeVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public YouTubeVisualPanel2 getComponent() 
    {
        if (component == null) 
        {
            component = new YouTubeVisualPanel2();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }   

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor descriptor) 
    {
        String title = (String)descriptor.getProperty(YouTubeVideo.PROP_VIDEO_TITLE);
        getComponent().setYouTubeVideoTitle(title); 
        String channelID = (String)descriptor.getProperty(YouTubeVideo.PROP_CHANNEL_ID);
        String channelTitle = (String)descriptor.getProperty(YouTubeVideo.PROP_CHANNEL_TITLE);
        getComponent().setYouTubeChannelTitle(channelTitle + " (" + channelID + ")"); 
        String duration = (String)descriptor.getProperty(YouTubeVideo.PROP_DURATION);
        getComponent().setYouTubeVideoDuration(Duration.parse(duration));
        DateTime publishedAt = (DateTime)descriptor.getProperty(YouTubeVideo.PROP_PUBLISHED_AT);
        getComponent().setYouTubeVideoPublishedAt(publishedAt);
        BigInteger views = (BigInteger)descriptor.getProperty(YouTubeVideo.PROP_VIEW_COUNT);
        getComponent().setYouTubeVideoViewCount(views);
        List<String> topicCategories = (List<String>)descriptor.getProperty(YouTubeVideo.PROP_TOPIC_CATEGORIES);
        getComponent().setYouTubeVideoTopicCategories(topicCategories);        
        List<String> tags = (List<String>)descriptor.getProperty(TagsProvider.PROP_TAGS);
        if(tags != null)
        {
            getComponent().setYouTubeVideoTags(tags);               
        }             
        String defaultLanguage = (String)descriptor.getProperty(YouTubeVideo.PROP_DEFAULT_LANGUAGE);
        if(defaultLanguage != null)
        {
            getComponent().setYouTubeVideoTextLanguage(defaultLanguage);            
        }
        String defaultAudioLanguage = (String)descriptor.getProperty(YouTubeVideo.PROP_DEFAULT_AUDIO_LANGUAGE);
        if(defaultAudioLanguage != null)
        {
            getComponent().setYouTubeVideoAudioLanguage(defaultAudioLanguage);                     
        }
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
    }    

    @Override
    public boolean isFinishPanel()
    {
        return true;
    }
}
