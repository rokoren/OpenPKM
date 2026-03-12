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
import java.beans.PropertyVetoException;
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
import java.util.Comparator;
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
import openpkm.base.Article;
import openpkm.base.ArticleProvider;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.Book;
import openpkm.base.BookProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.ChildrenGoal;
import openpkm.base.ChildrenTopic;
import openpkm.base.Content;
import openpkm.base.ContentProvider;
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
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import openpkm.base.DataGroupProvider;
import openpkm.base.Domain;
import openpkm.base.DomainsProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.IconsProvider;
import openpkm.base.Note;
import openpkm.base.Picture;
import openpkm.base.SourceProviders;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import openpkm.reference.ReferenceSourceProvider;
import openpkm.utils.ContentSourceProvider;
import openpkm.utils.DateTimeUtils;
import openpkm.youtube.YouTubeSourceProvider;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.util.Utilities;
import openpkm.base.NotebooksProvider;
import openpkm.base.Notebook;
import openpkm.base.Source.SourceState;
import openpkm.utils.DisplayNameProviderImpl;
import openpkm.utils.LogicalViewProviderImpl;

/**
 *
 * @author Rok Koren
 */
public class RaindropProject implements Project, TitleProvider, DescriptionProvider, PropertiesProvider, SourceProviders, BatchUpdateSupport
{
    public static final String PROP_RAINDROP_USER_ID         = "raindrop.user.id";    
    public static final String PROP_RAINDROP_COLLECTION_ID   = "raindrop.collection.id";
    public static final String PROP_RAINDROP_COLLECTION_ROOT = "raindrop.collection.root";
    public static final String PROP_RAINDROP_PUBLISHED_DATE  = "raindrop.published.date";
    
    public static final String PROP_NEO4J_INSTANCE_ID   = "neo4j.instance.id";   
    public static final String PROP_TRELLO_USERNAME     = "trello.username"; 
    public static final String PROP_TRELLO_WORKSPACE_ID = "trello.workspace.id";                
    
    private static final String RAINDROP_FEED_URL = "https://raindrop.io/collection/";   
    
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_NOTES     = 100;
    private static final int POSITION_DOCUMENTS = 200;
    private static final int POSITION_ARTICLES  = 300;
    private static final int POSITION_BOOKS     = 400;
    private static final int POSITION_LINKS     = 500;
    private static final int POSITION_PICTURES  = 600;    
    private static final int POSITION_VIDEOS    = 700;

