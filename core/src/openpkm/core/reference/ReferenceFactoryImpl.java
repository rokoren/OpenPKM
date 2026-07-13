/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.reference;

import com.dlsc.pdfviewfx.PDFView;
import com.gluonhq.richtextarea.model.Decoration;
import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.BeanInfo;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import openpkm.base.Article;
import openpkm.base.Book;
import openpkm.base.Document;
import openpkm.base.IconProvider;
import openpkm.base.PageProvider;
import openpkm.base.Picture;
import openpkm.base.Video;
import openpkm.javafx.TextProvider;
import openpkm.reference.AbstractFilesProvider;
import openpkm.reference.Reference;
import openpkm.reference.StyledTextFragment;
import openpkm.reference.StyledTextStripper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import openpkm.reference.ReferenceFactory;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=ReferenceFactory.class)
public class ReferenceFactoryImpl implements ReferenceFactory
{
    private static final Logger LOG = Logger.getLogger(ReferenceFactory.class.getName());     

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
    
    private static final class BookImpl extends AbstractReference implements Book, PageProvider, TextProvider, MultiViewDescription
    { 
        private static final String PROP_WEB_PAGE = "web.page";         
        private static final String PROP_GITHUB   = "github";         
        
        private PDDocument document;
        private com.gluonhq.richtextarea.model.Document textOnPage;     
     
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
                Object oldValue = props.remove(PROP_PAGE_NUMBER);
                if(oldValue != null)
                {
                    oldValue = Integer.parseInt(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_PAGE_NUMBER, oldValue, page);                
            }
            else
            {
                Object oldValue = props.setProperty(PROP_PAGE_NUMBER, page.toString());
                if(oldValue != null)
                {
                    oldValue = Integer.parseInt(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_PAGE_NUMBER, oldValue, page);                 
            }  
            markModified();
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

        @Override
        public Image getIcon()
        {
            IconProvider provider = getLookup().lookup(IconProvider.class);
            return provider.getIcon(BeanInfo.ICON_COLOR_16x16);
        }
    }
    
    private static final class ArticleImpl extends AbstractReference implements Article, PageProvider, MultiViewDescription
    {                      
        public ArticleImpl(Properties props)
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
                Object oldValue = props.remove(PROP_PAGE_NUMBER);
                if(oldValue != null)
                {
                    oldValue = Integer.parseInt(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_PAGE_NUMBER, oldValue, page);                
            }
            else
            {
                Object oldValue = props.setProperty(PROP_PAGE_NUMBER, page.toString());
                if(oldValue != null)
                {
                    oldValue = Integer.parseInt(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_PAGE_NUMBER, oldValue, page);                 
            }  
            markModified();
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
        
        @Override
        public Image getIcon()
        {
            IconProvider provider = getLookup().lookup(IconProvider.class);
            return provider.getIcon(BeanInfo.ICON_COLOR_16x16);
        }        
    }    
    
    private static final class DocumentImpl extends AbstractReference implements Document, PageProvider, MultiViewDescription
    {                   
        public DocumentImpl(Properties props)
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
                Object oldValue = props.remove(PROP_PAGE_NUMBER);
                if(oldValue != null)
                {
                    oldValue = Integer.parseInt(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_PAGE_NUMBER, oldValue, page);                
            }
            else
            {
                Object oldValue = props.setProperty(PROP_PAGE_NUMBER, page.toString());
                if(oldValue != null)
                {
                    oldValue = Integer.parseInt(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_PAGE_NUMBER, oldValue, page);                 
            }  
            markModified();
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

        @Override
        public Image getIcon()
        {
            IconProvider provider = getLookup().lookup(IconProvider.class);
            return provider.getIcon(BeanInfo.ICON_COLOR_16x16);
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
        
        @Override
        public Image getIcon()
        {
            IconProvider provider = getLookup().lookup(IconProvider.class);
            return provider.getIcon(BeanInfo.ICON_COLOR_16x16);
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

        @Override
        public Image getIcon()
        {
            IconProvider provider = getLookup().lookup(IconProvider.class);
            return provider.getIcon(BeanInfo.ICON_COLOR_16x16);
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
