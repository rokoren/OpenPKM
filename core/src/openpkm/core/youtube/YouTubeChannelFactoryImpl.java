/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.youtube;

import com.google.api.client.util.DateTime;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.CloseSupport;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.OpenSupport;
import openpkm.base.PropertiesProvider;
import openpkm.base.StateSupport;
import openpkm.domain.Domain;
import openpkm.utils.DisplayNameProviderImpl;
import openpkm.utils.ShortDescriptionProviderImpl;
import openpkm.utils.Utils;
import openpkm.youtube.YouTubeCefClientProvider;
import openpkm.youtube.YouTubeChannel;
import openpkm.youtube.YouTubeChannelFactory;
import org.cef.browser.CefBrowser;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author rok
 */
@ServiceProvider(service=YouTubeChannelFactory.class)
public class YouTubeChannelFactoryImpl implements YouTubeChannelFactory
{
    @Override
    public YouTubeChannel getChannel(Properties props) 
    {
        return new YouTubeChannelImpl(props);
    }

    @Override
    public YouTubeChannel getChannel(String channelID) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void save(YouTubeChannel channel, OutputStream os, String comments) throws IOException 
    {
        channel.getProperties().store(os, comments);
        LOG.info("YouTube Channel saved");
    }
    
    private static final class YouTubeChannelImpl implements YouTubeChannel, Domain, StateSupport, MultiViewDescription
    {
        private final Properties props; 
        private final PropertyChangeSupport propertyChangeSupport;        
        
        private Lookup lkp;  
        private State state;

        public YouTubeChannelImpl(Properties props)
        {
            this.props = props;   
            propertyChangeSupport = new PropertyChangeSupport(this);            
        } 
        
    // TODO Source

        @Override
        public String getAppID()
        {
            return props.getProperty(PROP_APP_ID);
        } 

        @Override
        public String getSourceID()
        {
            return getChannelID();
        } 
        
        @Override
        public LocalDateTime getTimeCreated() 
        {
            String created = props.getProperty(PROP_TIME_CREATED);
            if(created != null)
            {
                return LocalDateTime.parse(created, DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;
        }           

        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            {
                lkp = Lookups.fixed(this, new DisplayNameProviderImpl(this), new ShortDescriptionProviderImpl(this), new IconProviderImpl());              
            }
            return lkp;
        }  
        
    // TODO StateSupport
        
        @Override
        public boolean isModified() 
        {
            return state == State.MODIFIED;
        }

        @Override
        public void markModified()
        {
            State oldValue = state;
            state = State.MODIFIED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }   

        @Override
        public boolean isDeleted() 
        {
            return state == State.DELETED;
        }        
        
        @Override
        public void notifyDeleted()
        {
            State oldValue = state;
            state = State.DELETED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }          
        
    // TODO TitleProvider  

        @Override
        public String getTitle() 
        {
            return props.getProperty(PROP_TITLE);
        }

        @Override
        public void setTitle(String title) 
        {
            if(title == null)
            {
                Object oldValue = props.remove(PROP_TITLE);
                propertyChangeSupport.firePropertyChange(PROP_TITLE, oldValue, title);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_TITLE, title);  
                propertyChangeSupport.firePropertyChange(PROP_TITLE, oldValue, title);
            } 
        } 

