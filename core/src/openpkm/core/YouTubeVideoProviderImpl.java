/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import com.google.api.client.util.DateTime;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeListener;
import openpkm.base.IconProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.utils.DateTimeUtils;
import openpkm.youtube.YouTubeCefClientProvider;
import openpkm.youtube.YouTubeVideo;
import openpkm.youtube.YouTubeVideoProvider;
import org.cef.browser.CefBrowser;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
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
    public YouTubeVideo getVideo(Properties props) 
    {
        return new YouTubeVideoImpl(props);
    }
 
    private static class YouTubeVideoImpl implements YouTubeVideo, TitleProvider, PropertiesProvider, IconProvider, TopicsProvider, TagsProvider, VisibilityProvider, MultiViewDescription
    {    
        private final Properties props; 
        private final PropertyChangeSupport propertyChangeSupport;
        private final ChangeSupport changeSupport;         
        
        private boolean isDeleted;        

        public YouTubeVideoImpl(Properties props)
        {
            this.props = props;
            propertyChangeSupport = new PropertyChangeSupport(this);
            changeSupport = new ChangeSupport(this);            
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.removePropertyChangeListener(listener);
        } 

        @Override
        public void addChangeListener(ChangeListener listener)
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener)
        {
            changeSupport.removeChangeListener(listener);
        }     

        @Override
        public boolean isDeleted()
        {
            return isDeleted;
        }

        @Override
        public void setDeleted()
        {
            isDeleted = true;
            changeSupport.fireChange();
        }        
        
        @Override
        public Properties getProperties()
        {
            return props;
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
        public String getTitle() 
        {
            return getVideoTitle();
        }   
        
        @Override
        public String getDataFileExtension() 
        {
            return props.getProperty(PROP_DATA_FILE_EXTENSION);
        }

        @Override
        public void setDataFileExtension(String extension) 
        {
            if(extension == null)
            {
                props.remove(PROP_DATA_FILE_EXTENSION);
            }
            else
            {
                props.setProperty(PROP_DATA_FILE_EXTENSION, extension);
            }
        }

        @Override
        public void setTitle(String title) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
        public Image getIcon() 
        {    
            return ImageUtilities.loadImage(ICON); 
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
    
    private static final class MultiViewElementImpl extends JPanel implements MultiViewElement
    {
        private CefBrowser browser; 
        
        private transient MultiViewElementCallback callback;  
        
        private final YouTubeVideo video;

        public MultiViewElementImpl(YouTubeVideo video) 
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
            return new JToolBar();
        }

        @Override
        public Action[] getActions() 
        {
            return new Action[0];
        }

        @Override
        public Lookup getLookup() 
        {
            return Lookups.singleton(video);
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
    }
}
