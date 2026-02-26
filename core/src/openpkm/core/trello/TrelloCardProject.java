/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.dlsc.pdfviewfx.PDFView;
import com.julienvey.trello.Trello;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.DataGroupProvider;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.MarkdownSupport;
import openpkm.base.NodePositionProvider;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.RemoteDataProvider;
import openpkm.base.SourceProviders;
import openpkm.base.TitleProvider;
import openpkm.base.UpdateCookie;
import openpkm.core.TopComponentProvider;
import openpkm.jcef.CefClientProvider;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAccountsProvider;
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
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import openpkm.base.SourceGroupProvider;
import openpkm.trello.TrelloComment;
import org.openide.DialogDescriptor;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObject;
import openpkm.base.NodeActionsProvider;
import openpkm.base.RunnableFX;
import openpkm.jcef.CefAppProvider;
import openpkm.trello.TrelloLabel;
import openpkm.trello.TrelloLabelsProvider;
import org.cef.CefClient;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;

/**
 *
 * @author Rok Koren
 */
public class TrelloCardProject implements Project, TrelloCard, TitleProvider, PropertiesProvider, Sources, BatchUpdateSupport
{ 
    public static final String PROP_TRELLO_USERNAME = "trello.username";
    public static final String PROP_TRELLO_BOARD_ID = "trello.board.id";
    public static final String PROP_TRELLO_ACTIVITY = "trello.activity";         
    
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/trello.png"; 
    
    private static final int POSITION_CHECK_LISTS = 100;
    private static final int POSITION_COMMENTS    = 200;    
    private static final int POSITION_ATTACHMENTS = 300;
    private static final int POSITION_LABELS      = 400;    

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
    
    private TrelloAccount trelloAccount;
    private Trello trello;    
    private TrelloBoard trelloBoard;
    
    public TrelloCardProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;
               
        TrelloAttachmentProvider attachmentProvider = Lookup.getDefault().lookup(TrelloAttachmentProvider.class);
        if(attachmentProvider != null)
        {          
            SourceGroup attachments = new TrelloAttachmentsProviderImpl(attachmentProvider);
            sources.put(attachments.getName(), attachments);            
        }  
        
        SourceGroup checkLists = new TrelloCheckListsProviderImpl();
        sources.put(checkLists.getName(), checkLists);                       
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

            ParentProjectProvider parentProjectProvider = new ParentProjectProviderImpl();
            
            list.add(this);
            list.add(new Info());
            
            TrelloLabelsProvider labelsProvider = parentProjectProvider.getPartentProject().getLookup().lookup(TrelloLabelsProvider.class);
            if(labelsProvider != null)
            {
                list.add(new IconProviderImpl(labelsProvider));  
                list.add(new TrelloLabelsProviderImpl(labelsProvider));        
            }
            
            list.add(new RemoteDataProviderImpl());
            list.add(new TopComponentProviderImpl());
            list.add(new ProjectOpenedHookImpl());   
            list.add(new RootProjectProviderImpl());
            list.add(parentProjectProvider);              

            list.add(new TrelloCardLogicalView(this));
            list.add(new TrelloCardCustomizerProvider(this));   
            
            list.add(new HtmlFilesProviderImpl());   

            list.addAll(sources.values());  
            
            TrelloActionsProvider actionsProvider = parentProjectProvider.getPartentProject().getLookup().lookup(TrelloActionsProvider.class);              
            if(actionsProvider != null)
            {
                list.add(new CommentDataGroupProviderImpl(actionsProvider));                            
            }
            
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
        String string = props.getProperty(PROP_DELETED);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return false;
    }

    @Override
    public void setDeleted(boolean isDeleted)
    {
        boolean oldValue = isDeleted();
        props.setProperty(PROP_DELETED, Boolean.toString(isDeleted));
        propertyChangeSupport.firePropertyChange(PROP_DELETED, oldValue, isDeleted);
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
        
        @Override
        public void delete() 
        {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }         
    }     
    
