/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionsProvider;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.CloseSupport;
import openpkm.base.DataGroupProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.GroupProvider;
import openpkm.base.IconProvider;
import openpkm.base.MarkdownSupport;
import openpkm.base.NodeActionsProvider;
import openpkm.base.NodeDateTimeProvider;
import openpkm.base.NodePositionProvider;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.UpdateCookie;
import openpkm.jcef.CefClientProvider;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAccountsProvider;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloActionProvider;
import openpkm.trello.TrelloBoard;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloLabel;
import openpkm.trello.AbstractTrelloLabelProvider;
import openpkm.trello.TrelloList;
import openpkm.trello.TrelloListProvider;
import openpkm.trello.TrelloMember;
import openpkm.trello.TrelloMemberProvider;
import openpkm.utils.FileUtils;
import openpkm.utils.RoundRectIcon;
import openpkm.utils.Utils;
import org.cef.browser.CefBrowser;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ParentProjectProvider;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.RootProjectProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.DialogDisplayer;
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
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import openpkm.base.NotebooksProvider;
import openpkm.base.Notebook;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviders;
import openpkm.trello.TrelloService;
import openpkm.youtube.YouTubeUtils;
import openpkm.youtube.YouTubeVideo;
import org.netbeans.api.progress.*;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileSystem;
import openpkm.base.SourceGroupProvider;
import openpkm.base.SourceProviderWrapper;
import openpkm.trello.TrelloComment;
import openpkm.utils.DateTimeUtils;
import openpkm.utils.TopComponentProvider;
import openpkm.youtube.YouTubeVideoFactory;
import openpkm.trello.TrelloActionFactory;
import openpkm.trello.TrelloCommentFactory;
import openpkm.trello.TrelloCardFactory;
import openpkm.trello.TrelloCardLink;
import openpkm.trello.TrelloCardProvider;
import openpkm.trello.TrelloLabelFactory;
import openpkm.trello.TrelloMemberFactory;
import openpkm.trello.TrelloListFactory;

/**
 *
 * @author Rok Koren
 */
public class TrelloProject implements Notebook, TrelloBoard, SourceProviders, BatchUpdateSupport
{
    public static final String PROP_ACCOUNT_USERNAME  = "account.username";
    public static final String PROP_WORKSPACE_ID      = "workspace.id";    
    public static final String PROP_BOARD_ID          = "board.id";
    public static final String PROP_BOARD_NAME        = "board.name";
    public static final String PROP_BOARD_DESCRIPTION = "board.description";
    public static final String PROP_BOARD_URL         = "board.url";
    public static final String PROP_BOARD_SHORT_URL   = "board.short.url";
    public static final String PROP_BOARD_BACKGROUND  = "board.background";      
    
    public static final String PROP_TRELLO_ACTIVITY = "trello.activity";
    
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/trello.png";     
    
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_LISTS    = 100;
    private static final int POSITION_ACTIVITY = 200;    
    private static final int POSITION_LABELS   = 300;
    private static final int POSITION_MEMBERS  = 400;           

    private static final Logger LOG = Logger.getLogger(TrelloProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(TrelloProject.class);   
    
    private final Map<String, SourceGroup> sources = new HashMap();  
    private final List<UpdateCookie> cookies = new ArrayList();         
    
    private final FileObject projectDir;        
    private final ProjectState state;
    private final Properties props;   
    private final PropertyChangeSupport propertyChangeSupport;
    
    private Lookup lkp; 
    private FileObject dataDir;
    private LocalFileSystem fileSystem;
    
    private TrelloAccount trelloAccount;
    private Trello trello;    
    private TrelloBoard trelloBoard;    
    
    public TrelloProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props; 
        propertyChangeSupport = new PropertyChangeSupport(this); 
        
        TrelloCardFactory cardFactory = Lookup.getDefault().lookup(TrelloCardFactory.class);
        if(cardFactory != null)
        {
            SourceGroup provider = new TrelloCardProviderImpl(cardFactory);              
            sources.put(provider.getName(), provider);             
        }       
        
        TrelloActionFactory actionFactory = Lookup.getDefault().lookup(TrelloActionFactory.class);
        TrelloCommentFactory commentFactory = Lookup.getDefault().lookup(TrelloCommentFactory.class);              
        if(actionFactory != null && commentFactory != null)
        {
            SourceGroup provider = new TrelloActionProviderImpl(actionFactory, commentFactory);
            sources.put(provider.getName(), provider);              
        }        
        
        TrelloLabelFactory labelFactory = Lookup.getDefault().lookup(TrelloLabelFactory.class);
        if(labelFactory != null)
        {          
            SourceGroup provider = new TrelloLabelProviderImpl(labelFactory);
            sources.put(provider.getName(), provider);            
        }  
        
        TrelloMemberFactory memberFactory = Lookup.getDefault().lookup(TrelloMemberFactory.class);
        if(memberFactory != null)
        {          
            SourceGroup provider = new TrelloMemberProviderImpl(memberFactory);
            sources.put(provider.getName(), provider);            
        } 

        TrelloListFactory listFactory = Lookup.getDefault().lookup(TrelloListFactory.class);
        if(listFactory != null)
        {          
            SourceGroup provider = new TrelloListProviderImpl(listFactory);
            sources.put(provider.getName(), provider);                       
        }  
    } 
    