        @Override
        public void addTitleListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_TITLE, listener);
        }

        @Override
        public void removeTitleListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_TITLE, listener);
        }   

    // TODO DescriptionProvider  

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
                Object oldValue = props.remove(PROP_DESCRIPTION);
                propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldValue, desc);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_DESCRIPTION, desc);  
                propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldValue, desc);
            }   
        } 

        @Override
        public void addDescriptionListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_DESCRIPTION, listener);
        }

        @Override
        public void removeDescriptionListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_DESCRIPTION, listener);
        }      

    // TODO PropertiesProvider

        @Override
        public Properties getProperties()
        {
            return props;
        } 

        @Override
        public boolean merge(PropertiesProvider provider)
        {
            if(props.equals(provider.getProperties()))       
            {
                return false;
            }
            props.putAll(provider.getProperties());        
            return true;
        }  
        
    // YouTubeChannel 
    
        @Override
        public String getChannelID() 
        {
            return props.getProperty(PROP_CHANNEL_ID);
        }   

        @Override
        public String getThumbnail() 
        {
            return props.getProperty(PROP_THUMBNAIL);
        }

        @Override
        public void setThumbnail(String thumbnail) 
        {
            if(thumbnail == null)
            {
                Object oldValue = props.remove(PROP_THUMBNAIL);
                propertyChangeSupport.firePropertyChange(PROP_THUMBNAIL, oldValue, thumbnail);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_THUMBNAIL, thumbnail);  
                propertyChangeSupport.firePropertyChange(PROP_THUMBNAIL, oldValue, thumbnail);
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
                Object oldValue = props.remove(PROP_PUBLISHED_AT);
                if(oldValue != null)
                {
                    oldValue = new DateTime(oldValue.toString());
                }            
                propertyChangeSupport.firePropertyChange(PROP_PUBLISHED_AT, oldValue, time);            
            }
            else
            {
                Object oldValue = props.setProperty(PROP_PUBLISHED_AT, time.toStringRfc3339());
                if(oldValue != null)
                {
                    oldValue = new DateTime(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_PUBLISHED_AT, oldValue, time);
            }
        } 

        @Override
        public String getCustomUrl() 
        {
            return props.getProperty(PROP_CUSTOM_URL);
        }

        @Override
        public void setCustomUrl(String url) 
        {
            if(url == null)
            {
                Object oldValue = props.remove(PROP_CUSTOM_URL);
                propertyChangeSupport.firePropertyChange(PROP_CUSTOM_URL, oldValue, url);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_CUSTOM_URL, url);  
                propertyChangeSupport.firePropertyChange(PROP_CUSTOM_URL, oldValue, url);
            } 
        } 

        @Override
        public String getCountry() 
        {
            return props.getProperty(PROP_COUNTRY);
        }

        @Override
        public void setCountry(String country) 
        {
            if(country == null)
            {
                Object oldValue = props.remove(PROP_COUNTRY);
                propertyChangeSupport.firePropertyChange(PROP_COUNTRY, oldValue, country);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_COUNTRY, country);  
                propertyChangeSupport.firePropertyChange(PROP_COUNTRY, oldValue, country);
            } 
        }  

        @Override
        public String getLocalizedTitle() 
        {
            return props.getProperty(PROP_LOCALIZED_TITLE);
        }

        @Override
        public void setLocalizedTitle(String localizedTitle) 
        {
            if(localizedTitle == null)
            {
                Object oldValue = props.remove(PROP_LOCALIZED_TITLE);
                propertyChangeSupport.firePropertyChange(PROP_LOCALIZED_TITLE, oldValue, localizedTitle);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_LOCALIZED_TITLE, localizedTitle);  
                propertyChangeSupport.firePropertyChange(PROP_LOCALIZED_TITLE, oldValue, localizedTitle);
            } 
        } 

        @Override
        public String getLocalizedDescription() 
        {
            return props.getProperty(PROP_LOCALIZED_DESCRIPTION);
        }

        @Override
        public void setLocalizedDescription(String localizedDescription) 
        {
            if(localizedDescription == null)
            {
                Object oldValue = props.remove(PROP_LOCALIZED_DESCRIPTION);
                propertyChangeSupport.firePropertyChange(PROP_LOCALIZED_DESCRIPTION, oldValue, localizedDescription);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_LOCALIZED_DESCRIPTION, localizedDescription);  
                propertyChangeSupport.firePropertyChange(PROP_LOCALIZED_DESCRIPTION, oldValue, localizedDescription);
            } 
        } 

        @Override
        public Long getViewCount()
        {
            String viewCount = props.getProperty(PROP_VIDEO_COUNT);
            if(viewCount != null)
            {
                return Long.parseLong(viewCount);
            }
            return null;        
        }

        @Override
        public void setViewCount(Long viewCount) 
        {
            if(viewCount == null)
            {
                Object oldValue = props.remove(PROP_VIDEO_COUNT);
                propertyChangeSupport.firePropertyChange(PROP_VIDEO_COUNT, oldValue, viewCount);            
            }
            else
            {
                Object oldValue = props.setProperty(PROP_VIDEO_COUNT, viewCount.toString());
                if(oldValue != null)
                {
                    oldValue = Long.getLong(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_VIDEO_COUNT, oldValue, viewCount);
            }
        }  

        @Override
        public Long getSubscriberCount()
        {
            String subscriberCount = props.getProperty(PROP_SUBSCRIBER_COUNT);
            if(subscriberCount != null)
            {
                return Long.parseLong(subscriberCount);
            }
            return null;        
        }

        @Override
        public void setSubscriberCount(Long subscriberCount) 
        {
            if(subscriberCount == null)
            {
                Object oldValue = props.remove(PROP_SUBSCRIBER_COUNT);
                propertyChangeSupport.firePropertyChange(PROP_SUBSCRIBER_COUNT, oldValue, subscriberCount);            
            }
            else
            {
                Object oldValue = props.setProperty(PROP_SUBSCRIBER_COUNT, subscriberCount.toString());
                if(oldValue != null)
                {
                    oldValue = Long.getLong(oldValue.toString());
                }            
                propertyChangeSupport.firePropertyChange(PROP_SUBSCRIBER_COUNT, oldValue, subscriberCount);
            }
        }  

        @Override
        public Long getVideoCount()
        {
            String videoCount = props.getProperty(PROP_VIDEO_COUNT);
            if(videoCount != null)
            {
                return Long.parseLong(videoCount);
            }
            return null;        
        }

        @Override
        public void setVideoCount(Long videoCount) 
        {
            if(videoCount == null)
            {
                Object oldValue = props.remove(PROP_VIDEO_COUNT);
                if(oldValue != null)
                {
                    oldValue = Long.getLong(oldValue.toString());
                }             
                propertyChangeSupport.firePropertyChange(PROP_VIDEO_COUNT, oldValue, videoCount);            
            }
            else
            {
                Object oldValue = props.setProperty(PROP_VIDEO_COUNT, videoCount.toString());
                if(oldValue != null)
                {
                    oldValue = Long.getLong(oldValue.toString());
                }              
                propertyChangeSupport.firePropertyChange(PROP_VIDEO_COUNT, oldValue, videoCount);
            }
        }  

        @Override
        public Long getCommentCount()
        {
            String commentCount = props.getProperty(PROP_COMMENT_COUNT);
            if(commentCount != null)
            {
                return Long.parseLong(commentCount);
            }
            return null;        
        }

        @Override
        public void setCommentCount(Long commentCount) 
        {
            if(commentCount == null)
            {
                Object oldValue = props.remove(PROP_COMMENT_COUNT);
                propertyChangeSupport.firePropertyChange(PROP_COMMENT_COUNT, oldValue, commentCount);            
            }
            else
            {
                Object oldValue = props.setProperty(PROP_COMMENT_COUNT, commentCount.toString());
                if(oldValue != null)
                {
                    oldValue = Long.getLong(oldValue.toString());
                }             
                propertyChangeSupport.firePropertyChange(PROP_COMMENT_COUNT, oldValue, commentCount);
            }
        }  

        @Override
        public List<String> getTopicCategories() 
        {
            String topicCategories = props.getProperty(PROP_TOPIC_CATEGORIES);
            if(topicCategories != null)
            {
                return List.of(topicCategories.split(","));                   
            }                
            return Collections.EMPTY_LIST;
        }  

        @Override
        public void setTopicCategories(List<String> topicCategories)
        {
            if(topicCategories == null)
            {
                Object oldValue = props.remove(PROP_TOPIC_CATEGORIES);
                propertyChangeSupport.firePropertyChange(PROP_TOPIC_CATEGORIES, oldValue, topicCategories);            
            }
            else
            {
                StringJoiner joiner = new StringJoiner(",");
                for(String topicCategory : topicCategories)
                {
                    joiner.add(topicCategory);
                }
                Object oldValue = props.setProperty(PROP_TOPIC_CATEGORIES, joiner.toString());
                if(oldValue != null)
                {
                    oldValue = List.of(oldValue.toString().split(","));
                }             
                propertyChangeSupport.firePropertyChange(PROP_TOPIC_CATEGORIES, oldValue, topicCategories);
            }        
        }

        @Override
        public String getPrivacyStatus() 
        {
            return props.getProperty(PROP_PRIVACY_STATUS);
        }

        @Override
        public void setPrivacyStatus(String privacyStatus) 
        {
            if(privacyStatus == null)
            {
                Object oldValue = props.remove(PROP_PRIVACY_STATUS);
                propertyChangeSupport.firePropertyChange(PROP_PRIVACY_STATUS, oldValue, privacyStatus);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_PRIVACY_STATUS, privacyStatus);  
                propertyChangeSupport.firePropertyChange(PROP_PRIVACY_STATUS, oldValue, privacyStatus);
            } 
        }  
        