// TODO IconProvider    
    
    private final class IconProviderImpl implements IconProvider
    { 
        private static final int WIDTH  = 20;
        private static final int HEIGHT = 18;      
        
        private final ChangeSupport changeSupport = new ChangeSupport(this); 
        
        private final TrelloLabelsProvider provider;

        public IconProviderImpl(TrelloLabelsProvider provider) 
        {
            this.provider = provider;
        }
        
        public static Image createMiniCardIcon(List<Color> labels) 
        {
            BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int count = labels.size();

            if (count == 0) {
                // Prazna kartica – subtilen okvir
                g.setColor(new Color(180, 180, 180, 180));
                g.drawRoundRect(0, 2, WIDTH - 1, HEIGHT - 5, 4, 4);
            } else {
                // Dinamična širina labelov
                int spacing = 1;
                int totalSpacing = spacing * (count - 1);
                int availableWidth = WIDTH - totalSpacing;

                int labelWidth = availableWidth / count;
                int labelHeight = 12;
                int y = (HEIGHT - labelHeight) / 2;

                int x = 0;

                for (Color c : labels) {
                    g.setColor(c);
                    g.fillRoundRect(x, y, labelWidth, labelHeight, 3, 3);
                    x += labelWidth + spacing;
                }
            }

            g.dispose();
            return img;
        } 
                
        @Override
        public synchronized Image getIcon()
        { 
            List<Color> colors = new ArrayList<>();
            List<String> labelsID = getCardLabelsID();
            for(String labelID : labelsID)
            {
                TrelloLabel label = provider.getLabel(labelID);
                if(label != null)
                {
                    colors.add(label.getLabelColor());
                }
            }
            return createMiniCardIcon(colors);
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
    
// TODO DataGroupProvider

    private final class CommentDataGroupProviderImpl implements DataGroupProvider
    {
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/comments.png";         
        
        private final TrelloActionsProvider provider;
                
        public CommentDataGroupProviderImpl(TrelloActionsProvider provider)
        {
            this.provider = provider;
        } 

        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();            
            actions.add(new AddComment(getCardID(), provider, getTrelloAccount(), getTrello()));        
            return actions;
        }
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloCardProject.this;
        }        
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_COMMENTS;
        }                  

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            provider.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            provider.removeChangeListener(listener);
        }   

        @Override
        public FileObject getRootFolder() throws IOException 
        {
            ParentProjectProvider parent = getLookup().lookup(ParentProjectProvider.class);
            SourceProviders provider = parent.getPartentProject().getLookup().lookup(SourceProviders.class);
            return provider.getDataDirectory();
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return dateComparator();
        }  
        
        @Override
        public boolean isReversed()
        {
            return true;
        }

        @Override
        public String getName() 
        {
            return "comment";
        }

        @Override
        public String getDisplayName() 
        {
            return "Comments";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            return ImageUtilities.loadImage(ICON);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                TrelloComment comment = data.getLookup().lookup(TrelloComment.class);
                if(comment != null)
                {
                    if(comment.getCardID() != null)
                    {
                        return comment.getCardID().equals(getCardID());                        
                    }
                }                 
            }                                   
            return false;
        }
    }       
    
