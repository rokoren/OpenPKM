/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.Article;
import openpkm.base.IconProvider;
import openpkm.base.Link;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.base.WebPage;
import openpkm.base.WebPageProvider;
import openpkm.rss.Rss;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=WebPageProvider.class)
public class WebPageProviderImpl implements WebPageProvider 
{
    private static final Logger LOG = Logger.getLogger(WebPageProvider.class.getName());     
    
    @Override
    public WebPage getWebPage(Properties props) 
    {
        String name = props.getProperty(PROP_TYPE);
        if(name != null)
        {
            Optional<Type> type = Type.get(name);
            if(type.isPresent())
            {
                if(type.get() == Type.LINK)
                {
                    return new LinkImpl(props);
                }
                else if(type.get() == Type.RSS)
                {
                    return new RssImpl(props);
                }                 
                else if(type.get() == Type.ARTICLE)
                {
                    return new ArticleImpl(props);
                }                                 
            }
        }
        return null;
    }  
    
    private static abstract class AbstractWebPage implements WebPage, PropertiesProvider, IconProvider, TagsProvider, TopicsProvider, VisibilityProvider
    {         
        protected static final Logger LOG = Logger.getLogger(AbstractWebPage.class.getName());     

        protected final Properties props; 
        protected final PropertyChangeSupport propertyChangeSupport;
        protected final ChangeSupport changeSupport;  
        
        private boolean isDeleted;  

        public AbstractWebPage(Properties props) 
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
            LOG.info("Web Page Properties saved");      
        }     
    }  
    
    private static final class LinkImpl extends AbstractWebPage implements Link, TitleProvider
    { 
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/www_page.png";         
     
        public LinkImpl(Properties props)
        {
            super(props);
        }  
        
        @Override
        public String getSourceID()
        {
            return getTimeCreated().getNano() + "";
        }  
        
        @Override
        public String getLink() 
        {
            return props.getProperty(PROP_LINK);
        }         
        
        @Override
        public Document getDocument(String userAgent) throws IOException
        {
            return Jsoup.connect(getLink()).ignoreContentType(true).userAgent(userAgent).get();   
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
        public Image getIcon() 
        {  
            return ImageUtilities.loadImage(ICON);             
        }       
    }
    
    private static final class RssImpl extends AbstractWebPage implements Rss
    { 
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/rss.png";         
     
        public RssImpl(Properties props)
        {
            super(props);
        }  
        
        @Override
        public String getSourceID()
        {            
            return getRssID();
        } 

        @Override
        public String getRssID() 
        {
            return props.getProperty(PROP_RSS_ID);
        } 
        
        @Override
        public String getLink() 
        {
            return props.getProperty(PROP_LINK);
        }         
        
        @Override
        public Document getDocument(String userAgent) throws IOException
        {
            return Jsoup.connect(getLink()).ignoreContentType(true).userAgent(userAgent).get();   
        }         

        @Override
        public String getUri()
        {
            return props.getProperty(PROP_URI);
        }
                       
        @Override
        public LocalDateTime getPublishedDate() 
        {
            String string = props.getProperty(PROP_PUBLISHED_DATE);
            if(string != null)
            {
                return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
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
        public Image getIcon() 
        {  
            return ImageUtilities.loadImage(ICON);             
        } 
        
        @Override
        public boolean isWatchLater()
        {
            String string = props.getProperty(PROP_WATCH_LATER);
            if(string != null)
            {
                return Boolean.parseBoolean(string);
            }
            return false;
        }
        
        @Override
        public void setWatchLater(boolean watchLater)
        {
            Object oldValue = props.setProperty(PROP_WATCH_LATER, Boolean.toString(watchLater)); 
            if(oldValue != null)
            {
                oldValue = Boolean.parseBoolean(oldValue.toString());
            }
            propertyChangeSupport.firePropertyChange(PROP_WATCH_LATER, oldValue, watchLater);
        }        
    }    
    
    private static final class ArticleImpl extends AbstractWebPage implements Article, Link
    { 
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/www_page.png";        
        
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
        public String getLink() 
        {
            return props.getProperty(PROP_LINK);
        }         
        
        @Override
        public Document getDocument(String userAgent) throws IOException
        {
            return Jsoup.connect(getLink()).ignoreContentType(true).userAgent(userAgent).get();   
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
        public Image getIcon() 
        {  
            return ImageUtilities.loadImage(ICON);             
        } 
    }       
}
