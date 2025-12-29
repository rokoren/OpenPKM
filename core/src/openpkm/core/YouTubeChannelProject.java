/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.event.ChangeListener;
import openpkm.base.Article;
import openpkm.base.ArticleProvider;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.Book;
import openpkm.base.BookProvider;
import openpkm.base.BulletIconProvider;
import openpkm.base.DataGroupProvider;
import openpkm.base.Document;
import openpkm.base.Domain;
import openpkm.base.DomainsProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.Link;
import openpkm.base.Picture;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviders;
import openpkm.base.UpdateCookie;
import openpkm.base.Video;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import openpkm.reference.ReferenceSourceProvider;
import openpkm.utils.FileUtils;
import openpkm.utils.SavableImpl;
import openpkm.utils.Utils;
import openpkm.youtube.YouTubeChannel;
import openpkm.youtube.YouTubeService;
import openpkm.youtube.YouTubeSourceProvider;
import openpkm.youtube.YouTubeVideo;
import openpkm.youtube.YouTubeVideoProvider;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ParentProjectProvider;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.RootProjectProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class YouTubeChannelProject implements Domain, YouTubeChannel, PropertiesProvider, Sources, SourceProviders, BatchUpdateSupport
{    
    public static final String PROP_LAST_SOURCE      = "last.source";
    public static final String PROP_LAST_VIDEO_COUNT = "last.video.count"; 
    
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_NOTES     = 100;
    private static final int POSITION_DOCUMENTS = 200;
    private static final int POSITION_ARTICLES  = 300;
    private static final int POSITION_BOOKS     = 400;
    private static final int POSITION_LINKS     = 500;
    private static final int POSITION_PICTURES  = 600;    
    private static final int POSITION_VIDEOS    = 700;

    private static final Logger LOG = Logger.getLogger(YouTubeChannelProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(YouTubeChannelProject.class);   
    
    private final Map<String, SourceProvider> sources = new HashMap();  
    private final List<UpdateCookie> cookies = new ArrayList();      
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);   
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    
    private final FileObject projectDir;        
    private final ProjectState state;
    private final Properties props;   
    
    private Lookup lkp;  
    private FileObject dataDir;
    private LocalFileSystem fileSystem;
    private Source lastSource; 
     
    private RequestProcessor.Task task;     
    
    public YouTubeChannelProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;
       
        ReferenceProvider referenceProvider = Lookup.getDefault().lookup(ReferenceProvider.class);
        if(referenceProvider != null)
        {          
            SourceProvider references = new ReferenceSourceProviderImpl(referenceProvider);
            sources.put(references.getName(), references);            
        }

        YouTubeVideoProvider youtubeProvider = Lookup.getDefault().lookup(YouTubeVideoProvider.class);
        if(youtubeProvider != null)
        {
            SourceProvider videos = new YouTubeSourceProviderImpl(youtubeProvider);
            sources.put(videos.getName(), videos);                       
        }  
    }
    
    private synchronized LocalFileSystem getFileSystem() throws IOException, PropertyVetoException
    {
        if(fileSystem == null)
        {
            fileSystem = new LocalFileSystem();
            fileSystem.setRootDirectory(FileUtil.toFile(getDataDirectory()));            
        }
        return fileSystem;
    }
    
    @Override
    public SourceProvider getSourceProvider(String folder)
    {
        return sources.get(folder);
    }
    
    private synchronized FileObject getDataDirectory() throws IOException
    {
        if(dataDir == null)
        {
            dataDir = getProjectDirectory().getFileObject(DATA_FOLDER);
            if(dataDir == null)
            {
                dataDir = getProjectDirectory().createFolder(DATA_FOLDER);
                LOG.info("Data dir created: " + dataDir.getPath());                        
            }                 
        }                           
        return dataDir;       
    } 
    
    @Override
    public FileObject getFileWithAttrs(FileObject file, boolean refresh)
    {
        try
        {
            if(refresh) getFileSystem().getRoot().refresh();
            return getFileSystem().getRoot().getFileObject(file.getName(), file.getExt());            
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }
        catch(PropertyVetoException e)
        {
            LOG.warning(e.getMessage());
        }  
        return null;
    }

    private Source getLastSource() 
    {
        return lastSource;
    }

    private void setLastSource(Source source) 
    {
        Source oldSource = lastSource;
        lastSource = source;
        propertyChangeSupport.firePropertyChange(PROP_LAST_SOURCE, oldSource, source);
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
    
// TODO Sources    
    
    @Override
    public SourceGroup[] getSourceGroups(String string) 
    {
        if(string.equalsIgnoreCase(Sources.TYPE_GENERIC))
        {
            return sources.values().toArray(new SourceGroup[0]);                
        }
        return new SourceGroup[0];
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
    
// TODO Project
    
    @Override
    public FileObject getProjectDirectory()
    {
        return projectDir;
    } 
    
    @Override
    public Lookup getLookup() 
    {
        if (lkp == null) 
        { 
            List list = new ArrayList();

            list.add(this);
            list.add(new Info());
            list.add(new IconProviderImpl());
            list.add(new BulletIconProviderImpl());
            list.add(new ProjectOpenedHookImpl());   
            list.add(new RootProjectProviderImpl());
            list.add(new ParentProjectProviderImpl());              

            list.add(new YouTubeChannelLogicalView(this));
            list.add(new YouTubeChannelCustomizerProvider(this));  

            list.add(new HtmlFilesProviderImpl());                                  

            list.addAll(sources.values());

            list.add(new BookDataGroupProviderImpl()); 
            list.add(new ArticleDataGroupProviderImpl()); 
            list.add(new DocumentDataGroupProviderImpl()); 
            list.add(new LinkDataGroupProviderImpl());                
            list.add(new PictureDataGroupProviderImpl()); 
            list.add(new VideoDataGroupProviderImpl());             
            
            lkp = Lookups.fixed(list.toArray(new Object[list.size()]));              
        }
        return lkp;
    }      

// TODO Domain  
    
    @Override
    public String getDomainID() 
    {
        return getChannelID();
    }    
    
    @Override
    public String getAppID() 
    {
        return props.getProperty(PROP_APP_ID);
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
    
    public Long getLastVideoCount()
    {
        String videoCount = props.getProperty(PROP_LAST_VIDEO_COUNT);
        if(videoCount != null)
        {
            return Long.parseLong(videoCount);
        }
        return null;        
    }
    
    public void setLastVideoCount(Long videoCount) 
    {
        if(videoCount == null)
        {
            Object oldValue = props.remove(PROP_LAST_VIDEO_COUNT);
            propertyChangeSupport.firePropertyChange(PROP_LAST_VIDEO_COUNT, oldValue, videoCount);            
        }
        else
        {
            Object oldValue = props.setProperty(PROP_LAST_VIDEO_COUNT, videoCount.toString());
            if(oldValue != null)
            {
                oldValue = Long.getLong(oldValue.toString());
            }              
            propertyChangeSupport.firePropertyChange(PROP_LAST_VIDEO_COUNT, oldValue, videoCount);
        }
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

// TODO PropertiesProvider
    
    @Override
    public Properties getProperties()
    {
        return props;
    }     
    
// TODO BatchUpdateSupport    
    
    @Override
    public void registerUpdateCookie(UpdateCookie cookie) 
    {
        cookies.add(cookie);
    }

    @Override
    public boolean batchUpdate() 
    {
        if(cookies.isEmpty())
        {
            return false;
        }
        for(UpdateCookie cookie : cookies)
        {
            cookie.update();
        }
        cookies.clear();
        return true;
    }        
    
// TODO ProjectOpenedHook    
    
    private final class ProjectOpenedHookImpl extends ProjectOpenedHook implements PropertyChangeListener, Runnable
    {
        @Override
        protected void projectOpened() 
        {  
            task = RP.create(this);  
            task.schedule(60000);    
            propertyChangeSupport.addPropertyChangeListener(this);
        }

        @Override
        protected void projectClosed() 
        { 
            propertyChangeSupport.removePropertyChangeListener(this);
            task.cancel();
        } 

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            state.markModified(); 
            if(evt.getPropertyName().equals(PROP_TITLE))
            {
                propertyChangeSupport.firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME, evt.getOldValue(), evt.getNewValue());
            }
        }  
        
        @Override
        public void run()
        {  
            String googleKey = NbPreferences.forModule(YouTubeService.class).get(YouTubeService.PROP_GOOGLE_KEY, null);
            if(googleKey != null)
            {
                try
                {
                    YouTube youtubeService = YouTubeService.getDeafult().getService();
                    YouTube.Channels.List request = youtubeService.channels().list("snippet, statistics, topicDetails, status, brandingSettings");
                    request.setKey(googleKey);
                    ChannelListResponse response = request.setId(getChannelID()).execute();  
                    if(response.getItems() != null && !response.isEmpty())
                    {                   
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

                        setTitle(title);
                        setDescription(description);
                        setThumbnail(thumbnail);
                        setPublishedAt(publishedAt);
                        setCustomUrl(customUrl);
                        setCountry(country);
                        setLocalizedTitle(localizedTitle);
                        setLocalizedDescription(localizedDescription);
                        setViewCount(viewCount.longValue());
                        setSubscriberCount(subscriberCount.longValue());         
                        setVideoCount(videoCount.longValue());
                        if(commentCount != null)
                        {
                            setCommentCount(commentCount.longValue());                    
                        }
                        setTopicCategories(topicCategories);
                        setPrivacyStatus(privacyStatus);  
                        setVideoCount(videoCount.longValue());
                    }                 
                    task.schedule(100000);
                }
                catch (IOException e)
                {
                    LOG.warning(e.getMessage());
                }  
                catch (GeneralSecurityException e)
                {
                    LOG.warning(e.getMessage());
                }                 
            }
        }          
    }
    
// TODO ProjectInformation 
    
    private final class Info implements ProjectInformation
    {                     
        @Override
        public Icon getIcon()
        {  
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getIcon(IconsProvider.ICON.YOUTUBE_CHANNEL);
        }

        @Override
        public String getName() 
        {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() 
        {                       
            return getTitle();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.addPropertyChangeListener(ProjectInformation.PROP_DISPLAY_NAME, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(ProjectInformation.PROP_DISPLAY_NAME, listener);
        }                

        @Override
        public Project getProject() 
        {
            return YouTubeChannelProject.this;
        }
    }     
  
// TODO IconProvider    
    
    private final class IconProviderImpl implements IconProvider, Runnable
    {        
        private Image icon; 
        private boolean isLoading;
        
        private final ChangeSupport changeSupport = new ChangeSupport(this); 

        @Override
        public synchronized Image getIcon()
        {
            if(icon != null)
            {
                return icon;
            }
            if(!isLoading)
            {
                isLoading = true;                
                RP.post(this);                
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
                finally
                {
                    isLoading = false;
                }                
            }
        }                
    } 
    
// TODO BulletIconProvider    
    
    private final class BulletIconProviderImpl implements BulletIconProvider, PropertyChangeListener, Runnable
    {                
        private final ChangeSupport changeSupport = new ChangeSupport(this); 

        public BulletIconProviderImpl() 
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_VIDEO_COUNT, this);
            propertyChangeSupport.addPropertyChangeListener(PROP_VIDEO_COUNT, this);
            if(!hasVideos())
            {
                RP.post(this);
            }
        }                

        private boolean hasVideos()
        {
            Long lastVideoCount = getLastVideoCount();
            if(lastVideoCount != null && !lastVideoCount.equals(getVideoCount()))
            {
                return true;
            }            
            return false;
        }
        
        @Override
        public synchronized Image getBullet()
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);            
            if(hasVideos())
            {
                return provider.getImage(IconsProvider.ICON.BULLET_RED);
            }
            return null;
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
            String googleKey = NbPreferences.forModule(YouTubeService.class).get(YouTubeService.PROP_GOOGLE_KEY, null);
            if(googleKey != null)
            {
                try
                {
                    YouTube youtubeService = YouTubeService.getDeafult().getService();
                    YouTube.Channels.List request = youtubeService.channels().list("statistics");
                    request.setKey(googleKey);
                    ChannelListResponse response = request.setId(getChannelID()).execute();  
                    if(response.getItems() != null && !response.isEmpty())
                    { 
                        BigInteger videoCount = response.getItems().get(0).getStatistics().getVideoCount();        
                        setLastVideoCount(videoCount.longValue());                  
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
            } 
        }                

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            state.markModified();             
            changeSupport.fireChange();
        }
    }     

// TODO RootProjectProvider     

    private final class RootProjectProviderImpl implements RootProjectProvider
    {
        @Override
        public Project getRootProject() 
        {
            return Utils.getRootProject(YouTubeChannelProject.this);
        }         
    }
    
// TODO ParentProjectProvider     

    private final class ParentProjectProviderImpl implements ParentProjectProvider
    {
        private Project getProject(FileObject dir)
        {
            FileObject parent = dir.getParent();
            if(parent != null && parent.isFolder())
            {
                if(ProjectManager.getDefault().isProject(parent))
                {
                    try
                    {
                        Project project = ProjectManager.getDefault().findProject(parent);
                        DomainsProvider provider = project.getLookup().lookup(DomainsProvider.class);
                        if(provider != null)
                        {
                            return project;                        
                        }
                        else
                        {
                            return getProject(parent);
                        }
                    }
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }
                }
                else
                {
                    return getProject(parent);
                }
            }
            return null;         
        }

        @Override
        public Project getPartentProject() 
        {
            return getProject(getProjectDirectory());
        }          
    }        
    
