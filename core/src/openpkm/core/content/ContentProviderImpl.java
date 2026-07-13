/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.content;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import openpkm.base.Article;
import openpkm.base.Book;
import openpkm.base.Content;
import openpkm.base.DescriptionProvider;
import openpkm.base.Document;
import openpkm.base.IconProvider;
import openpkm.base.Note;
import openpkm.base.PropertiesProvider;
import openpkm.base.SomedayMaybeProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.utils.BulletIconProviderImpl;
import openpkm.utils.DisplayNameProviderImpl;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import openpkm.base.ContentFactory;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=ContentFactory.class)
public class ContentProviderImpl implements ContentFactory
{
    private static final Logger LOG = Logger.getLogger(ContentFactory.class.getName());     
    
    @Override
    public Content getContent(Properties props) 
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
                else if(type.get() == Type.NOTE)
                {
                    return new NoteImpl(props);
                } 
                else if(type.get() == Type.IDEA)
                {
                    return new Idea(props);
                }  
                else if(type.get() == Type.COMMENT)
                {
                    return new Comment(props);
                }                 
            }
        }
        return null;
    }  
    
    private static abstract class AbstractContent implements Content, TitleProvider, PropertiesProvider, IconProvider, TagsProvider, TopicsProvider, VisibilityProvider
    {          
        protected static final Logger LOG = Logger.getLogger(AbstractContent.class.getName());     

        protected final Properties props; 
        protected final PropertyChangeSupport propertyChangeSupport;
        
        private Lookup lkp;   
        private SourceState state;        

        public AbstractContent(Properties props) 
        {
            this.props = props;
            propertyChangeSupport = new PropertyChangeSupport(this);             
        }
        
        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            { 
                lkp = Lookups.fixed(this, new DisplayNameProviderImpl(this), new BulletIconProviderImpl(this, propertyChangeSupport));              
            }
            return lkp;
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
        public Properties getProperties()
        {
            return props;
        }  
        
        @Override
        public void merge(PropertiesProvider provider)
        {
            props.putAll(provider.getProperties());
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
                return LocalDateTime.parse(created, DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;
        }      
        
        @Override
        public String getCreator()
        {
            return props.getProperty(PROP_CONTENT_CREATOR);
        }

        @Override
        public void setCreator(String creator)
        {
            if(creator == null)
            {
                props.remove(PROP_CONTENT_CREATOR);
            }
            else
            {
                props.setProperty(PROP_CONTENT_CREATOR, creator);
            }
        }                   

        @Override
        public Set<String> getTags()
        {
            if(props.containsKey(PROP_TAGS))
            {
                String string = props.getProperty(PROP_TAGS);
                return Set.of(string.split(","));
            }   
            return Collections.EMPTY_SET;
        } 

        @Override
        public Set<String> getTopics()
        {
            String topics = props.getProperty(PROP_TOPICS);
            if(topics != null)
            {
                return Set.of(topics.split(","));                   
            }                
            return Collections.EMPTY_SET;
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
            LOG.info("Content Properties saved");      
        }     
    }  
    
    private static final class BookImpl extends AbstractContent implements Book, DescriptionProvider
    { 
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/book.png";         
     
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
        public Image getIcon(int type) 
        {  
            return ImageUtilities.loadImage(ICON);             
        }       
    }
    
    private static final class ArticleImpl extends AbstractContent implements Article
    { 
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/document_image.png";        
        
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
        public Image getIcon(int type) 
        {  
            return ImageUtilities.loadImage(ICON);             
        } 
    }    
    
    private static final class DocumentImpl extends AbstractContent implements Document, TitleProvider
    { 
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/document_notes.png";         
        
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
        public Image getIcon(int type) 
        {  
            return ImageUtilities.loadImage(ICON);             
        }         
    } 

    private static final class NoteImpl extends AbstractContent implements Note, TitleProvider
    { 
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/note_pin.png";         
        
        public NoteImpl(Properties props)
        {
            super(props);
        } 
        
        @Override
        public String getSourceID()
        {
            return getTimeCreated().getNano() + "";
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
        public Image getIcon(int type) 
        {  
            return ImageUtilities.loadImage(ICON);             
        }         
    } 
    
    private static final class Idea extends AbstractContent implements Note, SomedayMaybeProvider, TitleProvider
    { 
        @StaticResource()
        public static final String ICON_ON = "openpkm/core/resources/lightbulb.png";
        
        @StaticResource()
        public static final String ICON_OFF = "openpkm/core/resources/lightbulb-off.png";         
        
        public Idea(Properties props)
        {
            super(props);
        } 
        
        @Override
        public String getSourceID()
        {
            return getTimeCreated().getNano() + "";
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
        public boolean isActive()
        {
            if(props.containsKey(PROP_TICKLE_DATE))
            {
                String date = props.getProperty(PROP_TICKLE_DATE);
                if(LocalDate.parse(date).isAfter(LocalDate.now()))
                {
                    return false;
                }
            }
            return true;  
        }         
        
        @Override
        public Image getIcon(int type) 
        { 
            if(isActive())
            {
                return ImageUtilities.loadImage(ICON_ON);
            }    
            return ImageUtilities.loadImage(ICON_OFF);           
        }         

        @Override
        public LocalDate getTickleDate() 
        {
            String string = props.getProperty(PROP_TICKLE_DATE);
            if(string != null)
            {
                return LocalDate.parse(string);
            }
            return null;
        }

        @Override
        public void setTickleDate(LocalDate date) 
        {
            if(date == null)
            {
                props.remove(PROP_TICKLE_DATE);
            }
            else
            {
                props.setProperty(PROP_TICKLE_DATE, date.format(DateTimeFormatter.ISO_DATE));
            }
        }
    }  
    
    private static final class Comment extends AbstractContent implements Note, TitleProvider
    { 
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/comment.png";        
        
        public Comment(Properties props)
        {
            super(props);
        } 
        
        @Override
        public String getSourceID()
        {
            return getTimeCreated().getNano() + "";
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
        public Image getIcon(int type) 
        {    
            return ImageUtilities.loadImage(ICON);           
        }         
    }     
}