// TODO MultiViewDescription        
        
        @Override
        public String preferredID() 
        {
            return "youtube-channel";
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
        public Image getIcon() 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.YOUTUBE_CHANNEL);
        }         
        
        private final class IconProviderImpl implements IconProvider, OpenSupport, CloseSupport, ChangeSupportProvider, Runnable
        {        
            private final ChangeSupport changeSupport = new ChangeSupport(this);         

            private Image icon;         

            @Override
            public synchronized Image getIcon(int type)
            {
                if(icon != null)
                {
                    return icon;
                }
                IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
                return provider.getImage(IconsProvider.ICON.YOUTUBE_CHANNEL);
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
            public void run() 
            {
                String thumbnail = getThumbnail();
                if(thumbnail != null)
                {
                    try
                    {
                        URL url = new URL(thumbnail);
                        BufferedImage image = ImageIO.read(url);  
                        icon = Utils.resizeImage(image, 16, 16); 
                        changeSupport.fireChange();
                    }
                    catch(MalformedURLException e)
                    {
                        LOG.warning(e.getMessage());
                    }
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }               
                }
            }  

            @Override
            public void open() 
            {
                RP.post(this);
            }

            @Override
            public void close() 
            {
                icon = null;
                changeSupport.fireChange();
            }        
        }         
    }
    
    private static final class MultiViewElementImpl extends JPanel implements MultiViewElement
    {
        private CefBrowser browser; 
        private JToolBar toolbar;
        
        private transient MultiViewElementCallback callback;  
        
        private final YouTubeChannel channel;

        public MultiViewElementImpl(YouTubeChannel channel) 
        {
            this.channel = channel;
            setLayout(new BorderLayout());
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
                        browser = provider.getBrowser(channel);   
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
                /*
                JCheckBox watchLater = new JCheckBox("Watch Later");
                watchLater.setFocusable(false);
                watchLater.addItemListener(this);
                toolbar.add(watchLater);
                */
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
            return channel.getLookup();
        }        

        @Override
        public void componentOpened() 
        {
            Collection<? extends OpenSupport> providers = getLookup().lookupAll(OpenSupport.class);            
            for(OpenSupport provider : providers)
            {
                provider.open();
            }              
        }

        @Override
        public void componentClosed() 
        {
            if(browser != null)
            {
                browser.close(true);
            }
            
            Collection<? extends CloseSupport> providers = getLookup().lookupAll(CloseSupport.class);            
            for(CloseSupport provider : providers)
            {
                provider.close();
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
