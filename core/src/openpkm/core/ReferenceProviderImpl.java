/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import com.dlsc.pdfviewfx.PDFView;
import com.gluonhq.richtextarea.model.Decoration;
import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.event.ChangeListener;
import openpkm.base.Article;
import openpkm.base.Book;
import openpkm.base.DescriptionProvider;
import openpkm.base.Document;
import openpkm.base.IconProvider;
import openpkm.base.PageProvider;
import openpkm.base.Picture;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.TopicsProvider;
import openpkm.base.Video;
import openpkm.base.VisibilityProvider;
import openpkm.javafx.TextProvider;
import openpkm.reference.AbstractFilesProvider;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import openpkm.reference.StyledTextFragment;
import openpkm.reference.StyledTextStripper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=ReferenceProvider.class)
public class ReferenceProviderImpl implements ReferenceProvider
{
    private static final Logger LOG = Logger.getLogger(ReferenceProvider.class.getName());     

    @Override
    public Reference getReference(Properties props) 
    {
        String name = props.getProperty(PROP_TYPE);
        if(name != null)
        {
            Optional<Type> type = Type.get(name);
            if(type.isPresent())
            {
                if(type.get() == Type.BOOK)
                {
                    return new BookImpl(props);
                }
                else if(type.get() == Type.ARTICLE)
                {
                    return new ArticleImpl(props);
                }                  
                else if(type.get() == Type.DOCUMENT)
                {
                    return new DocumentImpl(props);
                }                
                else if(type.get() == Type.PICTURE)
                {
                    return new PictureImpl(props);
                } 
                else if(type.get() == Type.VIDEO)
                {
                    return new VideoImpl(props);
                }                 
            }
        }
        return null;
    }
    
    private static abstract class AbstractReference implements Reference, PropertiesProvider, TitleProvider, IconProvider, TagsProvider, TopicsProvider, VisibilityProvider
    {
        public static final String EXT_GIF = "gif";
        public static final String EXT_JPG = "jpg";
        public static final String EXT_PNG = "png";    
        public static final String EXT_PDF = "pdf";
        public static final String EXT_MP4 = "mp4";    

        @StaticResource()
        public static final String ICON_GIF = "openpkm/core/resources/file_extension_gif.png";   

        @StaticResource()
        public static final String ICON_JPG = "openpkm/core/resources/file_extension_jpg.png";  

        @StaticResource()
        public static final String ICON_PNG = "openpkm/core/resources/file_extension_png.png";      

        @StaticResource()
        public static final String ICON_PDF = "openpkm/core/resources/file_extension_pdf.png";   

        @StaticResource()
        public static final String ICON_MP4 = "openpkm/core/resources/file_extension_mp4.png";  

        protected static final Logger LOG = Logger.getLogger(AbstractReference.class.getName());     

        protected final Properties props; 
        protected final PropertyChangeSupport propertyChangeSupport;
        protected final ChangeSupport changeSupport;  
        
        private boolean isDeleted;  

        public AbstractReference(Properties props) 
        {
            this.props = props;
            propertyChangeSupport = new PropertyChangeSupport(this);
            changeSupport = new ChangeSupport(this);                
        }