// TODO SourceGroup    
    
    private final class TrelloLabelsProviderImpl implements SourceGroupProvider, NodeActionsProvider<TrelloLabel>
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/palette.png"; 
            
        private final TrelloLabelsProvider provider;
        
        protected final ChangeSupport changeSupport;         
        
        public TrelloLabelsProviderImpl(TrelloLabelsProvider provider) 
        {
            this.provider = provider;
            changeSupport = new ChangeSupport(this); 
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloCardProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_LABELS;
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
            //actions.add(new AddLabel(this));         
            return actions;
        } 
        
        @Override
        public List<Action> getActions(TrelloLabel label)
        {
            List<Action> actions = new ArrayList<>(1);
            //actions.add(new DeleteLabel(getTrello(), this, label));
            return actions;
        }        
        
        @Override
        public SortedSet<NodeProvider> getNodes()
        {
            SortedSet<NodeProvider> sorted = new TreeSet<NodeProvider>(NodeProvider.displayNameComparator());
            for(String labelID : getCardLabelsID())
            {
                TrelloLabel label = provider.getLabel(labelID);
                if(label != null)
                {
                    sorted.add(label);                    
                }
            }            
            return sorted;
        }                    

        @Override
        public String getName() 
        {
            return provider.getName();
        }

        @Override
        public String getDisplayName() 
        {
            return provider.getDisplayName();
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
    
    private final class TrelloAttachmentsProviderImpl extends TrelloAttachmentsProvider implements SourceGroupProvider, NodeActionsProvider<TrelloAttachment>, MultiViewDescription, FileChangeListener, Runnable
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
        public HttpURLConnection getAttachmentConn(TrelloAttachment attachment) throws MalformedURLException, IOException
        {
            TrelloAccount account = getTrelloAccount();
            String link = "https://api.trello.com/1/cards/" + getCardID() + "/attachments/" + attachment.getAttachmentID() + "/download/" + attachment.getAttachmentName();
            URL url = new URL(link);                     
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "OAuth oauth_consumer_key=\"" + account.getApiKey() + "\", oauth_token=\"" + account.getAccessToken() + "\"");              
            return conn;
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
            actions.add(new AddAttachmentLink(this));         
            return actions;
        } 
        
        @Override
        public SortedSet<? extends NodeProvider> getNodes()
        {
            SortedSet<NodePositionProvider> sorted = new TreeSet<NodePositionProvider>(NodePositionProvider.positionComparator());
            sorted.addAll(getAttachments());            
            return sorted.reversed();
        }
        
        @Override
        protected synchronized Map<String, TrelloAttachment> getAttachmentsById()
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
        public void createAttachmentLink(String url, String name)
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            TrelloAttachment attachment = service.createAttachmentLink(getCardID(), name, url, provider, getTrelloAccount());   
            try
            {
                OutputStream os = getRootFolder().createAndOpen(attachment.getAttachmentID() + "." + PropertiesProvider.EXTENSION);
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
        
        @Override
        public List<Action> getActions(TrelloAttachment attachment)
        {
            List<Action> actions = new ArrayList<>(1);
            actions.add(new DeleteAttachment(getTrello(), TrelloCardProject.this, this, attachment));
            return actions;
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
                getAttachmentsById().put(attachment.getAttachmentID(), attachment);               
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
                getAttachmentsById().put(attachment.getAttachmentID(), attachment);               
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
            TrelloAttachment attachment = getAttachmentsById().remove(file.getName());  
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
                List<TrelloAttachment> attachments = service.getAttachments(TrelloCardProject.this, provider, getTrelloAccount());
                Set<String> keys = new HashSet<>(getAttachmentsById().keySet());
                for(TrelloAttachment attachment : attachments)
                {                    
                    if(keys.remove(attachment.getAttachmentID()))
                    {
                        TrelloAttachment old = getAttachmentsById().get(attachment.getAttachmentID());
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
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }

        @Override
        public String getDisplayName() 
        {
            return "Attachments";
        }

        @Override
        public Image getIcon() 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.ATTACHMENT);
        }

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }

        @Override
        public String preferredID() 
        {
            return "attachment";
        }

        @Override
        public MultiViewElement createElement() 
        {
            return new AttachmentsMultiViewElementImpl(this);
        }           
    }  
    
    private final class TrelloCheckListsProviderImpl extends TrelloCheckListsProvider implements SourceGroupProvider, NodeActionsProvider<TrelloCheckList>, FileChangeListener, Runnable
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/to_do_list_checked_1.png"; 

        private static final String PROP_TRELLO_SYNC_CHECKLIST = "trello.sync.checklist";
         
        private final TrelloCheckListProvider provider;
        
        public TrelloCheckListsProviderImpl() 
        {
            provider = new TrelloCheckListProviderImpl(this);
            RP.post(this);    
        }          
        
        @Override
        public Lookup.Provider getProvider()
        {
            return TrelloCardProject.this;
        }  
        
        @Override
        public TrelloAccount getAccount()
        {
            return getTrelloAccount();
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
        public List<Action> getActions(TrelloCheckList checkList) 
        {
            List<Action> actions = new ArrayList();
            actions.add(new AddCheckListItem(checkList, this));         
            return actions;
        }         
        
        @Override
        public SortedSet<? extends NodeProvider> getNodes()
        {
            List<NodePositionProvider> list = getCheckLists().values().stream()
                    .filter(NodePositionProvider.class::isInstance)
                    .map(NodePositionProvider.class::cast)
                    .toList();        
            
            SortedSet<NodePositionProvider> sorted = new TreeSet<NodePositionProvider>(NodePositionProvider.positionComparator());
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
        public void createCheckList(String name)
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            TrelloCheckList checkList = service.createCheckList(getCardID(), name, provider, getTrelloAccount());   
            try
            {
                OutputStream os = getRootFolder().createAndOpen(checkList.getCheckListID() + "." + PropertiesProvider.EXTENSION);
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
                TrelloCheckList checkList = getCheckLists().get(file.getName()); 
                Properties props = Utils.getProperties(file);
                if(!checkList.getProperties().equals(props))
                {
                    if(Utils.equals(checkList.getProperties(), props, TrelloCheckListProvider.PROP_CHECKLIST_ITEMS))
                    {
                        checkList.getProperties().putAll(props);
                        changeSupport.fireChange();                                          
                    }
                    else
                    {
                        boolean isPosition = Utils.equals(checkList.getProperties(), props, TrelloCheckListProvider.PROP_CHECKLIST_POSITION);
                        boolean isName = Utils.equals(checkList.getProperties(), props, TrelloCheckListProvider.PROP_CHECKLIST_NAME);                    
                        checkList.getProperties().putAll(props);
                        checkList.getChangeSupport().fireChange();                    
                        if(!isPosition || !isName)
                        {
                            changeSupport.fireChange();     
                        }                    
                    }                    
                }
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
                                    checkList.getProperties().store(os, "Updated by Trello project: " + getTitle()); 
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
        private final TrelloCheckListsProvider provider;             

        public AddCheckList(TrelloCheckListsProvider provider) 
        {
            super("Add Checklist");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            NotifyDescriptor d = new NotifyDescriptor.InputLine("Name:", "Add Checklist");
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.OK_OPTION) 
            {
                String name = ((NotifyDescriptor.InputLine) d).getInputText();
                provider.createCheckList(name);
            }
        }
    }  
    
    private static final class AddCheckListItem extends AbstractAction
    {          
        private final TrelloCheckList checkList;  
        private final TrelloCheckListsProvider provider; 

        public AddCheckListItem(TrelloCheckList checkList, TrelloCheckListsProvider provider) 
        {
            super("Add Checklist Item");
            this.checkList = checkList;
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            NotifyDescriptor d = new NotifyDescriptor.InputLine("Name:", "Add Checklist Item");
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.OK_OPTION) 
            {
                String name = ((NotifyDescriptor.InputLine) d).getInputText();
                TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
                JSONObject json = service.createCheckListIem(checkList.getCheckListID(), name, provider.getAccount());
                
                String string = checkList.getProperties().getProperty(TrelloCheckListProvider.PROP_CHECKLIST_ITEMS);
                if(string != null)
                {
                    JSONArray jsons = new JSONArray(string);
                    jsons.put(json);
                    checkList.getProperties().setProperty(TrelloCheckListProvider.PROP_CHECKLIST_ITEMS, jsons.toString());
                }                  
                
                FileObject file = provider.getRootFolder().getFileObject(checkList.getCheckListID(), PropertiesProvider.EXTENSION);
                if(file != null)
                {
                    try
                    {
                        OutputStream os = file.getOutputStream();
                        TitleProvider titleProvider = provider.getProvider().getLookup().lookup(TitleProvider.class);
                        checkList.getProperties().store(os, "Updated by Trello project: " + titleProvider.getTitle()); 
                        os.close();
                        checkList.getChangeSupport().fireChange();
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
    }         

    private static final class AddAttachmentLink extends AbstractAction
    {  
        @StaticResource()
        public static final String BANNER = "openpkm/core/resources/banner.png";          
        
        protected final TrelloAttachmentsProvider provider;            

        public AddAttachmentLink(TrelloAttachmentsProvider provider) 
        {
            super("Add Link Attachment");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new AttachmentLinkWizardPanel1());
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
            wiz.setTitle("Add Link Attachment");  
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            { 
                String url = (String)wiz.getProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_URL);
                String name = (String)wiz.getProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_NAME);
                provider.createAttachmentLink(url, name);
            }
        }
    }  
    
    private static final class DeleteAttachment extends AbstractAction
    {  
        private final Trello trello;
        private final TrelloCard card;
        private final TrelloAttachmentsProvider provider; 
        private final TrelloAttachment attachment;

        public DeleteAttachment(Trello trello, TrelloCard card, TrelloAttachmentsProvider provider, TrelloAttachment attachment) 
        {
            super("Delete");
            this.trello = trello;
            this.card = card;
            this.provider = provider;
            this.attachment = attachment;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {  
            NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to delete: " + attachment.getAttachmentName(), "Delete Attachment", NotifyDescriptor.YES_NO_OPTION);
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.YES_OPTION) 
            {
                trello.deleteAttachment(card.getCardID(), attachment.getAttachmentID());  
                FileObject fo = provider.getRootFolder().getFileObject(attachment.getAttachmentID(), PropertiesProvider.EXTENSION);       
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
    
    private static final class AddComment extends AbstractAction implements ActionListener
    { 
        private static final String ACTION_COMMAND_ADD_COMMENT = "Add Comment";
        private static final String ACTION_COMMAND_OK          = "OK";
        
        private final Trello trello;  
        private final String cardID;   
        private final TrelloAccount account;   
        private final TrelloActionsProvider provider; 
        private final JTextArea area;

        public AddComment(String cardID, TrelloActionsProvider provider, TrelloAccount account, Trello trello) 
        {
            super(ACTION_COMMAND_ADD_COMMENT);
            this.cardID = cardID;
            this.provider = provider;
            this.account = account;
            this.trello = trello;
            area = new JTextArea();
            area.setFont(area.getFont().deriveFont(18f));
            area.setPreferredSize(new Dimension(400, 200));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {             
            if(evt.getActionCommand().equals(ACTION_COMMAND_ADD_COMMENT))
            {                
                DialogDescriptor d = new DialogDescriptor(
                area, // Component
                "Add Comment", // title
                true, // modality
                this); // ActionListener
                DialogDisplayer.getDefault().createDialog(d).setVisible(true);                  
            }
            else if(evt.getActionCommand().equals(ACTION_COMMAND_OK))
            {
                FileObject root = provider.getRootFolder();                  
                TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
                MarkdownSupport markdown = Lookup.getDefault().lookup(MarkdownSupport.class);  

                if(root != null && service != null && markdown != null)
                {                                                                                                     
                    TrelloComment comment = service.createComment(cardID, area.getText().trim(), provider.getActionProvider(), provider.getCommentProvider(), account, trello);
                    if(comment != null)
                    {
                        try
                        {                                                                                                                                            
                            provider.createData(comment, markdown);  
                            
                            FileSystem fs = root.getFileSystem();
                            fs.runAtomicAction(() -> {
                                OutputStream os = root.createAndOpen(comment.getActionID() + "." + PropertiesProvider.EXTENSION);
                                comment.save(os, "Saved by Add Comment Action");
                                os.close();  
                            });                                                                                                              
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
    
    private static final class AttachmentsMultiViewElementImpl extends JPanel implements MultiViewElement, ActionListener, CefLoadHandler
    {
        private JToolBar toolbar;

        private final DefaultComboBoxModel<TrelloAttachment> attachments = new DefaultComboBoxModel<>(); 
        private final Map<String, RunnableFX> inits = new HashMap<>(); 
        
        private transient MultiViewElementCallback callback;  
        
        private final TrelloAttachmentsProvider provider;
        private final JComboBox comboBox;
        
        private CefClient client;

        public AttachmentsMultiViewElementImpl(TrelloAttachmentsProvider provider) 
        {
            this.provider = provider;
            setLayout(new CardLayout());
            SortedSet<TrelloAttachment> sorted = new TreeSet<TrelloAttachment>(NodePositionProvider.positionComparator());
            sorted.addAll(provider.getAttachments());
            attachments.addAll(sorted.reversed());
            comboBox = new JComboBox(attachments);
            comboBox.setRenderer(new NodeProvider.ListCellRendererImpl());                          
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
            return this;
        }

        @Override
        public JComponent getToolbarRepresentation() 
        {
            if(toolbar == null)
            {
                toolbar = new JToolBar();
                toolbar.add(comboBox);
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
            return Lookup.EMPTY;
        }        

        @Override
        public void componentOpened() 
        {
            CefAppProvider browserProvider = Lookup.getDefault().lookup(CefAppProvider.class);
            if(browserProvider != null)
            {
                try
                {
                    client = browserProvider.getApp().createClient();   
                    client.addLoadHandler(this);
                }
                catch(Exception e)
                {
                    LOG.warning(e.getMessage());
                }
            }  

            for(TrelloAttachment attachment : provider.getAttachments())
            {
                Component comp = getVisualRepresentation(attachment);
                if(comp != null)
                {
                    add(comp, attachment.getAttachmentID());                    
                }
            }              
            
            comboBox.addActionListener(this); 
            if(comboBox.getItemCount() > 0)
            {
                comboBox.setSelectedIndex(0);
            }
        }

        @Override
        public void componentClosed() 
        {
            comboBox.removeActionListener(this);
            if(client != null)
            {
                client.dispose();
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
        
        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            CardLayout layout = (CardLayout)getLayout();
            TrelloAttachment attachment = (TrelloAttachment)attachments.getSelectedItem();
            if(attachment != null)
            {
                RunnableFX init = inits.remove(attachment.getAttachmentID());
                if(init != null)
                {
                    if(init.isFX())
                    {
                        Platform.runLater(init);                       
                    }
                    else
                    {
                        RP.post(init);
                    }
                }
                layout.show(this, attachment.getAttachmentID());  
            }          
        }  
        
        private Component getVisualRepresentation(TrelloAttachment attachment) 
        {
            String mimeType = attachment.getAttachmentMimeType();
            if(mimeType == null || mimeType.isEmpty())
            {
                String url = attachment.getAttachmentUrl();
                if(url != null)
                {
                    try
                    {
                        CefBrowser browser = client.createBrowser(url, false, false); 
                        return browser.getUIComponent();                             
                    }
                    catch(Exception e)
                    {
                        LOG.warning(e.getMessage());
                    }
                }                
            } 
            if(mimeType.equals(TrelloAttachmentProvider.MIME_TYPE_PDF))
            {                
                JFXPanel panel = new JFXPanel();
                panel.setLayout(new BorderLayout());
                LOG.info("Attachment URL: " + attachment.getAttachmentUrl());
                
                try
                {
                    HttpURLConnection conn = provider.getAttachmentConn(attachment);
                    inits.put(attachment.getAttachmentID(), new PdfCard(panel, conn));                      
                }
                catch(MalformedURLException e)
                {
                    LOG.warning(e.getMessage());
                }                 
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }          
                
                return panel; 
            } 
            else if(mimeType.equals(TrelloAttachmentProvider.MIME_TYPE_JPEG) || mimeType.equals(TrelloAttachmentProvider.MIME_TYPE_PNG))
            {                
                JFXPanel panel = new JFXPanel();
                panel.setLayout(new BorderLayout());
                LOG.info("Attachment URL: " + attachment.getAttachmentUrl());
                
                try
                {
                    HttpURLConnection conn = provider.getAttachmentConn(attachment);
                    inits.put(attachment.getAttachmentID(), new ImageCard(panel, conn));                      
                }
                catch(MalformedURLException e)
                {
                    LOG.warning(e.getMessage());
                }                 
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }          
                
                return panel; 
            }             
            return null;
        }        

        @Override
        public void onLoadingStateChange(CefBrowser cb, boolean bln, boolean bln1, boolean bln2) 
        {
            SwingUtilities.invokeLater(() -> {
                repaint();
            });
        }

        @Override
        public void onLoadStart(CefBrowser cb, CefFrame cf, CefRequest.TransitionType tt) 
        {
            SwingUtilities.invokeLater(() -> {
                repaint();
            });
        }

        @Override
        public void onLoadEnd(CefBrowser cb, CefFrame cf, int i) 
        {
            SwingUtilities.invokeLater(() -> {
                revalidate();
                doLayout();
                repaint();
            });
        }

        @Override
        public void onLoadError(CefBrowser cb, CefFrame cf, ErrorCode ec, String string, String string1) 
        {
            SwingUtilities.invokeLater(() -> {
                repaint();
            });
        }
    } 
    
    private static final class PdfCard implements RunnableFX
    {
        private final JFXPanel panel;
        private final HttpURLConnection conn;

        public PdfCard(JFXPanel panel, HttpURLConnection conn) {
            this.panel = panel;
            this.conn = conn;
        }
        
        @Override
        public boolean isFX()
        {
            return true;
        }
        
        @Override
        public void run()
        {
            try
            {
                PDFView pdfView = new PDFView();
                pdfView.load(conn.getInputStream());                              

                pdfView.setShowToolBar(false);
                pdfView.setShowThumbnails(false);
                pdfView.setCacheThumbnails(true);
                //pdfView.setShowAll(true);
                //pdfView.getStylesheets().setAll(Objects.requireNonNull(Installer.class.getResource("nord-dark.css")).toExternalForm(), Objects.requireNonNull(Installer.class.getResource("pdf-view-atlanta.css")).toExternalForm());                   
                Scene scene = new Scene(pdfView);
                /*
                if (isDarkLaF)
                {
                    scene.getStylesheets().add(getClass().getResource("/openpkm/asciidoc/resources/javafx-nb-dark.css").toString());
                    // Loading default content to force apply a content with css for dark background
                    //browser.getEngine().loadContent(readLoadingPage());
                }              
                */
                panel.setScene(scene);                                                                             
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }
        }
    }
    
    private static final class ImageCard implements RunnableFX
    {
        private final JFXPanel panel;
        private final HttpURLConnection conn;

        public ImageCard(JFXPanel panel, HttpURLConnection conn) {
            this.panel = panel;
            this.conn = conn;
        }
        
        @Override
        public boolean isFX()
        {
            return true;
        }
        
        @Override
        public void run()
        {            
            ImageView imageView = new ImageView();            
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(imageView);                  
            imageView.fitWidthProperty().bind(borderPane.widthProperty());
            imageView.fitHeightProperty().bind(borderPane.heightProperty());  
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            imageView.setCache(true);                  
            Scene scene = new Scene(borderPane);
            /*
            if (isDarkLaF)
            {
                scene.getStylesheets().add(getClass().getResource("/openpkm/asciidoc/resources/javafx-nb-dark.css").toString());
                // Loading default content to force apply a content with css for dark background
                //browser.getEngine().loadContent(readLoadingPage());
            }              
            */
            panel.setScene(scene);            
            
            try
            {
                javafx.scene.image.Image image = new javafx.scene.image.Image(conn.getInputStream());                
                imageView.setImage(image); 
            }                  
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }             
        }
    }    

    private static Comparator<DataObject> dateComparator() 
    {
        return new Comparator<DataObject>() 
        {
            @Override
            public int compare(DataObject data1, DataObject data2) 
            {
                TrelloComment comment1 = data1.getLookup().lookup(TrelloComment.class);
                TrelloComment comment2 = data2.getLookup().lookup(TrelloComment.class);
                if(comment1 != null && comment2 != null)
                {
                    return comment1.getDate().compareTo(comment2.getDate());                    
                }
                return -1;
            }
        };
    } 
}
