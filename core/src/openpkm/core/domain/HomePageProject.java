/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
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
import openpkm.base.CloseSupport;
import openpkm.base.DataGroupProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.Document;
import openpkm.base.FileTypeProvider;
import openpkm.base.GroupProvider;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.Link;
import openpkm.base.NodeProvider;
import openpkm.base.OpenSupport;
import openpkm.base.Picture;
import openpkm.base.PropertiesProvider;
import openpkm.base.ReadLaterProvider;
import openpkm.base.Source;
import openpkm.base.SourceEvent;
import openpkm.base.SourceEventListener;
import openpkm.base.SourceGroupProvider;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.SourceProviders;
import openpkm.base.StateSupport;
import openpkm.base.UpdateCookie;
import openpkm.base.Video;
import openpkm.base.WorkflowProvider;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import openpkm.utils.FileUtils;
import openpkm.utils.LogicalViewProviderImpl;
import openpkm.utils.Utils;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
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
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import openpkm.domain.Blog;
import openpkm.domain.BlogFactory;
import openpkm.domain.BlogProvider;
import openpkm.domain.Domain;
import openpkm.domain.FaviconProvider;
import openpkm.domain.MultiViewElementImpl;
import openpkm.domain.WebPage;
import openpkm.domain.WebPageFactory;
import openpkm.domain.WebPageProvider;
import openpkm.github.GitHubFactory;
import openpkm.github.GitHubProvider;
import openpkm.github.GitHubUser;
import openpkm.reference.ReferenceFactory;
import openpkm.rss.RssChannel;
import openpkm.rss.RssFactory;
import openpkm.rss.RssProvider;
import openpkm.utils.SourceEventImpl;
import openpkm.youtube.YouTubeChannel;
import openpkm.youtube.YouTubeChannelFactory;
import openpkm.youtube.YouTubeChannelProvider;
import org.openide.awt.NotificationDisplayer;
import org.openide.cookies.CloseCookie;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.windows.WindowManager;

/**
 *
 * @author Rok Koren
 */
public class HomePageProject implements Project, Blog, Domain, SourceProviders, MultiViewDescription, BatchUpdateSupport
{      
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_NOTES      = 100;
    private static final int POSITION_DOCUMENTS  = 200;
    private static final int POSITION_ARTICLES   = 300;
    private static final int POSITION_BOOKS      = 400;
    private static final int POSITION_LINKS      = 500;
    private static final int POSITION_PICTURES   = 600;    
    private static final int POSITION_VIDEOS     = 700;
    private static final int POSITION_RSS        = 800;    
    private static final int POSITION_READ_LATER = 900;

    private static final Logger LOG = Logger.getLogger(HomePageProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(HomePageProject.class);   
    
    private final Map<String, SourceGroup> sources = new HashMap();  
    private final List<UpdateCookie> cookies = new ArrayList();         
    
    private final FileObject projectDir;        
    private final ProjectState state;
    private final Properties props;
    private final PropertyChangeSupport propertyChangeSupport;
    private final EventListenerList listeners;
    
    private Lookup lkp;  
    private FileObject dataDir;
    private LocalFileSystem fileSystem; 
    
    public HomePageProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;
        propertyChangeSupport = new PropertyChangeSupport(this);
        listeners = new EventListenerList(); 
       
        WebPageFactory webPageFactory = Lookup.getDefault().lookup(WebPageFactory.class);
        if(webPageFactory != null)
        {          
            SourceProvider links = new WebPageProviderImpl(webPageFactory);
            sources.put(links.getName(), links);            
        }        
        
        ReferenceFactory referenceFactory = Lookup.getDefault().lookup(ReferenceFactory.class);
        if(referenceFactory != null)
        {          
            SourceProvider references = new ReferenceProviderImpl(referenceFactory);
            sources.put(references.getName(), references);            
        }

        YouTubeChannelFactory youTubeChannelFactory = Lookup.getDefault().lookup(YouTubeChannelFactory.class);
        if(youTubeChannelFactory != null)
        {
            SourceProvider provider = new YouTubeChannelProviderImpl(youTubeChannelFactory);
            sources.put(provider.getName(), provider);                       
        }  
        
        GitHubFactory gitHubFactory = Lookup.getDefault().lookup(GitHubFactory.class);
        if(gitHubFactory != null)
        {
            SourceProvider provider = new GitHubProviderImpl(gitHubFactory);
            sources.put(provider.getName(), provider);                       
        }          
        
        BlogFactory blogFactory = Lookup.getDefault().lookup(BlogFactory.class);
        if(blogFactory != null)
        {
            SourceProvider provider = new BlogProviderImpl(blogFactory);
            sources.put(provider.getName(), provider);                       
        } 

        RssFactory rssFactory = Lookup.getDefault().lookup(RssFactory.class);
        if(rssFactory != null)
        {
            RssProvider provider = new RssProviderImpl(rssFactory);
            sources.put(provider.getName(), provider);             
        }
    }  
    
// TODO Domain
        