        @Override
        public Properties getProperties()
        {
            return props;
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
        public String getAppID()
        {
            return props.getProperty(PROP_APP_ID);
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
        public LocalDateTime getTimeCreated() 
        {
            String created = props.getProperty(PROP_TIME_CREATED);
            if(created != null)
            {
                return LocalDateTime.parse(created, DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;
        }     

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
                props.remove(PROP_TITLE);
            }
            else
            {
                props.setProperty(PROP_TITLE, title);
            }
        }  
        
        @Override
        public String getDataFileExtension() 
        {
            return props.getProperty(PROP_DATA_FILE_EXTENSION);
        }

        @Override
        public void setDataFileExtension(String extension) 
        {
            if(extension == null)
            {
                props.remove(PROP_DATA_FILE_EXTENSION);
            }
            else
            {
                props.setProperty(PROP_DATA_FILE_EXTENSION, extension);
            }
        }          

        @Override
        public Image getIcon() 
        { 
            String nameExt = props.getProperty(PROP_FILE_EXT);
            if(nameExt != null)
            {
                if(nameExt.equalsIgnoreCase(EXT_GIF))
                {
                    return ImageUtilities.loadImage(ICON_GIF);                
                }
                else if(nameExt.equalsIgnoreCase(EXT_JPG))
                {
                    return ImageUtilities.loadImage(ICON_JPG);                
                } 
                else if(nameExt.equalsIgnoreCase(EXT_PNG))
                {
                    return ImageUtilities.loadImage(ICON_PNG);                
                }             
                else if(nameExt.equalsIgnoreCase(EXT_MP4))
                {
                    return ImageUtilities.loadImage(ICON_MP4);                
                }  
                else if(nameExt.equalsIgnoreCase(EXT_PDF))
                {
                    return ImageUtilities.loadImage(ICON_PDF);                
                }             
            }    
            return null;
        } 

        @Override
        public List<String> getTags()
        {
            if(props.containsKey(PROP_TAGS))
            {
                String string = props.getProperty(PROP_TAGS);
                return List.of(string.split(","));
            }   
            return Collections.EMPTY_LIST;
        } 

        @Override
        public List<String> getTopics()
        {
            String topics = props.getProperty(PROP_TOPICS);
            if(topics != null)
            {
                return List.of(topics.split(","));                   
            }                
            return Collections.EMPTY_LIST;
        }

        @Override
        public VisibilityProvider.Modifier getModifier()
        {
            String name = props.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
            if(name != null)
            {
                Optional<VisibilityProvider.Modifier> optional = VisibilityProvider.Modifier.get(name);
                if(optional.isPresent())
                {
                    return optional.get();
                }
            }
            return VisibilityProvider.Modifier.NONE;
        }

        @Override
        public void setModifier(VisibilityProvider.Modifier modifier)
        {
            if(modifier == null)
            {
                props.remove(VisibilityProvider.PROP_VISIBILITY_MODIFIER);         
            }
            else
            {
                props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, modifier.toString());  
            }
        }     

        @Override
        public void save(OutputStream os, String comments) throws IOException
        {
            props.store(os, comments); 
            LOG.info("Reference Properties saved");      
        }  

        protected FileObject getFile(AbstractFilesProvider provider) throws IOException
        {
            String filePath = props.getProperty(PROP_FILE_PATH);
            if(filePath == null)
            {
                throw new IOException("File path not set");
            }            
            return provider.getFile(filePath);
        }  

        public void setFile(FileObject file, AbstractFilesProvider provider) throws IOException
        {
            if(file == null)
            {
                props.remove(PROP_FILE_PATH);
                props.remove(PROP_FILE_NAME);
                props.remove(PROP_FILE_EXT);
            }
            else
            {                 
                props.setProperty(PROP_FILE_NAME, file.getName());
                props.setProperty(PROP_FILE_EXT, file.getExt());
                props.setProperty(PROP_FILE_PATH, provider.getRelativePath(file));                                              
            }
        }    
    } 
    
    private static final class BookImpl extends AbstractReference implements Book, DescriptionProvider, PageProvider, TextProvider, MultiViewDescription
    { 
        private static final String PROP_WEB_PAGE = "web.page";         
        private static final String PROP_GITHUB   = "github";         
        
        private PDDocument document;
        private com.gluonhq.richtextarea.model.Document textOnPage;
        private boolean pageModified;         
     
        public BookImpl(Properties props)
        {
            super(props);
        } 
        
        @Override
        public String getSourceID()
        {
            return getISBN();
        }
        
        @Override
        public FileObject getFile() throws IOException
        {          
            return getFile(AbstractFilesProvider.BOOKS);
        }  
        
        @Override
        public void setFile(FileObject file) throws IOException
        {
            setFile(file, AbstractFilesProvider.BOOKS);
        }  
        
