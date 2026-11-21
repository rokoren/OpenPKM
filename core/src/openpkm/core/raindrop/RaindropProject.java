/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.raindrop;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Activity;
import com.google.api.services.youtube.model.ActivityListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import openpkm.base.ActionsProvider;
import openpkm.base.Article;
import openpkm.base.ArticleProvider;
import openpkm.base.AsciiDocSupport;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.Book;
import openpkm.base.BookProvider;
import openpkm.base.BulletIconProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.Channel;
import openpkm.base.ChildrenDomain;
import openpkm.base.ChildrenGoal;
import openpkm.base.ChildrenThought;
import openpkm.base.ChildrenTopic;
import openpkm.base.CloseSupport;
import openpkm.base.Content;
import openpkm.base.ContentFactory;
import openpkm.base.DescriptionProvider;
import openpkm.base.Document;
import openpkm.base.Goal;
import openpkm.base.GoalsGraphProvider;
import openpkm.base.GoalsProvider;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.IconProvider;
import openpkm.base.Link;
import openpkm.base.MarkdownSupport;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.base.UpdateCookie;
import openpkm.base.Video;
import openpkm.base.VisibilityProvider;
import openpkm.utils.FileUtils;
import openpkm.utils.Utils;
import openpkm.neo4j.Neo4jInstance;
import openpkm.neo4j.Neo4jProvider;
import openpkm.youtube.YouTubeVideo;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ParentProjectProvider;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.RootProjectProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import openpkm.base.DataGroupProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.IconsProvider;
import openpkm.base.Note;
import openpkm.base.Picture;
import openpkm.base.SourceProviders;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import openpkm.utils.ContentProvider;
import openpkm.youtube.YouTubeVideoProvider;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.util.Utilities;
import openpkm.base.RecycleBinProvider;
import openpkm.base.SourceEvent;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.StateSupport;
import openpkm.utils.DisplayNameProviderImpl;
import openpkm.utils.LogicalViewProviderImpl;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import openpkm.youtube.YouTubeVideoFactory;
import openpkm.reference.ReferenceFactory;
import openpkm.base.SourceEventListener;
import openpkm.base.WorkflowProvider;
import openpkm.utils.SourceEventImpl;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import openpkm.base.ProjectManagementProvider;
import openpkm.base.ProjectManagement;
import openpkm.raindrop.Raindrop;
import openpkm.raindrop.RaindropAccount;
import openpkm.raindrop.RaindropChildrenCollection;
import openpkm.raindrop.RaindropCollection;
import openpkm.raindrop.RaindropCollectionProvider;
import openpkm.raindrop.RaindropFactory;
import openpkm.raindrop.RaindropProvider;
import openpkm.raindrop.RaindropService;
import openpkm.raindrop.RaindropTag;
import openpkm.raindrop.RaindropUtils;
import openpkm.base.DataProvider;
import openpkm.base.Domain;
import openpkm.base.DomainsGraphProvider;
import openpkm.base.DomainsProvider;
import openpkm.base.DrawboardPdf;
import openpkm.base.DrawboardPdfFactory;
import openpkm.base.LiteratureNote;
import openpkm.base.LiteratureNoteProvider;
import openpkm.base.OpenSupport;
import openpkm.base.ReadLaterProvider;
import openpkm.base.Thought;
import openpkm.base.ThoughtsGraphProvider;
import openpkm.base.ThoughtsProvider;
import openpkm.base.TopicsGraphProvider;
import openpkm.base.WatchLaterProvider;
import openpkm.core.drawboard.pdf.AbstractDrawboardPdf;
import openpkm.domain.WebPage;
import openpkm.domain.WebPageFactory;
import openpkm.domain.WebPageProvider;
import openpkm.rss.RssChannel;
import openpkm.rss.RssChannelProvider;
import openpkm.utils.DrawboardPdfProvider;
import openpkm.youtube.YouTubeChannel;
import openpkm.youtube.YouTubeChannelFactory;
import openpkm.youtube.YouTubeChannelProvider;
import org.neo4j.driver.Session;
import openpkm.rss.RssChannelFactory;
import openpkm.utils.NotificationUtils;
import openpkm.youtube.GooglePasswordProvider;
import openpkm.youtube.YouTubeService;

/**
 *
 * @author Rok Koren
 */
public class RaindropProject implements Project, PropertiesProvider, RaindropCollectionProvider, TitleProvider, DescriptionProvider, TagsProvider, SourceProviders, BatchUpdateSupport
{
    public static final String PROP_RAINDROP_USER_ID         = "raindrop.user.id";    
    public static final String PROP_RAINDROP_COLLECTION_ID   = "raindrop.collection.id";
    public static final String PROP_RAINDROP_COLLECTION_ROOT = "raindrop.collection.root";
    public static final String PROP_RAINDROP_PUBLISHED_DATE  = "raindrop.published.date";
    
    public static final String PROP_NEO4J_INSTANCE_ID   = "neo4j.instance.id";   
    public static final String PROP_TRELLO_USERNAME     = "trello.username"; 
    public static final String PROP_TRELLO_WORKSPACE_ID = "trello.workspace.id";                      
    
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_NOTES       = 100;
    private static final int POSITION_DOCUMENTS   = 200;
    private static final int POSITION_ARTICLES    = 300;
    private static final int POSITION_BOOKS       = 400;
    private static final int POSITION_LINKS       = 500;
    private static final int POSITION_PICTURES    = 600;    
    private static final int POSITION_VIDEOS      = 700;
    private static final int POSITION_READ_LATER  = 800;
    private static final int POSITION_WATCH_LATER = 900;
    private static final int POSITION_CHANNELS    = 1000;     
    private static final int POSITION_RECYCLE_BIN = 1100;           

