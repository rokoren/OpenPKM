/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.youtube;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;
import openpkm.base.FileTypeProvider;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public class YouTubeProjectWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(YouTubeProjectWizardPanel1.class.getName());
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private YouTubeProjectVisualPanel1 component;
    private ChannelListResponse response;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public YouTubeProjectVisualPanel1 getComponent() {
        if (component == null) {
            component = new YouTubeProjectVisualPanel1();
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
        if(getComponent().getChannelID().isBlank()) 
        {
            throw new WizardValidationException(getComponent(), "Channel ID can not be empty", null);
        }  
        if (getComponent().getFileType() == null) 
        {
            throw new WizardValidationException(component, "File Type can not be empty", null);
        }           
        try
        {
            YouTube youtubeService = YouTubeService.getDeafult().getService();
            YouTube.Channels.List request = youtubeService.channels().list("snippet, statistics, topicDetails, status, brandingSettings");                       
            request.setKey(provider.getKey());
            response = request.setId(getComponent().getChannelID()).execute();  
            if(response.getItems() == null || response.isEmpty())
            {   
                throw new WizardValidationException(getComponent(), "YouTube Channel not found", null);                 
            }           
        }
        catch(IOException e)
        {
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        }
        catch(GeneralSecurityException e)
        {
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
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
        // use wiz.getProperty to retrieve previous panel state          
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        // use wiz.putProperty to remember current panel state
        
        String channelID = response.getItems().get(0).getId();
        String title = response.getItems().get(0).getSnippet().getTitle();
        String description = response.getItems().get(0).getSnippet().getDescription();
        String thumbnail = response.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();  
        DateTime publishedAt = response.getItems().get(0).getSnippet().getPublishedAt();
        String customUrl = response.getItems().get(0).getSnippet().getCustomUrl();
        String country = response.getItems().get(0).getSnippet().getCountry();
        String localizedTitle = response.getItems().get(0).getSnippet().getLocalized().getTitle();
        String localizedDescription = response.getItems().get(0).getSnippet().getLocalized().getDescription();  
        BigInteger viewCount = response.getItems().get(0).getStatistics().getViewCount(); 
        BigInteger subscriberCount = response.getItems().get(0).getStatistics().getSubscriberCount(); 
        BigInteger videoCount = response.getItems().get(0).getStatistics().getVideoCount(); 
        BigInteger commentCount = response.getItems().get(0).getStatistics().getCommentCount(); 
        String privacyStatus = response.getItems().get(0).getStatus().getPrivacyStatus();
        List<String> topicCategories = response.getItems().get(0).getTopicDetails().getTopicCategories();       
                
        descriptor.putProperty(YouTubeChannel.PROP_CHANNEL_ID, channelID);
        descriptor.putProperty(YouTubeChannel.PROP_TITLE, title);
        descriptor.putProperty(YouTubeChannel.PROP_DESCRIPTION, description);
        descriptor.putProperty(YouTubeChannel.PROP_THUMBNAIL, thumbnail);
        descriptor.putProperty(YouTubeChannel.PROP_PUBLISHED_AT, publishedAt);
        descriptor.putProperty(YouTubeChannel.PROP_CUSTOM_URL, customUrl);
        descriptor.putProperty(YouTubeChannel.PROP_COUNTRY, country);
        descriptor.putProperty(YouTubeChannel.PROP_LOCALIZED_TITLE, localizedTitle);
        descriptor.putProperty(YouTubeChannel.PROP_LOCALIZED_DESCRIPTION, localizedDescription);
        descriptor.putProperty(YouTubeChannel.PROP_VIEW_COUNT, viewCount);
        descriptor.putProperty(YouTubeChannel.PROP_SUBSCRIBER_COUNT, subscriberCount);
        descriptor.putProperty(YouTubeChannel.PROP_VIDEO_COUNT, videoCount);
        descriptor.putProperty(YouTubeChannel.PROP_COMMENT_COUNT, commentCount);
        descriptor.putProperty(YouTubeChannel.PROP_PRIVACY_STATUS, privacyStatus);
        descriptor.putProperty(YouTubeChannel.PROP_TOPIC_CATEGORIES, topicCategories);                     
        
        descriptor.putProperty(FileTypeProvider.PROP_FILE_TYPE, getComponent().getFileType());  
        
        try
        {                
            URL url = new URL(thumbnail);
            BufferedImage image = ImageIO.read(url); 

            int spaceWidth = image.getWidth() / 2;
            int spaceHeight = image.getWidth() / 2;

            int newWidth = image.getWidth() + 2 * spaceWidth;
            int newHeight = image.getHeight() + 4 * spaceHeight;
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
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }         
    }
}
