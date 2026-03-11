/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.youtube;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import openpkm.base.DisplayNameProvider;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.utils.DateTimeUtils;
import openpkm.youtube.GooglePasswordProvider;
import openpkm.youtube.YouTubeCefClientProvider;
import openpkm.youtube.YouTubeService;
import openpkm.youtube.YouTubeVideo;
import openpkm.youtube.YouTubeVideoProvider;
import org.cef.browser.CefBrowser;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=YouTubeVideoProvider.class)
public class YouTubeVideoProviderImpl implements YouTubeVideoProvider
{
    private static final Logger LOG = Logger.getLogger(YouTubeVideoProvider.class.getName());     
    
    @Override
    public YouTubeVideo getVideo(Properties props, boolean displayName) 
    {
        if(displayName)
        {
            return new YouTubeVideoExtImpl(props);            
        }
        return new YouTubeVideoImpl(props);
    }
    
    @Override
    public YouTubeVideo getVideo(String videoID, boolean displayName)
    {
        try
        {         
            GooglePasswordProvider provider = Lookup.getDefault().lookup(GooglePasswordProvider.class);
            YouTube youtubeService = YouTubeService.getDeafult().getService();
            YouTube.Videos.List request = youtubeService.videos().list("snippet,contentDetails,statistics,topicDetails");
            //YouTube.Videos.List request = youtubeService.videos().list("snippet");
            request.setKey(provider.getKey());
            VideoListResponse response = request.setId(videoID).execute();  
            if(response.getItems() != null && !response.getItems().isEmpty())
            { 
                Properties props = new Properties();
                props.setProperty(YouTubeVideo.PROP_VIDEO_ID, response.getItems().get(0).getId());
                props.setProperty(YouTubeVideo.PROP_VIDEO_TITLE, response.getItems().get(0).getSnippet().getTitle());
                props.setProperty(YouTubeVideo.PROP_CHANNEL_ID, response.getItems().get(0).getSnippet().getChannelId()); 
                props.setProperty(YouTubeVideo.PROP_CHANNEL_TITLE, response.getItems().get(0).getSnippet().getChannelTitle());
         
                DateTime publishedAt = response.getItems().get(0).getSnippet().getPublishedAt();
                if (publishedAt != null)
                {
                    props.setProperty(YouTubeVideo.PROP_PUBLISHED_AT, publishedAt.toStringRfc3339());  
                }                

                if(response.getItems().get(0).getContentDetails() != null)
                {
                    String duration = response.getItems().get(0).getContentDetails().getDuration();                  
                    if (duration != null)
                    {
                        props.setProperty(YouTubeVideo.PROP_DURATION, duration);  
                    }   
                    
                    String caption = response.getItems().get(0).getContentDetails().getCaption();  
                    if(caption != null)
                    {
                        props.setProperty(YouTubeVideo.PROP_CAPTION, caption);
                    }                      
                } 
                
                String description = response.getItems().get(0).getSnippet().getDescription();
                if (description != null)
                {
                    props.setProperty(YouTubeVideo.PROP_DESCRIPTION, description);            
                }
                
                String defaultLanguage = response.getItems().get(0).getSnippet().getDefaultLanguage();
                if (defaultLanguage != null)
                {
                    props.setProperty(YouTubeVideo.PROP_DEFAULT_LANGUAGE, defaultLanguage);
                } 
                
                String defaultAudioLanguage = response.getItems().get(0).getSnippet().getDefaultAudioLanguage();  
                if (defaultAudioLanguage != null)
                {
                    props.setProperty(YouTubeVideo.PROP_DEFAULT_AUDIO_LANGUAGE, defaultAudioLanguage);
                }  
                
                String liveBroadcastContent = response.getItems().get(0).getSnippet().getLiveBroadcastContent();
                if (liveBroadcastContent != null)
                {
                    props.setProperty(YouTubeVideo.PROP_LIVEBROADCAST_CONTENT, liveBroadcastContent);
                }                                                
                
                String thumbnailDefault = response.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();                
                if(thumbnailDefault != null)
                {
                    props.setProperty(YouTubeVideo.PROP_THUMBNAIL_DEFAULT, thumbnailDefault);
                } 
                
                String thumbnailHigh = response.getItems().get(0).getSnippet().getThumbnails().getHigh().getUrl();                
                if(thumbnailHigh != null)
                {
                    props.setProperty(YouTubeVideo.PROP_THUMBNAIL_HIGH, thumbnailHigh);
                }  
                
                String thumbnailMedium = response.getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl();                
                if(thumbnailMedium != null)
                {
                    props.setProperty(YouTubeVideo.PROP_THUMBNAIL_MEDIUM, thumbnailMedium);
                } 
                
                if(response.getItems().get(0).getSnippet().getThumbnails().getStandard() != null)
                {
                    String thumbnailStandard = response.getItems().get(0).getSnippet().getThumbnails().getStandard().getUrl();  
                    props.setProperty(YouTubeVideo.PROP_THUMBNAIL_STANDARD, thumbnailStandard);              
                } 

                List<String> tags = response.getItems().get(0).getSnippet().getTags();
                if(tags != null)
                {
                    StringJoiner joiner = new StringJoiner(",");
                    for(String tag : tags)
                    {
                        joiner.add(tag);
                    }
                    props.setProperty(TagsProvider.PROP_TAGS, joiner.toString());
                } 
                
                if(response.getItems().get(0).getStatistics() != null)
                {
                    BigInteger views = response.getItems().get(0).getStatistics().getViewCount();  
                    if (views != null)
                    {
                        props.setProperty(YouTubeVideo.PROP_VIEW_COUNT, views.toString()); 
                    }

                    BigInteger likes = response.getItems().get(0).getStatistics().getLikeCount();  
                    if (likes != null)
                    {
                        props.setProperty(YouTubeVideo.PROP_LIKE_COUNT, likes.toString()); 
                    }   

                    BigInteger comments = response.getItems().get(0).getStatistics().getCommentCount();  
                    if (comments != null)
                    {
                        props.setProperty(YouTubeVideo.PROP_COMMENT_COUNT, comments.toString()); 
                    }                     
                }                 

                return getVideo(props, displayName);
            }                 
        }
        catch (IOException e)
        {
            LOG.warning(e.getMessage());   
        }
        catch (GeneralSecurityException e)
        {
            LOG.warning(e.getMessage());  
        }                                        
        return null;
    }    
 