    private static final Logger LOG = Logger.getLogger(RaindropProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(RaindropProject.class);         
    
    private final Map<String, SourceProvider> sources = new HashMap();  
    private final List<UpdateCookie> cookies = new ArrayList();  
    private final List<Domain> selectedDomains = new ArrayList(); 
    private final List<Topic> selectedTopics = new ArrayList(); 
    private final List<Goal> selectedGoals = new ArrayList();     
    private final List<Thought> selectedThoughts = new ArrayList();  
    
    private final FileObject projectDir;        
    private final ProjectState state;
    private final Properties props;
    private final PropertyChangeSupport propertyChangeSupport;  
    private final EventListenerList listeners;
    
    private Lookup lkp;  
    private FileObject dataDir;
    private LocalFileSystem fileSystem;
    
    private RaindropAccount raindropAccount; 
    private RaindropCollection raindropCollection;  
    private Neo4jInstance neo4jInstance;       
    
    public RaindropProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;
        propertyChangeSupport = new PropertyChangeSupport(this);
        listeners = new EventListenerList();        
        
        RaindropFactory raindropFactory = Lookup.getDefault().lookup(RaindropFactory.class);
        if(raindropFactory != null)
        {
            SourceProvider provider = new RaindropProviderImpl(raindropFactory);  
            sources.put(provider.getName(), provider);             
        }

        ContentFactory contentFactory = Lookup.getDefault().lookup(ContentFactory.class);
        if(contentFactory != null)
        {          
            SourceProvider provider = new ContentProviderImpl(contentFactory);
            sources.put(provider.getName(), provider);            
        }
        
        DrawboardPdfFactory drawboardPdfFactory = Lookup.getDefault().lookup(DrawboardPdfFactory.class);
        if(drawboardPdfFactory != null)
        {          
            SourceProvider provider = new DrawboardPdfProviderImpl(drawboardPdfFactory);
            sources.put(provider.getName(), provider);            
        }         
        
        ReferenceFactory referenceFactory = Lookup.getDefault().lookup(ReferenceFactory.class);
        if(referenceFactory != null)
        {          
            SourceProvider provider = new ReferenceProviderImpl(referenceFactory);
            sources.put(provider.getName(), provider);            
        }  
        
        WebPageFactory webPageFactory = Lookup.getDefault().lookup(WebPageFactory.class);
        if(webPageFactory != null)
        {          
            SourceProvider links = new WebPageProviderImpl(webPageFactory);
            sources.put(links.getName(), links);            
        }          

        YouTubeVideoFactory youTubeVideoFactory = Lookup.getDefault().lookup(YouTubeVideoFactory.class);
        if(youTubeVideoFactory != null)
        {
            SourceProvider provider = new YouTubeVideoProviderImpl(youTubeVideoFactory);
            sources.put(provider.getName(), provider);                       
        }  
        
        YouTubeChannelFactory youTubeChannelFactory = Lookup.getDefault().lookup(YouTubeChannelFactory.class);
        if(youTubeChannelFactory != null)
        {
            SourceProvider provider = new YouTubeChannelProviderImpl(youTubeChannelFactory);
            sources.put(provider.getName(), provider);                       
        }   
        
        RssChannelFactory rssFactory = Lookup.getDefault().lookup(RssChannelFactory.class);
        if(rssFactory != null)
        {
            RssChannelProvider provider = new RssChannelProviderImpl(rssFactory);
            sources.put(provider.getName(), provider);             
        }        
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
    public void sourceDeleted(SourceEvent evt)
    {
        for(SourceEventListener listener : listeners.getListeners(SourceEventListener.class))
        {
            listener.sourceDeleted(evt);
        }
    }
    
    @Override
    public void sourceModified(SourceEvent evt)
    {
        for(SourceEventListener listener : listeners.getListeners(SourceEventListener.class))
        {
            listener.sourceModified(evt);
        }
    }   
    
    @Override
    public void sourceAdded(SourceEvent evt)
    {
        for(SourceEventListener listener : listeners.getListeners(SourceEventListener.class))
        {
            listener.sourceAdded(evt);
        }
    }     
    
    @Override
    public SourceProvider getSourceProvider(String folder)
    {
        return sources.get(folder);
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
            Neo4jInstance instance = getNeo4jInstance();
            RaindropCollection collection = getRaindropCollection();
            if(collection != null && instance != null)
            {
                list.add(collection);
                list.add(instance);

                list.add(this);
                list.add(new Info());
                list.add(new SourcesImpl());
                list.add(new DisplayNameProviderImpl(this));
                list.add(new IconProviderImpl());
                list.add(new ProjectOpenedHookImpl());   
                list.add(new SubprojectProviderImpl());
                list.add(new RootProjectProviderImpl());
                
                if(collection instanceof RaindropChildrenCollection)
                {
                    list.add(new ParentProjectProviderImpl());  
                }                
                
                list.add(new LogicalViewProviderImpl(this));
                list.add(new RaindropCustomizerProvider(this));  
                
                /*
                list.add(new YouTubeProjectsProviderImpl());                
                list.add(new GtdProjectsProviderImpl());  
                */
                 
                list.add(new ProjectManagementProviderImpl()); 
                list.add(new HtmlFilesProviderImpl());  
                list.add(new DomainsGraphProviderImpl());
                list.add(new TopicsGraphProviderImpl());
                list.add(new GoalsGraphProviderImpl());   
                list.add(new ThoughtsGraphProviderImpl());  
                                
                list.addAll(sources.values());
                
                list.add(new ChannelProviderImpl());
                list.add(new NoteProviderImpl()); 
                list.add(new BookProviderImpl()); 
                list.add(new ArticleProviderImpl()); 
                list.add(new DocumentProviderImpl()); 
                list.add(new LinkProviderImpl());                
                list.add(new PictureProviderImpl()); 
                list.add(new VideoProviderImpl()); 
                list.add(new ReadLaterProviderImpl()); 
                list.add(new WatchLaterProviderImpl()); 
                list.add(new RecycleBinProviderImpl());  
                list.add(new DataProviderImpl()); 
            }                                   
            
            lkp = Lookups.fixed(list.toArray(new Object[list.size()]));              
        }
        return lkp;
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

// TODO TagsProvider

    @Override
    public Set<String> getTags()
    {
        String tags = props.getProperty(PROP_TAGS);
        if(tags != null&& !tags.isBlank())
        {
            return Set.of(tags.split(","));                   
        }                
        return Collections.EMPTY_SET;
    }    

    public void setTags(Set<String> tags)
    {
        if(tags == null)
        {
            Object oldValue = props.remove(PROP_TAGS);
            if(oldValue != null)
            {
                oldValue = Set.of(oldValue.toString().split(","));
            }
            propertyChangeSupport.firePropertyChange(PROP_TAGS, oldValue, tags);
        }
        else        
        {
            StringJoiner joiner = new StringJoiner(",");
            Iterator<String> iterator = tags.iterator();
            while(iterator.hasNext())
            {
                joiner.add(iterator.next());
            }                  

            Object oldValue = props.setProperty(PROP_TAGS, joiner.toString()); 

            if(oldValue != null)
            {
                oldValue = Set.of(oldValue.toString().split(","));
            }                

            propertyChangeSupport.firePropertyChange(PROP_TAGS, oldValue, tags);
        }             
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

// TODO RaindropCollectionProvider     
    
    @Override
    public synchronized RaindropAccount getRaindropAccount()
    {
        if(raindropAccount == null)
        {
            String userID = props.getProperty(PROP_RAINDROP_USER_ID);
            if (userID != null)
            {
                raindropAccount = RaindropService.getDefault().getAccount(Integer.parseInt(userID));              
            }             
        }
        return raindropAccount;
    }     
    
    @Override
    public synchronized RaindropCollection getRaindropCollection()
    {
        if(raindropCollection == null)
        {            
            String collectionID = props.getProperty(PROP_RAINDROP_COLLECTION_ID);
            String string = props.getProperty(PROP_RAINDROP_COLLECTION_ROOT);
            if (collectionID != null && string != null)
            {
                RaindropAccount account = getRaindropAccount();
                if(account != null)
                {
                    boolean isRoot= Boolean.parseBoolean(string);                        
                    try
                    {
                        if(isRoot)
                        {
                            raindropCollection = account.getRootCollection(Integer.parseInt(collectionID));                                                             
                        }
                        else
                        {
                            raindropCollection = account.getChildrenCollection(Integer.parseInt(collectionID)); 
                        }
                    }
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    } 
                }               
            }             
        }
        return raindropCollection;
    } 
    
    public LocalDateTime getRaindropPublishedDate()
    {
        String string = props.getProperty(PROP_RAINDROP_PUBLISHED_DATE);
        if(string != null)
        {
            return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
    }

    public void setRaindropPublishedDate(LocalDateTime time)
    {
        if(time == null)
        {
            Object oldValue = props.remove(PROP_RAINDROP_PUBLISHED_DATE);
            propertyChangeSupport.firePropertyChange(PROP_RAINDROP_PUBLISHED_DATE, oldValue, time);            
        }
        else
        {
            String newValue = time.format(DateTimeFormatter.ISO_DATE_TIME);
            Object oldValue = props.setProperty(PROP_RAINDROP_PUBLISHED_DATE, newValue);  
            propertyChangeSupport.firePropertyChange(PROP_RAINDROP_PUBLISHED_DATE, oldValue, newValue);            
        }
    }    
    
// TODO Neo4j    
    
    private synchronized Neo4jInstance getNeo4jInstance()
    {
        if(neo4jInstance == null)
        {
            String instanceID = props.getProperty(PROP_NEO4J_INSTANCE_ID);
            if(instanceID != null)
            {
                Neo4jProvider provider = Lookup.getDefault().lookup(Neo4jProvider.class);
                if(provider != null)
                {
                    neo4jInstance = provider.getInstance(instanceID);
                }
            }            
        }
        return neo4jInstance;
    } 
    
// TODO ProjectOpenedHook    
    
    private final class ProjectOpenedHookImpl extends ProjectOpenedHook implements PropertyChangeListener
    {
        @Override
        protected void projectOpened() 
        {  
            propertyChangeSupport.addPropertyChangeListener(this);
            
            Collection<? extends OpenSupport> providers = getLookup().lookupAll(OpenSupport.class);            
            for(OpenSupport provider : providers)
            {
                provider.open();
            }                
        }

        @Override
        protected void projectClosed() 
        { 
            propertyChangeSupport.removePropertyChangeListener(this);
            
            Collection<? extends CloseSupport> providers = getLookup().lookupAll(CloseSupport.class);            
            for(CloseSupport provider : providers)
            {
                provider.close();
            } 
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
    }
    
// TODO ProjectInformation 
    
    private final class Info implements ProjectInformation
    {                     
        @Override
        public Icon getIcon()
        {                    
            return new ImageIcon(ImageUtilities.loadImage(AbstractRaindrop.ICON));
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
            return RaindropProject.this;
        }
    }    
    
// TODO Sources    
    
    private final class SourcesImpl implements Sources
    {  
        private final ChangeSupport changeSupport = new ChangeSupport(this);         
        
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
    }          
  
// TODO IconProvider    
    
    private final class IconProviderImpl implements IconProvider, ChangeSupportProvider, Runnable
    {        
        private Image icon; 
        private boolean isLoading;
        
        private final ChangeSupport changeSupport = new ChangeSupport(this); 

        @Override
        public synchronized Image getIcon(int type)
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
            return ImageUtilities.loadImage(AbstractRaindrop.ICON);
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
            RaindropCollection collection = getRaindropCollection();
            try 
            {               
                BufferedImage image = collection.getImage();                                              
                if(image != null)
                {
                    icon = Utils.resizeImage(image, 16, 16);                  
                }                
                if(icon != null)
                {
                    changeSupport.fireChange();
                }
            } 
            catch (IOException ex) 
            {
                LOG.warning(ex.getMessage());
            }
            finally
            {
                isLoading = false;
            }
        }                
    } 

// TODO RootProjectProvider     

    private final class RootProjectProviderImpl implements RootProjectProvider
    {
        @Override
        public Project getRootProject() 
        {
            return Utils.getRootProject(RaindropProject.this);
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
                        if(project instanceof RaindropProject)
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
    
// TODO SubprojectProvider    
    
    private final class SubprojectProviderImpl implements SubprojectProvider
    {
        private static final String ROOT_FOLDER = "topics";
        
        private FileObject rootDir;                
        private Map<String, RaindropProject> projects;
        
        private final ChangeSupport changeSupport = new ChangeSupport(this);                         
        
        private synchronized FileObject getRootDirectory() throws IOException 
        {
            if(rootDir == null)
            {
                rootDir = getProjectDirectory().getFileObject(ROOT_FOLDER);
                if(rootDir == null)
                {
                    rootDir = getProjectDirectory().createFolder(ROOT_FOLDER);
                    LOG.info("YouTube channels root directory created: " + rootDir.getPath());
                }               
            }
            return rootDir;
        }

        private synchronized Map<String, RaindropProject> getProjectsMap() 
        {
            if(projects == null)
            {
                projects = new HashMap();
                try
                {
                    for (FileObject fo : getRootDirectory().getChildren()) 
                    {
                        if (fo.isFolder()) 
                        {
                            Project project = ProjectManager.getDefault().findProject(fo);
                            if (project instanceof RaindropProject)
                            {
                                RaindropProject prj = (RaindropProject)project;
                                projects.put(prj.getProjectDirectory().getName(), prj);
                            }                      
                        }                    
                    }                     
                }
                catch (IOException ex) 
                {
                    LOG.warning(ex.getMessage());
                }                 
            }
            return projects;
        }
        
        @Override
        public Set<? extends Project> getSubprojects()
        {
            return getProjects().stream().collect(Collectors.toUnmodifiableSet());
        }
        
        private Collection<RaindropProject> getProjects() 
        {
            return Collections.unmodifiableCollection(getProjectsMap().values());
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
    }     

// TODO BoardsProvider

    private final class ProjectManagementProviderImpl implements ProjectManagementProvider
    {                 
        private static final String ROOT_FOLDER = "projects";          
        
        private Map<String, ProjectManagement> projects; 
        private FileObject rootDir;            
        
        private final ChangeSupport changeSupport;              

        public ProjectManagementProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
        } 
        
        @Override
        public synchronized FileObject getRootDirectory() throws IOException
        {
            if(rootDir == null)
            {
                rootDir = getProjectDirectory().getFileObject(ROOT_FOLDER);
                if(rootDir == null)
                {
                    rootDir = getProjectDirectory().createFolder(ROOT_FOLDER);
                    LOG.info("Notebook dir created: " + rootDir.getPath());                        
                }                 
            }                           
            return rootDir;       
        }         
        
        private synchronized Map<String, ProjectManagement> getProjectsById()
        {
            if(projects == null)
            {
                projects = new HashMap<>();
                try
                {
                    for (FileObject fo : getRootDirectory().getChildren()) 
                    {
                        if(fo.isFolder())
                        {
                            Project prj = ProjectManager.getDefault().findProject(fo);
                            if(prj instanceof ProjectManagement project)
                            {
                                projects.put(project.getProjectID(), project);
                            }                                                                                    
                        }
                        else
                        {
                            DataObject data = DataObject.find(fo);
                            ProjectManagement project = data.getLookup().lookup(ProjectManagement.class);
                            if(project != null)
                            {
                                projects.put(project.getProjectID(), project);
                            }                            
                        }                                                                                                                                            
                    }                      
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                              
            }
            return projects;
        }  

        @Override
        public Collection<ProjectManagement> getProjects()
        {
            return Collections.unmodifiableCollection(getProjectsById().values());
        }
        
        @Override
        public void addProject(ProjectManagement project)
        {
            getProjectsById().put(project.getProjectID(), project);
            changeSupport.fireChange();            
        }
        
        @Override
        public void removeProject(String projectID)
        {
            ProjectManagement project = getProjectsById().remove(projectID);
            if(project != null)
            {
                changeSupport.fireChange();                            
            }
        }        
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/ProjectManagement"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
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
        public String getName() 
        {
            return "project_management";
        }

        @Override
        public String getDisplayName() 
        {
            return "Projects";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.PROJECTS);
        }               
    }     
   
// TODO DataProvider

    private final class DataProviderImpl implements DataProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public DataProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }              
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }              
        
        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {  
                        if(source instanceof StateSupport state)
                        {
                            if(state.isDeleted())
                            {
                                return false;
                            }
                        }
                        if(source instanceof WorkflowProvider provider)
                        {
                            if(provider.getWorkflow() == WorkflowProvider.Workflow.RECYCLE_BIN)
                            {
                                return false;                                
                            }
                        } 
                        return true;
                    }            
                }                                                                                  
            }                                    
            return false;            
        }

        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            changeSupport.fireChange();
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            changeSupport.fireChange();
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            changeSupport.fireChange();
        } 
    }     
    
