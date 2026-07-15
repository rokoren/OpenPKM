/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeListener;
import openpkm.base.Article;
import openpkm.base.Book;
import openpkm.base.BulletIconProvider;
import openpkm.base.Document;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.TagsProvider;
import openpkm.base.Video;
import openpkm.base.Link;
import openpkm.base.PropertiesProvider;
import openpkm.base.TitleProvider;
import openpkm.jcef.CefClientProvider;
import openpkm.utils.DisplayNameProviderImpl;
import org.cef.browser.CefBrowser;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.UndoRedo;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 *
 * @author Rok Koren
 */
public abstract class AbstractRaindrop implements Raindrop, IconProvider, TagsProvider, MultiViewDescription
{
    private static final Logger LOG = Logger.getLogger(AbstractRaindrop.class.getName());  
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");     
         
    protected final Properties props;    
    protected final PropertyChangeSupport propertyChangeSupport;
    
    protected Lookup lkp; 
    protected SourceState state;    

    public AbstractRaindrop(Properties props) 
    {
        this.props = props;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    @Override
    public Lookup getLookup() 
    {
        if (lkp == null) 
        { 
            lkp = Lookups.fixed(this, new DisplayNameProviderImpl(this), new BulletIconProviderImpl());              
        }
        return lkp;
    }          
    
    @Override
    public SourceState getState()
    {
        return state;
    }

    @Override
    public void markModified()
    {
        SourceState oldValue = getState();
        state = SourceState.MODIFIED;
        propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
    }   
    
    @Override
    public void notifyDeleted()
    {
        SourceState oldValue = getState();
        state = SourceState.DELETED;
        propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
    }  
    
    @Override
    public int getRaindropID() 
    {
        String raindropID = props.getProperty(PROPS_RAINDROP_ID);
        return Integer.parseInt(raindropID);
    }  
    
    @Override
    public String getSourceID()
    {
        return props.getProperty(PROPS_RAINDROP_ID);
    }
    
    @Override
    public String getAppID()
    {
        return props.getProperty(PROP_APP_ID);
    }    
    
    @Override
    public LocalDateTime getTimeCreated() 
    {
        String created = props.getProperty(PROP_TIME_CREATED);
        if(created != null)
        {
            return LocalDateTime.parse(created, FORMATTER);
        }
        return null;
    } 
    
    @Override
    public Properties getProperties()
    {
        return props;
    }
    
    @Override
    public void merge(PropertiesProvider provider)
    {
        props.putAll(provider.getProperties());
        markModified();
    }    
    
    @Override
    public RaindropUser getUser() 
    {
        String userID = props.getProperty(PROPS_RAINDROP_USER_ID);
        if(userID != null)
        {
            RaindropAccount account = RaindropService.getDefault().getAccount(Integer.parseInt(userID));   
            return account.getUser();
        }                    
        return null;
    }

    @Override
    public RaindropUser getCreator() 
    {
        String creatorID = props.getProperty(PROPS_RAINDROP_CREATOR_ID);
        if(creatorID != null)
        {
            RaindropAccount account = RaindropService.getDefault().getAccount(Integer.parseInt(creatorID));   
            return account.getUser();
        }                    
        return null;
    }

    @Override
    public RaindropCollection getCollection() 
    {
        String userID = props.getProperty(PROPS_RAINDROP_USER_ID);        
        String collectionID = props.getProperty(PROPS_RAINDROP_COLLECTION_ID);
        if(userID != null && collectionID != null)
        {
            RaindropAccount account = RaindropService.getDefault().getAccount(Integer.parseInt(userID)); 
            try
            {
                return account.getCollection(Integer.parseInt(collectionID));                
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }
        }                    
        return null;        
    }

    @Override
    public void setCollection(RaindropCollection collection) 
    {
        if(collection == null)
        {
            props.remove(PROPS_RAINDROP_COLLECTION_ID);
        }
        else
        {
            props.setProperty(PROPS_RAINDROP_COLLECTION_ID, collection.getCollectionID() + "");
        }
    }

    @Override
    public String getLink() 
    {
        return props.getProperty(PROPS_LINK);
    }

    @Override
    public void setLink(String link) 
    {
        if(link == null)
        {
            props.remove(PROPS_LINK);
        }
        else
        {
            props.setProperty(PROPS_LINK, link);
        }
    }

    @Override
    public String getTitle()
    {
        return props.getProperty(TitleProvider.PROP_TITLE);
    }

    @Override
    public void setTitle(String title)
    {
        if(title == null)
        {
            Object oldValue = props.remove(TitleProvider.PROP_TITLE);
            propertyChangeSupport.firePropertyChange(TitleProvider.PROP_TITLE, oldValue, title);
        }
        else
        {
            Object oldValue = props.setProperty(TitleProvider.PROP_TITLE, title);
            propertyChangeSupport.firePropertyChange(TitleProvider.PROP_TITLE, oldValue, title);
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

    @Override
    public String getExcerpt() 
    {
        return props.getProperty(PROPS_EXCERPT);
    }

    @Override
    public void setExcerpt(String excerpt) 
    {
        if(excerpt == null)
        {
            props.remove(PROPS_EXCERPT);
        }
        else
        {
            props.setProperty(PROPS_EXCERPT, excerpt);
        }
    }

    @Override
    public String getNote() 
    {
        return props.getProperty(PROPS_NOTE);
    }

    @Override
    public void setNote(String note) 
    {
        if(note == null)
        {
            props.remove(PROPS_NOTE);
        }
        else
        {
            props.setProperty(PROPS_NOTE, note);
        }
    }

    @Override
    public String getCover() 
    {
        return props.getProperty(PROPS_COVER);
    }

    @Override
    public void setCover(String cover) 
    {
        if(cover == null)
        {
            props.remove(PROPS_COVER);
        }
        else
        {
            props.setProperty(PROPS_COVER, cover);
        }
    }

    @Override
    public Set<String> getTags() 
    {
        String tags = props.getProperty(PROP_TAGS);
        if(tags != null)
        {
            return Set.of(tags.split(","));                   
        }                
        return Collections.EMPTY_SET;
    }

    @Override
    public boolean isImportant() 
    {
        String important = props.getProperty(PROPS_IMPORTANT);
        if(important != null)
        {
            return Boolean.parseBoolean(important);
        }
        return false;
    }

    @Override
    public void setImportant(boolean important) 
    {
        Object oldValue = props.setProperty(PROPS_IMPORTANT, Boolean.toString(important));
        if(oldValue != null)
        {
            oldValue = Boolean.parseBoolean(oldValue.toString());
        }
        propertyChangeSupport.firePropertyChange(PROPS_IMPORTANT, oldValue, important);
    }

    @Override
    public LocalDateTime getReminder() 
    {
        String reminder = props.getProperty(PROPS_REMINDER);
        if(reminder != null)
        {
            return LocalDateTime.parse(reminder, FORMATTER);
        }
        return null;
    }

    @Override
    public void setReminder(LocalDateTime reminder) 
    {
        if(reminder == null)
        {
            props.remove(PROPS_REMINDER);
        }
        else
        {
            props.setProperty(PROPS_REMINDER, reminder.format(FORMATTER));
        }
    }

    @Override
    public LocalDateTime getLastUpdate() 
    {
        String lastUpdate = props.getProperty(PROPS_LAST_UPDATE);
        if(lastUpdate != null)
        {
            return LocalDateTime.parse(lastUpdate, FORMATTER);
        }
        return null;
    }

    @Override
    public void setLastUpdate(LocalDateTime lastUpdate) 
    {
        if(lastUpdate == null)
        {
            props.remove(PROPS_LAST_UPDATE);
        }
        else
        {
            props.setProperty(PROPS_LAST_UPDATE, lastUpdate.format(FORMATTER));
        }
    }

    @Override
    public List<String> getHighlights() 
    {
        String highlights = props.getProperty(PROPS_HIGHLIGHTS);
        if(highlights != null)
        {
            return List.of(highlights.split(","));                   
        }                
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getDomain() 
    {
        return props.getProperty(PROPS_DOMAIN);
    }
    
    @Override
    public void setDomain(String domain)
    {
        if(domain == null)
        {
            props.remove(PROPS_DOMAIN);
        }
        else
        {
            props.setProperty(PROPS_DOMAIN, domain);
        }
    }

    @Override
    public boolean isRemoved() 
    {
        String removed = props.getProperty(PROPS_REMOVED);
        if(removed != null)
        {
            return Boolean.parseBoolean(removed);
        }
        return false;
    }

    @Override
    public void setRemoved(boolean removed) 
    {
        props.setProperty(PROPS_REMOVED, Boolean.toString(removed));
    } 

    @Override
    public Image getIcon(int type) 
    {    
        return ImageUtilities.loadImage(ICON); 
    }
    
    @Override
    public String preferredID() 
    {
        return "raindrop";
    }        

    @Override
    public MultiViewElement createElement() 
    {
        return new MultiViewElementImpl(this);
    }  

    @Override
    public HelpCtx getHelpCtx() 
    {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public String getDisplayName() 
    {
        return "Raindrop";
    }   

    @Override
    public int getPersistenceType() 
    {
        return TopComponent.PERSISTENCE_NEVER;
    }  

    @Override
    public Image getIcon() 
    {
        return ImageUtilities.loadImage(Raindrop.ICON);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof Raindrop)
        {
            Raindrop raindrop = (Raindrop)obj;
            boolean isID = raindrop.getRaindropID() == getRaindropID();
            boolean isType = raindrop.getType() == getType();
            boolean isRemoved = raindrop.isRemoved() == isRemoved();
            boolean isImportant = raindrop.isImportant() == isImportant();
            if(isID && isType && isRemoved && isImportant)
            {
                return true;
            }
        }
        return false;
    }    
    
    public static Raindrop getRaindrop(Properties props)
    {
        String name = props.getProperty(PROPS_TYPE);
        if(name != null)
        {
            Optional<Type> type = Type.get(name);
            if(type.isPresent())
            {
                if(type.get() == Type.ARTICLE)
                {
                    return new ArticleImpl(props);
                }  
                else if(type.get() == Type.VIDEO)
                {
                    return new VideoImpl(props);
                } 
                else if(type.get() == Type.LINK)
                {
                    return new LinkImpl(props);
                }
                else if(type.get() == Type.DOCUMENT)
                {
                    return new DocumentImpl(props);
                }  
                else if(type.get() == Type.BOOK)
                {
                    return new BookImpl(props);
                }                
            }           
        }
        return null;
    }

    public static final class ArticleImpl extends AbstractRaindrop implements Article
    {
        public ArticleImpl(Properties props) 
        {
            super(props);
        } 
        
        @Override
        public Type getType() 
        {
            return Raindrop.Type.ARTICLE;
        }          

        @Override
        public String getPublisher() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setPublisher(String publisher) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getLanguage() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setLanguage(String lang) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }  
    
    public static final class LinkImpl extends AbstractRaindrop implements Link
    {
        public LinkImpl(Properties props) 
        {
            super(props);
        }  
        
        @Override
        public Type getType() 
        {
            return Raindrop.Type.LINK;
        }          
    }  
    
    public static final class DocumentImpl extends AbstractRaindrop implements Document
    {
        private static final String FILE_TYPE_PDF = "application/pdf";

        private String fileName, fileType;
        private long fileSize;

        public DocumentImpl(Properties props) 
        {
            super(props);
        }  
        
        @Override
        public Type getType() 
        {
            return Raindrop.Type.DOCUMENT;
        }          

        public String getFileName() 
        {
            return fileName;
        }

        public void setFileName(String name)
        {
            fileName = name;
        }

        public long getFileSize() 
        {
            return fileSize;
        }

        public void setFileSize(long size)
        {
            fileSize = size;
        }

        public FileType getFileType() 
        {
            if(fileType.equals(FILE_TYPE_PDF))
            {
                return FileType.PDF;
            }
            return null;
        }

        public String getRaindropFileType()
        {
            return fileType;
        }

        public void setRaindropFileType(String type)
        {
            fileType = type;
        }

        @Override
        public String getSubtitle() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setSubtitle(String subtitle) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getAuthors() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setAuthors(String authors) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getInstitution() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setInstitution(String institution) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public LocalDate getPublishDate() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setPublishDate(LocalDate date) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getLanguage() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setLanguage(String lang) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    } 
    
    public static final class BookImpl extends AbstractRaindrop implements Book
    {
        private static final String FILE_TYPE_PDF = "application/pdf";

        private String fileName, fileType;
        private long fileSize;

        public BookImpl(Properties props) 
        {
            super(props);
        }  
        
        @Override
        public Type getType() 
        {
            return Raindrop.Type.DOCUMENT;
        }          

        public String getFileName() 
        {
            return fileName;
        }

        public void setFileName(String name)
        {
            fileName = name;
        }

        public long getFileSize() 
        {
            return fileSize;
        }

        public void setFileSize(long size)
        {
            fileSize = size;
        }

        public FileType getFileType() 
        {
            if(fileType.equals(FILE_TYPE_PDF))
            {
                return FileType.PDF;
            }
            return null;
        }

        public String getRaindropFileType()
        {
            return fileType;
        }

        public void setRaindropFileType(String type)
        {
            fileType = type;
        }

        @Override
        public String getSubtitle() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setSubtitle(String subtitle) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getAuthors() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setAuthors(String authors) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public LocalDate getPublishDate() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setPublishDate(LocalDate date) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getLanguage() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setLanguage(String lang) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getPublisher() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setPublisher(String publisher) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getISBN() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setISBN(String isbn) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getDescription()
        {
            return props.getProperty(PROP_DESCRIPTION);
        } 
        
        @Override
        public void setDescription(String description)
        {
            if(description == null)
            {
                Object oldValue = props.remove(PROP_DESCRIPTION);
                propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldValue, description);
            }
            else
            {
                Object oldValue = props.setProperty(PROP_DESCRIPTION, description);
                propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldValue, description);
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
    }     
    
    public static final class VideoImpl extends AbstractRaindrop implements Video
    {
        public VideoImpl(Properties props) 
        {
            super(props);
        } 
        
        @Override
        public Type getType() 
        {
            return Raindrop.Type.VIDEO;
        }         
    }         
    
    public static NotificationDisplayer.Priority getPriority(Raindrop raindrop)
    {
        if(raindrop.isImportant())
        {
            return NotificationDisplayer.Priority.NORMAL;
        }
        return NotificationDisplayer.Priority.SILENT;
    } 

    public static String getCategory(Raindrop raindrop)
    {
        if(raindrop instanceof Link)
        {
            return "Link-Category-Name";
        }
        else if(raindrop instanceof Video)
        {
            return "Video-Category-Name";
        }        
        return null;
    }     
    
    public enum FileType 
    {
        PDF("pdf"),
        EPUB("epub"),
        ASCIIDOC("asc"),
        MARKDOWN("mkd");

        private final String string;

        FileType(String string) 
        {
            this.string = string;
        }

        @Override
        public String toString() 
        {
            return string;
        }
        
        public static Optional<FileType> get(String string) 
        {
            return Arrays.stream(FileType.values())
                    .filter(type -> type.string.equalsIgnoreCase(string))
                    .findFirst();
        }     
    }  
    
    private final class BulletIconProviderImpl implements BulletIconProvider, PropertyChangeListener
    {
        private final ChangeSupport changeSupport; 
        
        public BulletIconProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(this);
        }          
        
        @Override
        public Image getBullet() 
        {
            if(getState() == SourceState.DELETED)
            {
                IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);            
                return provider.getImage(IconsProvider.ICON.BULLET_DELETE);                 
            }
            else if(isImportant())
            {
                IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);            
                return provider.getImage(IconsProvider.ICON.BULLET_STAR);                
            }
            return null;
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
            if(evt.getPropertyName().equals(PROP_STATE) || evt.getPropertyName().equals(PROPS_IMPORTANT))
            {
                changeSupport.fireChange();                
            }
        }        
    }
    
    private static final class MultiViewElementImpl extends JPanel implements MultiViewElement
    {
        private CefBrowser browser; 
        private JToolBar toolbar;
        
        private transient MultiViewElementCallback callback;  
        
        private final Raindrop raindrop;

        public MultiViewElementImpl(Raindrop raindrop) 
        {
            this.raindrop = raindrop;
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
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
            if(browser == null)
            {
                CefClientProvider provider = Lookup.getDefault().lookup(CefClientProvider.class);
                if(provider != null)
                {
                    try
                    {
                        browser = provider.getCefClient().createBrowser(raindrop.getLink(), false, false);   
                        if(browser != null)
                        {
                            add(browser.getUIComponent());
                        }
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
        public JComponent getToolbarRepresentation() 
        {
            if(toolbar == null)
            {
                toolbar = new JToolBar();
                /*
                JCheckBox watchLater = new JCheckBox("Watch Later");
                watchLater.setFocusable(false);
                watchLater.addItemListener(this);
                toolbar.add(watchLater);
                */
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
}