    private static class YouTubeVideoExtImpl extends YouTubeVideoImpl implements YouTubeVideo, PropertiesProvider, DisplayNameProvider, IconProvider, TopicsProvider, TagsProvider, VisibilityProvider, MultiViewDescription
    {    
        public YouTubeVideoExtImpl(Properties props)
        {
            super(props);
        }
        
        @Override
        public String getDisplayName(TextFormat format) 
        {
            if(format == TextFormat.PLAIN)
            {
                return getVideoTitle();
            }
            return null; 
        }          
    }
    
    private static class YouTubeVideoImpl implements YouTubeVideo, PropertiesProvider, IconProvider, TopicsProvider, TagsProvider, VisibilityProvider, MultiViewDescription
    {    
        private final Properties props; 
        private final PropertyChangeSupport propertyChangeSupport;       
        
        private Lookup lkp;  
        private SourceState state;      

        public YouTubeVideoImpl(Properties props)
        {
            this.props = props;
            propertyChangeSupport = new PropertyChangeSupport(this);         
        }
        
        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            { 
                lkp = Lookups.fixed(this);              
            }
            return lkp;
        }            

        @Override
        public SourceState getState()
        {
            return state;
        }

        @Override
        public void markModified()
        {
            SourceState oldValue = getState();
            state = SourceState.MODIFIED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }   

        @Override
        public void notifyDeleted()
        {
            SourceState oldValue = getState();
            state = SourceState.DELETED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }        
        
        @Override
        public Properties getProperties()
        {
            return props;
        }  
        
        @Override
        public void merge(PropertiesProvider provider)
        {
            props.putAll(provider.getProperties());
        }          

        @Override
        public String getSourceID()
        {
            return getVideoID();
        }

        @Override
        public String getAppID()
        {
            return props.getProperty(PROP_APP_ID);
        }         
        
        @Override
        public LocalDateTime getTimeCreated() 
        {
            Date date = new Date(getPublishedAt().getValue());
            return DateTimeUtils.convertToLocalDateTime(date);
        } 
        