        @Override
        public Integer getPageNumber()
        {
            String string = props.getProperty(PROP_PAGE_NUMBER);
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
        public void setPageNumber(Integer page)
        {
            String string = props.getProperty(PROP_PAGE_NUMBER);
            Integer oldValue = null;
            if(string != null)
            {
                try
                {
                    oldValue = Integer.parseInt(string);                    
                }
                catch(NumberFormatException e)
                {
                    LOG.warning(e.getMessage());
                }
            }
            if(page == null)
            {
                props.remove(PROP_PAGE_NUMBER);
            }
            else
            {
                props.setProperty(PROP_PAGE_NUMBER, page.toString());
            }  
            propertyChangeSupport.firePropertyChange(PROP_PAGE_NUMBER, oldValue, page);
        }          
        
        public String getWebPage()
        {
            return props.getProperty(PROP_WEB_PAGE);
        }
        
        public void setWebPage(String url)
        {
            if(url == null)
            {
                props.remove(PROP_WEB_PAGE);
            }
            else
            {
                props.setProperty(PROP_WEB_PAGE, url);
            }            
        }
        
        public String getGitHub()
        {
            return props.getProperty(PROP_GITHUB);
        }
        
        public void setGitHub(String url)
        {
            if(url == null)
            {
                props.remove(PROP_GITHUB);
            }
            else
            {
                props.setProperty(PROP_GITHUB, url);
            }            
        }        
        
        @Override
        public String getSubtitle() 
        {
            return props.getProperty(PROP_SUBTITLE);
        }

        @Override
        public void setSubtitle(String subtitle)
        {
            if(subtitle == null)
            {
                props.remove(PROP_SUBTITLE);
            }
            else
            {
                props.setProperty(PROP_SUBTITLE, subtitle);
            }
        }

        @Override
        public String getAuthors() 
        {
            return props.getProperty(PROP_AUTHORS);
        }

        @Override
        public void setAuthors(String authors) 
        {
            if(authors == null)
            {
                props.remove(PROP_AUTHORS);
            }
            else
            {
                props.setProperty(PROP_AUTHORS, authors);
            }
        }

        @Override
        public String getPublisher() 
        {
            return props.getProperty(PROP_PUBLISHER);
        }

        @Override
        public void setPublisher(String publisher) 
        {
            if(publisher == null)
            {
                props.remove(PROP_PUBLISHER);
            }
            else
            {
                props.setProperty(PROP_PUBLISHER, publisher);
            }
        }

        @Override
        public LocalDate getPublishDate() 
        {
            String string = props.getProperty(PROP_PUBLISH_DATE);
            if(string != null)
            {
                return LocalDate.parse(string, DateTimeFormatter.ISO_DATE);
            }
            return null;
        }

        @Override
        public void setPublishDate(LocalDate date) 
        {
            if(date == null)
            {
                props.remove(PROP_PUBLISH_DATE);
            }
            else
            {
                props.setProperty(PROP_PUBLISH_DATE, date.format(DateTimeFormatter.ISO_DATE));
            }
        }

        @Override
        public String getLanguage() 
        {
            return props.getProperty(PROP_LANGUAGE);
        }

        @Override
        public void setLanguage(String lang)
        {
            if(lang == null)
            {
                props.remove(PROP_LANGUAGE);
            }
            else
            {
                props.setProperty(PROP_LANGUAGE, lang);
            }
        }

        @Override
        public String getISBN() 
        {
            return props.getProperty(PROP_ISBN);
        }

        @Override
        public void setISBN(String isbn) 
        {
            if(isbn == null)
            {
                props.remove(PROP_ISBN);
            }
            else
            {
                props.setProperty(PROP_ISBN, isbn);
            }
        }
        
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
                props.remove(PROP_DESCRIPTION);
            }
            else
            {
                props.setProperty(PROP_DESCRIPTION, desc);
            }
        }                

        @Override
        public com.gluonhq.richtextarea.model.Document getTextOnPage()
        { 
            if(textOnPage == null)
            {
                int page = getPageNumber() + 1;
                try
                {
                    StyledTextStripper stripper = new StyledTextStripper();
                    stripper.setStartPage(page);
                    stripper.setEndPage(page);   
                    String fulltText = stripper.getText(getDocument());
                    List<StyledTextFragment> fragments = stripper.getFragments();
                    StringBuilder text = new StringBuilder();
                    List<DecorationModel> decorations = new ArrayList<>(); 
                    ParagraphDecoration left535 = ParagraphDecoration.builder().presets().alignment(TextAlignment.LEFT).topInset(5).bottomInset(3).spacing(5).build();                    
                    ParagraphDecoration right22 = ParagraphDecoration.builder().presets().alignment(TextAlignment.RIGHT).topInset(2).bottomInset(2).build();                                        
                    int offset = 0;
                    for (StyledTextFragment frag : fragments) {
                        String txt = frag.text;
                        TextPosition tp = frag.position;

                        // Zgradi besedilo
                        text.append(txt);

                        // Ustvari dekoracijo
                        FontWeight fontWeight = tp.getFont().getFontDescriptor().isForceBold() ? FontWeight.BOLD : FontWeight.NORMAL;                    
                        FontPosture fontStyle = tp.getFont().getFontDescriptor().isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR;
                        float fontSize = tp.getFontSizeInPt();
                        //String fontFamily = tp.getFont().getFontDescriptor().getFontFamily();
                        Decoration decoration = TextDecoration.builder().presets().fontPosture(fontStyle).fontWeight(fontWeight).fontSize(fontSize).build();

                        decorations.add(new DecorationModel(offset, txt.length(), decoration, left535));
                        offset += txt.length();
                    } 
                    textOnPage = new com.gluonhq.richtextarea.model.Document(text.toString(), decorations, 0);                    
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }
            }
            return textOnPage;
        }        
        