    @Override
    public String getFileName()
    {
        return props.getProperty(PROP_FILE_NAME);
    } 
               
    
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
    
    @Override
    public void notifyDeleted()
    {
        state.notifyDeleted();
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
        SourceGroup sourceGroup = sources.get(folder);
        if(sourceGroup instanceof SourceProvider provider)
        {
            return provider;
        }
        return null;
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

            list.add(this);
            list.add(new Info());
            list.add(new SourcesImpl());  
            list.add(new DisplayNameProviderImpl());
            list.add(new IconProviderImpl());
            list.add(new ProjectOpenedHookImpl());   
            list.add(new RootProjectProviderImpl());
            list.add(new ParentProjectProviderImpl());              

            list.add(new LogicalViewProviderImpl(this));
            list.add(new HomePageCustomizerProvider(this));  
            
            list.add(new HtmlFilesProviderImpl());                                  

            list.addAll(sources.values());
            
            list.add(new BookDataGroupProviderImpl()); 
            list.add(new ArticleDataGroupProviderImpl()); 
            list.add(new DocumentDataGroupProviderImpl()); 
            list.add(new LinkDataGroupProviderImpl());                
            list.add(new PictureDataGroupProviderImpl()); 
            list.add(new VideoDataGroupProviderImpl()); 
            list.add(new ReadLaterProviderImpl());             
            
            lkp = Lookups.fixed(list.toArray(new Object[list.size()]));              
        }
        return lkp;
    }      

