/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import com.gluonhq.richtextarea.model.Decoration;
import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
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
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

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
                    return ImageUtilities.loadImage(EXT_PNG);                
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
    
    private static final class BookImpl extends AbstractReference implements Book, DescriptionProvider, PageProvider, TextProvider
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
            if(page == null)
            {
                props.remove(PROP_PAGE_NUMBER);
            }
            else
            {
                props.setProperty(PROP_PAGE_NUMBER, page.toString());
            }  
            setPageModified(true);
        }  
        
        @Override
        public boolean isPageModified()
        {
            return pageModified;
        }
        
        @Override
        public void setPageModified(boolean newValue)
        {
            boolean oldValue = pageModified;
            pageModified = newValue;
            propertyChangeSupport.firePropertyChange(PROP_PAGE_MODIFIED, oldValue, newValue);
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
            if(textOnPage == null || isPageModified())
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
    }
    
    private static final class ArticleImpl extends AbstractReference implements Article, PageProvider
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
            setPageModified(true);
        }  
        
        @Override
        public boolean isPageModified()
        {
            return pageModified;
        }
        
        @Override
        public void setPageModified(boolean newValue)
        {
            boolean oldValue = pageModified;
            pageModified = newValue;
            pcs.firePropertyChange(PROP_PAGE_MODIFIED, oldValue, newValue);
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
    }    
    
    private static final class DocumentImpl extends AbstractReference implements Document, PageProvider
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
            setPageModified(true);
        }  
        
        @Override
        public boolean isPageModified()
        {
            return pageModified;
        }
        
        @Override
        public void setPageModified(boolean newValue)
        {
            boolean oldValue = pageModified;
            pageModified = newValue;
            pcs.firePropertyChange(PROP_PAGE_MODIFIED, oldValue, newValue);
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
    }  
    
    public static final class PictureImpl extends AbstractReference implements Picture
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
    }   
    
    private static final class VideoImpl extends AbstractReference implements Video
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
    }     
}