    private static final Logger LOG = Logger.getLogger(RaindropProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(RaindropProject.class);   
    
    private final Map<String, SourceProvider> sources = new HashMap();  
    private final List<UpdateCookie> cookies = new ArrayList();  
    private final List<Topic> selectedTopics = new ArrayList(); 
    private final List<Goal> selectedGoals = new ArrayList();     
    
    private final FileObject projectDir;        
    private final ProjectState state;
    private final Properties props;
    private final PropertyChangeSupport propertyChangeSupport;   
    
    private Lookup lkp;  
    private FileObject dataDir;
    private LocalFileSystem fileSystem;
    private Source lastSource; 
    
    private RaindropSourceProviderImpl raindrops;    
    private RaindropCollection raindropCollection;  
    private Neo4jInstance neo4jInstance;   
    private RequestProcessor.Task task;     
    
    public RaindropProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;
        propertyChangeSupport = new PropertyChangeSupport(this);
        
        RaindropProvider raindropProvider = Lookup.getDefault().lookup(RaindropProvider.class);
        if(raindropProvider != null)
        {
            raindrops = new RaindropSourceProviderImpl(raindropProvider);  
            sources.put(raindrops.getName(), raindrops);             
        }

        ContentProvider contentProvider = Lookup.getDefault().lookup(ContentProvider.class);
        if(contentProvider != null)
        {          
            SourceProvider contents = new ContentSourceProviderImpl(contentProvider);
            sources.put(contents.getName(), contents);            
        }
        
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
                
                list.add(new DomainsProviderImpl()); 
                list.add(new NotebooksProviderImpl()); 
                list.add(new HtmlFilesProviderImpl());                                 
                list.add(new KnowledgeGraphProviderImpl());
                list.add(new GoalsGraphProviderImpl());   
                                
                list.addAll(sources.values());
                
                list.add(new NoteDataGroupProviderImpl()); 
                list.add(new BookDataGroupProviderImpl()); 
                list.add(new ArticleDataGroupProviderImpl()); 
                list.add(new DocumentDataGroupProviderImpl()); 
                list.add(new LinkDataGroupProviderImpl());                
                list.add(new PictureDataGroupProviderImpl()); 
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
    public void merge(PropertiesProvider provider)
    {
        props.putAll(provider.getProperties());
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
                RaindropAccount account = RaindropService.getDefault().getAccount(Integer.parseInt(userID));
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
            
            for(SourceProvider provider : sources.values())
            {
                provider.projectClosed();
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
  
// TODO DomainsProvider

    private final class DomainsProviderImpl implements DomainsProvider
    {                        
        private static final String ROOT_FOLDER = "domain";          
        
        private Map<String, Domain> domains; 
        private FileObject rootDir;            
        
        private final ChangeSupport changeSupport;              

        public DomainsProviderImpl()
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
                    LOG.info("Domain dir created: " + rootDir.getPath());                        
                }                 
            }                           
            return rootDir;       
        }         
        
        private synchronized Map<String, Domain> getDomainsById()
        {
            if(domains == null)
            {
                domains = new HashMap<>();
                try
                {
                    for (FileObject fo : getRootDirectory().getChildren()) 
                    {
                        if(fo.isFolder())
                        {
                            Project project = ProjectManager.getDefault().findProject(fo);
                            if(project instanceof Domain domain)
                            {
                                domains.put(domain.getDomainID(), domain);
                            }                                                                                    
                        }
                        else
                        {
                            DataObject data = DataObject.find(fo);
                            Domain domain = data.getLookup().lookup(Domain.class);
                            if(domain != null)
                            {
                                domains.put(domain.getDomainID(), domain);
                            }                            
                        }                                                                                                                                            
                    }                      
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                              
            }
            return domains;
        }  

        @Override
        public Collection<Domain> getDomains()
        {
            return Collections.unmodifiableCollection(getDomainsById().values());
        }
        
        @Override
        public void addDomain(Domain domain)
        {
            getDomainsById().put(domain.getDomainID(), domain);
            changeSupport.fireChange();            
        }
        
        @Override
        public void removeDomain(String domainID)
        {
            Domain domain = getDomainsById().remove(domainID);
            if(domain != null)
            {
                changeSupport.fireChange();                            
            }
        }        
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Domain"));         
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
            return "domain";
        }

        @Override
        public String getDisplayName() 
        {
            return "Domains";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.DOMAINS);
        }               
    } 