// TODO Blog    
  
    @Override
    public String getUrl() 
    {
        return props.getProperty(PROP_URL);
    }     

    @Override
    public String getFavicon() 
    {
        return props.getProperty(PROP_FAVICON);
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
        return getFileName();
    }  
    
    @Override
    public LocalDateTime getTimeCreated() 
    {
        String string = props.getProperty(PROP_TIME_CREATED);
        if(string != null)
        {
            return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
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
    
    @Override
    public String preferredID() 
    {
        return "home_page";
    }         

    @Override
    public MultiViewElement createElement() 
    {
        return new MultiViewElementImpl(this, false);
    } 

    @Override
    public HelpCtx getHelpCtx() 
    {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public String getDisplayName() 
    {
        return "Home Page";
    }   

    @Override
    public int getPersistenceType() 
    {
        return TopComponent.PERSISTENCE_NEVER;
    }  

    @Override
    public Image getIcon() 
    {    
        IconProvider provider = getLookup().lookup(IconProvider.class);
        return provider.getIcon(BeanInfo.ICON_COLOR_16x16);
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
        private RequestProcessor.Task task;          
        
        @Override
        protected void projectOpened() 
        {
            task = RP.create(this);   
            task.schedule(1000);                                      
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
            task.cancel();  
            propertyChangeSupport.removePropertyChangeListener(this);    
            
            Collection<? extends CloseSupport> providers = getLookup().lookupAll(CloseSupport.class);            
            for(CloseSupport provider : providers)
            {
                provider.close();
            }  
            
            for(TopComponent topComponent : WindowManager.getDefault().getRegistry().getOpened())
            {
                DataObject data = topComponent.getLookup().lookup(DataObject.class);
                if (data != null) 
                {
                    SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                    if(sourceProvider != null)
                    {
                        Source source = sourceProvider.getSource();
                        if(source != null)
                        {
                            Project project = source.getLookup().lookup(Project.class);
                            if(project == HomePageProject.this)
                            {
                                CloseCookie close = data.getLookup().lookup(CloseCookie.class);
                                close.close();
                            }                                                                       
                        }                                                                                                          
                    }                                                                                                                                   
                }                  
            }              
        }  
        
        @Override
        public void run()
        {  
            RssProvider rssProvider = getLookup().lookup(RssProvider.class);
            if(rssProvider != null)
            {
                for(RssChannel channel : rssProvider.getChannels().getChannels())
                {
                    try
                    {
                        URL url = new URL(channel.getFeedUrl());
                        SyndFeedInput input = new SyndFeedInput();
                        SyndFeed feed = input.build(new XmlReader(url));
                        
                        RssChannel newValue = rssProvider.getFactory().getRssChannel(channel.getFeedUrl(), channel.getFileName(), feed);
                        if(!channel.getProperties().equals(newValue.getProperties()))
                        {
                            channel.merge(newValue);
                            channel.markModified();
                            WebPageProvider webPagePovider = getLookup().lookup(WebPageProvider.class);
                            if(webPagePovider != null)
                            {
                                for(SyndEntry syndEntry : feed.getEntries()) 
                                {
                                    String fileName = FileUtils.getFileName(webPagePovider.getRootFolder(), PropertiesProvider.EXTENSION);
                                    WebPage webPage = webPagePovider.getFactory().getWebPage(fileName, syndEntry);
                                    if(webPage != null && !webPagePovider.getPages().getPagesByUrl().containsKey(webPage.getLinkUrl()))
                                    {
                                        AsciiDocSupport fileTypeProvider = Lookup.getDefault().lookup(AsciiDocSupport.class);
                                        if(fileTypeProvider != null)            
                                        {
                                            FileObject file = webPagePovider.createData(webPage, fileTypeProvider);                           

                                             if(file != null)
                                             { 
                                                OutputStream os = webPagePovider.getRootFolder().createAndOpen(fileName + "." + PropertiesProvider.EXTENSION);  
                                                webPagePovider.getFactory().save(webPage, os, "New RSS Web Page Created by Project: " + getTitle());
                                                os.close();                                                   
                                                 
                                                 String text = getTitle() + ": " + syndEntry.getTitle();

                                                 BufferedImage image = null;
                                                 if(!syndEntry.getEnclosures().isEmpty())
                                                 {
                                                     SyndEnclosure syndEnclosure = syndEntry.getEnclosures().get(0);                                  
                                                     if(syndEnclosure.getType().startsWith("image"))
                                                     {
                                                         URL url2 = new URL(syndEnclosure.getUrl());
                                                         //Image image = Utils.resizeImage(ImageIO.read(url2), 320, 180); 
                                                         image = ImageIO.read(url2);                                         
                                                     }
                                                 }

                                                 IconProvider provider = channel.getLookup().lookup(IconProvider.class);
                                                 Icon icon = ImageUtilities.image2Icon(provider.getIcon(BeanInfo.ICON_COLOR_16x16)); 

                                                 JLabel baloonDetails = new JLabel();
                                                 if(image == null)
                                                 {
                                                     baloonDetails.addMouseListener(FileUtils.clicked2open(file));
                                                     baloonDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                                     JComponent details = createDetails(syndEntry.getDescription().getValue(), FileUtils.action2open(file), null);                                                                
                                                     NotificationDisplayer.getDefault().notify(text, icon, baloonDetails, details, NotificationDisplayer.Priority.NORMAL, "Web-Category-Name");                                 
                                                 }
                                                 else
                                                 {
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
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getIcon(IconsProvider.ICON.HOME_PAGE);
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
            return HomePageProject.this;
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
  
// TODO DisplayNameProvider

    private final class DisplayNameProviderImpl implements DisplayNameProvider, PropertyChangeListener, ChangeSupportProvider
    {
        private final ChangeSupport changeSupport;  

        public DisplayNameProviderImpl() 
        {
            changeSupport = new ChangeSupport(this);  
            addTitleListener(this);
        }

        @Override
        public String getDisplayName(TextFormat format) 
        {
            if(format == TextFormat.PLAIN)
            {
                return getTitle();
            }
            return null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
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
    }
    
// TODO IconProvider    
    
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
            return provider.getImage(IconsProvider.ICON.HOME_PAGE);
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
            String favicon = getFavicon();
            try
            {
                if(favicon != null)
                {
                    try
                    {
                        URL url = new URL(favicon);
                        BufferedImage image = ImageIO.read(url);  
                        if(image != null)
                        {
                            icon = Utils.resizeImage(image, 16, 16); 
                            changeSupport.fireChange();                        
                        }
                    }
                    catch(MalformedURLException e)
                    {
                        LOG.warning(e.getMessage());
                    }             
                }
                if(icon == null)
                {
                    FaviconProvider provider = Lookup.getDefault().lookup(FaviconProvider.class);
                    icon = provider.getFavicon(getUrl(), 16);  
                    changeSupport.fireChange(); 
                }                
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
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

// TODO RootProjectProvider     

    private final class RootProjectProviderImpl implements RootProjectProvider
    {
        @Override
        public Project getRootProject() 
        {
            return Utils.getRootProject(HomePageProject.this);
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
                        BlogProvider provider = project.getLookup().lookup(BlogProvider.class);
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
    
// TODO SourceGroup    
    
    private final class RssProviderImpl extends RssProvider implements SourceGroupProvider, CloseSupport, FileChangeListener
    {
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/rss.png";         
        
        public RssProviderImpl(RssFactory factory) 
        {
            super(factory);             
        }         
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
        } 

        @Override
        public DisplayNameProvider getDisplayNameProvider()
        {
            return new GroupProvider.DisplayNameProviderImpl(this);
        }
        
        @Override
        public IconProvider getIconProvider()
        {
            return new GroupProvider.IconProviderSourceGroupImpl(this);
        }        
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return new RssActionsProvider(this);
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_RSS;
        }  
        
        @Override
        public void close()
        {
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
                                factory.save(channel, os, "Updated by Home page project: " + getTitle());
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
        public Icon getIcon(boolean bln) 
        {
            return ImageUtilities.loadIcon(ICON);
        }                        
        
        @Override
        public SortedSet<NodeProvider> getNodes()
        {
            List<NodeProvider> list = getChannels().getChannels().stream()
                    .filter(NodeProvider.class::isInstance)
                    .map(NodeProvider.class::cast)
                    .toList();        
            
            SortedSet<NodeProvider> sorted = new TreeSet<NodeProvider>(NodeProvider.displayNameComparator());
            sorted.addAll(list);
            
            return sorted;
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
                            RssChannel channel = factory.getRssChannel(Utils.getProperties(file)); 
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
        public void fileFolderCreated(FileEvent evt) 
        {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileDataCreated(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            try
            {
                RssChannel channel = factory.getRssChannel(Utils.getProperties(file)); 
                getChannels().addChannel(channel);
                changeSupport.fireChange();
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
            if(getChannels().removeChannel(file.getName()) != null)
            {
                changeSupport.fireChange();
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
            return HomePageProject.this;
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
    
    private final class BookDataGroupProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public BookDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }              
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
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
    
    private final class ArticleDataGroupProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public ArticleDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
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
    
    private final class DocumentDataGroupProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public DocumentDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }             
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
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

    private final class LinkDataGroupProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 

        public LinkDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
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

    private final class PictureDataGroupProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 
                
        public PictureDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
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
    
    private final class VideoDataGroupProviderImpl implements DataGroupProvider, SourceEventListener
    {        
        private final ChangeSupport changeSupport; 
                
        public VideoDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            listeners.add(SourceEventListener.class, this);
        } 
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
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
            return HomePageProject.this;
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
                                factory.save(page, os, "Updated by Home page project: " + getTitle());
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
    
    private final class ReferenceProviderImpl extends ReferenceProvider implements FileChangeListener, CloseSupport
    {               
        public ReferenceProviderImpl(ReferenceFactory factory) 
        {
            super(factory);
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
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
                                factory.save(reference, os, "Updated by Home page project: " + getTitle());
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
    
    private final class YouTubeChannelProviderImpl extends YouTubeChannelProvider implements FileChangeListener
    {
        public YouTubeChannelProviderImpl(YouTubeChannelFactory factory) 
        {
            super(factory);
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
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
    
    private final class GitHubProviderImpl extends GitHubProvider implements FileChangeListener
    {
        public GitHubProviderImpl(GitHubFactory factory) 
        {
            super(factory);
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
        }                
        
        @Override
        public synchronized Map<String, GitHubUser> getUsersById()
        {
            if(users == null)
            {
                users = new HashMap<>();
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
                                    GitHubUser user = project.getLookup().lookup(GitHubUser.class);
                                    if(user != null)
                                    {
                                        users.put(user.getUserID(), user);
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
                                GitHubUser user = factory.getGitHubUser(Utils.getProperties(fo)); 
                                users.put(user.getUserID(), user);
                            }
                            catch(IOException e)
                            {
                                LOG.warning(e.getMessage());
                            }                             
                        }                                                                                                                                                                                                                                            
                    }                     
                }                
            }
            return users;
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
                        LOG.info("GitHub root folder created: " + rootDir.getPath());                        
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
        public FileObject createData(GitHubUser user, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, user.getSourceID());  

            if(fileTypeProvider instanceof ArticleProvider)
            {
                ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                OutputStream output = primaryFile.getOutputStream();
                output.write(articleProvider.getArticle(user.getTitle(), user.getDescription()).getBytes());
                output.close();
            }
            
            return primaryFile;             
        }                     
        
        @Override
        public void fileFolderCreated(FileEvent evt) 
        {
            FileObject folder = evt.getFile();
            if(!getUsersById().containsKey(folder.getName()))
            {
                try
                {
                    Project project = ProjectManager.getDefault().findProject(folder);
                    if(project != null)
                    {
                        GitHubUser user = project.getLookup().lookup(GitHubUser.class);  
                        if(user != null)
                        {
                            getUsersById().put(user.getUserID(), user);
                            sourceAdded(new SourceEventImpl(this, user));   
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
                GitHubUser user = factory.getGitHubUser(Utils.getProperties(file)); 
                if(user != null)
                {
                    getUsersById().put(user.getUserID(), user); 
                    sourceAdded(new SourceEventImpl(this, user));                     
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
            GitHubUser user = getUsersById().remove(file.getName());  
            if(user != null)
            {
                user.notifyDeleted();
                sourceDeleted(new SourceEventImpl(this, user)); 
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

    private final class BlogProviderImpl extends BlogProvider implements FileChangeListener, CloseSupport
    {
        public BlogProviderImpl(BlogFactory factory) 
        {
            super(factory);
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return HomePageProject.this;
        } 
        
        @Override
        public void close()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(Blog blog : getBlogs().getBlogs())
                {
                    if(blog instanceof StateSupport state) 
                    {
                        FileObject file = rootDir.getFileObject(blog.getSourceID(), PropertiesProvider.EXTENSION);
                        if(file != null)
                        {
                            try
                            {
                                if(state.isModified())
                                {
                                    OutputStream os = file.getOutputStream();
                                    factory.save(blog, os, "Updated by Raindrop project: " + getTitle());
                                    os.close();
                                }
                                else if(state.isDeleted())
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
        public synchronized Blogs getBlogs()
        {
            if(blogs == null)
            {
                blogs = new Blogs();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            Blog blog = factory.getBlog(Utils.getProperties(file)); 
                            blogs.addBlog(blog);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return blogs;
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
                        LOG.info("Blog root folder created: " + rootDir.getPath());                        
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
        public FileObject createData(Blog blog, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, blog.getSourceID());  

            if(fileTypeProvider instanceof ArticleProvider)
            {
                ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                OutputStream output = primaryFile.getOutputStream();
                output.write(articleProvider.getArticle(blog.getTitle(), blog.getDescription()).getBytes());
                output.close();
            }
            
            return primaryFile;             
        }  
        
        @Override
        public void fileFolderCreated(FileEvent evt) 
        {             
        }

        @Override
        public void fileDataCreated(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            try
            {
                Blog blog = factory.getBlog(Utils.getProperties(file)); 
                if(blog != null)
                {
                    getBlogs().addBlog(blog); 
                    sourceAdded(new SourceEventImpl(this, blog));                      
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
            Blog blog = getBlogs().removeBlog(file.getName());  
            if(blog != null)
            {
                blog.notifyDeleted();
                sourceDeleted(new SourceEventImpl(this, blog)); 
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
            return HomePageProject.this;
        }                 
    }  
    
    private static JComponent createDetails(String text, ActionListener action, Icon icon) 
    {
        if (null == action) {
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
        if(icon != null)
        {
            btn.setIcon(icon);
            btn.setIconTextGap(10);            
        }
        btn.setVerticalTextPosition(SwingConstants.TOP);
        btn.setHorizontalTextPosition(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setHorizontalAlignment(SwingConstants.LEFT);        
        return btn;
    }  
}