        private PDDocument getDocument() throws IOException
        {
            if(document == null)
            {
                document = Loader.loadPDF(getFile().asBytes()); 
            }
            return document;
        }
        
        @Override
        public String preferredID() 
        {
            return "book";
        }        
        
        @Override
        public MultiViewElement createElement() 
        {
            return new PdfMultiViewElement(this);
        }  

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Book";
        }   
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }         
    }
    
    private static final class ArticleImpl extends AbstractReference implements Article, PageProvider, MultiViewDescription
    { 
        private final PropertyChangeSupport pcs;
        
        private boolean pageModified;                
        
        public ArticleImpl(Properties props)
        {
            super(props);
            pcs = new PropertyChangeSupport(this);
        } 
        
        @Override
        public String getSourceID()
        {
            return getTimeCreated().getNano() + "";
        }
        
        @Override
        public FileObject getFile() throws IOException
        {          
            return getFile(AbstractFilesProvider.ARTICLES);
        }  
        
        @Override
        public void setFile(FileObject file) throws IOException
        {
            setFile(file, AbstractFilesProvider.ARTICLES);
        }  
        
        @Override
        public Integer getPageNumber()
        {
            String string = props.getProperty(PROP_PAGE_NUMBER);
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
        public void setPageNumber(Integer page)
        {
            if(page == null)
            {
                props.remove(PROP_PAGE_NUMBER);
            }
            else
            {
                props.setProperty(PROP_PAGE_NUMBER, page.toString());
            }  
        }          

        @Override
        public String getPublisher() 
        {
            return props.getProperty(PROP_PUBLISHER);
        }

        @Override
        public void setPublisher(String publisher) 
        {
            if(publisher == null)
            {
                props.remove(PROP_PUBLISHER);
            }
            else
            {
                props.setProperty(PROP_PUBLISHER, publisher);
            }
        }

        @Override
        public String getLanguage() 
        {
            return props.getProperty(PROP_LANGUAGE);
        }

        @Override
        public void setLanguage(String lang)
        {
            if(lang == null)
            {
                props.remove(PROP_LANGUAGE);
            }
            else
            {
                props.setProperty(PROP_LANGUAGE, lang);
            }
        } 
        
        @Override
        public String preferredID() 
        {
            return "article";
        }        
        
        @Override
        public MultiViewElement createElement() 
        {
            return new PdfMultiViewElement(this);
        }  

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Article";
        }   
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }         
    }    
    
    private static final class DocumentImpl extends AbstractReference implements Document, PageProvider, MultiViewDescription
    { 
        private final PropertyChangeSupport pcs;
        
        private boolean pageModified;           
        
        public DocumentImpl(Properties props)
        {
            super(props);
            pcs = new PropertyChangeSupport(this);
        }   
        
        @Override
        public String getSourceID()
        {
            return getTimeCreated().getNano() + "";
        }        

        @Override
        public FileObject getFile() throws IOException
        {          
            return getFile(AbstractFilesProvider.DOCUMENTS);
        }  
        
        @Override
        public void setFile(FileObject file) throws IOException
        {
            setFile(file, AbstractFilesProvider.DOCUMENTS);
        }  
        
        @Override
        public Integer getPageNumber()
        {
            String string = props.getProperty(PROP_PAGE_NUMBER);
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
        public void setPageNumber(Integer page)
        {
            if(page == null)
            {
                props.remove(PROP_PAGE_NUMBER);
            }
            else
            {
                props.setProperty(PROP_PAGE_NUMBER, page.toString());
            }  
        }          
        
        @Override
        public String getSubtitle() 
        {
            return props.getProperty(PROP_SUBTITLE);
        }

        @Override
        public void setSubtitle(String subtitle)
        {
            if(subtitle == null)
            {
                props.remove(PROP_SUBTITLE);
            }
            else
            {
                props.setProperty(PROP_SUBTITLE, subtitle);
            }
        }

        @Override
        public String getAuthors() 
        {
            return props.getProperty(PROP_AUTHORS);
        }

        @Override
        public void setAuthors(String authors) 
        {
            if(authors == null)
            {
                props.remove(PROP_AUTHORS);
            }
            else
            {
                props.setProperty(PROP_AUTHORS, authors);
            }
        }

        @Override
        public String getInstitution() 
        {
            return props.getProperty(PROP_INSTITUTION);
        }

        @Override
        public void setInstitution(String institution) 
        {
            if(institution == null)
            {
                props.remove(PROP_INSTITUTION);
            }
            else
            {
                props.setProperty(PROP_INSTITUTION, institution);
            }
        }

        @Override
        public LocalDate getPublishDate() 
        {
            String string = props.getProperty(PROP_PUBLISH_DATE);
            if(string != null)
            {
                return LocalDate.parse(string, DateTimeFormatter.ISO_DATE);
            }
            return null;
        }

        @Override
        public void setPublishDate(LocalDate date) 
        {
            if(date == null)
            {
                props.remove(PROP_PUBLISH_DATE);
            }
            else
            {
                props.setProperty(PROP_PUBLISH_DATE, date.format(DateTimeFormatter.ISO_DATE));
            }
        }

        @Override
        public String getLanguage() 
        {
            return props.getProperty(PROP_LANGUAGE);
        }

        @Override
        public void setLanguage(String lang)
        {
            if(lang == null)
            {
                props.remove(PROP_LANGUAGE);
            }
            else
            {
                props.setProperty(PROP_LANGUAGE, lang);
            }
        }  
        
        @Override
        public String preferredID() 
        {
            return "document";
        }        
        
        @Override
        public MultiViewElement createElement() 
        {
            return new PdfMultiViewElement(this);
        }  

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Document";
        }   
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }          
    }  
    
    public static final class PictureImpl extends AbstractReference implements Picture, MultiViewDescription
    {        
        public PictureImpl(Properties props)
        {
            super(props);
        } 
        
        @Override
        public String getSourceID()
        {
            return getTimeCreated().getNano() + "";
        }         
        
        @Override
        public FileObject getFile() throws IOException
        {          
            return getFile(AbstractFilesProvider.PICTURES);
        }  
        
        @Override
        public void setFile(FileObject file) throws IOException
        {
            setFile(file, AbstractFilesProvider.PICTURES);
        }  
        
        @Override
        public String preferredID() 
        {
            return "picture";
        }        
        
        @Override
        public MultiViewElement createElement() 
        {
            return new ImageMultiViewElement(this);
        }  

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Picture";
        }   
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }         
    }   
    
    private static final class VideoImpl extends AbstractReference implements Video, MultiViewDescription
    {        
        public VideoImpl(Properties props)
        {
            super(props);
        } 
        
        @Override
        public String getSourceID()
        {
            return getTimeCreated().getNano() + "";
        }         

        @Override
        public FileObject getFile() throws IOException
        {          
            return getFile(AbstractFilesProvider.VIDEOS);
        }  
        
        @Override
        public void setFile(FileObject file) throws IOException
        {
            setFile(file, AbstractFilesProvider.VIDEOS);
        }  
        
        @Override
        public String preferredID() 
        {
            return "video";
        }        
        
        @Override
        public MultiViewElement createElement() 
        {
            return new VideoMultiViewElement(this);
        }  

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Video";
        }   
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }          
    }  
    
    private static final class PdfMultiViewElement extends JFXPanel implements MultiViewElement, ItemListener
    {        
        private PDFView pdfView; 
        private JToolBar toolBar;
        private JCheckBox toolbar, thumbnails;            
        
        private transient MultiViewElementCallback callback;  
        
        private final Reference reference;  

        public PdfMultiViewElement(Reference reference) 
        {
            this.reference = reference;
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
            if(pdfView == null)
            {
                pdfView = new PDFView();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() 
                    {
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
                        setScene(scene);
                    }
                }); 
            }
            return this;
        }

        @Override
        public JComponent getToolbarRepresentation() 
        {
            if(toolBar ==  null)
            {
                toolBar = new JToolBar(JToolBar.HORIZONTAL);
                toolbar = new JCheckBox("Toolbar");
                toolbar.addItemListener(this);
                thumbnails = new JCheckBox("Thumbnails");
                thumbnails.addItemListener(this);       
                toolBar.add(toolbar);
                toolBar.add(thumbnails);
            }
            return toolBar;
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
            Platform.runLater(new Runnable() 
            {
                @Override
                public void run() 
                {
                    try
                    {
                        pdfView.load(reference.getFile().getInputStream());  
                        if(reference instanceof PageProvider)
                        {
                            PageProvider provider = (PageProvider)reference;
                            Integer page = provider.getPageNumber();
                            if(page != null)
                            {
                                pdfView.setPage(page);                               
                            }
                            pdfView.pageProperty().addListener(l -> updateCurrentPageNumber());                          
                        }                       
                    }
                    catch(FileNotFoundException e)
                    {
                        LOG.warning(e.getMessage());
                    }     
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }                 
                }
            });            
        }

        @Override
        public void componentClosed() 
        {
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
        
        private void updateCurrentPageNumber()
        {
            if(reference instanceof PageProvider)
            {
                PageProvider provider = (PageProvider)reference;
                provider.setPageNumber(pdfView.getPage());
            }
        }         
        
        @Override
        public void itemStateChanged(ItemEvent evt) 
        {
            boolean isSelected = evt.getStateChange() == ItemEvent.SELECTED;
            if(evt.getSource() == toolbar)
            {
                pdfView.setShowToolBar(isSelected);
            }
            else if(evt.getSource() == thumbnails)
            {
                pdfView.setShowThumbnails(isSelected);
            }
        }        
    } 
    
    private static final class ImageMultiViewElement extends JFXPanel implements MultiViewElement
    {        
        private ImageView imageView;       
        
        private transient MultiViewElementCallback callback;  
        
        private final Reference reference;  

        public ImageMultiViewElement(Reference reference) 
        {
            this.reference = reference;
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
            if(imageView == null)
            {
                imageView = new ImageView();
                Platform.runLater(new Runnable() 
                {
                    @Override
                    public void run() 
                    {
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
                        setScene(scene);
                    }
                }); 
            }
            return this;
        }

        @Override
        public JComponent getToolbarRepresentation() 
        {
            return new JToolBar();
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
            Platform.runLater(new Runnable() 
            {
                @Override
                public void run() 
                {
                    try
                    {
                        javafx.scene.image.Image image = new javafx.scene.image.Image(reference.getFile().getInputStream());                
                        imageView.setImage(image); 
                    }
                    catch(FileNotFoundException e)
                    {
                        LOG.warning(e.getMessage());
                    }                  
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }                                 
                }
            });             
        }

        @Override
        public void componentClosed() 
        {
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
    
    private static final class VideoMultiViewElement extends JFXPanel implements MultiViewElement
    {        
        private MediaPlayer mediaPlayer;
        
        private transient MultiViewElementCallback callback;  
        
        private final Reference reference;  

        public VideoMultiViewElement(Reference reference) 
        {
            this.reference = reference;
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
            if(mediaPlayer == null)
            {
                try
                {
                    Media media = new Media(reference.getFile().toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    Platform.runLater(new Runnable() 
                    {
                        @Override
                        public void run() 
                        {
                            MediaView mediaView = new MediaView(mediaPlayer);                    
                            BorderPane borderPane = new BorderPane();
                            borderPane.setCenter(mediaView);  
                            mediaView.fitWidthProperty().bind(borderPane.widthProperty());
                            mediaView.fitHeightProperty().bind(borderPane.heightProperty());  
                            mediaView.setPreserveRatio(false);
                            Scene scene = new Scene(borderPane);

                            mediaView.setOnMouseClicked(event -> {
                                //if (event.isPrimaryButtonDown()) 
                                {
                                    if(mediaPlayer.getStatus() == Status.PLAYING)
                                    {
                                        mediaPlayer.pause();
                                    }
                                    else
                                    {
                                        mediaPlayer.play();
                                    }
                                }
                            });                   

                            setScene(scene); 
                        }
                    });                    
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }
            }
            return this;
        }

        @Override
        public JComponent getToolbarRepresentation() 
        {
            return new JToolBar();
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
            /*
            Platform.runLater(new Runnable() 
            {
                @Override
                public void run() 
                {
                    mediaPlayer.play(); 
                }
            });
            */
        }

        @Override
        public void componentClosed() 
        {
            Platform.runLater(new Runnable() 
            {
                @Override
                public void run() 
                {
                    mediaPlayer.stop();
                    mediaPlayer.dispose(); 
                }
            });              
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
