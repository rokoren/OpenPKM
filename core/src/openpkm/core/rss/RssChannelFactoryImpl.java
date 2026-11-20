/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.rss;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndFeed;
import java.awt.Image;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import openpkm.base.Channel;
import openpkm.base.DisplayNameProvider;
import openpkm.base.DisplayNameProvider.TextFormat;
import openpkm.base.DomainsProvider;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.rss.RssChannel;
import openpkm.utils.DateTimeUtils;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import openpkm.rss.RssChannelFactory;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=RssChannelFactory.class)
public class RssChannelFactoryImpl implements RssChannelFactory
{         
    @Override
    public RssChannel getChannel(Properties props) 
    {
        return new RssChannelImpl(props);
    }
    
    @Override
    public Properties getProperties(String feedUrl, SyndFeed feed, String fileName) 
    {        
        Properties props = new Properties();
                     
        LocalDateTime publishedDate = DateTimeUtils.convertToLocalDateTime(feed.getPublishedDate());
        props.setProperty(PROP_FEED_URL, feedUrl);
        props.setProperty(PROP_FILE_NAME, fileName);              
        props.setProperty(PROP_PUBLISHED_DATE, publishedDate.format(DateTimeFormatter.ISO_DATE_TIME));
        props.setProperty(PROP_TITLE, feed.getTitle());
        props.setProperty(PROP_DESCRIPTION, feed.getDescription());
        props.setProperty(PROP_LINK, feed.getLink());
        
        if(feed.getUri() != null)
        {
            props.setProperty(PROP_URI, feed.getUri());               
        }
     
        if(feed.getAuthor() != null)
        {
            props.setProperty(PROP_AUTHOR, feed.getAuthor());            
        }

        if(feed.getCopyright() != null)
        {
            props.setProperty(PROP_COPYRIGHT, feed.getCopyright());            
        }    
        
        if(feed.getGenerator() != null)
        {
            props.setProperty(PROP_GENERATOR, feed.getGenerator());            
        }

        if(feed.getLanguage() != null)
        {
            props.setProperty(PROP_LANGUAGE, feed.getLanguage());               
        }
     
        if(feed.getManagingEditor() != null)
        {
            props.setProperty(PROP_MANAGING_EDITOR, feed.getManagingEditor());            
        }
        
        if(feed.getImage() != null)
        {
            props.setProperty(PROP_IMAGE, feed.getImage().getUrl());              
        }
                
        if(feed.getIcon() != null)
        {
            props.setProperty(PROP_ICON, feed.getIcon().getUrl());                    
        }

        if(feed.getCategories() != null)
        {
            StringJoiner joiner = new StringJoiner(",");
            for(SyndCategory category : feed.getCategories())
            {
                joiner.add(category.getLabel());
            }
            props.setProperty(PROP_CATEGORY, joiner.toString());                    
        }

        return props;
    }     
    
    @Override
    public void save(RssChannel channel, OutputStream os, String comments) throws IOException
    {
        channel.getProperties().store(os, comments);
        LOG.info("RSS Channel saved");      
    }     
    
    private static final class RssChannelImpl implements RssChannel, Channel, DisplayNameProvider, IconProvider, DomainsProvider
    {                 
        private final Properties props; 
        private final PropertyChangeSupport propertyChangeSupport;
        
        private Lookup lkp;  
        private State state;
        
        public RssChannelImpl(Properties props)
        {
            this.props = props; 
            propertyChangeSupport = new PropertyChangeSupport(this);  
        } 
        
        @Override
        public String getSourceID()
        {            
            return getFileName();
        }        
        
// TODO RssChannel        
         
        @Override
        public String getFeedUrl()
        {
            return props.getProperty(PROP_FEED_URL);
        }
        
        @Override
        public String getFileName()
        {
            return props.getProperty(PROP_FILE_NAME);
        }
        
        @Override
        public String getTitle()
        {
            return props.getProperty(PROP_TITLE);
        }
        
        @Override
        public String getDescription()
        {
            return props.getProperty(PROP_DESCRIPTION);
        }      

        @Override
        public String getLink() 
        {
            return props.getProperty(PROP_LINK);
        }    

        @Override
        public String getImage() 
        {
            return props.getProperty(PROP_IMAGE);
        } 

        @Override
        public String getIcon() 
        {
            return props.getProperty(PROP_ICON);
        } 

        @Override
        public String getUri() 
        {
            return props.getProperty(PROP_URI);
        }    

        @Override
        public String getAuthor() 
        {
            return props.getProperty(PROP_AUTHOR);
        }   

        @Override
        public String getCopyright() 
        {
            return props.getProperty(PROP_COPYRIGHT);
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
        public String getGenerator() 
        {
            return props.getProperty(PROP_GENERATOR);
        }  

        @Override
        public String getLanguage() 
        {
            return props.getProperty(PROP_LANGUAGE);
        }  

        @Override
        public String getManagingEditor() 
        {
            return props.getProperty(PROP_MANAGING_EDITOR);
        }   

        @Override
        public String getCategory() 
        {
            return props.getProperty(PROP_CATEGORY);
        }                 
        
// TODO DisplayNameProvider
        
        @Override
        public String getDisplayName(TextFormat format)
        {
            if(format == TextFormat.PLAIN)
            {
                return getTitle();
            }
            return null;
        }        
        
// TODO PropertiesProvider        
        
        @Override
        public Properties getProperties()
        {
            return props;
        }  
        
        @Override
        public boolean merge(PropertiesProvider provider)
        {
            if(props.equals(provider.getProperties()))       
            {
                return false;
            }
            props.putAll(provider.getProperties());        
            return true;
        } 
        
        @Override
        public String getAppID()
        {
            return props.getProperty(PROP_APP_ID);
        }         
        
        @Override
        public boolean isModified() 
        {
            return state == State.MODIFIED;
        }

        @Override
        public void markModified()
        {
            state = State.MODIFIED;
        }

        @Override
        public boolean isDeleted() 
        {
            return state == State.DELETED;
        } 
        
        @Override
        public void notifyDeleted()
        {
            State oldValue = state;
            state = State.DELETED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
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
        public Lookup getLookup() 
        {
            if (lkp == null) 
            {
                lkp = Lookups.fixed(this);              
            }
            return lkp;
        }                    

// TODO IconProvider    
    
        @Override
        public synchronized Image getIcon(int type)
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.RSS_CHANNEL);            
        }
        
// TODO DomainsProvider         
        
        @Override
        public Set<String> getDomains()
        {
            String domains = props.getProperty(PROP_DOMAINS);
            if(domains != null)
            {
                return Set.of(domains.split(","));                   
            }                
            return Collections.EMPTY_SET;
        }               
    }     
}
