/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.youtube;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;
import openpkm.base.FileTypeProvider;
import openpkm.base.TagsProvider;
import openpkm.base.VisibilityProvider;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public class YouTubeWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private YouTubeVisualPanel1 component;
    private VideoListResponse response;       

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public YouTubeVisualPanel1 getComponent() {
        if (component == null) {
            component = new YouTubeVisualPanel1();
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
    public void validate() throws WizardValidationException 
    {
        GooglePasswordProvider provider = Lookup.getDefault().lookup(GooglePasswordProvider.class);
        if(provider == null) 
        {
            throw new WizardValidationException(getComponent(), "Google Key not found.", null);
        }          
        if (getComponent().getVideoFileType() == null) 
        {
            throw new WizardValidationException(null, "File Type can not be empty", null);
        }                 
        if (getComponent().getVideoID().equals("")) 
        {
            throw new WizardValidationException(null, "Video ID can not be empty", null);
        }
        
        try
        {         
            YouTube youtubeService = YouTubeService.getDeafult().getService();
            YouTube.Videos.List request = youtubeService.videos().list("snippet,contentDetails,statistics,topicDetails");
            request.setKey(provider.getKey());
            response = request.setId(getComponent().getVideoID()).execute();  
            if(response.getItems() == null || response.getItems().isEmpty())
            {   
                throw new WizardValidationException(getComponent(), "Channel not found", null);                 
            }                 
        }
        catch (IOException e)
        {
            throw new WizardValidationException(getComponent(), e.getMessage(), e.getLocalizedMessage());   
        }
        catch (GeneralSecurityException e)
        {
            throw new WizardValidationException(getComponent(), e.getMessage(), e.getLocalizedMessage());   
        }  
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
        Lookup.Provider lookupProvider = (Lookup.Provider)descriptor.getProperty("provider");
        if(lookupProvider != null)
        {
            TagsProvider tagsProvider = lookupProvider.getLookup().lookup(TagsProvider.class);
            if(tagsProvider != null)
            {
                getComponent().setTags(tagsProvider.getTags());                                 
            }            
        }
        
        String videoID = (String)descriptor.getProperty(YouTubeVideo.PROP_VIDEO_ID);
        if(videoID != null)
        {
            getComponent().setVideoID(videoID);
        }
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        descriptor.putProperty(FileTypeProvider.PROP_FILE_TYPE, getComponent().getVideoFileType()); 
        descriptor.putProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, getComponent().getVideoVisibilityModifier());      
        descriptor.putProperty(TagsProvider.PROP_TAGS, getComponent().getVideoTags());

        if(response != null)
        {
            String videoID = response.getItems().get(0).getId();
            descriptor.putProperty(YouTubeVideo.PROP_VIDEO_ID, videoID); 
            String videoTitle = response.getItems().get(0).getSnippet().getTitle();
            descriptor.putProperty(YouTubeVideo.PROP_VIDEO_TITLE, videoTitle); 
            String channelID = response.getItems().get(0).getSnippet().getChannelId();
            descriptor.putProperty(YouTubeVideo.PROP_CHANNEL_ID, channelID);  
            String channelTitle = response.getItems().get(0).getSnippet().getChannelTitle();
            descriptor.putProperty(YouTubeVideo.PROP_CHANNEL_TITLE, channelTitle);            
            DateTime publishedAt = response.getItems().get(0).getSnippet().getPublishedAt();
            descriptor.putProperty(YouTubeVideo.PROP_PUBLISHED_AT, publishedAt); 
            String duration = response.getItems().get(0).getContentDetails().getDuration();  
            descriptor.putProperty(YouTubeVideo.PROP_DURATION, duration); 
            String caption = response.getItems().get(0).getContentDetails().getCaption();  
            descriptor.putProperty(YouTubeVideo.PROP_CAPTION, caption);             
            BigInteger views = response.getItems().get(0).getStatistics().getViewCount();  
            descriptor.putProperty(YouTubeVideo.PROP_VIEW_COUNT, views); 
            List<String> youTubeTags = response.getItems().get(0).getSnippet().getTags();
            descriptor.putProperty(YouTubeVideo.PROP_YOUTUBE_TAGS, youTubeTags); 
            if(response.getItems().get(0).getTopicDetails() != null)
            {
                List<String> topicCategories = response.getItems().get(0).getTopicDetails().getTopicCategories();
                descriptor.putProperty(YouTubeVideo.PROP_TOPIC_CATEGORIES, topicCategories);                 
            }             
            String defaultLanguage = response.getItems().get(0).getSnippet().getDefaultLanguage();
            descriptor.putProperty(YouTubeVideo.PROP_DEFAULT_LANGUAGE, defaultLanguage);
            String defaultAudioLanguage = response.getItems().get(0).getSnippet().getDefaultAudioLanguage();               
            descriptor.putProperty(YouTubeVideo.PROP_DEFAULT_AUDIO_LANGUAGE, defaultAudioLanguage);
            String description = response.getItems().get(0).getSnippet().getDescription();
            descriptor.putProperty(YouTubeVideo.PROP_DESCRIPTION, description); 
            String liveBroadcastContent = response.getItems().get(0).getSnippet().getLiveBroadcastContent();
            descriptor.putProperty(YouTubeVideo.PROP_LIVEBROADCAST_CONTENT, liveBroadcastContent); 
            
            String thumbnailDefault = response.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();
            descriptor.putProperty(YouTubeVideo.PROP_THUMBNAIL_DEFAULT, thumbnailDefault); 
            String thumbnailMedium = response.getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl();
            descriptor.putProperty(YouTubeVideo.PROP_THUMBNAIL_MEDIUM, thumbnailMedium); 
            String thumbnailHigh = response.getItems().get(0).getSnippet().getThumbnails().getHigh().getUrl();
            descriptor.putProperty(YouTubeVideo.PROP_THUMBNAIL_HIGH, thumbnailHigh); 
            if(response.getItems().get(0).getSnippet().getThumbnails().getStandard() != null)
            {
                String thumbnailStandard = response.getItems().get(0).getSnippet().getThumbnails().getStandard().getUrl();  
                descriptor.putProperty(YouTubeVideo.PROP_THUMBNAIL_STANDARD, thumbnailStandard);                
            } 
            
            /*
            descriptor.putProperty(YouTubeVideo.PROP_DESCRIPTION, getComponent().getYouTubeDescription());
            descriptor.putProperty(YouTubeVideo.PROP_CHANNEL_ID, getComponent().getYouTubeChannelID());        
            descriptor.putProperty(YouTubeVideo.PROP_CHANNEL_TITLE, getComponent().getYouTubeChannelTitle());
            descriptor.putProperty(YouTubeVideo.PROP_DEFAULT_LANGUAGE, getComponent().getYouTubeDefaultLanguage());        
            descriptor.putProperty(YouTubeVideo.PROP_DEFAULT_AUDIO_LANGUAGE, getComponent().getYouTubeDefaultAudioLanguage());        
            descriptor.putProperty("comments", getComponent().getComments());                                                

            DateTime publishedAt = response.getItems().get(0).getSnippet().getPublishedAt();
            setYouTubePublishedAt(publishedAt.toStringRfc3339());
            duration = response.getItems().get(0).getContentDetails().getDuration();                                   
            jTextField4.setText(Utils.formatDuration(Duration.parse(duration)));                        
            setYouTubeChannelTitle(channelTitle);      
            description = response.getItems().get(0).getSnippet().getDescription();
            channelID = response.getItems().get(0).getSnippet().getChannelId();
            thumbnail = response.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();
            defaultLanguage = response.getItems().get(0).getSnippet().getDefaultLanguage();
            defaultAudioLanguage = response.getItems().get(0).getSnippet().getDefaultAudioLanguage();   
            */

            String thumbnail = response.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();             
            try
            {
                URL url = new URL(thumbnail);
                BufferedImage image = ImageIO.read(url); 

                int spaceWidth = image.getWidth() / 4;
                int spaceHeight = image.getWidth() / 1;

                int newWidth = image.getWidth() + 2 * spaceWidth;
                int newHeight = image.getHeight() + 2 * spaceHeight;
                BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

                // Get the graphics context to draw on the new image
                Graphics2D g2d = newImage.createGraphics();

                // Fill the new image with a white background (or any other color)
                g2d.setColor(new Color(0, 0, 0, 0)); 
                g2d.fillRect(0, 0, newWidth, newHeight);

                // Draw the original image onto the new image with the desired padding
                int x = spaceWidth;
                int y = spaceHeight;
                g2d.drawImage(image, x, y, null);            

                descriptor.putProperty("WizardPanel_image", newImage);                   
            }
            catch (IOException e)
            {
                Exceptions.printStackTrace(e);
            }               
        }
    }
}