// TODO DataGroup    

    private final class ReadLaterProviderImpl implements ReadLaterProvider, BulletIconProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public ReadLaterProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }              
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }  
        
        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_READ_LATER;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_READ_LATER;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_READ_LATER;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_READ_LATER;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.timeCreatedComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return true;
        } 
        
        @Override
        public boolean isNotEmpty()
        {
            try
            {
                for(FileObject file : getFiles())
                {
                    DataObject data = null;
                    if(file.isData())
                    {
                        try
                        {
                            data = DataObject.find(file);                    
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.warning(e.getMessage());
                        }
                    }
                    else if(file.isFolder())
                    {
                        data = DataFolder.findFolder(file);
                    } 
                    
                    if(contains(data))
                    {
                        return true;                        
                    }
                }              
            } 
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }            
            
            return false;
        }                 
                
        @Override
        public String getName() 
        {
            return "read_later";
        }
        
        @Override
        public Image getBullet()
        {
            try
            {
                for(FileObject file : getFiles())
                {
                    DataObject data = DataObject.find(file);
                    if(contains(data))
                    {
                        IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);            
                        return provider.getImage(IconsProvider.ICON.BULLET_BLUE);
                    }
                }
            }
            catch(IOException e)                
            {
                LOG.warning(e.getMessage());
            }
            return null;
        }         
        
        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {                                               
                        WorkflowProvider workflowProvider = source.getLookup().lookup(WorkflowProvider.class);
                        if(workflowProvider != null)
                        {                                                       
                            return workflowProvider.getWorkflow() == WorkflowProvider.Workflow.READ_LATER;
                        }                                                 
                    }            
                }                                                                                  
            }                                    
            return false;            
        }                     

        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof WorkflowProvider)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof WorkflowProvider)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof WorkflowProvider)
            {
                changeSupport.fireChange();
            }
        } 
    }      
    
    private final class WatchLaterProviderImpl implements WatchLaterProvider, BulletIconProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public WatchLaterProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }              
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }  
        
        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_WATCH_LATER;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_WATCH_LATER;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_WATCH_LATER;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_WATCH_LATER;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.timeCreatedComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return true;
        } 
        
        @Override
        public boolean isNotEmpty()
        {
            try
            {
                for(FileObject file : getFiles())
                {
                    DataObject data = null;
                    if(file.isData())
                    {
                        try
                        {
                            data = DataObject.find(file);                    
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.warning(e.getMessage());
                        }
                    }
                    else if(file.isFolder())
                    {
                        data = DataFolder.findFolder(file);
                    } 
                    
                    if(contains(data))
                    {
                        return true;                        
                    }
                }              
            } 
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }            
            
            return false;
        }                 
                
        @Override
        public String getName() 
        {
            return "watch_later";
        }
        
        @Override
        public Image getBullet()
        {
            try
            {
                for(FileObject file : getFiles())
                {
                    DataObject data = DataObject.find(file);
                    if(contains(data))
                    {
                        IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);            
                        return provider.getImage(IconsProvider.ICON.BULLET_BLUE);
                    }
                }
            }
            catch(IOException e)                
            {
                LOG.warning(e.getMessage());
            }
            return null;
        }         
        
        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {  
                        if(source instanceof StateSupport state)
                        {
                            if(state.isDeleted())
                            {
                                return false;
                            }
                        }
                        if(source instanceof WorkflowProvider provider)
                        {
                            return provider.getWorkflow() == WorkflowProvider.Workflow.WATCH_LATER;
                        }                                                                        
                    }            
                }                                                                                  
            }                                    
            return false;            
        } 
        
        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof WorkflowProvider)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof WorkflowProvider)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof WorkflowProvider)
            {
                changeSupport.fireChange();
            }
        }         
    }     
    
    private final class RecycleBinProviderImpl implements RecycleBinProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public RecycleBinProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }              
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }  
        
        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_RECYCLE_BIN;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_RECYCLE_BIN;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_RECYCLE_BIN;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_RECYCLE_BIN;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public boolean isNotEmpty()
        {
            try
            {
                for(FileObject file : getFiles())
                {
                    DataObject data = null;
                    if(file.isData())
                    {
                        try
                        {
                            data = DataObject.find(file);                    
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.warning(e.getMessage());
                        }
                    }
                    else if(file.isFolder())
                    {
                        data = DataFolder.findFolder(file);
                    }  

                    if(contains(data))
                    {
                        return true;                        
                    }
                }              
            } 
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }            
            
            return false;
        }          
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.timeCreatedComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return true;
        }                
                
        @Override
        public String getName() 
        {
            return "recycle_bin";
        }               
        
        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {  
                        if(source instanceof StateSupport state)
                        {
                            if(state.isDeleted())
                            {
                                return false;
                            }
                        }
                        if(source instanceof WorkflowProvider provider)
                        {
                            return provider.getWorkflow() == WorkflowProvider.Workflow.RECYCLE_BIN;
                        }                                                                     
                    }            
                }                                                                                  
            }                                    
            return false;            
        }

        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof WorkflowProvider)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof WorkflowProvider)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof WorkflowProvider)
            {
                changeSupport.fireChange();
            }
        } 
    }        
    
    private final class ChannelProviderImpl implements DataGroupProvider, SourceEventListener
    {                        
        private final ChangeSupport changeSupport; 

        public ChannelProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }   
        
        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_CHANNEL;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_CHANNEL;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_CHANNEL;
        }          
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_CHANNELS;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.displayNameComparator();
        }  
        
        @Override
        public boolean isReversed()
        {
            return false;
        }        

        @Override
        public String getName() 
        {
            return "channel";
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {
                        Channel channel = source.getLookup().lookup(Channel.class);
                        if(channel != null)
                        {
                            return true;
                        }  
                    }            
                }                                                               
            }                                    
            return false;
        }           

        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Channel)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Channel)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Channel)
            {
                changeSupport.fireChange();
            }
        }
    }      
    
    private final class NoteProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public NoteProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }   
        
        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_NOTE;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_NOTE;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_NOTE;
        }          
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_NOTES;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.displayNameComparator();
        }  
        
        @Override
        public boolean isReversed()
        {
            return false;
        }        

        @Override
        public String getName() 
        {
            return "note";
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {
                        Note note = source.getLookup().lookup(Note.class);
                        if(note != null)
                        {
                            return true;
                        }  
                    }            
                }                                                               
            }                                    
            return false;
        }
        
        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Note)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Note)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Note)
            {
                changeSupport.fireChange();
            }
        }        
    }  
    
    private final class BookProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public BookProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }              
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 

        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_BOOK;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_BOOK;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_BOOK;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.displayNameComparator();
        }  
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "book";
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {
                        Book book = source.getLookup().lookup(Book.class);
                        if(book != null)
                        {
                            return true;
                        }  
                    }            
                }                                                 
            }                                  
            return false;
        }
        
        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Book)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Book)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Book)
            {
                changeSupport.fireChange();
            }
        }         
    }   
    
    private final class ArticleProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public ArticleProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }              
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }  
        
        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_ARTICLE;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_ARTICLE;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_ARTICLE;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.displayNameComparator();
        }  
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "article";
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {
                        Article article = source.getLookup().lookup(Article.class);
                        if(article != null)
                        {
                            return true;
                        }                      
                    }            
                }                                                                   
            }                                    
            return false;
        }
        
        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Article)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Article)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Article)
            {
                changeSupport.fireChange();
            }
        }          
    } 
    
    private final class DocumentProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public DocumentProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 

                @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_DOCUMENT;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_DOCUMENT;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_DOCUMENT;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.displayNameComparator();
        }  
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "document";
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {
                        Document document = source.getLookup().lookup(Document.class);
                        if(document != null)
                        {
                            return true;
                        }                           
                    }            
                }                                                                            
            }                                    
            return false;
        }
        
        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Document)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Document)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Document)
            {
                changeSupport.fireChange();
            }
        }         
    }  

    private final class LinkProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public LinkProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_LINK;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_LINK;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_LINK;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.displayNameComparator();
        }  
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "link";
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {
                        Link link = source.getLookup().lookup(Link.class);
                        if(link != null)
                        {
                            if(source instanceof StateSupport state)
                            {
                                if(state.isDeleted())
                                {
                                    return false;
                                }
                            }
                            if(source instanceof WorkflowProvider provider)
                            {
                                return provider.getWorkflow() == WorkflowProvider.Workflow.DEFAULT;
                            }
                            return true;
                        }                                                  
                    }            
                }                                                                                  
            }                                    
            return false;
        }
        
        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Link)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Link)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Link)
            {
                changeSupport.fireChange();
            }
        }          
    }  

    private final class PictureProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 
                
        public PictureProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_PICTURE;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_PICTURE;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_PICTURE;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.displayNameComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "picture";
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {
                        Picture picture = source.getLookup().lookup(Picture.class);
                        if(picture != null)
                        {
                            return true;
                        }                                                                          
                    }            
                }                                                                                
            }                                   
            return false;
        }
        
        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Picture)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Picture)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Picture)
            {
                changeSupport.fireChange();
            }
        }         
    } 
    
    private final class VideoProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 
                
        public VideoProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public DisplayNameProvider getDisplayNameProvider() 
        {
            return DISPLAY_NAME_PROVIDER_VIDEO;
        } 
        
        @Override
        public IconProvider getIconProvider()
        {
            return ICON_PROVIDER_VIDEO;
        }
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_VIDEO;
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
        public List<FileObject> getFiles() throws IOException
        {
            return List.of(getDataDirectory().getChildren());
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.displayNameComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "video";
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider != null)
                {
                    Source source = sourceProvider.getSource();
                    if(source != null)
                    {
                        Video video = source.getLookup().lookup(Video.class);
                        if(video != null)
                        {
                            if(source instanceof StateSupport state)
                            {
                                if(state.isDeleted())
                                {
                                    return false;
                                }
                            }
                            if(source instanceof WorkflowProvider provider)
                            {
                                return provider.getWorkflow() == WorkflowProvider.Workflow.DEFAULT;
                            }
                            return true;
                        }                                                                                                
                    }            
                }                                                                 
            }                                   
            return false;
        }
        
        @Override
        public void sourceDeleted(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Video)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceModified(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Video)
            {
                changeSupport.fireChange();
            }
        }

        @Override
        public void sourceAdded(SourceEvent evt) 
        {
            if(evt.getSource() instanceof Video)
            {
                changeSupport.fireChange();
            }
        }         
    }    
    