// TODO BoardsProvider

    private final class NotebooksProviderImpl implements NotebooksProvider
    {                        
        private static final String ROOT_FOLDER = "notebook";          
        
        private Map<String, Notebook> notebooks; 
        private FileObject rootDir;            
        
        private final ChangeSupport changeSupport;              

        public NotebooksProviderImpl()
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
        
        private synchronized Map<String, Notebook> getNotebooksById()
        {
            if(notebooks == null)
            {
                notebooks = new HashMap<>();
                try
                {
                    for (FileObject fo : getRootDirectory().getChildren()) 
                    {
                        if(fo.isFolder())
                        {
                            Project project = ProjectManager.getDefault().findProject(fo);
                            if(project instanceof Notebook notebook)
                            {
                                notebooks.put(notebook.getNotebookID(), notebook);
                            }                                                                                    
                        }
                        else
                        {
                            DataObject data = DataObject.find(fo);
                            Notebook notebook = data.getLookup().lookup(Notebook.class);
                            if(notebook != null)
                            {
                                notebooks.put(notebook.getNotebookID(), notebook);
                            }                            
                        }                                                                                                                                            
                    }                      
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                              
            }
            return notebooks;
        }  

        @Override
        public Collection<Notebook> getNotebooks()
        {
            return Collections.unmodifiableCollection(getNotebooksById().values());
        }
        
        @Override
        public void addNotebook(Notebook notebook)
        {
            getNotebooksById().put(notebook.getNotebookID(), notebook);
            changeSupport.fireChange();            
        }
        
        @Override
        public void removeNotebook(String motebookID)
        {
            Notebook notebook = getNotebooksById().remove(motebookID);
            if(notebook != null)
            {
                changeSupport.fireChange();                            
            }
        }        
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Notebook"));         
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
            return "notebook";
        }

        @Override
        public String getDisplayName() 
        {
            return "Notebooks";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.NOTEBOOKS);
        }               
    }     
    
