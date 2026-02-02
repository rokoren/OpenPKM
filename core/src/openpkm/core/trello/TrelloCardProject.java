/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
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
import openpkm.base.DescriptionProvider;
import openpkm.base.NodeGroup;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.TitleProvider;
import openpkm.base.UpdateCookie;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAttachment;
import openpkm.trello.TrelloAttachmentProvider;
import openpkm.trello.TrelloAttachmentSourceGroup;
import openpkm.trello.TrelloBoard;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardsProvider;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListItem;
import openpkm.trello.TrelloCheckListItemProvider;
import openpkm.trello.TrelloCheckListItemSourceGroup;
import openpkm.trello.TrelloCheckListProvider;
import openpkm.trello.TrelloCheckListSourceGroup;
import openpkm.utils.Utils;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.project.ProjectState;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.LocalFileSystem;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class TrelloCardProject implements Project, TrelloCard, TitleProvider, DescriptionProvider, PropertiesProvider, Sources, SourceProviders, BatchUpdateSupport
{
    public static final String PROP_TRELLO_USERNAME = "trello.username";
    public static final String PROP_TRELLO_BOARD_ID = "trello.board.id";
    public static final String PROP_TRELLO_ACTIVITY = "trello.activity";
    
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/panel.png";
    
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_CHECK_LISTS      = 100;
    private static final int POSITION_CHECK_LIST_ITEMS = 200;    
    private static final int POSITION_ATTACHMENTS      = 300;

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
    
    private TrelloAccount trelloAccount;
    private Trello trelloApi;    
    private TrelloBoard trelloBoard;
    
    public TrelloCardProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;

        TrelloAttachmentProvider attachmentProvider = Lookup.getDefault().lookup(TrelloAttachmentProvider.class);
        if(attachmentProvider != null)
        {          
            SourceGroup attachments = new TrelloAttachmentSourceGroupImpl(attachmentProvider);
            sources.put(attachments.getName(), attachments);            
        }  
        
        TrelloCheckListProvider checkListProvider = Lookup.getDefault().lookup(TrelloCheckListProvider.class);
        if(checkListProvider != null)
        {          
            SourceGroup checkLists = new TrelloCheckListSourceGroupImpl(checkListProvider);
            sources.put(checkLists.getName(), checkLists);            
        }  

        TrelloCheckListItemProvider checkListItemProvider = Lookup.getDefault().lookup(TrelloCheckListItemProvider.class);
        if(checkListItemProvider != null)
        {          
            SourceGroup checkListItems = new TrelloCheckListItemSourceGroupImpl(checkListItemProvider);
            sources.put(checkListItems.getName(), checkListItems);            
        } 
        
    }  
   
    public TrelloAccount getTrelloAccount()
    {
        if(trelloAccount == null)
        {
            String username = props.getProperty(PROP_TRELLO_USERNAME);
            if (username != null)
            {
                trelloAccount = TrelloService.getDefault().getAccount(username);           
            }                        
        }
        return trelloAccount;
    }     
    
    public Trello getTrelloApi()
    {
        if(trelloApi == null)
        {
            TrelloAccount account = getTrelloAccount();
            if(account != null)
            {
                trelloApi = new TrelloImpl(account.getApiKey(), account.getAccessToken(), new JDKTrelloHttpClient());                
            }            
        }
        return trelloApi;
    }
    
    public TrelloBoard getTrelloBoard()
    {
        if(trelloBoard == null)
        {
            TrelloAccount account = getTrelloAccount();
            if(account != null)
            {
                String boardID = props.getProperty(PROP_TRELLO_BOARD_ID);
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
            list.add(new TopComponentProviderImpl());
            list.add(new ProjectOpenedHookImpl());   
            list.add(new RootProjectProviderImpl());
            list.add(new ParentProjectProviderImpl());              

            list.add(new TrelloLogicalView(this));
            list.add(new TrelloCustomizerProvider(this));  
            
            list.add(new TrelloCardsProviderImpl()); 
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

// TODO TrelloCard  
    
    @Override
    public String getBoardID() 
    {
        return props.getProperty(PROP_TRELLO_BOARD_ID);
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
                        browser = provider.getCefClient().createBrowser(board.getShortUrl(), false, false);   
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
            TrelloBoard board = getTrelloBoard();
            if(board != null)
            {
                Color color = board.getBackground();
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
                        BoardsProvider provider = project.getLookup().lookup(BoardsProvider.class);
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
    
    private final class TrelloCardsProviderImpl implements TrelloCardsProvider
    {                        
        private static final String ROOT_FOLDER = "cards";          
        
        private Map<String, TrelloCard> cards; 
        private FileObject rootDir;            
        
        private final ChangeSupport changeSupport;              

        public TrelloCardsProviderImpl()
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
                    LOG.info("Cards dir created: " + rootDir.getPath());                        
                }                 
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
            actions.add(new AddCard(this));         
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
    }      
    
// TODO SourceGroup

    private final class TrelloActionSourceGroupImpl extends TrelloActionSourceGroup implements NodeGroup, FileChangeListener
    {                        
        public TrelloActionSourceGroupImpl(TrelloActionProvider provider) 
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
    
    private final class TrelloAttachmentSourceGroupImpl extends TrelloAttachmentSourceGroup implements NodeGroup, FileChangeListener
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/attach.png";         
                
        public TrelloAttachmentSourceGroupImpl(TrelloAttachmentProvider provider) 
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
                        LOG.info("Attachment root folder created: " + rootDir.getPath());                        
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
    }  
    
    private final class TrelloCheckListSourceGroupImpl extends TrelloCheckListSourceGroup implements NodeGroup, FileChangeListener
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/date_task.png";         
                
        public TrelloCheckListSourceGroupImpl(TrelloCheckListProvider provider) 
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
            return POSITION_CHECK_LISTS;
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
    }      

    private final class TrelloCheckListItemSourceGroupImpl extends TrelloCheckListItemSourceGroup implements NodeGroup, FileChangeListener
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/date_task.png";         
                
        public TrelloCheckListItemSourceGroupImpl(TrelloCheckListItemProvider provider) 
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
            return POSITION_CHECK_LIST_ITEMS;
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
            List<NodeProvider> list = getCheckListItems().values().stream()
                    .filter(NodeProvider.class::isInstance)
                    .map(NodeProvider.class::cast)
                    .toList();        
            
            SortedSet<NodeProvider> sorted = new TreeSet<NodeProvider>(NodeProvider.displayNameComparator());
            sorted.addAll(list);
            
            return sorted;
        }
        
        @Override
        protected synchronized Map<String, TrelloCheckListItem> getCheckListItems()
        {
            if(checkListItems == null)
            {
                checkListItems = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            TrelloCheckListItem checkListItem = provider.getCheckListItem(Utils.getProperties(file)); 
                            checkListItems.put(checkListItem.getCheckListItemID(), checkListItem);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return checkListItems;
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
                        LOG.info("Checklist items root folder created: " + rootDir.getPath());                        
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
                TrelloCheckListItem checkListItem = provider.getCheckListItem(Utils.getProperties(file)); 
                getCheckListItems().put(checkListItem.getCheckListItemID(), checkListItem);               
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
            TrelloCheckListItem checkListItem = getCheckListItems().remove(file.getName());  
            if(checkListItem != null)
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
        protected final TrelloListSourceGroup sourceGroup;            

        public AddList(TrelloListSourceGroup sourceGroup) 
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

    private static final class AddCard extends AbstractAction
    {                          
        private final TrelloCardsProvider provider;            

        public AddCard(TrelloCardsProvider provider) 
        {
            super("Add Card");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new HomePageWizardPanel1());
            panels.add(new HomePageWizardPanel2());
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
            wiz.setTitle("Add Home Page");  
            //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            wiz.putProperty("provider", provider.getProvider());
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            { 
                LocalDateTime now = LocalDateTime.now();
                String domainID = null;

                String url = (String) wiz.getProperty(HomePageProject.PROP_URL);
                String title = (String) wiz.getProperty(TitleProvider.PROP_TITLE);
                String description = (String) wiz.getProperty(DescriptionProvider.PROP_DESCRIPTION);  
                List<Topic> topics = (List<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);            

                Document document = (Document) wiz.getProperty("document"); 
                String canonical = document.select("link[rel=canonical]").attr("href");
                if(canonical != null && !canonical.isBlank())
                {
                    domainID = canonical;
                }
                else
                { 
                    String signature = getSignature(document, url);  

                    try
                    {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        byte[] hash = digest.digest(signature.getBytes(StandardCharsets.UTF_8));

                        StringBuilder hex = new StringBuilder();
                        for (byte b : hash) {
                            hex.append(String.format("%02x", b));
                        }

                        domainID = hex.toString();                      
                    } 
                    catch(NoSuchAlgorithmException e)
                    {
                        LOG.warning(e.getMessage());
                    }
                }            

                Properties props = new Properties();
                props.setProperty(HomePageProject.PROP_HOME_PAGE_ID, domainID);
                props.setProperty(Domain.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
                props.setProperty(TitleProvider.PROP_TITLE, title);       
                props.setProperty(DescriptionProvider.PROP_DESCRIPTION, description);            
                props.setProperty(HomePageProject.PROP_URL, url);  

                String favicon = getFavicon(document);
                if(favicon == null)
                {
                    favicon = getFaviconGoogle(document);
                }

                if(favicon != null)
                {
                    props.setProperty(HomePageProject.PROP_FAVICON, favicon);
                }

                if(topics != null)
                {
                    KnowledgeGraphProvider knowledgeGraphProvider = provider.getProvider().getLookup().lookup(KnowledgeGraphProvider.class);
                    if(knowledgeGraphProvider != null)
                    {
                        StringJoiner joiner = new StringJoiner(",");
                        for(Topic topic : topics)
                        {
                            joiner.add(knowledgeGraphProvider.getTreeID(topic));
                        }
                        props.setProperty(TopicsProvider.PROP_TOPICS, joiner.toString());                    
                    }
                }  

                try
                {  
                    FileObject projectDirectory = FileUtil.createFolder(provider.getRootDirectory(), domainID);           
                    FileObject projectFolder = FileUtil.createFolder(projectDirectory, HomePageProjectFactory.PROJECT_FOLDER);                   

                    OutputStream os = projectFolder.createAndOpen(HomePageProjectFactory.PROJECT_FILE);
                    props.store(os, "OpenPKM Home Page Project"); 
                    os.close(); 

                    StatusDisplayer.getDefault().setStatusText("OpenPKM Home Page Project saved: " + title); 

                    Project project = ProjectManager.getDefault().findProject(projectDirectory);
                    if(project != null)
                    {
                        Domain domain = project.getLookup().lookup(Domain.class);
                        if(domain != null)
                        {
                            provider.addDomain(domain);
                            /*
                            Project[] projects = {domain};
                            OpenProjects.getDefault().open(projects, false);   
                            */
                        }
                    }                  
                }
                catch(IOException e) 
                {
                    LOG.warning(e.getMessage());
                }                                              
            } 
        }
    } 
    
    private static final class AddLabel extends AbstractAction
    {                          
        private final TrelloLabelSourceGroup sourceGroup;            

        public AddLabel(TrelloLabelSourceGroup sourceGroup) 
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

    private static final class AddAttachment extends AbstractAction
    {  
        @StaticResource()
        public static final String BANNER = "openpkm/core/resources/banner.png";          
        
        protected final TrelloAttachmentSourceGroup sourceGroup;            

        public AddAttachment(TrelloAttachmentSourceGroup sourceGroup) 
        {
            super("Add Attachment");
            this.sourceGroup = sourceGroup;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new AttachmentWizardPanel1());
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
            wiz.putProperty("project", provider.getProject());
            wiz.putProperty(GtdActionImpl.PROP_TRELLO_CARD_ID, cardID);
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            { 
                TrelloAttachment trelloAttachment = (TrelloAttachment) wiz.getProperty("attachment");
                if(trelloAttachment != null)
                {
                    Attachment attachment = new AttachmentImpl(trelloAttachment);
                    provider.addAttachment(attachment);
                    StatusDisplayer.getDefault().setStatusText("Trello attachment added: " + attachment.getDisplayName());
                }
            }
        }
    }    
}
