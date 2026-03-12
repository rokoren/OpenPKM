/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.BorderLayout;
import java.awt.Image;
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
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionsProvider;
import openpkm.base.Article;
import openpkm.base.ArticleProvider;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.Book;
import openpkm.base.BookProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DataGroupProvider;
import openpkm.base.DisplayNameProvider;
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
import openpkm.base.Source.SourceState;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviders;
import openpkm.base.UpdateCookie;
import openpkm.base.Video;
import openpkm.base.WebPage;
import openpkm.base.WebPageProvider;
import openpkm.facebook.FacebookPage;
import openpkm.jcef.CefClientProvider;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import openpkm.reference.ReferenceSourceProvider;
import openpkm.utils.FileUtils;
import openpkm.utils.LogicalViewProviderImpl;
import openpkm.utils.TopComponentProvider;
import openpkm.utils.Utils;
import openpkm.utils.WebSourceProvider;
import org.cef.browser.CefBrowser;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.netbeans.spi.project.ParentProjectProvider;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.RootProjectProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.awt.UndoRedo;
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
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 *
 * @author Rok Koren
 */
public class FacebookProject implements Domain, FacebookPage, PropertiesProvider, SourceProviders, BatchUpdateSupport
{
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_NOTES       = 100;
    private static final int POSITION_DOCUMENTS   = 200;
    private static final int POSITION_ARTICLES    = 300;
    private static final int POSITION_BOOKS       = 400;
    private static final int POSITION_LINKS       = 500;
    private static final int POSITION_PICTURES    = 600;    
    private static final int POSITION_VIDEOS      = 700;
    private static final int POSITION_WATCH_LATER = 800;