    public Properties getProperties()
    {
        return props;
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
   
    public TrelloAccount getTrelloAccount()
    {
        if(trelloAccount == null)
        {
            String username = getAccountUsername();
            if (username != null)
            {
                TrelloAccountsProvider provider = Lookup.getDefault().lookup(TrelloAccountsProvider.class);
                if(provider != null)
                {
                    trelloAccount = provider.getAccount(username);                      
                }         
            }                        
        }
        return trelloAccount;
    }     
    
    public synchronized Trello getTrello()
    {
        if(trello == null)
        {
            TrelloAccount account = getTrelloAccount();
            if(account != null)
            {
                trello = new TrelloImpl(account.getApiKey(), account.getAccessToken(), new JDKTrelloHttpClient());                
                //trello = new TrelloImpl(account.getApiKey(), account.getAccessToken(), new OkHttpTrelloHttpClient());                
            }            
        }
        return trello;
    }
    
    public TrelloBoard getTrelloBoard()
    {
        if(trelloBoard == null)
        {
            TrelloAccount account = getTrelloAccount();
            if(account != null)
            {
                String boardID = getBoardID();
                if (boardID != null)
                {
                    trelloBoard = account.getBoard(boardID);          
                }                 
            }                       
        }
        return trelloBoard;
    }     
    
    public LocalDateTime getTrelloActivity()
    {
        String activity = props.getProperty(PROP_TRELLO_ACTIVITY);
        if(activity != null)
        {
            return LocalDateTime.parse(activity, DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
    } 
    
    public void setTrelloActivity(LocalDateTime time)
    {
        if(time == null)
        {
            Object oldValue = props.remove(PROP_TRELLO_ACTIVITY);
            propertyChangeSupport.firePropertyChange(PROP_TRELLO_ACTIVITY, oldValue, time); 
        }
        else        
        {
            String activity = time.format(DateTimeFormatter.ISO_DATE_TIME);
            Object oldValue = props.setProperty(PROP_TRELLO_ACTIVITY, activity);  
            propertyChangeSupport.firePropertyChange(PROP_TRELLO_ACTIVITY, oldValue, activity); 
        }
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
            list.add(new TopComponentProviderImpl());
            list.add(new ProjectOpenedHookImpl());   
            list.add(new RootProjectProviderImpl());
            list.add(new ParentProjectProviderImpl());

            list.add(new TrelloLogicalView(this));
            list.add(new TrelloCustomizerProvider(this));                                         

            list.addAll(sources.values());  
            
            lkp = Lookups.fixed(list.toArray(new Object[list.size()]));              
        }
        return lkp;
    }      

// TrelloBoard
   
    @Override
    public String getAccountUsername()
    {
        return props.getProperty(PROP_ACCOUNT_USERNAME);
    }    
    
    @Override
    public String getWorkspaceID()
    {
        return props.getProperty(PROP_WORKSPACE_ID);
    }
    
    @Override
    public String getBoardID()
    {
        return props.getProperty(PROP_BOARD_ID);
    }    
    
    @Override
    public String getBoardName()
    {
        return props.getProperty(PROP_BOARD_NAME);
    } 

    @Override
    public void setBoardName(String name)
    {
        if(name == null)
        {
            Object oldValue = props.remove(PROP_BOARD_NAME);
            propertyChangeSupport.firePropertyChange(PROP_BOARD_NAME, oldValue, name);
        }
        else
        {
            Object oldValue = props.setProperty(PROP_BOARD_NAME, name);
            propertyChangeSupport.firePropertyChange(PROP_BOARD_NAME, oldValue, name);            
        }
    }
    
    public void addBoardNameListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(PROP_BOARD_NAME, listener);
    }
    
    public void removeBoardNameListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(PROP_BOARD_NAME, listener);
    }    
    
    @Override
    public String getBoardDescription()
    {
        return props.getProperty(PROP_BOARD_DESCRIPTION);
    }     
    
    @Override
    public String getBoardUrl()
    {
        return props.getProperty(PROP_BOARD_URL);
    } 
    
    @Override
    public String getBoardShortUrl()
    {
        return props.getProperty(PROP_BOARD_SHORT_URL);
    }   
    
    @Override
    public Color getBoardBackground()
    {
        String hex = props.getProperty(PROP_BOARD_BACKGROUND);
        if(hex != null)
        {
            return Color.decode(hex);
        }
        return null;
    }
    
    @Override
    public void setBoardBackground(Color color)
    {
        if(color == null)
        {
            Object oldValue = props.remove(PROP_BOARD_BACKGROUND);
            if(oldValue != null)
            {
                oldValue = Color.decode(oldValue.toString());
            }
            propertyChangeSupport.firePropertyChange(PROP_BOARD_BACKGROUND, oldValue, color);
        }
        else
        {
            String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());            
            Object oldValue = props.setProperty(PROP_BOARD_BACKGROUND, hex);
            if(oldValue != null)
            {
                oldValue = Color.decode(oldValue.toString());
            }
            propertyChangeSupport.firePropertyChange(PROP_BOARD_BACKGROUND, oldValue, color);            
        }
    }
    
