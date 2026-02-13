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
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.IconProvider;
import openpkm.base.NodeGroup;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.RemoteDataProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviders;
import openpkm.base.TitleProvider;
import openpkm.base.UpdateCookie;
import openpkm.core.TopComponentProvider;
import openpkm.jcef.CefClientProvider;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAccountsProvider;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloActionProvider;
import openpkm.trello.TrelloActionsProvider;
import openpkm.trello.TrelloAttachment;
import openpkm.trello.TrelloAttachmentProvider;
import openpkm.trello.TrelloAttachmentsProvider;
import openpkm.trello.TrelloBoard;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardProvider;
import openpkm.trello.TrelloCardsProvider;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListProvider;
import openpkm.trello.TrelloCheckListsProvider;
import openpkm.trello.TrelloService;
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
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 *
 * @author Rok Koren
 */
public class TrelloCardProject implements Project, TrelloCard, TitleProvider, PropertiesProvider, Sources, SourceProviders, BatchUpdateSupport
{ 
    public static final String PROP_TRELLO_USERNAME = "trello.username";
    public static final String PROP_TRELLO_BOARD_ID = "trello.board.id";
    public static final String PROP_TRELLO_ACTIVITY = "trello.activity";         
    
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/date_task.png";
    
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_CHECK_LISTS      = 100;
    private static final int POSITION_CHECK_LIST_ITEMS = 200;    
    private static final int POSITION_ATTACHMENTS      = 300;
    private static final int POSITION_ACTIONS          = 400;

    private static final Logger LOG = Logger.getLogger(TrelloCardProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(TrelloCardProject.class);   
    
    private final Map<String, SourceGroup> sources = new HashMap(); 
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
    private boolean isDeleted;      
    
    private TrelloAccount trelloAccount;
    private Trello trello;    
    private TrelloBoard trelloBoard;
    
    public TrelloCardProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;

        TrelloActionProvider actionProvider = Lookup.getDefault().lookup(TrelloActionProvider.class);
        if(actionProvider != null)
        {          
            SourceGroup actions = new TrelloActionsProviderImpl(actionProvider);
            sources.put(actions.getName(), actions);            
        }         
        
        TrelloAttachmentProvider attachmentProvider = Lookup.getDefault().lookup(TrelloAttachmentProvider.class);
        if(attachmentProvider != null)
        {          
            SourceGroup attachments = new TrelloAttachmentsProviderImpl(attachmentProvider);
            sources.put(attachments.getName(), attachments);            
        }  
        
        TrelloCheckListProvider checkListProvider = Lookup.getDefault().lookup(TrelloCheckListProvider.class);
        if(checkListProvider != null)
        {          
            SourceGroup checkLists = new TrelloCheckListsProviderImpl(checkListProvider);
            sources.put(checkLists.getName(), checkLists);            
        }  
        
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
    
    private synchronized LocalFileSystem getFileSystem() throws IOException, PropertyVetoException
    {
        if(fileSystem == null)
        {
            fileSystem = new LocalFileSystem();
            fileSystem.setRootDirectory(FileUtil.toFile(getDataDirectory()));            
        }
        return fileSystem;
    }
   
// SourceProviders    
    
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
            //list.add(new IconProviderImpl());
            list.add(new RemoteDataProviderImpl());
            list.add(new TopComponentProviderImpl());
            list.add(new ProjectOpenedHookImpl());   
            list.add(new RootProjectProviderImpl());
            list.add(new ParentProjectProviderImpl());              

            list.add(new TrelloCardLogicalView(this));
            list.add(new TrelloCardCustomizerProvider(this));                                 

            list.addAll(sources.values());           
            
            lkp = Lookups.fixed(list.toArray(new Object[list.size()]));              
        }
        return lkp;
    }      