// TODO DataGroup     
    
    private final class BookDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public BookDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Book"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return YouTubeChannelProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_BOOKS;
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
        public FileObject getRootFolder() throws IOException
        {
            return getDataDirectory();
        }

        @Override
        public String getName() 
        {
            return "book";
        }

        @Override
        public String getDisplayName() 
        {
            return "Books";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.BOOKS);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Book book = data.getLookup().lookup(Book.class);
                if(book != null)
                {
                    return true;
                } 
            }                                  
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Book)
            {
                changeSupport.fireChange();
            }
        }
    }   
    
    private final class ArticleDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public ArticleDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Article"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return YouTubeChannelProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_ARTICLES;
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
        public FileObject getRootFolder() throws IOException
        {
            return getDataDirectory();
        }

        @Override
        public String getName() 
        {
            return "article";
        }

        @Override
        public String getDisplayName() 
        {
            return "Articles";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.ARTICLES);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Article article = data.getLookup().lookup(Article.class);
                if(article != null)
                {
                    return true;
                }                 
            }                                    
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Article)
            {
                changeSupport.fireChange();
            }
        }
    } 
    
    private final class DocumentDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public DocumentDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Document"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return YouTubeChannelProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_DOCUMENTS;
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
        public FileObject getRootFolder() throws IOException 
        {
            return getDataDirectory();
        }

        @Override
        public String getName() 
        {
            return "document";
        }

        @Override
        public String getDisplayName() 
        {
            return "Documents";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.DOCUMENTS);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Document document = data.getLookup().lookup(Document.class);
                if(document != null)
                {
                    return true;
                }                 
            }                                    
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Document)
            {
                changeSupport.fireChange();
            }
        }
    }  

    private final class LinkDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public LinkDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Link"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return YouTubeChannelProject.this;
        }        
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_LINKS;
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
        public FileObject getRootFolder() throws IOException
        {
            return getDataDirectory();
        }

        @Override
        public String getName() 
        {
            return "link";
        }

        @Override
        public String getDisplayName() 
        {
            return "Links";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.LINKS);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Link link = data.getLookup().lookup(Link.class);
                if(link != null)
                {
                    return true;
                }                 
            }                                    
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Link)
            {
                changeSupport.fireChange();
            }
        }
    }  

    private final class PictureDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 
                
        public PictureDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 

        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Picture"));         
            return actions;
        }
        
        @Override
        public Lookup.Provider getProvider()
        {
            return YouTubeChannelProject.this;
        }        
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_PICTURES;
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
        public FileObject getRootFolder() throws IOException 
        {
            return getDataDirectory();
        }

        @Override
        public String getName() 
        {
            return "picture";
        }

        @Override
        public String getDisplayName() 
        {
            return "Pictures";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.PICTURES);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Picture picture = data.getLookup().lookup(Picture.class);
                if(picture != null)
                {
                    return true;
                }                 
            }                                   
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Picture)
            {
                changeSupport.fireChange();
            }
        }
    } 
    
    private final class VideoDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 
                
        public VideoDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 

        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Video"));         
            return actions;
        }
        
        @Override
        public Lookup.Provider getProvider()
        {
            return YouTubeChannelProject.this;
        }        
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_VIDEOS;
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
        public FileObject getRootFolder() throws IOException 
        {
            return getDataDirectory();
        }

        @Override
        public String getName() 
        {
            return "video";
        }

        @Override
        public String getDisplayName() 
        {
            return "Videos";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.VIDEOS);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Video video = data.getLookup().lookup(Video.class);
                if(video != null)
                {
                    return true;
                }                 
            }                                   
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Video)
            {
                changeSupport.fireChange();
            }
        }
    }    
    