    private static final Logger LOG = Logger.getLogger(FacebookProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(FacebookProject.class);   
    
    private final Map<String, SourceProvider> sources = new HashMap();  
    private final List<UpdateCookie> cookies = new ArrayList();         
    
    private final FileObject projectDir;        
    private final ProjectState state;
    private final Properties props; 
    private final PropertyChangeSupport propertyChangeSupport;
    
    private Lookup lkp;  
    private FileObject dataDir;
    private LocalFileSystem fileSystem;
    private Source lastSource;   
    
    public FacebookProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;
        propertyChangeSupport = new PropertyChangeSupport(this);

        WebPageProvider webPageProvider = Lookup.getDefault().lookup(WebPageProvider.class);
        if(webPageProvider != null)
        {          
            SourceProvider links = new WebSourceProviderImpl(webPageProvider);
            sources.put(links.getName(), links);            
        }        
        
        ReferenceProvider referenceProvider = Lookup.getDefault().lookup(ReferenceProvider.class);
        if(referenceProvider != null)
        {          
            SourceProvider references = new ReferenceSourceProviderImpl(referenceProvider);
            sources.put(references.getName(), references);            
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

            list.add(this);
            list.add(new Info());
            list.add(new SourcesImpl()); 
            list.add(new IconProviderImpl());
            list.add(new TopComponentProviderImpl());
            list.add(new ProjectOpenedHookImpl());   
            list.add(new RootProjectProviderImpl());
            list.add(new ParentProjectProviderImpl());              

            list.add(new LogicalViewProviderImpl(this));
            list.add(new FacebookCustomizerProvider(this));  

            list.add(new DomainsProviderImpl()); 
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
        return getPageID();
    }    
    
    @Override
    public String getAppID() 
    {
        return props.getProperty(PROP_APP_ID);
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
    
// TODO FacebookPage    
    
    @Override
    public String getPageID() 
    {
        return props.getProperty(PROP_PAGE_ID);
    }  

    @Override
    public String getPicture() 
    {
        return props.getProperty(PROP_PICTURE);
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
    
// TODO TopComponentProvider
    
    private final class TopComponentProviderImpl implements TopComponentProvider, MultiViewDescription, MultiViewElement
    {
        private TopComponent tc;           
        private JToolBar toolbar;
        private CefBrowser browser;
        
        private transient MultiViewElementCallback callback;                         
        
        @Override
        public TopComponent getTopComponent()
        {
            if(tc == null)
            {
                MultiViewDescription[] mvds = new MultiViewDescription[1];
                mvds[0] = this;
                tc = MultiViewFactory.createMultiView(mvds, this);
                tc.setDisplayName(getTitle());
            }
            return tc;
        }        
        
        @Override
        public String preferredID() 
        {
            return "facebook";
        }         
        
        @Override
        public MultiViewElement createElement() 
        {
            return this;
        } 

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Facebook";
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
            CefClientProvider provider = Lookup.getDefault().lookup(CefClientProvider.class);
            if(provider != null)
            {
                try
                {
                    //browser = provider.getCefClient().createBrowser(GitHubUser.GITHUB_URL + getUserName(), false, false);      ;   
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(browser.getUIComponent(), BorderLayout.CENTER);
                    return panel;
                }
                catch(Exception e)
                {
                    LOG.warning(e.getMessage());
                }
            }
            return null;
        }

        @Override
        public JComponent getToolbarRepresentation() 
        {
            if(toolbar == null)
            {
                toolbar = new JToolBar();
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
            return FacebookProject.this.getLookup();
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
    
// TODO ProjectOpenedHook    
    
    private final class ProjectOpenedHookImpl extends ProjectOpenedHook implements PropertyChangeListener
    {                
        @Override
        protected void projectOpened() 
        {  
            propertyChangeSupport.addPropertyChangeListener(this);           
        }

        @Override
        protected void projectClosed() 
        {          
            propertyChangeSupport.removePropertyChangeListener(this);   
            
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
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getIcon(IconsProvider.ICON.FACEBOOK);
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
            return FacebookProject.this;
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
            String picture = getPicture();
            if(picture != null)
            {
                try
                {
                    URL url = new URL(picture);
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

// TODO RootProjectProvider     

    private final class RootProjectProviderImpl implements RootProjectProvider
    {
        @Override
        public Project getRootProject() 
        {
            return Utils.getRootProject(FacebookProject.this);
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
            return FacebookProject.this;
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
        public Lookup.Provider getLookupProvider()
        {
            return FacebookProject.this;
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
        public Lookup.Provider getLookupProvider()
        {
            return FacebookProject.this;
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
        public Lookup.Provider getLookupProvider()
        {
            return FacebookProject.this;
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
        public Lookup.Provider getLookupProvider()
        {
            return FacebookProject.this;
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
        public Lookup.Provider getLookupProvider()
        {
            return FacebookProject.this;
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
        public Lookup.Provider getLookupProvider()
        {
            return FacebookProject.this;
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
   
    private final class WebSourceProviderImpl extends WebSourceProvider implements FileChangeListener
    {  
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/www_page.png";         
        
        public WebSourceProviderImpl(WebPageProvider provider) 
        {
            super(provider);
        }               
        
        @Override
        public Lookup.Provider getLookupProvider()
        {
            return FacebookProject.this;
        } 
        
        @Override
        public void projectClosed()
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(WebPage link : getLinks())
                {
                    SourceState state = link.getState();
                    if(state != null)
                    {
                        FileObject file = rootDir.getFileObject(link.getSourceID(), PropertiesProvider.EXTENSION);
                        if(file != null)
                        {
                            try
                            {
                                if(state == SourceState.MODIFIED)
                                {
                                    OutputStream os = file.getOutputStream();
                                    link.save(os, "Updated by Blog project: " + getTitle());
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
        public synchronized Map<String, WebPage> getLinksById()
        {
            if(links == null)
            {
                links = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            WebPage webPage = provider.getWebPage(Utils.getProperties(file)); 
                            links.put(webPage.getSourceID(), webPage);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return links;
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
        public FileObject createData(WebPage webPage, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, webPage.getSourceID());  

            if(webPage instanceof Article)
            {
                Article article = (Article)webPage;
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
                WebPage webPage = provider.getWebPage(Utils.getProperties(file)); 
                getLinksById().put(webPage.getSourceID(), webPage);               
                setLastSource(webPage);                
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
            WebPage webPage = getLinksById().get(file.getName());  
            if(webPage != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            WebPage webPage = getLinksById().remove(file.getName());  
            if(webPage != null)
            {
                setLastSource(webPage);
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
            return FacebookProject.this;
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
                                    reference.save(os, "Updated by Blog project: " + getTitle());
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
            return FacebookProject.this;
        }                 
    }      
}