// TODO DataGroup    

    private final class NoteDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public NoteDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Note"));         
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Idea")); 
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Comment")); 
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RaindropProject.this;
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
        public FileObject getRootFolder() throws IOException
        {
            return getDataDirectory();
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
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
        public String getDisplayName() 
        {
            return "Notes";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.NOTES);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Note note = data.getLookup().lookup(Note.class);
                if(note != null)
                {
                    return true;
                }                 
            }                                    
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Note)
            {
                changeSupport.fireChange();
            }
        }
    }  
    
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
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
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
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
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
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
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
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
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
            return RaindropProject.this;
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
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
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
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
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

    private final class ContentSourceProviderImpl extends ContentSourceProvider implements FileChangeListener
    {  
        @StaticResource()
        private static final String ICON = "openpkm/raindrop/resources/book_edit.png";         
        
        public ContentSourceProviderImpl(ContentProvider provider) 
        {
            super(provider);
        }               
        
        @Override
        public Lookup.Provider getLookupProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public void projectClosed()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(Content content : getContents())
                {
                    SourceState state = content.getState();
                    if(state != null)
                    {
                        FileObject file = rootDir.getFileObject(content.getSourceID(), PropertiesProvider.EXTENSION);
                        if(file != null)
                        {
                            try
                            {
                                if(state == SourceState.MODIFIED)
                                {
                                    OutputStream os = file.getOutputStream();
                                    content.save(os, "Updated by Raindrop project: " + getTitle());
                                    os.close();
                                }
                                else if(state == SourceState.DELETED)
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
                            Content content = provider.getContent(Utils.getProperties(file)); 
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
                Content content = provider.getContent(Utils.getProperties(file)); 
                getContentsById().put(content.getSourceID(), content);               
                setLastSource(content);                
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
            Content content = getContentsById().get(file.getName());  
            if(content != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Content content = getContentsById().remove(file.getName());  
            if(content != null)
            {
                setLastSource(content);
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
    
    private final class ReferenceSourceProviderImpl extends ReferenceSourceProvider implements FileChangeListener
    {               
        public ReferenceSourceProviderImpl(ReferenceProvider provider) 
        {
            super(provider);
        }               
        
        @Override
        public Lookup.Provider getLookupProvider()
        {
            return RaindropProject.this;
        }  
        
        @Override
        public void projectClosed()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(Reference reference : getReferences())
                {
                    SourceState state = reference.getState();
                    if(state != null)
                    {
                        FileObject file = rootDir.getFileObject(reference.getSourceID(), PropertiesProvider.EXTENSION);
                        if(file != null)
                        {
                            try
                            {
                                if(state == SourceState.MODIFIED)
                                {
                                    OutputStream os = file.getOutputStream();
                                    reference.save(os, "Updated by Raindrop project: " + getTitle());
                                    os.close();
                                }
                                else if(state == SourceState.DELETED)
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
                Reference reference = provider.getReference(Utils.getProperties(file)); 
                getReferencesById().put(reference.getSourceID(), reference);               
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
            Reference reference = getReferencesById().get(file.getName());  
            if(reference != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Reference reference = getReferencesById().remove(file.getName());  
            if(reference != null)
            {
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
    
    private final class RaindropSourceProviderImpl extends RaindropSourceProvider implements FileChangeListener, Runnable 
    { 
        public RaindropSourceProviderImpl(RaindropProvider provider) 
        {
            super(provider);
        }        
        
        @Override
        public Lookup.Provider getLookupProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public void projectClosed()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(Raindrop raindrop : getRaindrops())
                {
                    SourceState state = raindrop.getState();
                    if(state != null)
                    {
                        FileObject file = rootDir.getFileObject(raindrop.getSourceID(), PropertiesProvider.EXTENSION);
                        if(file != null)
                        {
                            try
                            {
                                if(state == SourceState.MODIFIED)
                                {
                                    OutputStream os = file.getOutputStream();
                                    raindrop.save(os, "Updated by Raindrop project: " + getTitle());
                                    os.close();
                                }
                                else if(state == SourceState.DELETED)
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
                    LocalDateTime newPublishedDate = DateTimeUtils.convertToLocalDateTime(feed.getPublishedDate());
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
        public FileObject createData(Raindrop raindrop, FileTypeProvider fileTypeProvider)     
        {
            /*
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
            */
            return null;
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

                if(Utils.getAppID().equals(raindrop.getAppID()))
                {
                    String fileName = FileUtils.getFileName(getDataDirectory(), MarkdownSupport.EXTENSION);
                    FileObject fo = getFileSystem().getRoot().createData(fileName, MarkdownSupport.EXTENSION); 
                    fo.setAttribute(ATTR_SOURCE_PROVIDER, getName());
                    fo.setAttribute(ATTR_SOURCE_ID, raindrop.getSourceID());                  

                    if(raindrop.getNote() != null && !raindrop.getNote().isBlank())
                    {
                        OutputStream output = fo.getOutputStream();
                        output.write(raindrop.getNote().getBytes());
                        output.close();
                    }  
                    
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

                setLastSource(raindrop);                               
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }   
            catch(PropertyVetoException e)
            {
                LOG.warning(e.getMessage());
            }             
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Raindrop raindrop = getRaindropsById().get(file.getName());  
            if(raindrop != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Raindrop raindrop = getRaindropsById().remove(file.getName());  
            if(raindrop != null)
            {
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
    
    private final class YouTubeSourceProviderImpl extends YouTubeSourceProvider implements FileChangeListener
    {
        public YouTubeSourceProviderImpl(YouTubeVideoProvider provider) 
        {
            super(provider);
        }          
        
        @Override
        public Lookup.Provider getLookupProvider()
        {
            return RaindropProject.this;
        } 
        
        @Override
        public void projectClosed()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(YouTubeVideo video : getVideos())
                {
                    SourceState state = video.getState();
                    if(state != null)
                    {
                        FileObject file = rootDir.getFileObject(video.getVideoID(), PropertiesProvider.EXTENSION);
                        if(file != null)
                        {
                            try
                            {
                                if(state == SourceState.MODIFIED)
                                {
                                    OutputStream os = file.getOutputStream();
                                    video.save(os, "Updated by Raindrop project: " + getTitle());
                                    os.close();
                                }
                                else if(state == SourceState.DELETED)
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
                            YouTubeVideo video = provider.getVideo(Utils.getProperties(file), true); 
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
                YouTubeVideo video = provider.getVideo(Utils.getProperties(file), true); 
                getVideosById().put(video.getSourceID(), video);                                                              
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
            YouTubeVideo video = getVideosById().get(file.getName());  
            if(video != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            YouTubeVideo video = getVideosById().remove(file.getName());  
            if(video != null)
            {
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