// TODO SourceGroup          
    
    private final class ContentProviderImpl extends ContentProvider implements FileChangeListener, CloseSupport
    {  
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/book_edit.png";         
        
        public ContentProviderImpl(ContentFactory factory) 
        {
            super(factory);
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public void close()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(Content content : getContents())
                {
                    FileObject file = rootDir.getFileObject(content.getSourceID(), PropertiesProvider.EXTENSION);
                    if(file != null)
                    {
                        try
                        {
                            if(content.isModified())
                            {
                                OutputStream os = file.getOutputStream();
                                factory.save(content, os, "Updated by Raindrop project: " + getTitle());
                                os.close();
                            }
                            else if(content.isDeleted())
                            {
                                file.delete();
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
        
        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }        
        
        @Override
        public synchronized Map<String, Content> getContentsById()
        {
            if(contents == null)
            {
                contents = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            Content content = factory.getContent(Utils.getProperties(file)); 
                            contents.put(content.getSourceID(), content);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return contents;
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
                        LOG.info("Content root folder created: " + rootDir.getPath());                        
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
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }         
        
        @Override
        public FileObject createData(Content content, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, content.getSourceID());  

            if(content instanceof Article)
            {
                Article article = (Article)content;
                if(fileTypeProvider instanceof ArticleProvider)
                {
                    ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(articleProvider.getArticle(article.getTitle(), article.getPublisher()).getBytes());
                    output.close();
                }                         
            }
            else if(content instanceof Book)
            {
                Book book = (Book)content;
                if(fileTypeProvider instanceof BookProvider)
                {
                    BookProvider bookProvider = (BookProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(bookProvider.getBook(book.getTitle(), book.getAuthors()).getBytes());
                    output.close();
                }                         
            } 
            else if(content instanceof LiteratureNote)
            {
                LiteratureNote literatureNote = (LiteratureNote)content;
                if(fileTypeProvider instanceof LiteratureNoteProvider)
                {
                    LiteratureNoteProvider literatureNoteProvider = (LiteratureNoteProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(literatureNoteProvider.getLiteratureNote(literatureNote.getPrimaryFileName(), literatureNote.getPrimaryTitle(), literatureNote.getTitle(), literatureNote.getSubtitle(), literatureNote.getAuthorName(), literatureNote.getSourceUrl(), literatureNote.getSummary()).getBytes());
                    output.close();
                }                         
            }             
            
            return primaryFile;            
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
                Content content = factory.getContent(Utils.getProperties(file)); 
                getContentsById().put(content.getSourceID(), content);               
                sourceAdded(new SourceEventImpl(this, content));
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }               
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            Content content = getContentsById().get(file.getName());  
            if(content != null)
            {
                
            }
            */
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Content content = getContentsById().remove(file.getName());  
            if(content != null)
            {
                content.notifyDeleted();
                sourceDeleted(new SourceEventImpl(this, content)); 
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
    
    private final class DrawboardPdfProviderImpl extends DrawboardPdfProvider implements FileChangeListener, CloseSupport
    {          
        public DrawboardPdfProviderImpl(DrawboardPdfFactory factory) 
        {
            super(factory);
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public void close()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(DrawboardPdf pdf : getPdfs())
                {
                    FileObject file = rootDir.getFileObject(pdf.getSourceID(), PropertiesProvider.EXTENSION);
                    if(file != null)
                    {
                        try
                        {
                            if(pdf.isModified())
                            {
                                OutputStream os = file.getOutputStream();
                                factory.save(pdf, os, "Updated by Raindrop project: " + getTitle());
                                os.close();
                            }
                            else if(pdf.isDeleted())
                            {
                                file.delete();
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
        
        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(AbstractDrawboardPdf.ICON));
        }        
        
        @Override
        public synchronized Map<String, DrawboardPdf> getPdfsById()
        {
            if(pdfs == null)
            {
                pdfs = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            DrawboardPdf pdf = factory.getDrawboardPdf(Utils.getProperties(file)); 
                            pdfs.put(pdf.getSourceID(), pdf);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return pdfs;
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
                        LOG.info("Drawboard PDF root folder created: " + rootDir.getPath());                        
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
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }         
        
        @Override
        public FileObject createData(DrawboardPdf pdf, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, pdf.getSourceID());  

            if(pdf instanceof Article)
            {
                Article article = (Article)pdf;
                if(fileTypeProvider instanceof ArticleProvider)
                {
                    ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(articleProvider.getArticle(article.getTitle(), article.getPublisher()).getBytes());
                    output.close();
                }                         
            }
            else if(pdf instanceof Book)
            {
                Book book = (Book)pdf;
                if(fileTypeProvider instanceof BookProvider)
                {
                    BookProvider bookProvider = (BookProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(bookProvider.getBook(book.getTitle(), book.getAuthors()).getBytes());
                    output.close();
                }                         
            } 
            else if(pdf instanceof LiteratureNote)
            {
                LiteratureNote literatureNote = (LiteratureNote)pdf;
                if(fileTypeProvider instanceof LiteratureNoteProvider)
                {
                    LiteratureNoteProvider literatureNoteProvider = (LiteratureNoteProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(literatureNoteProvider.getLiteratureNote(literatureNote.getPrimaryFileName(), literatureNote.getPrimaryTitle(), literatureNote.getTitle(), literatureNote.getSubtitle(), literatureNote.getAuthorName(), literatureNote.getSourceUrl(), literatureNote.getSummary()).getBytes());
                    output.close();
                }                         
            }             
            
            return primaryFile;            
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
                DrawboardPdf pdf = factory.getDrawboardPdf(Utils.getProperties(file)); 
                getPdfsById().put(pdf.getSourceID(), pdf);               
                sourceAdded(new SourceEventImpl(this, pdf));
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }               
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            Content content = getContentsById().get(file.getName());  
            if(content != null)
            {
                
            }
            */
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            DrawboardPdf pdf = getPdfsById().remove(file.getName());  
            if(pdf != null)
            {
                pdf.notifyDeleted();
                sourceDeleted(new SourceEventImpl(this, pdf)); 
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
    
    private final class ReferenceProviderImpl extends ReferenceProvider implements FileChangeListener, CloseSupport
    {               
        public ReferenceProviderImpl(ReferenceFactory factory) 
        {
            super(factory);
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }  
        
        @Override
        public void close()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(Reference reference : getReferences())
                {
                    FileObject file = rootDir.getFileObject(reference.getSourceID(), PropertiesProvider.EXTENSION);
                    if(file != null)
                    {
                        try
                        {
                            if(reference.isModified())
                            {
                                OutputStream os = file.getOutputStream();
                                factory.save(reference, os, "Updated by Raindrop project: " + getTitle());
                                os.close();
                            }
                            else if(reference.isDeleted())
                            {
                                file.delete();
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
        
        @Override
        public synchronized Map<String, Reference> getReferencesById()
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
                            Reference reference = factory.getReference(Utils.getProperties(file)); 
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
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }         
        
        @Override
        public FileObject createData(Reference reference, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, reference.getSourceID());  

            if(reference instanceof Article)
            {
                Article article = (Article)reference;
                if(fileTypeProvider instanceof ArticleProvider)
                {
                    ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
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
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(bookProvider.getBook(book.getTitle(), book.getAuthors()).getBytes());
                    output.close();
                }                         
            }            

            return primaryFile;
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
                Reference reference = factory.getReference(Utils.getProperties(file)); 
                getReferencesById().put(reference.getSourceID(), reference);               
                sourceAdded(new SourceEventImpl(this, reference));             
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }               
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            Reference reference = getReferencesById().get(file.getName());  
            if(reference != null)
            {
                
            }
            */
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Reference reference = getReferencesById().remove(file.getName());  
            if(reference != null)
            {
                reference.notifyDeleted();
                sourceDeleted(new SourceEventImpl(this, reference));                          
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
    
    private final class WebPageProviderImpl extends WebPageProvider implements FileChangeListener, CloseSupport
    {  
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/www_page.png";         
        
        public WebPageProviderImpl(WebPageFactory factory) 
        {
            super(factory);
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public void close()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(WebPage page : getPages().getPages())
                {
                    FileObject file = rootDir.getFileObject(page.getFileName(), PropertiesProvider.EXTENSION);
                    if(file != null)
                    {
                        try
                        {
                            if(page.isModified())
                            {
                                OutputStream os = file.getOutputStream();
                                factory.save(page, os, "Updated by Raindrope project: " + getTitle());
                                os.close();
                            }
                            else if(page.isDeleted())
                            {
                                file.delete();
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
        
        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }        
        
        @Override
        public synchronized Pages getPages()
        {
            if(pages == null)
            {
                pages = new Pages();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            WebPage page = factory.getWebPage(Utils.getProperties(file)); 
                            pages.addPage(page);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return pages;
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
                        LOG.info("Content root folder created: " + rootDir.getPath());                        
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
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }               
        
        @Override
        public FileObject createData(WebPage page, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, page.getSourceID());  

            if(page instanceof Article)
            {
                Article article = (Article)page;
                if(fileTypeProvider instanceof ArticleProvider)
                {
                    ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(articleProvider.getArticle(article.getTitle(), article.getPublisher()).getBytes());
                    output.close();
                }                         
            }  
            
            return primaryFile;            
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
                WebPage page = factory.getWebPage(Utils.getProperties(file)); 
                getPages().addPage(page);                             
                sourceAdded(new SourceEventImpl(this, page));              
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }               
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            WebPage webPage = getLinksById().get(file.getName());  
            if(webPage != null)
            {                
            }
            */
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            WebPage page = getPages().removePage(file.getName());
            if(page != null)
            {
                page.notifyDeleted();
                sourceDeleted(new SourceEventImpl(this, page)); 
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
    
    private final class RaindropProviderImpl extends AbstractAction implements RaindropProvider, FileChangeListener, ActionsProvider, OpenSupport, CloseSupport, Runnable 
    { 
        private static final String PROP_RAINDROP_SYNC = "raindrop.sync";         
        
        private static final String ROOT_FOLDER = "raindrop";                            
        
        private Map<String, Raindrop> raindrops; 
        private FileObject rootDir; 

        private final RaindropFactory factory;           
        
        public RaindropProviderImpl(RaindropFactory factory) 
        {
            super("Synchronize Raindrops");
            this.factory = factory;          
        }  
        
        @Override
        public boolean isLiteratureNoteProvider()
        {
            return true;
        }          
        
        public LocalDateTime getLastSync()
        {
            String string = props.getProperty(PROP_RAINDROP_SYNC);
            if(string != null)
            {
                return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;
        } 

        public void setLastSync(LocalDateTime time)
        {
            if(time == null)
            {
                Object oldValue = props.remove(PROP_RAINDROP_SYNC);
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }                
                propertyChangeSupport.firePropertyChange(PROP_RAINDROP_SYNC, oldValue, time); 
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_RAINDROP_SYNC, time.format(DateTimeFormatter.ISO_DATE_TIME)); 
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }
                propertyChangeSupport.firePropertyChange(PROP_RAINDROP_SYNC, oldValue, time); 
            }
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public Collection<Raindrop> getRaindrops()
        {
            return Collections.unmodifiableCollection(getRaindropsById().values());
        }

        @Override
        public RaindropFactory getFactory()
        {
            return factory;
        }

        @Override
        public Raindrop getSource(String sourceID) 
        {
            return getRaindropsById().get(sourceID);
        }  

        @Override
        public String getName() 
        {
            return ROOT_FOLDER;
        }

        @Override
        public String getDisplayName() 
        {
            return "Raindrop";
        }

        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(AbstractRaindrop.ICON));
        }

        @Override
        public boolean contains(FileObject file) 
        {                                   
            return getRaindropsById().containsKey(file.getName());
        }        
        
        @Override
        public void open()
        {
            RP.post(this, 1000);            
        }        
        
        @Override
        public void close()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(Raindrop raindrop : getRaindrops())
                {
                    FileObject file = rootDir.getFileObject(raindrop.getSourceID(), PropertiesProvider.EXTENSION);
                    if(file != null)
                    {
                        try
                        {
                            if(raindrop.isModified())
                            {
                                OutputStream os = file.getOutputStream();
                                factory.save(raindrop, os, "Updated by Raindrop project: " + getTitle());
                                os.close();
                            }
                            else if(raindrop.isDeleted())
                            {
                                file.delete();
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
        
        @Override
        public synchronized Map<String, Raindrop> getRaindropsById()
        {
            if(raindrops == null)
            {
                raindrops = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            Raindrop raindrop = AbstractRaindrop.getRaindrop(Utils.getProperties(file)); 
                            raindrops.put(raindrop.getSourceID(), raindrop);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return raindrops;
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
                        LOG.info("Raindrop root folder created: " + rootDir.getPath());                        
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
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }         
        
        private List<RaindropTag> getTags(RaindropCollection collection) throws IOException
        {
            List<RaindropTag> tags = RaindropUtils.getTags(getRaindropAccount(), collection);
            List<RaindropChildrenCollection> collections = getRaindropAccount().getChildrenCollections(collection.getCollectionID());
            if(!collections.isEmpty())
            {
                for(RaindropChildrenCollection childrenCollection : collections)
                {
                    tags.addAll(getTags(childrenCollection));
                }
            }
            return tags;
        }  
        
        private List<Properties> getRaindrops(RaindropCollection collection)
        {
            List<Properties> props = RaindropUtils.getRaindrops(getRaindropAccount(), collection); 
            try
            {
                List<RaindropChildrenCollection> collections = getRaindropAccount().getChildrenCollections(collection.getCollectionID());
                if(!collections.isEmpty())
                {
                    for(RaindropChildrenCollection childrenCollection : collections)
                    {
                        props.addAll(getRaindrops(childrenCollection));
                    }
                }                
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }
            return props;
        } 

        private String getTreeID(RaindropChildrenCollection collection) throws IOException
        {
            RaindropCollection rc = getRaindropAccount().getCollection(collection.getParentID());
            if(rc instanceof RaindropChildrenCollection)
            {
                RaindropChildrenCollection childrenCollection = (RaindropChildrenCollection)rc;
                StringBuilder sb = new StringBuilder();                        
                sb.append(getTreeID(childrenCollection));
                sb.append(collection.getCollectionID());
                return sb.toString();
            }
            return collection.getCollectionID() + "";
        }        
        
        @Override
        public void run() 
        {
            try
            {
                //List<RaindropTag> list = RaindropUtils.getTags(getRaindropAccount(), getRaindropCollection());
                List<RaindropTag> list = getTags(getRaindropCollection());

                Set<String> tags = new HashSet<>();
                tags.addAll(RaindropProject.this.getTags());

                if(!list.isEmpty())
                {
                    for(RaindropTag tag : list)
                    {
                        tags.add(tag.getTag());
                    }             
                } 
                setTags(tags);                
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }

            
            FileObject root = getRootFolder();
            MarkdownSupport markdown = Lookup.getDefault().lookup(MarkdownSupport.class);  
            if(root != null && markdown != null)
            {
                ProgressHandle handle = ProgressHandleFactory.createHandle("Syncing Raindrops");
                handle.start();
                handle.switchToIndeterminate();                
                
                // List<Properties> props = RaindropUtils.getRaindrops(getRaindropAccount(), getRaindropCollection());   
                List<Properties> props = getRaindrops(getRaindropCollection());                
                List<Raindrop> raindrops = new ArrayList<>(props.size());
                for(Properties prop : props)
                {
                    Raindrop raindrop = getFactory().getRaindrop(prop);
                    if(raindrop != null)
                    {
                        raindrops.add(raindrop);
                        
                        if(raindrop.getCollection() instanceof RaindropChildrenCollection collection)
                        {
                            try
                            {
                                String topic = getTreeID(collection);
                                raindrop.getProperties().setProperty(TopicsProvider.PROP_TOPICS, topic);                               
                            }
                            catch(IOException e)
                            {
                                LOG.warning(e.getMessage());
                            }
                        }                        
                    }
                }

                Set<String> keys = new HashSet<>(getRaindropsById().keySet());
                for(Raindrop raindrop : raindrops)
                {                    
                    if(keys.remove(raindrop.getSourceID()))
                    {
                        boolean isTime = false;
                        LocalDateTime lastSync = getLastSync();
                        if(lastSync == null)
                        {
                            isTime = true;
                        }
                        else
                        {
                            LocalDateTime lastUpdate = raindrop.getLastUpdate().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();   
                            if(lastSync.isBefore(lastUpdate))
                            {
                                isTime = true;
                            }                            
                        }
                        
                        Raindrop oldRaindrop = getRaindropsById().get(raindrop.getSourceID());   
                        if(oldRaindrop.getType() == raindrop.getType())
                        {
                            if(isTime)
                            {
                                if(oldRaindrop.merge(raindrop))
                                {
                                    oldRaindrop.markModified();
                                    sourceModified(new SourceEventImpl(this, oldRaindrop));
                                }
                            }                                    
                        }
                        else
                        {
                            raindrop.markModified();
                            getRaindropsById().put(raindrop.getSourceID(), raindrop);
                            sourceDeleted(new SourceEventImpl(this, oldRaindrop));   
                            sourceAdded(new SourceEventImpl(this, raindrop));                                
                        }                                                                                                                                                   
                    }
                    else
                    {                                
                        try
                        {                            
                            OutputStream os = root.createAndOpen(raindrop.getSourceID() + "." + PropertiesProvider.EXTENSION);                            
                            raindrop.getProperties().store(os, "Created by Raindrop project: " + getTitle()); 
                            os.close();
                            FileObject file = createData(raindrop, markdown); 
                            
                            URL url = new URL(raindrop.getCover());
                            Image image = Utils.resizeImage(ImageIO.read(url), 320, 180); 
                            Icon picture = ImageUtilities.image2Icon(image);                                                                                                  
                            Icon icon = ImageUtilities.loadImageIcon(AbstractRaindrop.ICON, true);                                
                            JLabel baloonDetails = new JLabel(picture);
                            baloonDetails.addMouseListener(FileUtils.clicked2open(file));
                            baloonDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            JComponent details = createDetails(raindrop.getExcerpt(), FileUtils.action2open(file), picture); 
                            String title = getTitle() + ": " + raindrop.getTitle();                                                                  
                            NotificationDisplayer.getDefault().notify(title, icon, baloonDetails, details, AbstractRaindrop.getPriority(raindrop), AbstractRaindrop.getCategory(raindrop));                               
                            
                            LOG.info("Raindrop saved: " + raindrop.getSourceID());                             
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                 
                    }
                }
                if(!keys.isEmpty())
                {
                    for(String key : keys)
                    {
                        Raindrop raindrop = getRaindropsById().get(key);
                        if (raindrop != null && raindrop.getWorkflow() != WorkflowProvider.Workflow.RECYCLE_BIN)
                        {
                            raindrop.setWorkflow(WorkflowProvider.Workflow.RECYCLE_BIN);
                            raindrop.markModified();
                            sourceModified(new SourceEventImpl(this, raindrop));
                        }                     
                    }
                } 

                setLastSync(LocalDateTime.now());   

                LOG.info("Syncing Raindrops succeeded");
                handle.finish();  
                
            }                                                 
            
            /*
            for(Raindrop raindrop : raindrops)
            {
                FileObject folder = getRootFolder();
                if(folder != null)
                {
                    FileObject file = folder.getFileObject(raindrop.getSourceID());
                    if(file == null)
                    { 
                        OutputStream os = folder.createAndOpen(raindrop.getSourceID() + "." + PropertiesProvider.EXTENSION);
                        raindrop.save(os, comments); 
                        os.close();                                                        
                    } 
                    else
                    {
                        FileLock lock = null;                    
                        try
                        {                     
                            lock = file.lock(); 
                            OutputStream os = file.getOutputStream(lock);
                            raindrop.save(os, comments); 
                            os.close();                      
                        }                   
                        catch (IOException e) 
                        {
                            LOG.info(e.getMessage());
                        }                            
                        finally 
                        {
                            if (lock != null) 
                            {
                                lock.releaseLock();
                            }
                        }                     
                    }                    
                }
            }             
            */          
        } 

        @Override
        public FileObject createRaindrop(String link, boolean important, String note, List<String> tags, Set<Domain> domains, Set<Topic> topics)
        {
            return createRaindrop(getRaindropCollection(), link, important, note, tags, domains, topics);
        }
        
        @Override
        public FileObject createRaindrop(RaindropCollection collection, String link, boolean important, String note, List<String> tags, Set<Domain> domains, Set<Topic> topics)
        {
            FileObject root = getRootFolder();
            MarkdownSupport markdown = Lookup.getDefault().lookup(MarkdownSupport.class);  
            if(root != null && markdown != null)
            {
                Properties props = RaindropUtils.createRaindrop(getRaindropAccount(), collection, link, important, tags, note);
                if(collection instanceof RaindropChildrenCollection childrenCollection)
                {
                    if(domains == null)
                    {
                        try
                        {
                            props.setProperty(DomainsProvider.PROP_DOMAINS, getTreeID(childrenCollection));                        
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                        
                    }
                    else if(!domains.isEmpty())
                    {
                        DomainsGraphProvider domainsProvider = getLookup().lookup(DomainsGraphProvider.class);
                        if(domainsProvider != null)
                        {
                            StringJoiner joiner = new StringJoiner(",");
                            for(Domain domain : domains)
                            {
                                joiner.add(domainsProvider.getTreeID(domain));
                            }
                            props.setProperty(DomainsProvider.PROP_DOMAINS, joiner.toString());                    
                        }                      
                    }
                    if(topics == null)
                    {
                        try
                        {
                            props.setProperty(TopicsProvider.PROP_TOPICS, getTreeID(childrenCollection));                        
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                        
                    }
                    else if(!topics.isEmpty())
                    {
                        TopicsGraphProvider topicsGraphProvider = getLookup().lookup(TopicsGraphProvider.class);
                        if(topicsGraphProvider != null)
                        {
                            StringJoiner joiner = new StringJoiner(",");
                            for(Topic topic : topics)
                            {
                                joiner.add(topicsGraphProvider.getTreeID(topic));
                            }
                            props.setProperty(TopicsProvider.PROP_TOPICS, joiner.toString());                    
                        }                        
                    }
                }
                Raindrop raindrop = getFactory().getRaindrop(props);
                if(raindrop != null)
                {
                    try
                    {
                        OutputStream os = root.createAndOpen(raindrop.getSourceID() + "." + PropertiesProvider.EXTENSION);                            
                        factory.save(raindrop, os, "Created by Raindrop project: " + getTitle()); 
                        os.close();
                        FileObject file = createData(raindrop, markdown);                         
                        LOG.info("Raindrop saved: " + raindrop.getSourceID());                             
                        return file;
                    }
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }                  
                }                
            } 
            return null;
        }
        
        @Override
        public FileObject createData(Raindrop raindrop, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, raindrop.getSourceID()); 
            
            if(raindrop.getNote() != null && !raindrop.getNote().isBlank())
            {
                OutputStream output = primaryFile.getOutputStream();
                output.write(raindrop.getNote().getBytes());
                output.close();
            }               
                                  
            return primaryFile;             
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
                Raindrop raindrop = AbstractRaindrop.getRaindrop(Utils.getProperties(file)); 
                getRaindropsById().put(raindrop.getSourceID(), raindrop);               
                sourceAdded(new SourceEventImpl(this, raindrop));                             
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }              
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            Raindrop raindrop = getRaindropsById().get(file.getName());  
            if(raindrop != null)
            {
                setLastSource(raindrop);      
            }
            */
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Raindrop raindrop = getRaindropsById().remove(file.getName());  
            if(raindrop != null)
            {
                raindrop.notifyDeleted();
                sourceDeleted(new SourceEventImpl(this, raindrop));
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
        public void actionPerformed(ActionEvent evt) 
        {
            RP.post(this);            
        }
        
        @Override
        public List<Action> getActions()
        {
            List<Action> actions = new ArrayList<>();
            actions.add(this);
            return actions;
        }
    }  
    
    private final class YouTubeVideoProviderImpl extends YouTubeVideoProvider implements FileChangeListener, CloseSupport
    {
        public YouTubeVideoProviderImpl(YouTubeVideoFactory factory) 
        {
            super(factory);
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public void close()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(YouTubeVideo video : getVideos())
                {
                    FileObject file = rootDir.getFileObject(video.getVideoID(), PropertiesProvider.EXTENSION);
                    if(file != null)
                    {
                        try
                        {
                            if(video.isModified())
                            {
                                OutputStream os = file.getOutputStream();
                                factory.save(video, os, "Updated by Raindrop project: " + getTitle());
                                os.close();
                            }
                            else if(video.isDeleted())
                            {
                                file.delete();
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
        
        @Override
        public synchronized Map<String, YouTubeVideo> getVideosById()
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
                            YouTubeVideo video = factory.getVideo(Utils.getProperties(file)); 
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
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }         
        
        @Override
        public FileObject createData(YouTubeVideo video, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, video.getSourceID());  

            if(fileTypeProvider instanceof ArticleProvider)
            {
                ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                OutputStream output = primaryFile.getOutputStream();
                output.write(articleProvider.getArticle(video.getVideoTitle(), video.getChannelTitle()).getBytes());
                output.close();
            }
            
            return primaryFile;             
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
                YouTubeVideo video = factory.getVideo(Utils.getProperties(file)); 
                getVideosById().put(video.getSourceID(), video); 
                sourceAdded(new SourceEventImpl(this, video));            
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }             
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            YouTubeVideo video = getVideosById().get(file.getName());  
            if(video != null)
            {
                
            }
            */
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            YouTubeVideo video = getVideosById().remove(file.getName());  
            if(video != null)
            {
                video.notifyDeleted();   
                sourceDeleted(new SourceEventImpl(this, video));
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
    
    private final class YouTubeChannelProviderImpl extends YouTubeChannelProvider implements FileChangeListener, OpenSupport, CloseSupport, Runnable
    {
        private RequestProcessor.Task task;  
        
        public YouTubeChannelProviderImpl(YouTubeChannelFactory factory) 
        {
            super(factory);
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 

        @Override
        public void open()
        {
            task = YouTubeChannelFactory.RP.create(this);   
            task.schedule(1000);             
        }
        
        @Override
        public void close()
        {
            task.cancel();  
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(YouTubeChannel channel : getChannels())
                {
                    FileObject file = rootDir.getFileObject(channel.getChannelID(), PropertiesProvider.EXTENSION);
                    if(file != null)
                    {
                        try
                        {
                            if(channel.isModified())
                            {
                                OutputStream os = file.getOutputStream();
                                factory.save(channel, os, "Updated by Raindrop project: " + getTitle());
                                os.close();
                            }
                            else if(channel.isDeleted())
                            {
                                file.delete();
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
        
        @Override
        public void addNewVideos(YouTubeChannel channel)
        {
            AsciiDocSupport fileTypeProvider = Lookup.getDefault().lookup(AsciiDocSupport.class);
            GooglePasswordProvider googlePasswordProvider = Lookup.getDefault().lookup(GooglePasswordProvider.class);
            YouTubeVideoProvider provider = getLookup().lookup(YouTubeVideoProvider.class);
            if(fileTypeProvider != null && googlePasswordProvider != null && provider != null)
            {
                DateTime lastUploadTime = channel.getLastUploadTime();
                try
                {                   
                    YouTube.Activities.List request1 = YouTubeService.getDeafult().getService().activities().list("contentDetails,snippet");
                    request1.setKey(googlePasswordProvider.getKey());
                    ActivityListResponse response1 = request1.setChannelId(channel.getChannelID()).execute(); 
                    if(response1.getItems() != null && !response1.isEmpty())
                    {                                           
                        DateTime publishedAtMax = null;
                        for (Activity activity : response1.getItems())
                        {
                            DateTime publishedAt = activity.getSnippet().getPublishedAt();
                            String type = activity.getSnippet().getType();                    
                            if (type.equals(YouTubeService.ACTIVITY_TYPE_UPLOAD) && (lastUploadTime == null || publishedAt.getValue() > lastUploadTime.getValue()))
                            {
                                String videoID = activity.getContentDetails().getUpload().getVideoId();
                                String title = activity.getSnippet().getTitle();
                                String description = activity.getSnippet().getDescription();
                                String thumbnail = activity.getSnippet().getThumbnails().getHigh().getUrl();
                                                                 
                                YouTube.Videos.List request2 = YouTubeService.getDeafult().getService().videos().list("snippet,contentDetails,statistics,topicDetails");
                                request2.setKey(googlePasswordProvider.getKey());
                                VideoListResponse response2 = request2.setId(videoID).execute();  
                                if(response2.getItems() != null && !response2.getItems().isEmpty())
                                {   
                                    YouTubeVideo video = provider.getFactory().getVideo(YouTubeVideoFactory.getProperties(response2));  
                                    
                                    String domains = channel.getProperties().getProperty(DomainsProvider.PROP_DOMAINS);
                                    if(domains != null)
                                    {
                                        video.getProperties().setProperty(DomainsProvider.PROP_DOMAINS, domains);
                                    }
                                    
                                    FileObject file = provider.createData(video, fileTypeProvider);

                                    FileObject root = provider.getRootFolder();
                                    if(root != null)
                                    {  
                                        OutputStream os = root.createAndOpen(video.getVideoID() + "." + PropertiesProvider.EXTENSION);  
                                        provider.getFactory().save(video, os, "New YouTube Video Created");
                                        os.close();  
                                    }   
                                    
                                    channel.addVideoCountNew(-1);

                                    String text = channel.getTitle() + ": " + title;
                                    IconProvider iconProvider = channel.getLookup().lookup(IconProvider.class);
                                    Icon icon = ImageUtilities.image2Icon(iconProvider.getIcon(BeanInfo.ICON_COLOR_16x16));                           

                                    URL url = new URL(thumbnail);
                                    BufferedImage image = ImageIO.read(url);                          

                                    JLabel baloonDetails = new JLabel();
                                    baloonDetails.setIcon(ImageUtilities.image2Icon(Utils.resizeImage(image, text, baloonDetails.getFont().deriveFont(Font.BOLD), 16)));
                                    baloonDetails.addMouseListener(FileUtils.clicked2open(file));
                                    baloonDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                    JComponent details = NotificationUtils.createDetails(Utils.getDescription(description, 600), FileUtils.action2open(file), ImageUtilities.image2Icon(Utils.resizeImage(image, 320)));

                                    //NotificationDisplayer.getDefault().notify(getTitle(), info.getIcon(), title, new YouTubeVideoAction(videoID, getProjectDirectory(), youTubeServiceProvider), NotificationDisplayer.Priority.NORMAL, "YouTube-Category-Name");  
                                    NotificationDisplayer.getDefault().notify(text, icon, baloonDetails, details, NotificationDisplayer.Priority.NORMAL, "Video-Category-Name");                 
                                }                                                                    

                                if (publishedAtMax == null || publishedAt.getValue() > publishedAtMax.getValue())
                                {
                                    publishedAtMax = publishedAt;
                                }
                            }
                        }
                        if(publishedAtMax != null && (lastUploadTime == null || publishedAtMax.getValue() > lastUploadTime.getValue()))
                        {
                            channel.setLastUploadTime(publishedAtMax);  
                            channel.markModified();
                        } 
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
                finally
                {
                    if(channel.getVideoCountNew() != null && channel.getVideoCountNew() != 0)
                    {
                        channel.setVideoCountNew(null);
                    }
                }
            }              
        }        
        
        @Override
        public void run()
        {
            GooglePasswordProvider googlePasswordProvider = Lookup.getDefault().lookup(GooglePasswordProvider.class);
            if(googlePasswordProvider != null)
            {
                for(YouTubeChannel channel : getChannels())
                { 
                    try
                    {
                        YouTubeChannel newValue = getFactory().getChannel(googlePasswordProvider, channel.getChannelID());
                        Long videoCountNew = newValue.getVideoCount() - channel.getVideoCount(); 
                        channel.setVideoCount(newValue.getVideoCount());
                        channel.addVideoCountNew(videoCountNew.intValue());
                    }
                    catch(GeneralSecurityException e)
                    {
                        LOG.warning(e.getMessage());
                    }
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }     
                }
                task.schedule(100000);
            }
        }
        
        @Override
        public synchronized Map<String, YouTubeChannel> getChannelsById()
        {
            if(channels == null)
            {
                channels = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject fo : folder.getChildren()) 
                    {
                        if(fo.isFolder())
                        {
                            try
                            {
                                Project project = ProjectManager.getDefault().findProject(fo);  
                                if(project != null)
                                {
                                    YouTubeChannel channel = project.getLookup().lookup(YouTubeChannel.class);
                                    if(channel != null)
                                    {
                                        channels.put(channel.getChannelID(), channel);
                                    }                                      
                                }                                
                            }
                            catch(IOException e)
                            {
                                LOG.warning(e.getMessage());
                            }
                        }
                        else
                        {
                            try
                            {
                                YouTubeChannel channel = factory.getChannel(Utils.getProperties(fo)); 
                                channels.put(channel.getSourceID(), channel);
                            }
                            catch(IOException e)
                            {
                                LOG.warning(e.getMessage());
                            }                             
                        }                                                                                                                                                                                                                                            
                    }                     
                }                
            }
            return channels;
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
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }                 
        
        @Override
        public FileObject createData(YouTubeChannel channel, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, channel.getSourceID());  

            if(fileTypeProvider instanceof ArticleProvider)
            {
                ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                OutputStream output = primaryFile.getOutputStream();
                output.write(articleProvider.getArticle(channel.getTitle(), channel.getDescription()).getBytes());
                output.close();
            }
            
            return primaryFile;             
        }          
        
        @Override
        public void fileFolderCreated(FileEvent evt) 
        {
            FileObject folder = evt.getFile();
            if(!getChannelsById().containsKey(folder.getName()))
            {
                try
                {
                    Project project = ProjectManager.getDefault().findProject(folder);
                    if(project != null)
                    {
                        YouTubeChannel channel = project.getLookup().lookup(YouTubeChannel.class);  
                        if(channel != null)
                        {
                            getChannelsById().put(channel.getChannelID(), channel);
                            sourceAdded(new SourceEventImpl(this, channel));    
                        }                    
                    }                
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                 
            }  
        }

        @Override
        public void fileDataCreated(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            try
            {
                YouTubeChannel channel = factory.getChannel(Utils.getProperties(file)); 
                if(channel != null)
                {
                    getChannelsById().put(channel.getChannelID(), channel); 
                    sourceAdded(new SourceEventImpl(this, channel));                      
                }          
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }             
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            YouTubeChannel channel = getChannelsById().remove(file.getName());  
            if(channel != null)
            {
                channel.notifyDeleted();
                sourceDeleted(new SourceEventImpl(this, channel));  
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
    
    private final class RssChannelProviderImpl extends RssChannelProvider implements FileChangeListener, OpenSupport, CloseSupport, Runnable
    {
        private RequestProcessor.Task task;  
        
        public RssChannelProviderImpl(RssChannelFactory factory) 
        {
            super(factory);
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public void open()
        {
            task = RP.create(this);   
            task.schedule(1000);             
        }        

        @Override
        public void close()
        {
            task.cancel();
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(RssChannel channel : getChannels().getChannels())
                {
                    FileObject file = rootDir.getFileObject(channel.getFileName(), PropertiesProvider.EXTENSION);
                    if(file != null)
                    {
                        try
                        {
                            if(channel.isModified())
                            {
                                OutputStream os = file.getOutputStream();
                                factory.save(channel, os, "Updated by Raindrop project: " + getTitle());
                                os.close();
                            }
                            else if(channel.isDeleted())
                            {
                                file.delete();
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
        
        @Override
        public void run()
        {
            AsciiDocSupport fileTypeProvider = Lookup.getDefault().lookup(AsciiDocSupport.class);
            WebPageProvider webPagePovider = getLookup().lookup(WebPageProvider.class);
            if(fileTypeProvider != null && webPagePovider != null)
            {
                for(RssChannel channel : getChannels().getChannels())
                {
                    try
                    {
                        URL url = new URL(channel.getFeedUrl());
                        SyndFeedInput input = new SyndFeedInput();
                        SyndFeed feed = input.build(new XmlReader(url));

                        Properties props = getFactory().getProperties(channel.getFeedUrl(), feed, channel.getFileName());
                        if(!channel.getProperties().equals(props))
                        {
                            channel.getProperties().putAll(props);
                            channel.markModified();

                            for(SyndEntry syndEntry : feed.getEntries()) 
                            {
                                String fileName = FileUtils.getFileName(webPagePovider.getRootFolder(), PropertiesProvider.EXTENSION);
                                WebPage webPage = webPagePovider.getFactory().getWebPage(fileName, syndEntry);
                                
                                String domains = channel.getProperties().getProperty(DomainsProvider.PROP_DOMAINS);
                                if(domains != null)
                                {
                                    webPage.getProperties().setProperty(DomainsProvider.PROP_DOMAINS, domains);
                                }                                
                                
                                if(webPage != null && !webPagePovider.getPages().getPagesByUrl().containsKey(webPage.getLinkUrl()))
                                {                                    
                                    FileObject file = webPagePovider.createData(webPage, fileTypeProvider);
                                    if (file != null) {
                                        OutputStream os = webPagePovider.getRootFolder().createAndOpen(fileName + "." + PropertiesProvider.EXTENSION);
                                        webPagePovider.getFactory().save(webPage, os, "New RSS Web Page Created by Project: " + getTitle());
                                        os.close();

                                        String text = channel.getTitle() + ": " + syndEntry.getTitle();

                                        BufferedImage image = null;
                                        if (!syndEntry.getEnclosures().isEmpty()) {
                                            SyndEnclosure syndEnclosure = syndEntry.getEnclosures().get(0);
                                            if (syndEnclosure.getType().startsWith("image")) {
                                                URL url2 = new URL(syndEnclosure.getUrl());
                                                //Image image = Utils.resizeImage(ImageIO.read(url2), 320, 180); 
                                                image = ImageIO.read(url2);
                                            }
                                        }

                                        IconProvider provider = channel.getLookup().lookup(IconProvider.class);
                                        Icon icon = ImageUtilities.image2Icon(provider.getIcon(BeanInfo.ICON_COLOR_16x16));

                                        JLabel baloonDetails = new JLabel();
                                        if (image == null) {
                                            baloonDetails.addMouseListener(FileUtils.clicked2open(file));
                                            baloonDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                            JComponent details = createDetails(syndEntry.getDescription().getValue(), FileUtils.action2open(file), null);
                                            NotificationDisplayer.getDefault().notify(text, icon, baloonDetails, details, NotificationDisplayer.Priority.NORMAL, "Web-Category-Name");
                                        } else {
                                            baloonDetails.setIcon(ImageUtilities.image2Icon(Utils.resizeImage(image, text, baloonDetails.getFont().deriveFont(Font.BOLD), icon.getIconWidth())));
                                            baloonDetails.addMouseListener(FileUtils.clicked2open(file));
                                            baloonDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                            JComponent details = createDetails(syndEntry.getDescription().getValue(), FileUtils.action2open(file), ImageUtilities.image2Icon(Utils.resizeImage(image, 320)));
                                            NotificationDisplayer.getDefault().notify(text, icon, baloonDetails, details, NotificationDisplayer.Priority.NORMAL, "Web-Category-Name");
                                        }
                                    }                                       
                                }
                            }                                  
                        }                                                    
                    }
                    catch (MalformedURLException e)
                    {
                        LOG.warning(e.getMessage());
                    }
                    catch (IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }    
                    catch (FeedException e)
                    {
                        LOG.warning(e.getMessage());
                    }                                      
                }                
                task.schedule(100000);                  
            }                                      
        }        

        @Override
        public Icon getIcon(boolean bln) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getIcon(IconsProvider.ICON.RSS_CHANNEL);
        }           
        
        @Override
        public synchronized Channels getChannels()
        {
            if(channels == null)
            {
                channels = new Channels();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            RssChannel channel = factory.getChannel(Utils.getProperties(file)); 
                            channels.addChannel(channel);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return channels;
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
                        LOG.info("RSS Channel root folder created: " + rootDir.getPath());                        
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
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }              
        
        @Override
        public FileObject createData(RssChannel channel, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, channel.getSourceID());  

            if(channel instanceof Article)
            {
                Article article = (Article)channel;
                if(fileTypeProvider instanceof ArticleProvider)
                {
                    ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(articleProvider.getArticle(article.getTitle(), article.getPublisher()).getBytes());
                    output.close();
                }                         
            }  
            
            return primaryFile;            
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
                RssChannel channel = factory.getChannel(Utils.getProperties(file)); 
                getChannels().addChannel(channel);             
                sourceAdded(new SourceEventImpl(this, channel));                
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }               
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            WebPage webPage = getLinksById().get(file.getName());  
            if(webPage != null)
            {                
            }
            */
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            RssChannel channel = getChannels().removeChannel(file.getName());
            if(channel != null)
            {
                channel.notifyDeleted();
                sourceDeleted(new SourceEventImpl(this, channel));
            }
        }

        @Override
        public void fileRenamed(FileRenameEvent fre) 
        {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fae) 
        {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }         
    }     

// TODO DomainsGraphProvider     
    
    private final class DomainsGraphProviderImpl implements DomainsGraphProvider, ChangeSupportProvider
    {
        private List<Domain> rootDomains; 
        
        private final Map<String, Domain> domains = new HashMap<>();
        private final Map<String, List<ChildrenDomain>> childrenDomains = new HashMap<>();        
        private final ChangeSupport changeSupport = new ChangeSupport(this);  
        
        private List<Domain> getDomains(List<? extends Domain> parentDomains)
        {
            List<Domain> domains = new ArrayList<>();
            for(Domain domain : parentDomains)
            {
                List<ChildrenDomain> childrenDomains = getChildrenDomains(domain.getDomainID());
                domains.addAll(childrenDomains);
                domains.addAll(getDomains(childrenDomains));
            }  
            return domains;
        }
        
        @Override
        public List<Domain> getDomains()
        {
            return Collections.unmodifiableList(getDomains(getRootDomains()));
        }        
        
        @Override
        public List<Domain> getRootDomains()
        {
            if(rootDomains == null)
            {
                rootDomains = getNeo4jInstance().getRootDomains(getProjectDirectory().getName());
                for(Domain domain : rootDomains)
                {
                    domains.put(domain.getDomainID(), domain);
                }
            }
            return rootDomains;
        }
        
        @Override
        public List<ChildrenDomain> getChildrenDomains(String parentID)
        {
            List<ChildrenDomain> list = childrenDomains.get(parentID);
            if(list == null)
            {
                list = getNeo4jInstance().getChildrenDomains(parentID);
                childrenDomains.put(parentID, list);
                for(ChildrenDomain domain : list)
                {
                    domains.put(domain.getDomainID(), domain);
                }
            }  
            return list;
        }          
        
        @Override
        public void addRootDomain(String name, String tag) 
        {
            Domain domain = getNeo4jInstance().addRootDomain(getProjectDirectory().getName(), name, tag);
            if(domain != null)
            {
                getRootDomains().add(domain);
                domains.put(domain.getDomainID(), domain);
                changeSupport.fireChange();                 
            }             
        }
        
        @Override
        public void addRootDomain(String domainID, String name, String tag) 
        {
            Domain domain = getNeo4jInstance().addRootDomain(getProjectDirectory().getName(), domainID, name, tag);
            if(domain != null)
            {
                getRootDomains().add(domain);
                domains.put(domain.getDomainID(), domain);
                changeSupport.fireChange();                 
            }             
        }        

        @Override
        public void removeRootDomain(Domain domain) 
        {
            getRootDomains().remove(domain); 
            domains.remove(domain.getDomainID());
            changeSupport.fireChange();
        }  
        
        @Override
        public void addChildrenDomain(String parentID, String name, String tag, VisibilityProvider.Modifier modifier) 
        {
            ChildrenDomain domain = getNeo4jInstance().addChildrenDomain(parentID, name, tag, modifier);
            if(domain != null)
            {
                getChildrenDomains(domain.getParentID()).add(domain);
                domains.put(domain.getDomainID(), domain);
                changeSupport.fireChange();                 
            }
        }
        
        @Override
        public void addChildrenDomain(String parentID, String domainID, String name, String tag, VisibilityProvider.Modifier modifier) 
        {
            ChildrenDomain domain = getNeo4jInstance().addChildrenDomain(parentID, domainID, name, tag, modifier);
            if(domain != null)
            {
                getChildrenDomains(domain.getParentID()).add(domain);
                domains.put(domain.getDomainID(), domain);
                changeSupport.fireChange();                 
            }
        }        

        @Override
        public void removeChildrenDomain(ChildrenDomain domain) 
        {
            getChildrenDomains(domain.getParentID()).remove(domain);
            domains.remove(domain.getDomainID());
            changeSupport.fireChange(); 
        } 
        
        @Override
        public List<String> getTags(Domain domain)
        {
            List<ChildrenDomain> domains = getChildrenDomains(domain.getDomainID());
            if(domains.isEmpty())
            {
                List<String> tags = new ArrayList<>(1);
                tags.add(domain.getTag());
                return Collections.unmodifiableList(tags);
            }
            List<String> tags = new ArrayList<>();
            tags.add(domain.getTag());
            for(ChildrenDomain childrenDomain : domains)
            {
                tags.addAll(getTags(childrenDomain));                
            }
            return Collections.unmodifiableList(tags);
        }

        @Override
        public Lookup.Provider getProvider() 
        {
            return RaindropProject.this;
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
        public Collection<Domain> getSelectedDomains() 
        {
            if(selectedDomains == null)
            {
                return Collections.EMPTY_LIST;
            }
            return Collections.unmodifiableCollection(selectedDomains);
        }
        
        @Override
        public void clearSelectedDomains()
        {
            selectedDomains.clear();
            changeSupport.fireChange();
        }

        @Override
        public boolean isTag(TagsProvider provider) 
        {
            if(selectedDomains.isEmpty())
            {
                return true;
            }
            else
            {
                Set<String> tags = getTags(getSelectedDomains());
                for(String tag : provider.getTags())
                {
                    if(tags.contains(tag))
                    {
                        return true;
                    }
                }                
            }
            return false;
        } 
        
        @Override
        public boolean isDomain(DomainsProvider provider)
        {
            Set<String> domains = provider.getDomains();
            if(selectedDomains.isEmpty() || domains.isEmpty())
            {
                return true;
            }
            else
            {
                Iterator<Domain> iterator1 = getSelectedDomains().iterator();
                while(iterator1.hasNext())
                {
                    String treeID = getTreeID(iterator1.next());
                    Iterator<String> iterator2 = domains.iterator();
                    while(iterator2.hasNext())
                    {
                        if(iterator2.next().startsWith(treeID))
                        {
                            return true;
                        }                        
                    }
                }                
            }
            return false;            
        }
        
        @Override
        public String getTreeID(Domain domain)
        {
            if(domain instanceof ChildrenDomain)
            {
                ChildrenDomain childrenDomain = (ChildrenDomain)domain;
                Domain parentDomain = domains.get(childrenDomain.getParentID());
                if(parentDomain != null)
                {
                    StringBuilder sb = new StringBuilder();                        
                    sb.append(getTreeID(parentDomain));
                    sb.append(domain.getDomainID());
                    return sb.toString();
                }  
            }
            return domain.getDomainID();
        }
        
        private Set<String> getRootDomains(Collection<Domain> domains)
        {
            Set<String> rootDomains = new HashSet<>();
            for(Domain domain : domains)
            {
                Domain rootDomain = getRootDomain(domain);
                if(rootDomain != null)
                {
                    rootDomains.add(rootDomain.getDomainID());                    
                }
            }
            return Collections.unmodifiableSet(rootDomains);            
        }
        
        private Domain getRootDomain(Domain domain)
        {
            if(domain instanceof ChildrenDomain)
            {
                ChildrenDomain childrenDomain = (ChildrenDomain)domain;
                Domain parentDomain = domains.get(childrenDomain.getParentID());
                if(parentDomain != null)
                {
                    return getRootDomain(parentDomain);
                }   
                return null;
            }
            return domain;
        }
        
        @Override
        public Set<String> getTags(Collection<Domain> domains)
        {
            Set<String> tags = new HashSet<>();
            for(Domain domain : domains)
            {
                tags.addAll(getTags(domain));
            }
            return Collections.unmodifiableSet(tags);
        }

        @Override
        public void selectDomain(Domain domain) 
        {
            selectedDomains.add(domain);          
            changeSupport.fireChange();  
        }
    }     
    
// TODO TopicsGraphProvider     
    
    private final class TopicsGraphProviderImpl implements TopicsGraphProvider, ChangeSupportProvider
    {
        private List<Topic> rootTopics; 
        
        private final Map<String, Topic> topics = new HashMap<>();
        private final Map<String, List<ChildrenTopic>> childrenTopics = new HashMap<>();        
        private final ChangeSupport changeSupport = new ChangeSupport(this);  
        
        private List<Topic> getTopics(List<? extends Topic> parentTopics)
        {
            List<Topic> topics = new ArrayList<>();
            for(Topic topic : parentTopics)
            {
                List<ChildrenTopic> childrenTopics = getChildrenTopics(topic.getTopicID());
                topics.addAll(childrenTopics);
                topics.addAll(getTopics(childrenTopics));
            }  
            return topics;
        }
        
        @Override
        public List<Topic> getTopics()
        {
            return Collections.unmodifiableList(getTopics(getRootTopics()));
        }        
        
        @Override
        public List<Topic> getRootTopics()
        {
            if(rootTopics == null)
            {
                rootTopics = getNeo4jInstance().getRootTopics(getProjectDirectory().getName());
                for(Topic topic : rootTopics)
                {
                    topics.put(topic.getTopicID(), topic);
                }
            }
            return rootTopics;
        }
        
        @Override
        public List<ChildrenTopic> getChildrenTopics(String parentID)
        {
            List<ChildrenTopic> list = childrenTopics.get(parentID);
            if(list == null)
            {
                list = getNeo4jInstance().getChildrenTopics(parentID);
                childrenTopics.put(parentID, list);
                for(Topic topic : list)
                {
                    topics.put(topic.getTopicID(), topic);
                }
            }  
            return list;
        }          
        
        @Override
        public void addRootTopic(String name, String tag) 
        {
            Topic topic = getNeo4jInstance().addRootTopic(getProjectDirectory().getName(), name, tag);
            if(topic != null)
            {
                getRootTopics().add(topic);
                topics.put(topic.getTopicID(), topic);
                changeSupport.fireChange();                 
            }             
        }
        
        @Override
        public void addRootTopic(String topicID, String name, String tag) 
        {
            Topic topic = getNeo4jInstance().addRootTopic(getProjectDirectory().getName(), topicID, name, tag);
            if(topic != null)
            {
                getRootTopics().add(topic);
                topics.put(topic.getTopicID(), topic);
                changeSupport.fireChange();                 
            }             
        }        

        @Override
        public void removeRootTopic(Topic topic) 
        {
            getRootTopics().remove(topic); 
            topics.remove(topic.getTopicID());
            changeSupport.fireChange();
        }  
        
        @Override
        public void addChildrenTopic(String parentID, String name, String tag, VisibilityProvider.Modifier modifier) 
        {
            ChildrenTopic topic = getNeo4jInstance().addChildrenTopic(parentID, name, tag, modifier);
            if(topic != null)
            {
                getChildrenTopics(topic.getParentID()).add(topic);
                topics.put(topic.getTopicID(), topic);
                changeSupport.fireChange();                 
            }
        }
        
        @Override
        public void addChildrenTopic(String parentID, String topicID, String name, String tag, VisibilityProvider.Modifier modifier) 
        {
            ChildrenTopic topic = getNeo4jInstance().addChildrenTopic(parentID, topicID, name, tag, modifier);
            if(topic != null)
            {
                getChildrenTopics(topic.getParentID()).add(topic);
                topics.put(topic.getTopicID(), topic);
                changeSupport.fireChange();                 
            }
        }        

        @Override
        public void removeChildrenTopic(ChildrenTopic topic) 
        {
            getChildrenTopics(topic.getParentID()).remove(topic);
            topics.remove(topic.getTopicID());
            changeSupport.fireChange(); 
        } 
        
        @Override
        public List<String> getTags(Topic topic)
        {
            List<ChildrenTopic> topics = getChildrenTopics(topic.getTopicID());
            if(topics.isEmpty())
            {
                List<String> tags = new ArrayList<>(1);
                tags.add(topic.getTag());
                return Collections.unmodifiableList(tags);
            }
            List<String> tags = new ArrayList<>();
            tags.add(topic.getTag());
            for(ChildrenTopic childrenTopic : topics)
            {
                tags.addAll(getTags(childrenTopic));                
            }
            return Collections.unmodifiableList(tags);
        }

        @Override
        public Lookup.Provider getProvider() 
        {
            return RaindropProject.this;
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
        public Collection<Topic> getSelectedTopics() 
        {
            if(selectedTopics == null)
            {
                return Collections.EMPTY_LIST;
            }
            return Collections.unmodifiableCollection(selectedTopics);
        }
        
        @Override
        public void clearSelectedTopics()
        {
            selectedTopics.clear();
            changeSupport.fireChange();
        }

        @Override
        public boolean isTag(TagsProvider provider) 
        {
            if(selectedTopics.isEmpty())
            {
                return true;
            }
            else
            {
                Set<String> tags = getTags(getSelectedTopics());
                for(String tag : provider.getTags())
                {
                    if(tags.contains(tag))
                    {
                        return true;
                    }
                }                
            }
            return false;
        } 
        
        @Override
        public boolean isTopic(TopicsProvider provider)
        {
            Set<String> topics = provider.getTopics();
            if(selectedTopics.isEmpty() || topics.isEmpty())
            {
                return true;
            }
            else
            {
                Iterator<Topic> iterator1 = getSelectedTopics().iterator();
                while(iterator1.hasNext())
                {
                    String treeID = getTreeID(iterator1.next());
                    Iterator<String> iterator2 = topics.iterator();
                    while(iterator2.hasNext())
                    {
                        if(iterator2.next().startsWith(treeID))
                        {
                            return true;
                        }                        
                    }
                }                
            }
            return false;            
        }
        
        @Override
        public String getTreeID(Topic topic)
        {
            if(topic instanceof ChildrenTopic)
            {
                ChildrenTopic childrenTopic = (ChildrenTopic)topic;
                Topic parentTopic = topics.get(childrenTopic.getParentID());
                if(parentTopic != null)
                {
                    StringBuilder sb = new StringBuilder();                        
                    sb.append(getTreeID(parentTopic));
                    sb.append(topic.getTopicID());
                    return sb.toString();
                }  
            }
            return topic.getTopicID();
        }
        
        private Set<String> getRootTopics(Collection<Topic> topics)
        {
            Set<String> rootTopics = new HashSet<>();
            for(Topic topic : topics)
            {
                Topic rootTopic = getRootTopic(topic);
                if(rootTopic != null)
                {
                    rootTopics.add(rootTopic.getTopicID());                    
                }
            }
            return Collections.unmodifiableSet(rootTopics);            
        }
        
        private Topic getRootTopic(Topic topic)
        {
            if(topic instanceof ChildrenTopic)
            {
                ChildrenTopic childrenTopic = (ChildrenTopic)topic;
                Topic parentTopic = topics.get(childrenTopic.getParentID());
                if(parentTopic != null)
                {
                    return getRootTopic(parentTopic);
                }   
                return null;
            }
            return topic;
        }
        
        @Override
        public Set<String> getTags(Collection<Topic> topics)
        {
            Set<String> tags = new HashSet<>();
            for(Topic topic : topics)
            {
                tags.addAll(getTags(topic));
            }
            return Collections.unmodifiableSet(tags);
        }

        @Override
        public void selectTopic(Topic topic) 
        {
            selectedTopics.add(topic);          
            changeSupport.fireChange();  
        }
    }  
    
// TODO GoalsGraphProvider    
    
    private final class GoalsGraphProviderImpl implements GoalsGraphProvider, ChangeSupportProvider
    {
        private List<Goal> rootGoals; 
        
        private final Map<String, Goal> goals = new HashMap<>();
        private final Map<String, List<ChildrenGoal>> childrenGoals = new HashMap<>();        
        private final ChangeSupport changeSupport = new ChangeSupport(this);  
        
        private List<Goal> getGoals(List<? extends Goal> parentGoals)
        {
            List<Goal> goals = new ArrayList<>();
            for(Goal goal : parentGoals)
            {
                List<ChildrenGoal> childrenGoals = getChildrenGoals(goal.getGoalID());
                goals.addAll(childrenGoals);
                goals.addAll(getGoals(childrenGoals));
            }  
            return goals;
        }
        
        @Override
        public List<Goal> getGoals()
        {
            return Collections.unmodifiableList(getGoals(getRootGoals()));
        }        
        
        @Override
        public List<Goal> getRootGoals()
        {
            if(rootGoals == null)
            {
                rootGoals = getNeo4jInstance().getRootGoals(getProjectDirectory().getName());
                for(Goal goal : rootGoals)
                {
                    goals.put(goal.getGoalID(), goal);
                }
            }
            return rootGoals;
        }
        
        @Override
        public List<ChildrenGoal> getChildrenGoals(String parentID)
        {
            List<ChildrenGoal> list = childrenGoals.get(parentID);
            if(list == null)
            {
                list = getNeo4jInstance().getChildrenGoals(parentID);
                childrenGoals.put(parentID, list);
                for(Goal goal : list)
                {
                    goals.put(goal.getGoalID(), goal);
                }
            }  
            return list;
        }          
        
        @Override
        public void addRootGoal(String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming) 
        {
            Goal goal = getNeo4jInstance().addRootGoal(getProjectDirectory().getName(), name, tag, level, startDate, endDate, vision, accountability, rewards, obstacles, support, brainstorming);
            if(goal != null)
            {
                getRootGoals().add(goal);
                goals.put(goal.getGoalID(), goal);
                changeSupport.fireChange();                 
            }             
        }

        @Override
        public void removeRootGoal(Goal goal) 
        {
            getRootGoals().remove(goal); 
            goals.remove(goal.getGoalID());
            changeSupport.fireChange();
        }  
        
        @Override
        public void addChildrenGoal(String parentID, String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming, VisibilityProvider.Modifier modifier) 
        {
            ChildrenGoal goal = getNeo4jInstance().addChildrenGoal(parentID, name, tag, level, startDate, endDate, vision, accountability, rewards, obstacles, support, brainstorming, modifier);
            if(goal != null)
            {
                getChildrenGoals(goal.getParentID()).add(goal);
                goals.put(goal.getGoalID(), goal);
                changeSupport.fireChange();                 
            }
        }

        @Override
        public void removeChildrenGoal(ChildrenGoal goal) 
        {
            getChildrenGoals(goal.getParentID()).remove(goal);
            goals.remove(goal.getGoalID());
            changeSupport.fireChange(); 
        } 
        
        @Override
        public List<String> getTags(Goal goal)
        {
            List<ChildrenGoal> goals = getChildrenGoals(goal.getGoalID());
            if(goals.isEmpty())
            {
                List<String> tags = new ArrayList<>(1);
                tags.add(goal.getTag());
                return Collections.unmodifiableList(tags);
            }
            List<String> tags = new ArrayList<>();
            tags.add(goal.getTag());
            for(ChildrenGoal childrenGoal : goals)
            {
                tags.addAll(getTags(childrenGoal));              
            }
            return Collections.unmodifiableList(tags);
        }

        @Override
        public Lookup.Provider getProvider() 
        {
            return RaindropProject.this;
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
        public Collection<Goal> getSelectedGoals() 
        {
            if(selectedGoals == null)
            {
                return Collections.EMPTY_LIST;
            }
            return Collections.unmodifiableCollection(selectedGoals);
        }
        
        @Override
        public void clearSelectedGoals()
        {
            selectedGoals.clear();
            changeSupport.fireChange();
        }

        @Override
        public boolean isTag(TagsProvider provider) 
        {
            if(selectedGoals.isEmpty())
            {
                return true;
            }
            else
            {
                Set<String> tags = getTags(getSelectedGoals());
                for(String tag : provider.getTags())
                {
                    if(tags.contains(tag))
                    {
                        return true;
                    }
                }                
            }
            return false;
        } 
        
        @Override
        public boolean isGoal(GoalsProvider provider)
        {
            Set<String> goals = provider.getGoals();
            if(selectedGoals.isEmpty() || goals.isEmpty())
            {
                return true;
            }
            else
            {
                Iterator<Goal> iterator1 = getSelectedGoals().iterator();
                while(iterator1.hasNext())
                {
                    String treeID = getTreeID(iterator1.next());
                    Iterator<String> iterator2 = goals.iterator();
                    while(iterator2.hasNext())
                    {
                        if(iterator2.next().startsWith(treeID))
                        {
                            return true;
                        }                        
                    }
                }                
            }
            return false;                         
        }
        
        @Override
        public String getTreeID(Goal goal)
        {
            if(goal instanceof ChildrenGoal)
            {
                ChildrenGoal childrenGoal = (ChildrenGoal)goal;
                Goal parentGoal = goals.get(childrenGoal.getParentID());
                if(parentGoal != null)
                {
                    StringBuilder sb = new StringBuilder();                        
                    sb.append(getTreeID(parentGoal));
                    sb.append(goal.getGoalID());
                    return sb.toString();
                }  
            }
            return goal.getGoalID();
        }        
        
        private Set<String> getRootGoals(Collection<Goal> goals)
        {
            Set<String> rootGoals = new HashSet<>();
            for(Goal goal : goals)
            {
                Goal rootGoal = getRootGoal(goal);
                if(rootGoal != null)
                {
                    rootGoals.add(rootGoal.getGoalID());                    
                }
            }
            return Collections.unmodifiableSet(rootGoals);            
        }
        
        private Goal getRootGoal(Goal goal)
        {
            if(goal instanceof ChildrenGoal)
            {
                ChildrenGoal childrenGoal = (ChildrenGoal)goal;
                Goal parentGoal = goals.get(childrenGoal.getParentID());
                if(parentGoal != null)
                {
                    return getRootGoal(parentGoal);
                } 
                return null;
            }
            return goal;
        }
        
        @Override
        public Set<String> getTags(Collection<Goal> goals)
        {
            Set<String> tags = new HashSet<>();
            for(Goal goal : goals)
            {
                tags.addAll(getTags(goal));
            }
            return Collections.unmodifiableSet(tags);
        }

        @Override
        public void selectGoal(Goal goal) 
        {
            selectedGoals.add(goal);          
            changeSupport.fireChange();  
        }
    }  
    
// TODO ThoughtsGraphProvider    
    
    private final class ThoughtsGraphProviderImpl implements ThoughtsGraphProvider, ChangeSupportProvider
    {
        private List<Thought> rootThoughts; 
        
        private final Map<String, Thought> thoughts = new HashMap<>();        
        private final Map<String, List<ChildrenThought>> childrenThoughts = new HashMap<>();        
        private final ChangeSupport changeSupport = new ChangeSupport(this);     
        
        @Override
        public Thought getThought(String toughtID)
        {
            Thought thought = thoughts.get(toughtID);
            if(thought == null)
            {
                try
                {
                    thought = getNeo4jInstance().getThought(toughtID);
                    thoughts.put(toughtID, thought);                   
                }
                catch(NoSuchElementException e)
                {
                    LOG.info(e.getMessage());
                }
            }
            return thought;
        }
        
        @Override
        public List<Thought> getRootThoughts()
        {
            if(rootThoughts == null)
            {
                rootThoughts = getNeo4jInstance().getRootThoughts(getProjectDirectory().getName());
                for(Thought thought : rootThoughts)
                {
                    thoughts.put(thought.getThoughtID(), thought);
                }
            }
            return rootThoughts;
        }
        
        @Override
        public List<ChildrenThought> getChildrenThoughts(String parentID)
        {
            List<ChildrenThought> list = childrenThoughts.get(parentID);
            if(list == null)
            {
                list = getNeo4jInstance().getChildrenThoughts(parentID);
                childrenThoughts.put(parentID, list);
                for(Thought thought : list)
                {
                    thoughts.put(thought.getThoughtID(), thought);
                }
            }  
            return list;
        }          

        @Override
        public Thought addRootThought(String text, Thought.Type type, Set<String> tags, Set<Topic> topics, Set<Goal> goals)
        {
            Session session = null;
            try
            {
                session = getNeo4jInstance().getSession();
                Thought thought = getNeo4jInstance().addRootThought(session, getProjectDirectory().getName(), text, type, tags);
                
                getRootThoughts().add(thought);
                
                for(Topic topic : topics)
                {
                    getNeo4jInstance().thoughtHasTopic(session, thought, topic, VisibilityProvider.Modifier.PUBLIC);
                }
                
                for(Goal goal : goals)
                {
                    getNeo4jInstance().thoughtHasGoal(session, thought, goal, VisibilityProvider.Modifier.PUBLIC);
                }                

                thoughts.put(thought.getThoughtID(), thought);
                changeSupport.fireChange();  
                return thought;
            }
            catch(Exception e)
            {
                LOG.warning(e.getMessage());
            }
            finally
            {
                if(session != null)
                {
                    session.close();
                }
            }  
            return null;
        }        
        
        @Override
        public ChildrenThought addChildrenThought(Thought parent, String text, Thought.Type type, Set<String> tags, Set<Topic> topics, Set<Goal> goals)
        {
            Session session = null;
            try
            {
                session = getNeo4jInstance().getSession();
                ChildrenThought thought = getNeo4jInstance().addChildrenThought(session, getProjectDirectory().getName(), parent, text, type, tags);                
                getChildrenThoughts(parent.getThoughtID()).add(thought);
                
                for(Topic topic : topics)
                {
                    getNeo4jInstance().thoughtHasTopic(session, thought, topic, VisibilityProvider.Modifier.PUBLIC);
                }
                
                for(Goal goal : goals)
                {
                    getNeo4jInstance().thoughtHasGoal(session, thought, goal, VisibilityProvider.Modifier.PUBLIC);
                }                

                thoughts.put(thought.getThoughtID(), thought);
                return thought;
            }
            catch(Exception e)
            {
                LOG.warning(e.getMessage());
            }
            finally
            {
                if(session != null)
                {
                    session.close();
                }
            }  
            return null;
        }        
        
        @Override
        public Lookup.Provider getProvider() 
        {
            return RaindropProject.this;
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
        public void selectThought(Thought thought)
        {
            if(selectedThoughts.add(thought))
            {
                changeSupport.fireChange();                              
            }
        }
        
        @Override
        public void clearSelectedThought(Thought thought)
        {
            if(selectedThoughts.remove(thought))
            {
                changeSupport.fireChange();
            }
        }
        
        @Override
        public Collection<Thought> getSelectedThoughts() 
        {
            if(selectedThoughts == null)
            {
                return Collections.EMPTY_LIST;
            }
            return Collections.unmodifiableCollection(selectedThoughts);
        }
        
        @Override
        public void clearSelectedThoughts()
        {
            selectedThoughts.clear();
            changeSupport.fireChange();
        }   
        
        private boolean isParent(Thought selectedThought, ChildrenThought childrenThought)
        {
            if(selectedThought.getThoughtID().equals(childrenThought.getParentID()))
            {
                return true;
            }
            Thought parentThought = getThought(childrenThought.getParentID());
            if(parentThought instanceof ChildrenThought thought)
            {
                return isParent(selectedThought, thought);
            }
            return false;
        }
        
        @Override
        public boolean isThought(ThoughtsProvider provider)
        {
            if(!selectedThoughts.isEmpty())
            {
                Set<Thought> thoughts = provider.getThoughts();
                if(!thoughts.isEmpty())
                {
                    for(Thought selectedThought : selectedThoughts)
                    {
                        for(Thought thought : thoughts)
                        {
                            if(selectedThought.getThoughtID().equals(thought.getThoughtID()))
                            {
                                return true;
                            }
                            else if(thought instanceof ChildrenThought childrenThought)
                            {
                                if(isParent(selectedThought, childrenThought))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }
            return true;
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
            return RaindropProject.this;
        }                 
    }  

    private static JComponent createDetails(String text, ActionListener action, Icon icon) 
    {
        if (null == action) 
        {
            return new JLabel(text);
        }   
        
        JButton btn = new JButton(Utils.convertStringToHtml(text, 50));
        btn.setFocusable(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFont(btn.getFont().deriveFont(btn.getFont().getSize() + 2));
        btn.addActionListener(action);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color c = UIManager.getColor("nb.html.link.foreground"); //NOI18N
        if (c != null) {
            btn.setForeground(c);
        }
        btn.setIcon(icon);
        btn.setIconTextGap(10);        
        btn.setVerticalTextPosition(SwingConstants.TOP);
        btn.setHorizontalTextPosition(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }     
}
