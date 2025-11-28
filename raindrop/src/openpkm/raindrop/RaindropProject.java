/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import openpkm.base.Article;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.Book;
import openpkm.base.ChildrenGoal;
import openpkm.base.ChildrenTopic;
import openpkm.base.DescriptionProvider;
import openpkm.base.Document;
import openpkm.base.Goal;
import openpkm.base.GoalsGraphProvider;
import openpkm.base.GoalsProvider;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.IconProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.Link;
import openpkm.base.MarkdownSupport;
import openpkm.base.PropertiesProvider;
import openpkm.base.Reference;
import openpkm.base.ReferenceProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.SourcesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.Thought;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.base.UpdateCookie;
import openpkm.base.Video;
import openpkm.base.VisibilityProvider;
import openpkm.core.FileUtils;
import openpkm.core.Utils;
import openpkm.neo4j.Neo4jInstance;
import openpkm.neo4j.Neo4jProvider;
import openpkm.youtube.YouTubeVideo;
import openpkm.youtube.YouTubeVideoProvider;
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
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import openpkm.base.DataGroupProvider;

/**
 *
 * @author Rok Koren
 */
public class RaindropProject implements Project, TitleProvider, DescriptionProvider, PropertiesProvider, BatchUpdateSupport
{
    public static final String PROP_RAINDROP_USER_ID         = "raindrop.user.id";    
    public static final String PROP_RAINDROP_COLLECTION_ID   = "raindrop.collection.id";
    public static final String PROP_RAINDROP_COLLECTION_ROOT = "raindrop.collection.root";
    public static final String PROP_RAINDROP_PUBLISHED_DATE  = "raindrop.published.date";
    
    public static final String PROP_NEO4J_INSTANCE_ID   = "neo4j.instance.id";   
    public static final String PROP_TRELLO_USERNAME     = "trello.username"; 
    public static final String PROP_TRELLO_WORKSPACE_ID = "trello.workspace.id";  
    
    public static final String PROP_LAST_SOURCE = "last.source";           
    
    private static final String RAINDROP_FEED_URL = "https://raindrop.io/collection/";   
    
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_THOUGHTS  = 100;
    private static final int POSITION_DOCUMENTS = 200;
    private static final int POSITION_ARTICLES  = 300;
    private static final int POSITION_BOOKS     = 400;
    private static final int POSITION_LINKS     = 500;
    private static final int POSITION_VIDEOS    = 600;

    private static final Logger LOG = Logger.getLogger(RaindropProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(RaindropProject.class);   
    
    private final Map<String, SourceProvider> sources = new HashMap<>();  
    private final List<UpdateCookie> cookies = new ArrayList();  
    private final List<Topic> selectedTopics = new ArrayList(); 
    private final List<Goal> selectedGoals = new ArrayList();     
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);     
    
    private final FileObject projectDir;        
    private final ProjectState state;
    private final Properties props;   
    private final RaindropSourceGroup raindrops;    
    
    private Lookup lkp;    
    private Source lastSource;
    private FileObject dataDir;     
    private RaindropCollection raindropCollection;  
    private Neo4jInstance neo4jInstance;   
    private RequestProcessor.Task task;     
    
    public RaindropProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir;        
        this.state = state;
        this.props = props;
        raindrops = new RaindropSourceGroup();
        
        ReferenceProvider referenceProvider = Lookup.getDefault().lookup(ReferenceProvider.class);
        if(referenceProvider != null)
        {
            SourceProvider references = new ReferenceSourceGroup(referenceProvider);            
            sources.put(references.getRootFolder().getName(), references);            
        }

        YouTubeVideoProvider youtubeProvider = Lookup.getDefault().lookup(YouTubeVideoProvider.class);
        if(youtubeProvider != null)
        {
            SourceProvider youTube = new YouTubeSourceGroup(youtubeProvider);
            sources.put(youTube.getRootFolder().getName(), youTube);                       
        }        
        