// TODO TrelloCard  
    
    @Override
    public String getAppID() 
    {
        return props.getProperty(TrelloCardProvider.PROP_APP_ID);
    }   
    
    @Override
    public LocalDateTime getTimeCreated() 
    {
        String string = props.getProperty(TrelloCardProvider.PROP_TIME_CREATED);
        if(string != null)
        {
            return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
    } 

    @Override
    public String getSourceID()
    {
        return getCardID();
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
    public void save(OutputStream os, String comments) throws IOException
    {
        props.store(os, comments); 
        LOG.info("Trello Card Properties saved");      
    }      
    
    @Override
    public String getAccountUsername()
    {
        return props.getProperty(TrelloCardProvider.PROP_ACCOUNT_USERNAME);
    }    
    
    @Override
    public String getBoardID() 
    {
        return props.getProperty(TrelloCardProvider.PROP_BOARD_ID);
    }    
    
    @Override
    public String getListID() 
    {
        return props.getProperty(TrelloCardProvider.PROP_LIST_ID);
    } 
    
    @Override
    public String getCardID() 
    {
        return props.getProperty(TrelloCardProvider.PROP_CARD_ID);
    }   
    
    @Override
    public String getCardName() 
    {
        return props.getProperty(TrelloCardProvider.PROP_CARD_NAME);
    }      
    
    @Override
    public Integer getCardPosition() 
    {
        String string = props.getProperty(TrelloCardProvider.PROP_CARD_POSITION);
        if(string != null)
        {
            try
            {
                return Integer.parseInt(string);                    
            }
            catch(NumberFormatException e)
            {
                LOG.warning(e.getMessage());
            }
        }
        return null;
    }  
    
    @Override
    public Boolean isCardClosed()
    {
        String string = props.getProperty(TrelloCardProvider.PROP_CARD_CLOSED);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    } 
    
    @Override
    public Boolean isCardSubsribed() 
    {
        String string = props.getProperty(TrelloCardProvider.PROP_CARD_SUBSCRIBED);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    }

    @Override
    public Boolean isCardPinned() 
    {
        String string = props.getProperty(TrelloCardProvider.PROP_CARD_PINNED);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    }

    @Override
    public Boolean isCardDueComplete()
    {
        String string = props.getProperty(TrelloCardProvider.PROP_CARD_DUE_COMPLETE);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    }

    @Override
    public Boolean isCardTemplate()
    {
        String string = props.getProperty(TrelloCardProvider.PROP_CARD_TEMPLATE);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return null;
    }

    @Override
    public LocalDateTime getDateLastActivity() 
    {
        String string = props.getProperty(TrelloCardProvider.PROP_CARD_DATE_LAST_ACTIVITY);
        if(string != null)
        {
            return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE);                   
        }                
        return null;
    }

    @Override
    public List<String> getCardLabelsID() 
    {
        String string = props.getProperty(TrelloCardProvider.PROP_CARD_LABELS_ID);
        if(string != null)
        {
            return List.of(string.split(","));                   
        }                
        return null;
    }    
    
    @Override
    public String getCardRole() 
    {
        return props.getProperty(TrelloCardProvider.PROP_CARD_ROLE);
    }          

    @Override
    public boolean isCardLink() 
    {
        String cardRole = getCardRole();
        if(cardRole != null)
        {
            return cardRole.equalsIgnoreCase(TrelloCardProvider.CARD_ROLE_LINK);
        }
        return false;
    } 
    
// TODO TitleProvider  
    
    @Override
    public String getTitle() 
    {
        return getCardName();
    }

    @Override
    public void setTitle(String title) 
    {
        throw new UnsupportedOperationException();
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
                        browser = provider.getCefClient().createBrowser(board.getBoardShortUrl(), false, false);   
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
            return TrelloCardProject.this.getLookup();
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
            return TrelloCardProject.this;
        }
    }     

// TODO RemoteDataProvider    
    
    private final class RemoteDataProviderImpl implements RemoteDataProvider
    {                
        @Override
        public String pull() 
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            return service.getCardDescription(getCardID(), trello);
        }

        @Override
        public void push(String data) 
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            service.setCardDescription(getCardID(), data, getTrelloAccount());
        }                      
    }     
    