        @Override
        public Image getIcon(int type) 
        {    
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.YOUTUBE_VIDEO);
        }            

        @Override
        public String getVideoID() 
        {
            return props.getProperty(PROP_VIDEO_ID);
        }

        @Override
        public String getChannelID() 
        {
            return props.getProperty(PROP_CHANNEL_ID);
        }

        @Override
        public String getVideoTitle() 
        {
            return props.getProperty(PROP_VIDEO_TITLE);
        }

        @Override
        public void setVideoTitle(String title) 
        {
            if(title == null)
            {
                props.remove(PROP_VIDEO_TITLE);
            }
            else
            {
                props.setProperty(PROP_VIDEO_TITLE, title);
            }
        }

        @Override
        public String getChannelTitle() 
        {
            return props.getProperty(PROP_CHANNEL_TITLE);
        }

        @Override
        public void setChannelTitle(String title) 
        {
            if(title == null)
            {
                props.remove(PROP_CHANNEL_TITLE);
            }
            else
            {
                props.setProperty(PROP_CHANNEL_TITLE, title);
            }
        } 

        @Override
        public String getDescription()
        {
            return props.getProperty(PROP_DESCRIPTION);
        }

        @Override
        public void setDescription(String desc)
        {
            if(desc == null)
            {
                props.remove(PROP_DESCRIPTION);
            }
            else
            {
                props.setProperty(PROP_DESCRIPTION, desc);
            }
        }

        @Override
        public DateTime getPublishedAt() 
        {
            String publishedAt = props.getProperty(PROP_PUBLISHED_AT);
            if(publishedAt != null)
            {
                return new DateTime(publishedAt);
            }
            return null;
        }

        @Override
        public void setPublishedAt(DateTime time) 
        {
            if(time == null)
            {
                props.remove(PROP_PUBLISHED_AT);
            }
            else
            {
                props.setProperty(PROP_PUBLISHED_AT, time.toStringRfc3339());
            }
        }

        @Override
        public List<String> getTags() 
        {
            String tags = props.getProperty(PROP_TAGS);
            if(tags != null)
            {
                return List.of(tags.split(","));                   
            }                
            return Collections.EMPTY_LIST;
        } 

        @Override
        public List<String> getTopics() 
        {
            String topics = props.getProperty(PROP_TOPICS);
            if(topics != null)
            {
                return List.of(topics.split(","));                   
            }                
            return Collections.EMPTY_LIST;
        } 

        @Override
        public VisibilityProvider.Modifier getModifier()
        {
            String name = props.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
            if(name != null)
            {
                Optional<VisibilityProvider.Modifier> optional = VisibilityProvider.Modifier.get(name);
                if(optional.isPresent())
                {
                    return optional.get();
                }
            }
            return VisibilityProvider.Modifier.NONE;
        }

        @Override
        public void setModifier(VisibilityProvider.Modifier modifier)
        {
            if(modifier == null)
            {
                props.remove(VisibilityProvider.PROP_VISIBILITY_MODIFIER);         
            }
            else
            {
                props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, modifier.toString());  
            }
        }    

        @Override
        public List<String> getYouTubeTags() 
        {
            String tags = props.getProperty(PROP_YOUTUBE_TAGS);
            if(tags != null)
            {
                return List.of(tags.split(","));                   
            }                
            return Collections.EMPTY_LIST;
        }     

        @Override
        public String getThumbnailDefault()
        {
            return props.getProperty(PROP_THUMBNAIL_DEFAULT);
        }

        @Override
        public void setThumbnailDefault(String thumbnail) 
        {
            if(thumbnail == null)
            {
                props.remove(PROP_THUMBNAIL_DEFAULT);
            }
            else
            {
                props.setProperty(PROP_THUMBNAIL_DEFAULT, thumbnail);
            }
        }

        @Override
        public String getThumbnailMedium() 
        {
            return props.getProperty(PROP_THUMBNAIL_MEDIUM);
        }

        @Override
        public void setThumbnailMedium(String thumbnail) 
        {
            if(thumbnail == null)
            {
                props.remove(PROP_THUMBNAIL_MEDIUM);
            }
            else
            {
                props.setProperty(PROP_THUMBNAIL_MEDIUM, thumbnail);
            }
        }

        @Override
        public String getThumbnailHigh() 
        {
            return props.getProperty(PROP_THUMBNAIL_HIGH);
        }

        @Override
        public void setThumbnailHigh(String thumbnail) 
        {
            if(thumbnail == null)
            {
                props.remove(PROP_THUMBNAIL_HIGH);
            }
            else
            {
                props.setProperty(PROP_THUMBNAIL_HIGH, thumbnail);
            }
        }

        @Override
        public String getThumbnailStadard() 
        {
            return props.getProperty(PROP_THUMBNAIL_STANDARD);
        }

        @Override
        public void setThumbnailStandard(String thumbnail) 
        {
            if(thumbnail == null)
            {
                props.remove(PROP_THUMBNAIL_STANDARD);
            }
            else
            {
                props.setProperty(PROP_THUMBNAIL_STANDARD, thumbnail);
            }
        } 
        
        @Override
        public boolean isWatchLater()
        {
            String string = props.getProperty(PROP_WATCH_LATER);
            if(string != null)
            {
                return Boolean.parseBoolean(string);
            }
            return false;
        }
        
        @Override
        public void setWatchLater(boolean watchLater)
        {
            Object oldValue = props.setProperty(PROP_WATCH_LATER, Boolean.toString(watchLater)); 
            if(oldValue != null)
            {
                oldValue = Boolean.parseBoolean(oldValue.toString());
            }
            propertyChangeSupport.firePropertyChange(PROP_WATCH_LATER, oldValue, watchLater);
        }

        @Override
        public Image getIcon() 
        {    
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.YOUTUBE_VIDEO);
        }     

        @Override
        public void save(OutputStream os, String comments) throws IOException
        {
            props.store(os, comments); 
            LOG.info("YouTube video saved");
        } 
        
        @Override
        public String preferredID() 
        {
            return "youtube";
        }        
        
        @Override
        public MultiViewElement createElement() 
        {
            return new MultiViewElementImpl(this);
        }  

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "YouTube";
        }   
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }        

        @Override
        public String getThumbnail(Thumbnail thumbnail) 
        {
            if (thumbnail == Thumbnail.HIGH)
            {
                return getThumbnailHigh();            
            }
            else if (thumbnail == Thumbnail.MEDIUM)
            {
                return getThumbnailMedium();            
            } 
            else if (thumbnail == Thumbnail.STANDARD)
            {
                return getThumbnailStadard();            
            }         
            return getThumbnailDefault();
        }          
    }    
    
    private static final class MultiViewElementImpl extends JPanel implements MultiViewElement, ItemListener
    {
        private CefBrowser browser; 
        private JToolBar toolbar;
        
        private transient MultiViewElementCallback callback;  
        
        private final YouTubeVideo video;

        public MultiViewElementImpl(YouTubeVideoImpl video) 
        {
            this.video = video;
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        }                
        
        @Override
        public UndoRedo getUndoRedo() 
        {
            return UndoRedo.NONE;
        }

        @Override
        public void setMultiViewCallback(MultiViewElementCallback callback) 
        {
            this.callback = callback;
        }

        @Override
        public CloseOperationState canCloseElement() 
        {
            return CloseOperationState.STATE_OK;
        } 
        
        @Override
        public JComponent getVisualRepresentation() 
        {
            if(browser == null)
            {
                YouTubeCefClientProvider provider = Lookup.getDefault().lookup(YouTubeCefClientProvider.class);
                if(provider != null)
                {
                    try
                    {
                        browser = provider.getBrowser(video);   
                        if(browser != null)
                        {
                            add(browser.getUIComponent());
                        }
                    }
                    catch(Exception e)
                    {
                        LOG.warning(e.getMessage());
                    }
                }
            }
            return this;
        }

        @Override
        public JComponent getToolbarRepresentation() 
        {
            if(toolbar == null)
            {
                toolbar = new JToolBar();
                JCheckBox watchLater = new JCheckBox("Watch Later");
                watchLater.setFocusable(false);
                watchLater.setSelected(video.isWatchLater());
                watchLater.addItemListener(this);
                toolbar.add(watchLater);
            }
            return toolbar;
        }

        @Override
        public Action[] getActions() 
        {
            return new Action[0];
        }

        @Override
        public Lookup getLookup() 
        {
            return Lookup.EMPTY;
        }        

        @Override
        public void componentOpened() 
        {
            
        }

        @Override
        public void componentClosed() 
        {
            if(browser != null)
            {
                browser.close(true);
            }
        }

        @Override
        public void componentShowing() 
        {
            
        }

        @Override
        public void componentHidden() 
        {
            
        }

        @Override
        public void componentActivated() 
        {
            
        }

        @Override
        public void componentDeactivated() 
        {
            
        }

        @Override
        public void itemStateChanged(ItemEvent evt) 
        {
            boolean isWatchLater = evt.getStateChange() == ItemEvent.SELECTED;
            video.setWatchLater(isWatchLater);
        }
    }
}