// TODO SourceGroup
    
    private final class ReferenceSourceProviderImpl extends ReferenceSourceProvider implements FileChangeListener, PropertyChangeListener
    {               
        public ReferenceSourceProviderImpl(ReferenceProvider provider) 
        {
            super(provider);
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return YouTubeChannelProject.this;
        }  
        
        @Override
        public synchronized Map<String, Reference> getReferences()
        {
            if(references == null)
            {
                references = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            Reference reference = provider.getReference(Utils.getProperties(file)); 
                            reference.addPropertyChangeListener(this);
                            references.put(reference.getSourceID(), reference);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return references;
        }                

        @Override
        public FileObject getRootFolder() 
        {
            if(rootDir == null)
            {
                try
                {                
                    rootDir = getProjectDirectory().getFileObject(ROOT_FOLDER);
                    if(rootDir == null)
                    {
                        rootDir = getProjectDirectory().createFolder(ROOT_FOLDER);
                        LOG.info("Reference root folder created: " + rootDir.getPath());                        
                    } 
                    rootDir.addFileChangeListener(this);                                        
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                
            }
            return rootDir;
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
        public boolean createSource(Properties props, FileTypeProvider fileTypeProvider)     
        {
            Reference reference = provider.getReference(props);
            if(reference != null)
            {
                try
                {
                    String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
                    FileObject file = getFileWithAttrs(getDataDirectory().createData(fileName, fileTypeProvider.getExtension()), true);
                    file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
                    file.setAttribute(ATTR_SOURCE_ID, reference.getSourceID());  
                    
                    if(reference instanceof Article)
                    {
                        Article article = (Article)reference;
                        if(fileTypeProvider instanceof ArticleProvider)
                        {
                            ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                            OutputStream output = file.getOutputStream();
                            output.write(articleProvider.getArticle(article.getTitle(), article.getPublisher()).getBytes());
                            output.close();
                        }                         
                    }
                    else if(reference instanceof Book)
                    {
                        Book book = (Book)reference;
                        if(fileTypeProvider instanceof BookProvider)
                        {
                            BookProvider bookProvider = (BookProvider)fileTypeProvider;
                            OutputStream output = file.getOutputStream();
                            output.write(bookProvider.getBook(book.getTitle(), book.getAuthors()).getBytes());
                            output.close();
                        }                         
                    }
                    
                    FileObject folder = getRootFolder();
                    if(folder != null)
                    {  
                        OutputStream os = folder.createAndOpen(reference.getSourceID() + "." + PropertiesProvider.EXTENSION);  
                        reference.save(os, "New Reference Created");
                        os.close();  
                        return true;
                    }                                                          
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }
            } 
            return true;
        }          
        
        @Override
        public void fileFolderCreated(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            DataFolder data = DataFolder.findFolder(file);
            setLastData(data);
            */
        }

        @Override
        public void fileDataCreated(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            try
            {
                Reference reference = provider.getReference(Utils.getProperties(file)); 
                reference.addPropertyChangeListener(this);
                getReferences().put(reference.getSourceID(), reference);               
                setLastSource(reference);                
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }               
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Reference reference = getReferences().get(file.getName());  
            if(reference != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Reference reference = getReferences().remove(file.getName());  
            if(reference != null)
            {
                reference.removePropertyChangeListener(this);
                reference.setDeleted();
                setLastSource(reference);
            }
        }

        @Override
        public void fileRenamed(FileRenameEvent fre) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fae) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }          

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            new SavableImpl(this, evt);
        }
    }  
    
    private final class YouTubeSourceProviderImpl extends YouTubeSourceProvider implements FileChangeListener
    {
        public YouTubeSourceProviderImpl(YouTubeVideoProvider provider) 
        {
            super(provider);
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return YouTubeChannelProject.this;
        } 
        
        @Override
        public synchronized Map<String, YouTubeVideo> getVideos()
        {
            if(videos == null)
            {
                videos = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            YouTubeVideo video = provider.getVideo(Utils.getProperties(file)); 
                            videos.put(video.getSourceID(), video);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return videos;
        }        

        @Override
        public synchronized FileObject getRootFolder() 
        {
            if(rootDir == null)
            {
                try
                {                
                    rootDir = getProjectDirectory().getFileObject(ROOT_FOLDER);
                    if(rootDir == null)
                    {
                        rootDir = getProjectDirectory().createFolder(ROOT_FOLDER);
                        LOG.info("YouTube root folder created: " + rootDir.getPath());                        
                    } 
                    rootDir.addFileChangeListener(this);                                        
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                
            }
            return rootDir;
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
        public boolean createSource(Properties props, FileTypeProvider fileTypeProvider)     
        {
            YouTubeVideo video = provider.getVideo(props);
            if(video != null)
            {
                try
                {
                    String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
                    FileObject file = getFileWithAttrs(getDataDirectory().createData(fileName, fileTypeProvider.getExtension()), true);
                    file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
                    file.setAttribute(ATTR_SOURCE_ID, video.getSourceID());  
                    
                    if(fileTypeProvider instanceof ArticleProvider)
                    {
                        ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                        OutputStream output = file.getOutputStream();
                        output.write(articleProvider.getArticle(video.getVideoTitle(), video.getChannelTitle()).getBytes());
                        output.close();
                    }                     
                    
                    FileObject folder = getRootFolder();
                    if(folder != null)
                    {  
                        OutputStream os = folder.createAndOpen(video.getVideoID() + "." + PropertiesProvider.EXTENSION);  
                        video.save(os, "New YouTube Video Created");
                        os.close();  
                        return true;
                    }                                                          
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }
            }                           
            return true;
        }          
        
        @Override
        public void fileFolderCreated(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            DataFolder data = DataFolder.findFolder(file);
            setLastData(data);
            */
        }

        @Override
        public void fileDataCreated(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            try
            {
                YouTubeVideo video = provider.getVideo(Utils.getProperties(file)); 
                getVideos().put(video.getSourceID(), video);                                                              
                setLastSource(video);                
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }             
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            YouTubeVideo video = getVideos().get(file.getName());  
            if(video != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            YouTubeVideo video = getVideos().remove(file.getName());  
            if(video != null)
            {
                video.setDeleted();
                setLastSource(video);
            }
        }

        @Override
        public void fileRenamed(FileRenameEvent fre) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fae) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }         
    }           

// TODO HtmlFilesProvider        
    
    private final class HtmlFilesProviderImpl implements HtmlFilesProvider, FileChangeListener
    {
        private static final String DATA_FOLDER = "html";       
        
        private FileObject dataDir;   
        private Map<String, FileObject> dataFiles;        
        
        private final ChangeSupport changeSupport = new ChangeSupport(this);             

        @Override
        public String getLastDataID() 
        {
            return props.getProperty(PROP_LAST_DATA_ID);
        }

        @Override
        public void setLastDataID(String dataID) 
        {
            if(dataID == null)
            {
                Object oldValue = props.remove(PROP_LAST_DATA_ID);
                propertyChangeSupport.firePropertyChange(PROP_LAST_DATA_ID, oldValue, dataID);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_LAST_DATA_ID, dataID);  
                propertyChangeSupport.firePropertyChange(PROP_LAST_DATA_ID, oldValue, dataID);
            }   
        }                   
        
        @Override
        public synchronized FileObject getDataDirectory() throws IOException 
        {
            if(dataDir == null)
            {
                dataDir = getProjectDirectory().getFileObject(DATA_FOLDER);
                if(dataDir == null)
                {
                    dataDir = getProjectDirectory().createFolder(DATA_FOLDER);
                    LOG.info("Html data dir created: " + dataDir.getPath());
                } 
                dataDir.addFileChangeListener(this);                  
            }
            return dataDir;
        } 
        
        private synchronized Map<String, FileObject> getDataMap()
        {
            if(dataFiles == null)
            {
                dataFiles = new HashMap<>();
                try
                {
                    for (FileObject file : getDataDirectory().getChildren()) 
                    {
                        if(file.getExt().equalsIgnoreCase(FILE_EXT))
                        {
                            dataFiles.put(file.getName(), file);                             
                        }                                                   
                    }                     
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }
            } 
            return dataFiles;
        }  
        
        private synchronized void clear()
        {
            dataFiles = null;           
        }         
        
        @Override
        public Collection<FileObject> getDataFiles()
        {
            return Collections.unmodifiableCollection(getDataMap().values());
        }
        
        @Override
        public FileObject getDataFile(String dataID)
        {
            return getDataMap().get(dataID);
        }
        
        @Override
        public void fileFolderCreated(FileEvent fe) 
        {
            /*
            clear();
            fileEvent = fe;
            cs.fireChange();
            */
        }

        @Override
        public void fileDataCreated(FileEvent fe) 
        {
            FileObject file = fe.getFile();
            if(file.getExt().equalsIgnoreCase(FILE_EXT))
            {
                getDataMap().put(file.getName(), file);                             
            }               
            changeSupport.fireChange();
        }

        @Override
        public void fileChanged(FileEvent fe) 
        {
            changeSupport.fireChange();
        }

        @Override
        public void fileDeleted(FileEvent fe) 
        {
            FileObject file = fe.getFile();
            if(file.getExt().equalsIgnoreCase(FILE_EXT))
            {
                getDataMap().remove(file.getName());                             
            }               
            changeSupport.fireChange();
        }

        @Override
        public void fileRenamed(FileRenameEvent fre) 
        {
            clear();
            changeSupport.fireChange();
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fae) 
        {
            changeSupport.fireChange();
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
        public Lookup.Provider getProvider() 
        {
            return YouTubeChannelProject.this;
        }                 
    }     
}