// TODO IconProvider    
    
    private final class IconProviderImpl implements IconProvider
    {                
        private final ChangeSupport changeSupport = new ChangeSupport(this); 

        @Override
        public synchronized Image getIcon()
        {            
            TrelloBoard board = getTrelloBoard();
            if(board != null)
            {
                Color color = board.getBoardBackground();
                if(color != null)
                {
                    Icon icon = new RoundRectIcon(16, 16, color);
                    return ImageUtilities.icon2Image(icon);
                }
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
            return Utils.getRootProject(TrelloCardProject.this);
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
                        TrelloCardsProvider provider = project.getLookup().lookup(TrelloCardsProvider.class);
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

    private final class TrelloActionsProviderImpl extends TrelloActionsProvider implements NodeGroup, FileChangeListener
    {                        
        public TrelloActionsProviderImpl(TrelloActionProvider provider) 
        {
            super(provider);            
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloCardProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_ACTIONS;
        }  

        @Override
        public Icon getIcon(boolean open)
        {
            return ImageUtilities.loadIcon(ICON);
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
            actions.add(new AddComment(this));         
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
                        LOG.info("Actions root folder created: " + rootDir.getPath());                        
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
    
    private final class TrelloAttachmentsProviderImpl extends TrelloAttachmentsProvider implements NodeGroup, FileChangeListener, Runnable
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/attach.png"; 

        private static final String PROP_TRELLO_SYNC_ATTACHMENT = "trello.sync.attachment";        
                
        public TrelloAttachmentsProviderImpl(TrelloAttachmentProvider provider) 
        {
            super(provider);    
            RP.post(this);            
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloCardProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_ATTACHMENTS;
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
            actions.add(new AddAttachment(this));         
            return actions;
        } 
        
        @Override
        public SortedSet<NodeProvider> getNodes()
        {
            List<NodeProvider> list = getAttachments().values().stream()
                    .filter(NodeProvider.class::isInstance)
                    .map(NodeProvider.class::cast)
                    .toList();        

            SortedSet<NodeProvider> sorted = new TreeSet<NodeProvider>(NodeProvider.displayNameComparator());
            sorted.addAll(list);
            
            return sorted;
        }
        
        @Override
        protected synchronized Map<String, TrelloAttachment> getAttachments()
        {
            if(attachments == null)
            {
                attachments = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            TrelloAttachment attachment = provider.getAttachment(Utils.getProperties(file)); 
                            attachments.put(attachment.getAttachmentID(), attachment);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return attachments;
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
                        LOG.info("Attachments root folder created: " + rootDir.getPath());                        
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
                TrelloAttachment attachment = provider.getAttachment(Utils.getProperties(file)); 
                getAttachments().put(attachment.getAttachmentID(), attachment);               
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
                TrelloAttachment attachment = provider.getAttachment(Utils.getProperties(file)); 
                getAttachments().put(attachment.getAttachmentID(), attachment);               
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
            TrelloAttachment attachment = getAttachments().remove(file.getName());  
            if(attachment != null)
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
            String string = props.getProperty(PROP_TRELLO_SYNC_ATTACHMENT);
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
                Object oldValue = props.remove(PROP_TRELLO_SYNC_ATTACHMENT);
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }                
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_ATTACHMENT, oldValue, time); 
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_TRELLO_SYNC_ATTACHMENT, time.format(DateTimeFormatter.ISO_DATE_TIME)); 
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_ATTACHMENT, oldValue, time); 
            }
        } 

        @Override
        public void run()
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            if(service != null)
            {
                List<TrelloAttachment> attachments = service.getAttachments(TrelloCardProject.this, provider, getTrello());
                Set<String> keys = new HashSet<>(getAttachments().keySet());
                for(TrelloAttachment attachment : attachments)
                {                    
                    if(keys.remove(attachment.getAttachmentID()))
                    {
                        TrelloAttachment old = getAttachments().get(attachment.getAttachmentID());
                        if(!old.getProperties().equals(attachment.getProperties()))
                        {
                            FileObject file = getRootFolder().getFileObject(attachment.getAttachmentID(), PropertiesProvider.EXTENSION);
                            if(file != null)
                            {
                                try
                                {
                                    OutputStream os = file.getOutputStream();
                                    attachment.getProperties().store(os, "Created by Trello project: " + getTitle()); 
                                    os.close();
                                    LOG.info("Trello attachment saved: " + attachment.getAttachmentID());  
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
                            OutputStream os = getRootFolder().createAndOpen(attachment.getAttachmentID() + "." + PropertiesProvider.EXTENSION);                            
                            attachment.getProperties().store(os, "Created by Trello project: " + getTitle()); 
                            os.close();
                            LOG.info("Trello attachment saved: " + attachment.getAttachmentID());                              
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
    
    private final class TrelloCheckListsProviderImpl extends TrelloCheckListsProvider implements NodeGroup, FileChangeListener, Runnable
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/to_do_list_checked_1.png"; 

        private static final String PROP_TRELLO_SYNC_CHECKLIST = "trello.sync.checklist";
                
        public TrelloCheckListsProviderImpl(TrelloCheckListProvider provider) 
        {
            super(provider); 
            RP.post(this);    
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloCardProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_CHECK_LISTS;
        } 
        
        @Override
        public Icon getIcon(boolean isOpen)
        {
            return ImageUtilities.image2Icon(getIcon(false, isOpen));
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
            actions.add(new AddCheckList(this));         
            return actions;
        } 
        
        @Override
        public SortedSet<NodeProvider> getNodes()
        {
            List<NodeProvider> list = getCheckLists().values().stream()
                    .filter(NodeProvider.class::isInstance)
                    .map(NodeProvider.class::cast)
                    .toList();        
            
            SortedSet<NodeProvider> sorted = new TreeSet<NodeProvider>(NodeProvider.displayNameComparator());
            sorted.addAll(list);
            
            return sorted;
        }
        
        @Override
        protected synchronized Map<String, TrelloCheckList> getCheckLists()
        {
            if(checkLists == null)
            {
                checkLists = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            TrelloCheckList checkList = provider.getCheckList(Utils.getProperties(file)); 
                            checkLists.put(checkList.getCheckListID(), checkList);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return checkLists;
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
                        LOG.info("Checklist root folder created: " + rootDir.getPath());                        
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
                TrelloCheckList checkList = provider.getCheckList(Utils.getProperties(file)); 
                getCheckLists().put(checkList.getCheckListID(), checkList);               
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
                TrelloCheckList checkList = provider.getCheckList(Utils.getProperties(file)); 
                getCheckLists().put(checkList.getCheckListID(), checkList);               
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
            TrelloCheckList checkList = getCheckLists().remove(file.getName());  
            if(checkList != null)
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
            String string = props.getProperty(PROP_TRELLO_SYNC_CHECKLIST);
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
                Object oldValue = props.remove(PROP_TRELLO_SYNC_CHECKLIST);
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }                
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_CHECKLIST, oldValue, time); 
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_TRELLO_SYNC_CHECKLIST, time.format(DateTimeFormatter.ISO_DATE_TIME)); 
                if(oldValue != null)
                {
                    oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
                }
                propertyChangeSupport.firePropertyChange(PROP_TRELLO_SYNC_CHECKLIST, oldValue, time); 
            }
        } 

        @Override
        public void run()
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            if(service != null)
            {
                List<TrelloCheckList> checkLists = service.getCheckLists(TrelloCardProject.this, provider, getTrelloAccount());
                Set<String> keys = new HashSet<>(getCheckLists().keySet());
                for(TrelloCheckList checkList : checkLists)
                {
                    if(keys.remove(checkList.getCheckListID()))
                    {
                        TrelloCheckList old = getCheckLists().get(checkList.getCheckListID());
                        if(!old.getProperties().equals(checkList.getProperties()))
                        {
                            FileObject file = getRootFolder().getFileObject(checkList.getCheckListID(), PropertiesProvider.EXTENSION);
                            if(file != null)
                            {
                                try
                                {
                                    OutputStream os = file.getOutputStream();
                                    checkList.getProperties().store(os, "Created by Trello project: " + getTitle()); 
                                    os.close();
                                    LOG.info("Trello checklist saved: " + checkList.getCheckListID());  
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
                            OutputStream os = getRootFolder().createAndOpen(checkList.getCheckListID() + "." + PropertiesProvider.EXTENSION);                            
                            checkList.getProperties().store(os, "Created by Trello project: " + getTitle()); 
                            os.close();
                            LOG.info("Trello checklist saved: " + checkList.getCheckListID());                              
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
            return TrelloCardProject.this;
        }                 
    } 
    
    private static final class AddCheckList extends AbstractAction
    {  
        @StaticResource()
        public static final String BANNER = "openpkm/core/resources/banner.png";          
        
        protected final TrelloCheckListsProvider provider;            

        public AddCheckList(TrelloCheckListsProvider provider) 
        {
            super("Add Checklist");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            //panels.add(new AttachmentWizardPanel1());
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
            wiz.setTitle("Add Checklist");  
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            //wiz.putProperty("provider", provider.getProject());
            //wiz.putProperty(GtdActionImpl.PROP_TRELLO_CARD_ID, cardID);
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            { 
                //StatusDisplayer.getDefault().setStatusText("Trello attachment added: " + attachment.getDisplayName());
            }
        }
    }  
    
    private static final class AddCheckListItem extends AbstractAction
    {  
        @StaticResource()
        public static final String BANNER = "openpkm/core/resources/banner.png";          
        
        protected final TrelloCheckListProvider provider;            

        public AddCheckListItem(TrelloCheckListProvider provider) 
        {
            super("Add Checklist");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            //panels.add(new AttachmentWizardPanel1());
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
            wiz.setTitle("Add Checklist");  
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            //wiz.putProperty("provider", provider.getProject());
            //wiz.putProperty(GtdActionImpl.PROP_TRELLO_CARD_ID, cardID);
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            { 
                //StatusDisplayer.getDefault().setStatusText("Trello attachment added: " + attachment.getDisplayName());
            }
        }
    }    

    private static final class AddAttachment extends AbstractAction
    {  
        @StaticResource()
        public static final String BANNER = "openpkm/core/resources/banner.png";          
        
        protected final TrelloAttachmentsProvider provider;            

        public AddAttachment(TrelloAttachmentsProvider provider) 
        {
            super("Add Attachment");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            //panels.add(new AttachmentWizardPanel1());
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
            wiz.setTitle("Add Attachment");  
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            //wiz.putProperty("provider", provider.getProject());
            //wiz.putProperty(GtdActionImpl.PROP_TRELLO_CARD_ID, cardID);
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            { 
                //StatusDisplayer.getDefault().setStatusText("Trello attachment added: " + attachment.getDisplayName());
            }
        }
    }  
    
    private static final class AddComment extends AbstractAction
    {  
        @StaticResource()
        public static final String BANNER = "openpkm/core/resources/banner.png";          
        
        protected final TrelloActionsProvider provider;            

        public AddComment(TrelloActionsProvider provider) 
        {
            super("Add Comment");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            //panels.add(new AttachmentWizardPanel1());
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
            wiz.setTitle("Add Comment");  
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            //wiz.putProperty("provider", provider.get());
            //wiz.putProperty(GtdActionImpl.PROP_TRELLO_CARD_ID, cardID);
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            { 
                //StatusDisplayer.getDefault().setStatusText("Trello comment added: " + attachment.getDisplayName());
            }
        }
    }     
}
