/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.DataProvider;
import openpkm.base.DataProviders;
import openpkm.base.DataSource;
import openpkm.base.DescriptionProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.IconProvider;
import openpkm.base.NodeGroup;
import openpkm.base.NodePositionProvider;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TitleProvider;
import openpkm.base.UpdateCookie;
import openpkm.core.TopComponentProvider;
import openpkm.jcef.CefClientProvider;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAccountsProvider;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloActionProvider;
import openpkm.trello.TrelloActionsProvider;
import openpkm.trello.TrelloBoard;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardProvider;
import openpkm.trello.TrelloCardsProvider;
import openpkm.trello.TrelloLabel;
import openpkm.trello.TrelloLabelProvider;
import openpkm.trello.TrelloLabelsProvider;
import openpkm.trello.TrelloList;
import openpkm.trello.TrelloListProvider;
import openpkm.trello.TrelloListsProvider;
import openpkm.trello.TrelloMember;
import openpkm.trello.TrelloMemberProvider;
import openpkm.trello.TrelloMembersProvider;
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
import org.openide.WizardDescriptor;
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
import openpkm.trello.TrelloService;

/**
 *
 * @author Rok Koren
 */
public class TrelloProject implements Notebook, TrelloBoard, TitleProvider, DescriptionProvider, PropertiesProvider, Sources, DataProviders, BatchUpdateSupport
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
    
    private static final int POSITION_LISTS   = 100;
    private static final int POSITION_ACTIONS = 200;    
    private static final int POSITION_LABELS  = 300;
    private static final int POSITION_MEMBERS = 400;

    private static final Logger LOG = Logger.getLogger(TrelloProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(TrelloProject.class);   
    
    private final Map<String, SourceGroup> sources = new HashMap();  
    private final Map<String, DataProvider> dataProviders = new HashMap();
    private final List<UpdateCookie> cookies = new ArrayList();      
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);   
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    
    private final FileObject projectDir;        
    private final ProjectState state;
    private final Properties props;   
    
    private Lookup lkp; 
    private FileObject dataDir;
    private LocalFileSystem fileSystem;
    private DataSource dataSource;   
    
    private TrelloAccount trelloAccount;
    private Trello trello;    
    private TrelloBoard trelloBoard;
    
    public TrelloProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;
          
        TrelloCardProvider cardProvider = Lookup.getDefault().lookup(TrelloCardProvider.class);
        if(cardProvider != null)
        {
            DataProvider cards = new TrelloCardsProviderImpl(cardProvider);
            dataProviders.put(cards.getName(), cards);    
        }
        
        TrelloActionProvider actionProvider = Lookup.getDefault().lookup(TrelloActionProvider.class);
        if(actionProvider != null)
        {
            SourceGroup actions = new TrelloActionsProviderImpl(actionProvider);
            sources.put(actions.getName(), actions);              
        }
        
        TrelloListProvider listProvider = Lookup.getDefault().lookup(TrelloListProvider.class);
        if(listProvider != null)
        {          
            SourceGroup lists = new TrelloListsProviderImpl(listProvider);
            sources.put(lists.getName(), lists);            
        }  
        
        TrelloLabelProvider labelProvider = Lookup.getDefault().lookup(TrelloLabelProvider.class);
        if(labelProvider != null)
        {          
            SourceGroup labels = new TrelloLabelsProviderImpl(labelProvider);
            sources.put(labels.getName(), labels);            
        }  
        
        TrelloMemberProvider memberProvider = Lookup.getDefault().lookup(TrelloMemberProvider.class);
        if(memberProvider != null)
        {          
            SourceGroup members = new TrelloMembersProviderImpl(memberProvider);
            sources.put(members.getName(), members);            
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
    
    public Trello getTrello()
    {
        if(trello == null)
        {
            TrelloAccount account = getTrelloAccount();
            if(account != null)
            {
                trello = new TrelloImpl(account.getApiKey(), account.getAccessToken(), new JDKTrelloHttpClient());                
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
    
// TODO DataProviders

    @Override
    public DataProvider getDataProvider(String name)
    {
        return dataProviders.get(name);
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
    
    @Override
    public DataSource getDataSource() 
    {
        return dataSource;
    }

    @Override
    public void setDataSource(DataSource newValue) 
    {
        DataSource oldValue = dataSource;
        dataSource = newValue;
        propertyChangeSupport.firePropertyChange(PROP_DATA_SOURCE, oldValue, newValue);
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
            list.add(new TopComponentProviderImpl());
            list.add(new ProjectOpenedHookImpl());   
            list.add(new RootProjectProviderImpl());
            list.add(new ParentProjectProviderImpl());

            list.add(new TrelloLogicalView(this));
            list.add(new TrelloCustomizerProvider(this));                 
            
            list.add(new HtmlFilesProviderImpl());   

            list.addAll(sources.values()); 
            list.addAll(dataProviders.values());    
            
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
    
// TODO TitleProvider  
    
    @Override
    public String getTitle() 
    {
        return getBoardName();
    }

    @Override
    public void setTitle(String title) 
    {
        throw new UnsupportedOperationException();
    } 
    
// TODO DescriptionProvider  
    
    @Override
    public String getDescription() 
    {
        return getBoardDescription();
    }

    @Override
    public void setDescription(String desc) 
    {
        throw new UnsupportedOperationException();
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
            return provider.getIcon();
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
            return TrelloProject.this;
        }
    }     
  
// TODO IconProvider    
    
    private final class IconProviderImpl implements IconProvider
    {                
        private final ChangeSupport changeSupport = new ChangeSupport(this); 

        @Override
        public synchronized Image getIcon()
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
    
// TODO TrelloCardsProvider
    
    private final class TrelloCardsProviderImpl implements TrelloCardsProvider, FileChangeListener
    {                        
        private static final String ROOT_FOLDER = "cards";          
        
        private Map<String, TrelloCard> cards; 
        private FileObject rootDir;            
        
        private final TrelloCardProvider provider;
        private final ChangeSupport changeSupport;              

        public TrelloCardsProviderImpl(TrelloCardProvider provider)
        {
            this.provider = provider;
            changeSupport = new ChangeSupport(this); 
        } 
        
        @Override
        public String getName()
        {
            return ROOT_FOLDER;
        }
        
        @Override
        public TrelloCardProvider getCardProvider()
        {
            return provider;
        }
        
        @Override
        public DataSource getSource(String sourceID)
        {
            return getCardsById().get(sourceID);
        }
        
        @Override
        public FileObject createData(Properties props, FileTypeProvider fileTypeProvider)     
        {
            TrelloCard card = provider.createCard(props, this);
            if(card != null)
            {
                try
                {
                    String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
                    FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
                    FileObject file = getFileWithAttrs(primaryFile, true);
                    file.setAttribute(ATTR_DATA_PROVIDER, getName());
                    file.setAttribute(ATTR_DATA_SOURCE_ID, card.getSourceID());                                                          
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }
            } 
            return null;
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
                    LOG.info("Cards dir created: " + rootDir.getPath());                        
                }  
                rootDir.addFileChangeListener(this);            
            }                           
            return rootDir;       
        }         
        
        private synchronized Map<String, TrelloCard> getCardsById()
        {
            if(cards == null)
            {
                cards = new HashMap<>();
                try
                {
                    for (FileObject fo : getRootDirectory().getChildren()) 
                    {
                        if(fo.isFolder())
                        {
                            Project project = ProjectManager.getDefault().findProject(fo);
                            if(project instanceof TrelloCard card)
                            {
                                cards.put(card.getCardID(), card);
                            }                                                                                    
                        }
                        else
                        {
                            DataObject data = DataObject.find(fo);
                            TrelloCard card = data.getLookup().lookup(TrelloCard.class);
                            if(card != null)
                            {
                                cards.put(card.getCardID(), card);
                            }                            
                        }                                                                                                                                            
                    }                      
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                              
            }
            return cards;
        }  

        @Override
        public Collection<TrelloCard> getCards()
        {
            return Collections.unmodifiableCollection(getCardsById().values());
        }
        
        @Override
        public void addCard(TrelloCard card)
        {
            getCardsById().put(card.getCardID(), card);
            changeSupport.fireChange();            
        }
        
        @Override
        public void removeCard(String cardID)
        {
            TrelloCard card = getCardsById().remove(cardID);
            if(card != null)
            {
                changeSupport.fireChange();                            
            }
        }        
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            //actions.add(new AddCard(this));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloProject.this;
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
        public void fileFolderCreated(FileEvent evt) 
        {
            TrelloCard card = provider.getCard(evt.getFile());
            if(card != null)
            {
                getCardsById().put(card.getCardID(), card);
                setDataSource(card);    
            }
        }

        @Override
        public void fileDataCreated(FileEvent evt) 
        {
            TrelloCard card = provider.getCard(evt.getFile());
            if(card != null)
            {
                getCardsById().put(card.getCardID(), card);
                setDataSource(card);    
            }             
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            TrelloCard card = provider.getCard(evt.getFile());
            if(card != null)
            {
                getCardsById().remove(card.getCardID());
                setDataSource(card);    
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
    
// TODO SourceGroup

    private final class TrelloActionsProviderImpl extends TrelloActionsProvider implements NodeGroup, FileChangeListener
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/action_log.png";          
        
        public TrelloActionsProviderImpl(TrelloActionProvider provider) 
        {
            super(provider);            
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_ACTIONS;
        }  

        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }        
        
        @Override
        public Image getIcon(boolean isEmpty, boolean isOpen)
        {
            return ImageUtilities.loadImage(ICON);
        }
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            //actions.add(new AddComment(this));         
            return actions;
        } 
        
        @Override
        public SortedSet<NodeProvider> getNodes()
        {
            List<NodeProvider> list = getActivity().values().stream()
                    .filter(NodeProvider.class::isInstance)
                    .map(NodeProvider.class::cast)
                    .toList();        
            
            SortedSet<NodeProvider> sorted = new TreeSet<NodeProvider>(NodeProvider.displayNameComparator());
            sorted.addAll(list);
            
            return sorted;
        }
        
        @Override
        protected synchronized Map<String, TrelloAction> getActivity()
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
                            TrelloAction action = provider.getAction(Utils.getProperties(file)); 
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
                TrelloAction action = provider.getAction(Utils.getProperties(file)); 
                getActivity().put(action.getActionID(), action);               
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
            TrelloAction action = getActivity().remove(file.getName());  
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
    }      
    
    private final class TrelloLabelsProviderImpl extends TrelloLabelsProvider implements NodeGroup, FileChangeListener, Runnable
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/palette.png"; 

        private static final String PROP_TRELLO_SYNC_LABEL = "trello.sync.label"; 
                
        public TrelloLabelsProviderImpl(TrelloLabelProvider provider) 
        {
            super(provider);  
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
        public Image getIcon(boolean isEmpty, boolean isOpen)
        {
            return ImageUtilities.loadImage(ICON);
        }
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.add(new AddLabel(this));         
            return actions;
        } 
        
        @Override
        public SortedSet<NodeProvider> getNodes()
        {
            List<NodeProvider> list = getLabels().values().stream()
                    .filter(NodeProvider.class::isInstance)
                    .map(NodeProvider.class::cast)
                    .toList();        
            
            SortedSet<NodeProvider> sorted = new TreeSet<NodeProvider>(NodeProvider.displayNameComparator());
            sorted.addAll(list);
            
            return sorted;
        }
        
        @Override
        protected synchronized Map<String, TrelloLabel> getLabels()
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
                            TrelloLabel label = provider.getLabel(Utils.getProperties(file)); 
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
                TrelloLabel label = provider.getLabel(Utils.getProperties(file)); 
                getLabels().put(label.getLabelID(), label);               
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
            TrelloLabel label = getLabels().remove(file.getName());  
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
                List<TrelloLabel> labels = service.getLabels(TrelloProject.this, provider, getTrello());
                for(TrelloLabel label : labels)
                {
                    if(!getLabels().containsKey(label.getLabelID()))
                    {
                        if(label instanceof PropertiesProvider properties)
                        {
                            try
                            {
                                OutputStream os = getRootFolder().createAndOpen(label.getLabelID() + "." + PropertiesProvider.EXTENSION);                            
                                properties.getProperties().store(os, "Created by Trello project: " + getTitle()); 
                                os.close();
                                LOG.info("Trello label saved: " + label.getLabelID());                              
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
    
    private final class TrelloMembersProviderImpl extends TrelloMembersProvider implements NodeGroup, FileChangeListener, Runnable
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/group.png"; 
        
        private static final String PROP_TRELLO_SYNC_MEMBER = "trello.sync.member";         
                
        public TrelloMembersProviderImpl(TrelloMemberProvider provider) 
        {
            super(provider);   
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
        public Image getIcon(boolean isEmpty, boolean isOpen)
        {
            return ImageUtilities.loadImage(ICON);
        }
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.add(new AddMember(this));         
            return actions;
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
                            TrelloMember member = provider.getMember(Utils.getProperties(file)); 
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
                TrelloMember member = provider.getMember(Utils.getProperties(file)); 
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
                List<TrelloMember> members = service.getMembers(TrelloProject.this, provider, getTrello());
                for(TrelloMember member : members)
                {
                    if(!getMembers().containsKey(member.getMemberID()))
                    {
                        if(member instanceof PropertiesProvider properties)
                        {
                            try
                            {
                                OutputStream os = getRootFolder().createAndOpen(member.getMemberID() + "." + PropertiesProvider.EXTENSION);                            
                                properties.getProperties().store(os, "Created by Trello project: " + getTitle()); 
                                os.close();
                                LOG.info("Trello member saved: " + member.getMemberID());                              
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

    private final class TrelloListsProviderImpl extends TrelloListsProvider implements NodeGroup, FileChangeListener, Runnable
    {  
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/application_view_columns.png"; 
        
        private static final String PROP_TRELLO_SYNC_LIST = "trello.sync.list";        
        
        public TrelloListsProviderImpl(TrelloListProvider provider) 
        {
            super(provider);  
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
        public Image getIcon(boolean isEmpty, boolean isOpen)
        {
            return ImageUtilities.loadImage(ICON);
        }
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.add(new AddList(this));         
            return actions;
        } 
        
        @Override
        public SortedSet<? extends NodeProvider> getNodes()
        {
            List<NodePositionProvider> list = getLists().values().stream()
                    .filter(NodePositionProvider.class::isInstance)
                    .map(NodePositionProvider.class::cast)
                    .toList();        
            
            SortedSet<NodePositionProvider> sorted = new TreeSet<NodePositionProvider>(NodePositionProvider.positionComparator());
            sorted.addAll(list);
            
            return sorted;
        }
        
        @Override
        protected synchronized Map<String, TrelloList> getLists()
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
                            TrelloList list = provider.getList(Utils.getProperties(file)); 
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
                TrelloList list = provider.getList(Utils.getProperties(file)); 
                getLists().put(list.getListID(), list);               
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
            TrelloList list = getLists().remove(file.getName());  
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
                List<TrelloList> lists = service.getLists(TrelloProject.this, provider, getTrello());
                for(TrelloList list : lists)
                {
                    if(!getLists().containsKey(list.getListID()))
                    {
                        if(list instanceof PropertiesProvider properties)
                        {
                            try
                            {
                                OutputStream os = getRootFolder().createAndOpen(list.getListID() + "." + PropertiesProvider.EXTENSION);                            
                                properties.getProperties().store(os, "Created by Trello project: " + getTitle()); 
                                os.close();
                                LOG.info("Trello list saved: " + list.getListID());                              
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
            return TrelloProject.this;
        }                 
    } 

    private static final class AddList extends AbstractAction
    {                         
        protected final TrelloListsProvider sourceGroup;            

        public AddList(TrelloListsProvider sourceGroup) 
        {
            super("Add List");
            this.sourceGroup = sourceGroup;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            //panels.add(new ThoughtWizardPanel1());
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) 
            {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
            WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()  
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle("Add List"); 
            /*
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            wiz.putProperty("project", provider.getProject());
            */
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {                      
            }
        }
    }     
    
    private static final class AddLabel extends AbstractAction
    {                          
        private final TrelloLabelsProvider sourceGroup;            

        public AddLabel(TrelloLabelsProvider sourceGroup) 
        {
            super("Add Label");
            this.sourceGroup = sourceGroup;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            //panels.add(new MemberWizardPanel1());
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) 
            {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
            WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()  
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle("Add Label");  
            /*
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));  
            wiz.putProperty("project", provider.getProject());
            */
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {  
                //sourceGroup.getRootFolder();
            }
        }
    } 

    private static final class AddMember extends AbstractAction
    {                          
        protected final TrelloMembersProvider sourceGroup;            

        public AddMember(TrelloMembersProvider sourceGroup) 
        {
            super("Add Member");
            this.sourceGroup = sourceGroup;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            //panels.add(new MemberWizardPanel1());
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) 
            {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
            WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()  
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle("Add Member");  
            /*
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            wiz.putProperty("project", provider.getProject());
            */
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {  

            }
        }
    }  
}