// TODO Notebook  
    
    @Override
    public String getNotebookID() 
    {
        return getBoardID();
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
    
// TODO DisplayNameProvider     
    
    private final class DisplayNameProviderImpl implements DisplayNameProvider, ChangeSupportProvider, PropertyChangeListener
    {
        private final ChangeSupport changeSupport;

        public DisplayNameProviderImpl() 
        {            
            changeSupport = new ChangeSupport(this);
            addBoardNameListener(this);
        }                
        
        @Override
        public String getDisplayName(TextFormat format) 
        {
            if(format == TextFormat.PLAIN)
            {
                return getBoardName();
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
    
// TODO TopComponentProvider
    
    private final class TopComponentProviderImpl extends TopComponent implements TopComponentProvider
    {        
        private CefBrowser browser; 

        public TopComponentProviderImpl() 
        {
            setLayout(new BorderLayout());
        }                
        
        @Override
        public TopComponent getTopComponent()
        {
            if(browser == null)
            {
                CefClientProvider provider = Lookup.getDefault().lookup(CefClientProvider.class);
                TrelloBoard board = getTrelloBoard();
                if(provider != null && board != null)
                {
                    try
                    {
                        browser = provider.getCefClient().createBrowser(getBoardShortUrl(), false, false);   
                        add(browser.getUIComponent(), BorderLayout.CENTER);
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
        public String preferredID() 
        {
            return "trello";
        }         

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Trello";
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
        public Action[] getActions() 
        {
            return new Action[0];
        }

        @Override
        public Lookup getLookup() 
        {
            return TrelloProject.this.getLookup();
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
            if(evt.getPropertyName().equals(PROP_BOARD_NAME))
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
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }

        @Override
        public String getName() 
        {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() 
        {                       
            return getBoardName();
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
            return TrelloProject.this;
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
    
    private final class IconProviderImpl implements IconProvider, ChangeSupportProvider
    {                
        private final ChangeSupport changeSupport = new ChangeSupport(this); 

        @Override
        public synchronized Image getIcon(int type)
        { 
            Color color = getBoardBackground();
            if(color != null)
            {
                Icon icon = new RoundRectIcon(14, 14, color);
                return ImageUtilities.icon2Image(icon);
            }
            return ImageUtilities.loadImage(ICON);
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

// TODO RootProjectProvider     

    private final class RootProjectProviderImpl implements RootProjectProvider
    {
        @Override
        public Project getRootProject() 
        {
            return Utils.getRootProject(TrelloProject.this);
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
                        NotebooksProvider provider = project.getLookup().lookup(NotebooksProvider.class);
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

    private final class ListProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final TrelloList list;
        private final ChangeSupport changeSupport; 
                
        public ListProviderImpl(TrelloList list)
        {
            this.list = list;
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(TrelloCardProvider.PROP_TRELLO_SYNC_CARD, this);
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        }
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloProject.this;
        }   
        
        @Override
        public DisplayNameProvider getDisplayNameProvider()
        {
            return list.getLookup().lookup(DisplayNameProvider.class);
        }
        
        @Override
        public IconProvider getIconProvider()
        {
            return list.getLookup().lookup(IconProvider.class);
        }        
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            TrelloCardProvider provider = getProvider().getLookup().lookup(TrelloCardProvider.class);
            if(provider != null)
            {
                return new TrelloCardActionsProvider(list, provider);                
            }
            return null;
        }           
        
        @Override
        public Integer getPosition() 
        {
            return list.getListPosition();
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
            return positionComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return list.getListID();
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
                        TrelloCard card = source.getLookup().lookup(TrelloCard.class);
                        if(card != null)
                        {
                            return card.getListID().equals(list.getListID());
                        }                                                
                    }            
                }                                                                                  
            }                                    
            return false;            
        }        

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(evt.getPropertyName().equals(TrelloCardProvider.PROP_TRELLO_SYNC_CARD))
            {
                changeSupport.fireChange();  
            }
            else if(evt.getPropertyName().equals(PROP_LAST_SOURCE))
            {
                if(evt.getNewValue() instanceof TrelloCard)
                {
                    TrelloCard card = (TrelloCard)evt.getNewValue();
                    if(card.getListID() != null && card.getListID().equals(list.getListID()))
                    {
                        changeSupport.fireChange();                    
                    }                      
                }
            }
        }
    }     
    
// TODO TrelloCardsProvider
    
    private final class TrelloCardProviderImpl extends TrelloCardProvider implements FileChangeListener, CloseSupport, Runnable
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/panel.png";           

        public TrelloCardProviderImpl(TrelloCardFactory factory)
        {
            super(factory);
            RP.post(this);                              
        }         
        
        @Override
        public TrelloAccount getAccount()
        {
            return getTrelloAccount();
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
        public void addSourceListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, listener);
        }

        @Override
        public void removeSourceListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(PROP_LAST_SOURCE, listener);
        }                         
        
        @Override
        public FileObject createData(TrelloCard card, FileTypeProvider fileTypeProvider) throws IOException     
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, card.getCardID());                      
            return primaryFile; 
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
                        LOG.info("Cards root folder created: " + rootDir.getPath());                        
                    }                                                      
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                
            }
            return rootDir;
        }               
        
        @Override
        public synchronized Map<String, TrelloCard> getCardsById()
        {
            if(cards == null)
            {
                cards = new HashMap<>();
                FileObject root = getRootFolder();
                if(root != null)
                {
                    for (FileObject fo : root.getChildren()) 
                    {
                        if(fo.isFolder())
                        {
                            try
                            {
                                Project project = ProjectManager.getDefault().findProject(fo);  
                                if(project != null)
                                {
                                    TrelloCard card = project.getLookup().lookup(TrelloCard.class);
                                    if(card != null)
                                    {
                                        cards.put(card.getCardID(), card);
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
                                TrelloCard card = factory.getCard(Utils.getProperties(fo));
                                if(card != null)
                                {
                                    cards.put(card.getCardID(), card);
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
            return cards;
        } 
        
        @Override
        public void close() 
        {
            if(rootDir != null)
            {
                rootDir.removeFileChangeListener(this);
                
                for(TrelloCard card : getCards())
                {
                    if(card instanceof TrelloCardLink link)
                    {
                        FileObject file = rootDir.getFileObject(card.getSourceID(), PropertiesProvider.EXTENSION);
                        if(file != null)
                        {
                            try
                            {
                                if(link.isModified())
                                {
                                    OutputStream os = file.getOutputStream();
                                    factory.save(card, os, "Updated by Trello project: " + getBoardName());
                                    os.close();
                                }
                                else if(link.isDeleted())
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
        public void createLink(TrelloList list, String url)
        {
            FileObject root = getRootFolder();
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            MarkdownSupport markdown = Lookup.getDefault().lookup(MarkdownSupport.class);
            if(root != null && service != null && markdown != null)
            {
                TrelloCardLink card = service.createLink(list.getListID(), url, factory, getTrelloAccount());
                String videoID = YouTubeUtils.getVideoID(url);
                if(videoID != null)
                {  
                    YouTubeVideoFactory youTubeVideoProvider = Lookup.getDefault().lookup(YouTubeVideoFactory.class);
                    if(youTubeVideoProvider != null)
                    {
                        YouTubeVideo video = youTubeVideoProvider.getVideo(videoID, YouTubeVideoFactory.Type.BASIC);
                        if(video != null)
                        {
                            card.merge(video);
                        }
                    }
                }                                
                
                try
                {
                    createData(card, markdown);                     
                    FileSystem fs = root.getFileSystem();
                    fs.runAtomicAction(() -> {
                        FileObject file = root.createData(card.getCardID(), PropertiesProvider.EXTENSION);
                        OutputStream os = file.getOutputStream();
                        factory.save(card, os, "Saved by Trello project: " + getBoardName());
                        os.close();  
                    });                                                             
                }
                catch(Exception e)
                {
                    LOG.warning(e.getMessage());
                }                
            }            
        }
        
        @Override
        public void createCard(TrelloList list, String name)
        {
            FileObject root = getRootFolder();
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            MarkdownSupport markdown = Lookup.getDefault().lookup(MarkdownSupport.class);
            if(root != null && service != null && markdown != null)
            {
                TrelloCard card = service.createCard(list.getListID(), name, factory, getTrelloAccount());                                               
                try
                {
                    createData(card, markdown); 
                    FileSystem fs = root.getFileSystem();
                    fs.runAtomicAction(() -> {
                        FileObject projectDirectory = FileUtil.createFolder(root, card.getCardID());           
                        FileObject projectFolder = FileUtil.createFolder(projectDirectory, TrelloCardProjectFactory.PROJECT_FOLDER);                   
                        OutputStream os = projectFolder.createAndOpen(TrelloCardProjectFactory.PROJECT_FILE);
                        factory.save(card, os, "OpenPKM Trello Card Project");
                        //props.store(os, "OpenPKM Trello Card Project"); 
                        os.close();  
                    });                                                             
                }
                catch(Exception e)
                {
                    LOG.warning(e.getMessage());
                }                
            }            
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloProject.this;
        } 
        
        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }        
        
        @Override
        public boolean contains(FileObject file) 
        {
            if(file.isData())
            {
                return getCardsById().containsKey(file.getName());                
            }
            return false;
        }         
        
        @Override
        public void fileFolderCreated(FileEvent evt) 
        {
            FileObject folder = evt.getFile();
            if(!getCardsById().containsKey(folder.getName()))
            {
                try
                {
                    Project project = ProjectManager.getDefault().findProject(folder);
                    if(project != null)
                    {
                        TrelloCard card = project.getLookup().lookup(TrelloCard.class);  
                        if(card != null)
                        {
                            getCardsById().put(card.getCardID(), card);
                            propertyChangeSupport.firePropertyChange(PROP_LAST_SOURCE, null, card);    
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
            if(!getCardsById().containsKey(file.getName()))
            {
                try                         
                {
                    Properties props = Utils.getProperties(file); 
                    TrelloCard card = factory.getCard(props);
                    if(card != null)
                    {
                        getCardsById().put(card.getCardID(), card);
                        propertyChangeSupport.firePropertyChange(PROP_LAST_SOURCE, null, card);    
                    }             
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                  
            }             
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            try
            {
                Properties props = Utils.getProperties(evt.getFile()); 
                TrelloCard card = factory.getCard(props);
                if(card != null)
                {
                    getCardsById().remove(card.getCardID());
                    propertyChangeSupport.firePropertyChange(PROP_LAST_SOURCE, card, null);   
                }             
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }                          
        }

        @Override
        public void fileRenamed(FileRenameEvent fre) {
            //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fae) {
            //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }   
        
        public LocalDateTime getLastSync()
        {
            String string = props.getProperty(PROP_TRELLO_SYNC_CARD);
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
                Object oldValue = props.remove(PROP_TRELLO_SYNC_CARD);
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }                
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_CARD, oldValue, time); 
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_TRELLO_SYNC_CARD, time.format(DateTimeFormatter.ISO_DATE_TIME)); 
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_CARD, oldValue, time); 
            }
        }         
        
        @Override
        public void run()
        {
            FileObject root = getRootFolder();
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            MarkdownSupport markdown = Lookup.getDefault().lookup(MarkdownSupport.class);
            if(root != null && service != null && markdown != null)
            {
                ProgressHandle handle = ProgressHandleFactory.createHandle("Syncing Trello cards");
                handle.start();
                handle.switchToIndeterminate();
                                
                List<Card> cards = service.getCards(TrelloProject.this, getTrello());
                Set<String> keys = new HashSet<>(getCardsById().keySet());
                for(Card card : cards)
                {
                    if(keys.remove(card.getId()))
                    {
                        boolean isTime = false;
                        LocalDateTime lastSync = getLastSync();
                        if(lastSync == null)
                        {
                            isTime = true;
                        }
                        else
                        {
                            LocalDateTime lastActivity = DateTimeUtils.convertToLocalDateTime(card.getDateLastActivity());
                            if(lastSync.isBefore(lastActivity))
                            {
                                isTime = true;
                            }                            
                        }
                        
                        if(isTime)
                        {
                            TrelloCard oldCard = getCardsById().get(card.getId()); 
                            TrelloCard newCard = service.getCard(card.getId(), factory, getTrelloAccount());
                            if(!oldCard.getProperties().equals(newCard.getProperties()))
                            {
                                oldCard.merge(newCard);
                                FileObject fo = getRootFolder().getFileObject(oldCard.getCardID());
                                if(fo != null && fo.isFolder())
                                {
                                    try
                                    {
                                        OutputStream os = new FileOutputStream(fo.getFileObject(TrelloCardProjectFactory.PROJECT_FOLDER).getFileObject(TrelloCardProjectFactory.PROJECT_FILE).getPath());
                                        oldCard.getProperties().store(os, "Trello Card project updated");
                                        os.close();                
                                    }
                                    catch(IOException e)
                                    {
                                        LOG.warning(e.getMessage());
                                    }                                                                                                                                  
                                }  
                                else
                                {
                                    fo = getRootFolder().getFileObject(oldCard.getCardID(), PropertiesProvider.EXTENSION);
                                    if(fo != null)
                                    {
                                        try
                                        {
                                            OutputStream os = fo.getOutputStream();
                                            oldCard.getProperties().store(os, "Updated by Trello project: " + getBoardName()); 
                                            os.close();
                                        }  
                                        catch(FileAlreadyLockedException e)
                                        {
                                            LOG.warning(e.getMessage());
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
                    else
                    {
                        try
                        {
                            TrelloCard trelloCard = service.getCard(card.getId(), factory, getTrelloAccount());
                            if(trelloCard instanceof TrelloCardLink link)
                            {
                                String videoID = YouTubeUtils.getVideoID(trelloCard.getCardName());
                                if(videoID != null)
                                {  
                                    YouTubeVideoFactory youTubeVideoProvider = Lookup.getDefault().lookup(YouTubeVideoFactory.class);
                                    if(youTubeVideoProvider != null)
                                    {
                                        YouTubeVideo video = youTubeVideoProvider.getVideo(videoID, YouTubeVideoFactory.Type.BASIC);
                                        if(video != null)
                                        {
                                            link.merge(video);
                                        }
                                    }
                                }                                
                                
                                FileObject file = root.createData(card.getId(), PropertiesProvider.EXTENSION);
                                OutputStream os = file.getOutputStream();
                                factory.save(trelloCard, os, "Saved by Trello project: " + getBoardName());
                                os.close();                                  
                            }
                            else
                            {
                                FileObject projectDirectory = FileUtil.createFolder(root, card.getId());           
                                FileObject projectFolder = FileUtil.createFolder(projectDirectory, TrelloCardProjectFactory.PROJECT_FOLDER);                   

                                OutputStream os = projectFolder.createAndOpen(TrelloCardProjectFactory.PROJECT_FILE);
                                factory.save(trelloCard, os, "OpenPKM Trello Card Project");
                                //props.store(os, "OpenPKM Trello Card Project"); 
                                os.close();  

                                Project project = ProjectManager.getDefault().findProject(projectDirectory);
                                if(project != null)
                                {
                                    trelloCard = project.getLookup().lookup(TrelloCard.class);                                      
                                }
                            }                                
                            if(card != null)
                            {
                                getCardsById().put(trelloCard.getCardID(), trelloCard);  
                                createData(trelloCard, markdown);                                                                     
                            }                                                                                                
                        }
                        catch(Exception e)
                        {
                            LOG.warning(e.getMessage());
                        }                        
                    }
                }
                if(!keys.isEmpty())
                {
                    for(String key : keys)
                    {
                        FileObject fo = getRootFolder().getFileObject(key, PropertiesProvider.EXTENSION);
                        if(fo == null)
                        { 
                            fo = getRootFolder().getFileObject(key);
                        }        
                        if(fo != null)
                        {
                            try
                            {
                                fo.delete();    
                                getCardsById().remove(key);
                            }
                            catch(IOException e)
                            {
                                LOG.warning(e.getMessage());
                            }                            
                        }
                    }
                }                 
                setLastSync(LocalDateTime.now()); 
                LOG.info("Syncing Trello cards succeeded");
                handle.finish();
                rootDir.addFileChangeListener(this);  
            }                       
        }         
    }      
    
// TODO SourceGroup

    private final class TrelloActionProviderImpl extends TrelloActionProvider implements SourceGroupProvider, IconProvider, FileChangeListener, Runnable
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/action_log.png"; 
        
        private static final String PROP_TRELLO_SYNC_ACTION = "trello.sync.action";         
        
        public TrelloActionProviderImpl(TrelloActionFactory actionFactory, TrelloCommentFactory commentFactory) 
        {
            super(actionFactory, commentFactory); 
            RP.post(this);                    
        }              
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_ACTIVITY;
        }          
        
        @Override
        public Image getIcon(int type)
        {
            return ImageUtilities.loadImage(ICON);
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloProject.this;
        }  
        
        @Override
        public DisplayNameProvider getDisplayNameProvider()
        {
            return new GroupProvider.DisplayNameProviderImpl(this);
        }
        
        @Override
        public IconProvider getIconProvider()
        {
            return this;
        }        
        
        @Override
        public ActionsProvider getActionsProvider() 
        {
            return ACTIONS_PROVIDER_EMPTY;
        }        

        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }               
        
        @Override
        protected synchronized Map<String, TrelloAction> getActionsById()
        {
            if(activity == null)
            {
                activity = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            TrelloAction action = actionFactory.getAction(Utils.getProperties(file)); 
                            activity.put(action.getActionID(), action);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return activity;
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
        public void addSourceListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, listener);
        }

        @Override
        public void removeSourceListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(PROP_LAST_SOURCE, listener);
        }          
        
        @Override
        public Source getSource(String sourceID)
        {
            TrelloAction action = getActionsById().get(sourceID);
            return commentFactory.getComment(action, getTrello(), getTrelloAccount());   
        }  
        
        @Override
        public void deleteSource(String sourceID) throws IOException
        {
            FileObject root = getRootFolder();
            if(root != null)
            {
                FileObject file = root.getFileObject(sourceID, PropertiesProvider.EXTENSION);
                if(file != null)
                {                      
                    TrelloAction action = getActionsById().get(sourceID);
                    if(action instanceof TrelloComment comment)
                    {
                        TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
                        int status = service.deleteComment(comment.getCardID(), comment.getActionID(), getTrelloAccount());            
                        LOG.info("Delete Comment status: " + status);                    

                        file.delete();                           
                    }                 
                }              
            }  
        }        
        
        @Override
        public FileObject createData(TrelloComment comment, FileTypeProvider fileTypeProvider) throws IOException     
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());                                    
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            
            OutputStream output = primaryFile.getOutputStream();
            output.write(comment.getText().getBytes());
            output.close();            
            
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, comment.getActionID());                      
            return primaryFile; 
        }   
        
        @Override
        public SortedSet<? extends NodeProvider> getNodes()
        {
            List<NodeDateTimeProvider> list = getActionsById().values().stream()
                    .filter(NodeDateTimeProvider.class::isInstance)
                    .map(NodeDateTimeProvider.class::cast)
                    .toList();        
            
            SortedSet<NodeDateTimeProvider> sorted = new TreeSet<NodeDateTimeProvider>(NodeDateTimeProvider.dateTimeComparator());
            sorted.addAll(list);            
            
            return sorted.reversed();
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
                TrelloAction action = actionFactory.getAction(Utils.getProperties(file)); 
                getActionsById().put(action.getActionID(), action);               
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
            /*
            FileObject file = evt.getFile();
            TrelloLabel label = getLabels().get(file.getName());  
            if(label != null)
            {
                
            }
            */
            //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            TrelloAction action = getActionsById().remove(file.getName());  
            if(action != null)
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
        
        public LocalDateTime getLastSync()
        {
            String string = props.getProperty(PROP_TRELLO_SYNC_ACTION);
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
                Object oldValue = props.remove(PROP_TRELLO_SYNC_ACTION);
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }                
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_ACTION, oldValue, time); 
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_TRELLO_SYNC_ACTION, time.format(DateTimeFormatter.ISO_DATE_TIME)); 
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_ACTION, oldValue, time); 
            }
        }
        
        @Override
        public void run()
        {
            FileObject root = getRootFolder();                  
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            MarkdownSupport markdown = Lookup.getDefault().lookup(MarkdownSupport.class);            
            if(root != null && service != null && markdown != null)
            {
                ProgressHandle handle = ProgressHandleFactory.createHandle("Syncing Trello actions");
                handle.start();
                handle.switchToIndeterminate();
                   
                try
                {
                    List<TrelloAction> actions = service.getActions(TrelloProject.this, getLastSync(), actionFactory, getTrello());
                    for(TrelloAction action : actions)
                    {
                        if(!getActionsById().containsKey(action.getActionID()))
                        {
                            TrelloComment comment = commentFactory.getComment(action, getTrello(), getTrelloAccount());                       
                            try
                            {
                                if(comment == null)
                                {
                                    FileObject file = root.createData(action.getActionID(), PropertiesProvider.EXTENSION);
                                    OutputStream os = file.getOutputStream();
                                    action.getProperties().store(os, "Saved by Trello project: " + getBoardName());
                                    os.close();   
                                }   
                                else
                                {
                                    createData(comment, markdown);  
                                    FileObject file = root.createData(comment.getActionID(), PropertiesProvider.EXTENSION);
                                    OutputStream os = file.getOutputStream();
                                    getFactory().save(comment, os, "Saved by Trello project: " + getBoardName());
                                    os.close();                                 
                                } 
                                getActionsById().put(action.getActionID(), action);  
                            }
                            catch(IOException e)
                            {
                                LOG.warning(e.getMessage());
                            }                          
                        }
                    }                     
                }
                catch(Exception e)
                {
                    LOG.warning(e.getMessage());
                }
                finally
                {
                    setLastSync(LocalDateTime.now()); 
                    LOG.info("Syncing Trello actions succeeded");
                    handle.finish();
                    rootDir.addFileChangeListener(this);                      
                }               
            }                       
        }           
    }      
    
    private final class TrelloLabelProviderImpl extends AbstractTrelloLabelProvider implements SourceGroupProvider, NodeActionsProvider<TrelloLabel>, FileChangeListener, Runnable
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/palette.png";         
        
        private static final String PROP_TRELLO_SYNC_LABEL = "trello.sync.label"; 
                
        public TrelloLabelProviderImpl(TrelloLabelFactory factory) 
        {
            super(factory);  
            RP.post(this);             
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloProject.this;
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
            return new TrelloLabelActionsProvider(this);
        }          
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_LABELS;
        }  

        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }                        
        
        @Override
        public List<Action> getActions(TrelloLabel label)
        {
            List<Action> actions = new ArrayList<>(1);
            actions.add(new DeleteLabel(getTrello(), this, label));
            return actions;
        }        
        
        @Override
        public SortedSet<NodeProvider> getNodes()
        {            
            SortedSet<NodeProvider> sorted = new TreeSet<NodeProvider>(NodeProvider.displayNameComparator());
            sorted.addAll(getLabels());            
            return sorted;
        }
        
        @Override
        protected synchronized Map<String, TrelloLabel> getLabelsById()
        {
            if(labels == null)
            {
                labels = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            TrelloLabel label = factory.getLabel(Utils.getProperties(file)); 
                            labels.put(label.getLabelID(), label);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return labels;
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
                TrelloLabel label = factory.getLabel(Utils.getProperties(file)); 
                getLabelsById().put(label.getLabelID(), label);               
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
            /*
            FileObject file = evt.getFile();
            TrelloLabel label = getLabels().get(file.getName());  
            if(label != null)
            {
                
            }
            */
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            TrelloLabel label = getLabelsById().remove(file.getName());  
            if(label != null)
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

        public LocalDateTime getLastSync()
        {
            String string = props.getProperty(PROP_TRELLO_SYNC_LABEL);
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
                Object oldValue = props.remove(PROP_TRELLO_SYNC_LABEL);
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }                
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_LABEL, oldValue, time); 
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_TRELLO_SYNC_LABEL, time.format(DateTimeFormatter.ISO_DATE_TIME)); 
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_LABEL, oldValue, time); 
            }
        } 
        
        @Override
        public void run()
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            if(service != null)
            {
                List<TrelloLabel> labels = service.getLabels(TrelloProject.this, factory, getTrello());
                for(TrelloLabel label : labels)
                {
                    if(!getLabelsById().containsKey(label.getLabelID()))
                    {
                        try
                        {
                            OutputStream os = getRootFolder().createAndOpen(label.getLabelID() + "." + PropertiesProvider.EXTENSION);                            
                            label.getProperties().store(os, "Created by Trello project: " + getBoardName()); 
                            os.close();
                            LOG.info("Trello label saved: " + label.getLabelID());                              
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }  
                    }
                }
                setLastSync(LocalDateTime.now());
            }
        }         
    }  
    
    private final class TrelloMemberProviderImpl extends TrelloMemberProvider implements SourceGroupProvider, FileChangeListener, Runnable
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/group.png"; 
        
        private static final String PROP_TRELLO_SYNC_MEMBER = "trello.sync.member";         
                
        public TrelloMemberProviderImpl(TrelloMemberFactory factory) 
        {
            super(factory);   
            if(getLastSync() == null)
            {
                RP.post(this);                
            }            
        }         
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloProject.this;
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
            return new TrelloMemberActionsProvider(this);
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_MEMBERS;
        }  

        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }                        
        
        @Override
        public SortedSet<NodeProvider> getNodes()
        {
            List<NodeProvider> list = getMembers().values().stream()
                    .filter(NodeProvider.class::isInstance)
                    .map(NodeProvider.class::cast)
                    .toList();        
            
            SortedSet<NodeProvider> sorted = new TreeSet<NodeProvider>(NodeProvider.displayNameComparator());
            sorted.addAll(list);
            
            return sorted;
        }
        
        @Override
        protected synchronized Map<String, TrelloMember> getMembers()
        {
            if(members == null)
            {
                members = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            TrelloMember member = factory.getMember(Utils.getProperties(file)); 
                            members.put(member.getMemberID(), member);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return members;
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
                TrelloMember member = factory.getMember(Utils.getProperties(file)); 
                getMembers().put(member.getMemberID(), member);               
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
            /*
            FileObject file = evt.getFile();
            TrelloLabel label = getLabels().get(file.getName());  
            if(label != null)
            {
                
            }
            */
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            TrelloMember member = getMembers().remove(file.getName());  
            if(member != null)
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

        public LocalDateTime getLastSync()
        {
            String string = props.getProperty(PROP_TRELLO_SYNC_MEMBER);
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
                Object oldValue = props.remove(PROP_TRELLO_SYNC_MEMBER);
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }                
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_MEMBER, oldValue, time); 
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_TRELLO_SYNC_MEMBER, time.format(DateTimeFormatter.ISO_DATE_TIME)); 
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_MEMBER, oldValue, time); 
            }
        } 
        
        @Override
        public void run()
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            if(service != null)
            {
                List<TrelloMember> members = service.getMembers(TrelloProject.this, factory, getTrello());
                for(TrelloMember member : members)
                {
                    if(!getMembers().containsKey(member.getMemberID()))
                    {
                        try
                        {
                            OutputStream os = getRootFolder().createAndOpen(member.getMemberID() + "." + PropertiesProvider.EXTENSION);                            
                            member.getProperties().store(os, "Created by Trello project: " + getBoardName()); 
                            os.close();
                            LOG.info("Trello member saved: " + member.getMemberID());                              
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        } 
                    }
                }
                setLastSync(LocalDateTime.now());
            }
        }        
    }      

    private final class TrelloListProviderImpl extends TrelloListProvider implements SourceGroupProvider, FileChangeListener, Runnable
    {  
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/application_view_columns.png"; 
        
        private static final String PROP_TRELLO_SYNC_LIST = "trello.sync.list";        
        
        public TrelloListProviderImpl(TrelloListFactory factory) 
        {
            super(factory);  
            RP.post(this); 
        }               
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloProject.this;
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
            return new TrelloListActionsProvider(this);
        }          
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_LISTS;
        }  

        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }               
        
        @Override
        public SortedSet<? extends NodeProvider> getNodes()
        {                        
            SortedSet<NodePositionProvider> sorted = new TreeSet<NodePositionProvider>(NodePositionProvider.positionComparator());
            for(TrelloList list : getListsById().values())
            {
                DataGroupProvider provider = new ListProviderImpl(list);
                TrelloListNode node = new TrelloListNode(provider);
                sorted.add(node);
            }            
            return sorted;
        }
        
        @Override
        protected synchronized Map<String, TrelloList> getListsById()
        {
            if(lists == null)
            {
                lists = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            TrelloList list = factory.getList(Utils.getProperties(file)); 
                            lists.put(list.getListID(), list);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return lists;
        } 
        
        @Override
        public void createList(String name)
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            TrelloList list = service.createList(getBoardID(), name, factory, getTrelloAccount());   
            try
            {
                OutputStream os = getRootFolder().createAndOpen(list.getListID() + "." + PropertiesProvider.EXTENSION);
                list.getProperties().store(os, "Created by Trello project: " + getBoardName()); 
                os.close();
                LOG.info("Trello list saved: " + list.getListID());  
            }  
            catch(FileAlreadyLockedException e)
            {
                LOG.warning(e.getMessage());
            }                             
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }            
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
                TrelloList list = factory.getList(Utils.getProperties(file)); 
                getListsById().put(list.getListID(), list);               
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
            FileObject file = evt.getFile();
            try
            {
                TrelloList list = getListsById().get(file.getName()); 
                Properties props = Utils.getProperties(file);
                list.getProperties().putAll(props);
                changeSupport.fireChange();  
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            } 
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            TrelloList list = getListsById().remove(file.getName());  
            if(list != null)
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
        
        public LocalDateTime getLastSync()
        {
            String string = props.getProperty(PROP_TRELLO_SYNC_LIST);
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
                Object oldValue = props.remove(PROP_TRELLO_SYNC_LIST);
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }                
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_LIST, oldValue, time); 
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_TRELLO_SYNC_LIST, time.format(DateTimeFormatter.ISO_DATE_TIME)); 
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_LIST, oldValue, time); 
            }
        }         
        
        @Override
        public void run()
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            if(service != null)
            {
                List<TrelloList> lists = service.getLists(TrelloProject.this, factory, getTrello());
                Set<String> keys = new HashSet<>(getListsById().keySet());
                for(TrelloList list : lists)
                {
                    if(keys.remove(list.getListID()))
                    {
                        TrelloList old = getListsById().get(list.getListID());
                        if(!old.getProperties().equals(list.getProperties()))
                        {
                            FileObject file = getRootFolder().getFileObject(list.getListID(), PropertiesProvider.EXTENSION);
                            if(file != null)
                            {
                                try
                                {
                                    OutputStream os = file.getOutputStream();
                                    list.getProperties().store(os, "Updated by Trello project: " + getBoardName()); 
                                    os.close();
                                    LOG.info("Trello list saved: " + list.getListID());  
                                }  
                                catch(FileAlreadyLockedException e)
                                {
                                    LOG.warning(e.getMessage());
                                }                             
                                catch(IOException e)
                                {
                                    LOG.warning(e.getMessage());
                                }                                                             
                            }                              
                        }                         
                    } 
                    else
                    {
                        try
                        {
                            OutputStream os = getRootFolder().createAndOpen(list.getListID() + "." + PropertiesProvider.EXTENSION);                            
                            list.getProperties().store(os, "Created by Trello project: " + getBoardName()); 
                            os.close();
                            LOG.info("Trello list saved: " + list.getListID());                              
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
                        FileObject file = getRootFolder().getFileObject(key, PropertiesProvider.EXTENSION);
                        if(file != null)
                        {  
                            try
                            {
                                file.delete();                                
                            }
                            catch(IOException e)
                            {
                                LOG.warning(e.getMessage());
                            }
                        }                      
                    }
                }                
                setLastSync(LocalDateTime.now());
            }
        }
    }        
    
    private static final class DeleteLabel extends AbstractAction
    {  
        private final Trello trello;
        private final AbstractTrelloLabelProvider provider; 
        private final TrelloLabel label;

        public DeleteLabel(Trello trello, AbstractTrelloLabelProvider provider, TrelloLabel label) 
        {
            super("Delete");
            this.trello = trello;
            this.provider = provider;
            this.label = label;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {  
            NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to delete: " + label.getLabelName(), "Delete Label", NotifyDescriptor.YES_NO_OPTION);
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.YES_OPTION) 
            {
                trello.deleteLabel(label.getLabelID());  
                FileObject fo = provider.getRootFolder().getFileObject(label.getLabelID(), PropertiesProvider.EXTENSION);       
                if(fo != null)
                {
                    try
                    {
                        fo.delete();    
                    }
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }                            
                } 
            }                                   
        }
    }      
    
    private static Comparator<DataObject> positionComparator() 
    {
        return new Comparator<DataObject>() 
        {
            @Override
            public int compare(DataObject data1, DataObject data2) 
            {
                TrelloCard card1 = data1.getLookup().lookup(TrelloCard.class);
                TrelloCard card2 = data2.getLookup().lookup(TrelloCard.class);
                if(card1 != null && card1 != null)
                {
                    return card1.getCardPosition().compareTo(card2.getCardPosition());                    
                }
                return -1;
            }
        };
    }     
}