        sources.put(raindrops.getRootFolder().getName(), raindrops);            
    } 
    
    private synchronized FileObject getDataDirectory() throws IOException
    {
        if(dataDir == null)
        {
            dataDir = projectDir.getFileObject(DATA_FOLDER);
            if(dataDir == null)
            {
                dataDir = projectDir.createFolder(DATA_FOLDER);
                LOG.info("Data dir created: " + dataDir.getPath());                        
            }                 
        }                           
        return dataDir;       
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
    
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
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
                list.add(new IconProviderImpl());
                list.add(new ProjectOpenedHookImpl());   
                list.add(new SubprojectProviderImpl());
                list.add(new RootProjectProviderImpl());
                
                if(collection instanceof RaindropChildrenCollection)
                {
                    list.add(new ParentProjectProviderImpl());  
                }                
                
                list.add(new RaindropProjectLogicalView(this));
                list.add(new RaindropCustomizerProvider(this));  
                
                /*
                list.add(new YouTubeProjectsProviderImpl());                
                list.add(new DomainProjectsProviderImpl());
                list.add(new GtdProjectsProviderImpl());  
                */
                
                list.add(new HtmlFilesProviderImpl());                                 
                list.add(new KnowledgeGraphProviderImpl());
                list.add(new GoalsGraphProviderImpl());   
                
                
                list.add(new SourcesImpl()); 
                
                list.add(new ThoughtDataGroupProviderImpl()); 
                list.add(new BookDataGroupProviderImpl()); 
                list.add(new ArticleDataGroupProviderImpl()); 
                list.add(new DocumentDataGroupProviderImpl()); 
                list.add(new LinkDataGroupProviderImpl());                
                list.add(new VideoDataGroupProviderImpl()); 
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

// TODO Raindrop     
    
    private synchronized RaindropCollection getRaindropCollection()
    {
        if(raindropCollection == null)
        {
            String userID = props.getProperty(PROP_RAINDROP_USER_ID);
            String collectionID = props.getProperty(PROP_RAINDROP_COLLECTION_ID);
            String string = props.getProperty(PROP_RAINDROP_COLLECTION_ROOT);
            if (userID != null && collectionID != null && string != null)
            {
                RaindropAccount account = RaindropService.getDeafult().getAccount(Integer.parseInt(userID));
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
            task = RP.create(raindrops);  
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
    }
    
// TODO ProjectInformation 
    
    private final class Info implements ProjectInformation
    {                                       
        @Override
        public Icon getIcon()
        {                    
            return new ImageIcon(ImageUtilities.loadImage(Raindrop.ICON));
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
            return ImageUtilities.loadImage(Raindrop.ICON);
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
    
// TODO DataGroup    

    private final class ThoughtDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {
        @StaticResource()
        private static final String ICON = "openpkm/raindrop/resources/notes_pin.png"; 
        
        private final ChangeSupport changeSupport; 

        public ThoughtDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_THOUGHTS;
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
            return "thought";
        }

        @Override
        public String getDisplayName() 
        {
            return "Thoughts";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            Image image = ImageUtilities.loadImage(ICON, false);
            if(hasChildren)
            {
                return image;
            }
            return ImageUtilities.createDisabledImage(image);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Thought thought = data.getLookup().lookup(Thought.class);
                if(thought != null)
                {
                    return true;
                }                 
            }                                    
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Thought)
            {
                changeSupport.fireChange();
            }
        }
    }  
    
    private final class BookDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {
        @StaticResource()
        private static final String ICON = "openpkm/raindrop/resources/books.png"; 
        
        private final ChangeSupport changeSupport; 

        public BookDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
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
            Image image = ImageUtilities.loadImage(ICON, false);
            if(hasChildren)
            {
                return image;
            }
            return ImageUtilities.createDisabledImage(image);
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
        @StaticResource()
        private static final String ICON = "openpkm/raindrop/resources/newspaper.png"; 
        
        private final ChangeSupport changeSupport; 

        public ArticleDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
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
            Image image = ImageUtilities.loadImage(ICON, false);
            if(hasChildren)
            {
                return image;
            }
            return ImageUtilities.createDisabledImage(image);
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
        @StaticResource()
        private static final String ICON = "openpkm/raindrop/resources/inbox_document.png"; 
        
        private final ChangeSupport changeSupport; 

        public DocumentDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
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
            Image image = ImageUtilities.loadImage(ICON, false);
            if(hasChildren)
            {
                return image;
            }
            return ImageUtilities.createDisabledImage(image);
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
        @StaticResource()
        private static final String ICON = "openpkm/raindrop/resources/web_layout.png"; 
        
        private final ChangeSupport changeSupport; 

        public LinkDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
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
            Image image = ImageUtilities.loadImage(ICON, false);
            if(hasChildren)
            {
                return image;
            }
            return ImageUtilities.createDisabledImage(image);
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
    
    private final class VideoDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {
        @StaticResource()
        private static final String ICON = "openpkm/raindrop/resources/television.png"; 
        
        private final ChangeSupport changeSupport; 

        public VideoDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
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
            Image image = ImageUtilities.loadImage(ICON, false);
            if(hasChildren)
            {
                return image;
            }
            return ImageUtilities.createDisabledImage(image);
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

// TODO Sources

    private final class SourcesImpl implements Sources, SourcesProvider, PropertyChangeListener
    {
        private final ChangeSupport changeSupport; 

        public SourcesImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        }        
        
        @Override
        public SourceProvider getSourceProvider(String folder)
        {
            return sources.get(folder);
        }
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }
        
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
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            changeSupport.fireChange();
        }        
    }
    
// TODO SourceGroup    
    
    private final class ReferenceSourceGroup implements SourceProvider, FileChangeListener
    {               
        @StaticResource()
        private static final String ICON = "openpkm/raindrop/resources/web_disk.png";   
        
        private static final String ROOT_FOLDER = "reference";       
              
        private Map<String, Reference> references;         
        private FileObject rootDir;  

        private final ReferenceProvider provider;

        public ReferenceSourceGroup(ReferenceProvider provider) 
        {
            this.provider = provider;
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }  
        
        private synchronized Map<String, Reference> getReferences()
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
        public Source getSource(String sourceID) 
        {
            return getReferences().get(sourceID);
        }                 

        @Override
        public FileObject getRootFolder() 
        {
            if(rootDir == null)
            {
                try
                {                
                    rootDir = getDataDirectory().getFileObject(ROOT_FOLDER);
                    if(rootDir == null)
                    {
                        rootDir = getDataDirectory().createFolder(ROOT_FOLDER);
                        LOG.info("Reference root folder created: " + dataDir.getPath());                        
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
        public String getName() 
        {
            return "reference";
        }

        @Override
        public String getDisplayName() 
        {
            return "References";
        }

        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }

        @Override
        public boolean contains(FileObject file) 
        {
            return getReferences().containsKey(file.getName());
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
                getReferences().put(reference.getSourceID(), reference);
                
                String filename = null;
                if(reference instanceof TitleProvider)
                {
                    TitleProvider provider = (TitleProvider)reference;
                    filename = FileUtil.findFreeFileName(getDataDirectory(), provider.getTitle(), reference.getDataFileExtension());                    
                }
                else
                {
                    String name = reference.getTimeCreated().format(DateTimeFormatter.BASIC_ISO_DATE);   
                    filename = FileUtil.findFreeFileName(getDataDirectory(), name, reference.getDataFileExtension());                    
                }
                
                FileObject fo = getDataDirectory().createData(filename, reference.getDataFileExtension());  
                fo.setAttribute(ATTR_SOURCE_FOLDER, getRootFolder().getName());
                fo.setAttribute(ATTR_SOURCE_ID, reference.getSourceID());

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
    } 
    
    private final class RaindropSourceGroup implements SourceProvider, FileChangeListener, Runnable 
    {
        private static final String ROOT_FOLDER = "raindrop";       
              
        private Map<String, Raindrop> raindrops; 
        
        private FileObject rootDir;         
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        } 
        
        private synchronized Map<String, Raindrop> getRaindrops()
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
        public Source getSource(String sourceID) 
        {
            return getRaindrops().get(sourceID);
        }                    

        @Override
        public synchronized FileObject getRootFolder() 
        {
            if(rootDir == null)
            {
                try
                {                
                    rootDir = getDataDirectory().getFileObject(ROOT_FOLDER);
                    if(rootDir == null)
                    {
                        rootDir = getDataDirectory().createFolder(ROOT_FOLDER);
                        LOG.info("Raindrop root folder created: " + dataDir.getPath());                        
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
        public String getName() 
        {
            return "raindrop";
        }

        @Override
        public String getDisplayName() 
        {
            return "Raindrops";
        }

        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(Raindrop.ICON));
        }

        @Override
        public boolean contains(FileObject file) 
        {                                   
            return getRaindrops().containsKey(file.getName());
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
        public void run() 
        {
            //System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " Raindrop RSS : " + getTitle());
            String source = RAINDROP_FEED_URL + getRaindropCollection().getCollectionID() + "/feed";
            try
            {
                URL url = new URL(source);
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(url));
                if(feed.getPublishedDate() != null)
                {
                    LocalDateTime newPublishedDate = Utils.convertToLocalDateTime(feed.getPublishedDate());
                    LocalDateTime publishedDate = getRaindropPublishedDate();
                    if(publishedDate == null || newPublishedDate.isAfter(publishedDate))
                    {                      
                        setRaindropPublishedDate(newPublishedDate);
                        saveRaindrops("Saving raindrops by project: " + getTitle());                                                              
                    }
                }                                                        
            }
            catch (MalformedURLException e)
            {
                LOG.warning("Raindrop malformed URL: " + e.getMessage());
            }
            catch (IOException e)
            {
                LOG.warning("Raindrop IO: " + e.getMessage());
            }    
            catch (FeedException e)
            {
                LOG.warning("Raindrop feed: " + e.getMessage());
            }              
            task.schedule(100000);
        } 
        
        private void saveRaindrops(String comments) throws IOException
        {
            List<Raindrop> raindrops = getRaindropCollection().getRaindrops();
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
                getRaindrops().put(raindrop.getSourceID(), raindrop);                
                
                String filename = null;
                if(raindrop instanceof TitleProvider)
                {
                    TitleProvider provider = (TitleProvider)raindrop;
                    filename = FileUtil.findFreeFileName(getDataDirectory(), provider.getTitle(), MarkdownSupport.EXTENSION);                    
                }
                else
                {
                    String name = raindrop.getTimeCreated().format(DateTimeFormatter.BASIC_ISO_DATE);   
                    filename = FileUtil.findFreeFileName(getDataDirectory(), name, MarkdownSupport.EXTENSION);                    
                }                
                
                FileObject fo = getDataDirectory().createData(filename, MarkdownSupport.EXTENSION);  
                fo.setAttribute(ATTR_SOURCE_FOLDER, getRootFolder().getName());
                fo.setAttribute(ATTR_SOURCE_ID, raindrop.getSourceID());
                if(raindrop.getNote() != null && !raindrop.getNote().isBlank())
                {
                    OutputStream output = fo.getOutputStream();
                    output.write(raindrop.getNote().getBytes());
                    output.close();
                }

                setLastSource(raindrop);
                
                URL url = new URL(raindrop.getCover());
                Image image = Utils.resizeImage(ImageIO.read(url), 320, 180); 
                Icon picture = ImageUtilities.image2Icon(image);                                                                                                  
                Icon icon = ImageUtilities.loadImageIcon(Raindrop.ICON, true);                                
                JLabel baloonDetails = new JLabel(picture);
                baloonDetails.addMouseListener(FileUtils.clicked2open(fo));
                baloonDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                JComponent details = createDetails(raindrop.getExcerpt(), FileUtils.action2open(fo), picture); 
                String title = getTitle() + ": " + raindrop.getTitle();                                                                  
                NotificationDisplayer.getDefault().notify(title, icon, baloonDetails, details, AbstractRaindrop.getPriority(raindrop), AbstractRaindrop.getCategory(raindrop));                   
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
            Raindrop raindrop = getRaindrops().get(file.getName());  
            if(raindrop != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Raindrop raindrop = getRaindrops().remove(file.getName());  
            if(raindrop != null)
            {
                raindrop.setDeleted();
                setLastSource(raindrop);
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
    
    private final class YouTubeSourceGroup implements SourceProvider, FileChangeListener
    { 
        private static final String ROOT_FOLDER = "youtube";       
              
        private Map<String, YouTubeVideo> videos; 
        
        private FileObject rootDir; 
        
        private final YouTubeVideoProvider provider;

        public YouTubeSourceGroup(YouTubeVideoProvider provider) 
        {
            this.provider = provider;
        }                
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
        }         
        
        private synchronized Map<String, YouTubeVideo> getVideos()
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
        public Source getSource(String sourceID) 
        {
            return getVideos().get(sourceID);
        }                    

        @Override
        public synchronized FileObject getRootFolder() 
        {
            if(rootDir == null)
            {
                try
                {                
                    rootDir = getDataDirectory().getFileObject(ROOT_FOLDER);
                    if(rootDir == null)
                    {
                        rootDir = getDataDirectory().createFolder(ROOT_FOLDER);
                        LOG.info("Raindrop root folder created: " + dataDir.getPath());                        
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
        public String getName() 
        {
            return "youtube";
        }

        @Override
        public String getDisplayName() 
        {
            return "YouTube";
        }

        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(YouTubeVideo.ICON));
        }

        @Override
        public boolean contains(FileObject file) 
        {
            return getVideos().containsKey(file.getName());
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
                
                String filename = null;
                if(video instanceof TitleProvider)
                {
                    TitleProvider provider = (TitleProvider)video;
                    filename = FileUtil.findFreeFileName(getDataDirectory(), provider.getTitle(), video.getDataFileExtension());                    
                }
                else
                {
                    String name = video.getTimeCreated().format(DateTimeFormatter.BASIC_ISO_DATE);   
                    filename = FileUtil.findFreeFileName(getDataDirectory(), name, video.getDataFileExtension());                    
                }                
                
                FileObject fo = getDataDirectory().createData(filename, video.getDataFileExtension());  
                fo.setAttribute(ATTR_SOURCE_FOLDER, getRootFolder().getName());
                fo.setAttribute(ATTR_SOURCE_ID, video.getSourceID());

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

// TODO KnowledgeGraphProvider     
    
    private final class KnowledgeGraphProviderImpl implements KnowledgeGraphProvider
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
            List<String> topics = provider.getTopics();
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
    
    private final class GoalsGraphProviderImpl implements GoalsGraphProvider
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
            if(selectedGoals.isEmpty())
            {
                return true;
            }
            else
            {
                Set<String> rootTopics = getRootGoals(getSelectedGoals());
                for(String topic : provider.getRootGoals())
                {
                    if(rootTopics.contains(topic))
                    {
                        return true;
                    }
                }                
            }
            return false;            
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
