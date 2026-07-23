/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndFeed;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.DisplayNameProvider.TextFormat;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.rss.RssChannel;
import openpkm.rss.RssFactory;
import openpkm.utils.DateTimeUtils;
import openpkm.utils.Utils;
import org.openide.nodes.Children;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author rok
 */
@ServiceProvider(service=RssFactory.class)
public class RssFactoryImpl implements RssFactory
{
    public static final String PROP_TITLE           = "title"; 
    public static final String PROP_DESCRIPTION     = "description";    
    public static final String PROP_LINK            = "link"; 
    public static final String PROP_IMAGE           = "image";     
    public static final String PROP_ICON            = "icon";
    public static final String PROP_URI             = "uri";
    public static final String PROP_AUTHOR          = "author";
    public static final String PROP_COPYRIGHT       = "copyright";
    public static final String PROP_PUBLISHED_DATE  = "published.date"; 
    public static final String PROP_GENERATOR       = "generator";     
    public static final String PROP_LANGUAGE        = "language";
    public static final String PROP_MANAGING_EDITOR = "managing.editor";    
    public static final String PROP_CATEGORY        = "category";   
    
    private static final RequestProcessor RP = new RequestProcessor(RssFactory.class);   
    private static final Logger LOG = Logger.getLogger(RssFactory.class.getName());  
    
    @Override
    public RssChannel getRssChannel(Properties props) 
    {
        return new RssChannelImpl(props);
    }

    @Override
    public RssChannel getRssChannel(SyndFeed feed) 
    {
        Properties props = new Properties();
        LocalDateTime publishedDate = DateTimeUtils.convertToLocalDateTime(feed.getPublishedDate());
        props.setProperty(PROP_PUBLISHED_DATE, publishedDate.format(DateTimeFormatter.ISO_DATE_TIME));
        props.setProperty(PROP_TITLE, feed.getTitle());
        props.setProperty(PROP_DESCRIPTION, feed.getDescription());
        props.setProperty(PROP_LINK, feed.getLink());
        props.setProperty(PROP_URI, feed.getUri());        
        props.setProperty(PROP_AUTHOR, feed.getAuthor());
        props.setProperty(PROP_COPYRIGHT, feed.getCopyright());    
        
        props.setProperty(PROP_GENERATOR, feed.getGenerator());
        props.setProperty(PROP_LANGUAGE, feed.getLanguage());        
        props.setProperty(PROP_MANAGING_EDITOR, feed.getManagingEditor());
        
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

        return getRssChannel(props);
    } 
    
    @Override
    public void save(RssChannel channel, OutputStream os, String comments) throws IOException
    {
        channel.getProperties().store(os, comments);
        LOG.info("RSS Channel saved");      
    }     
    
    private static final class RssChannelImpl implements RssChannel, DisplayNameProvider
    {
        private final Properties props; 
        
        private Lookup lkp;  
        private State state;
        
        public RssChannelImpl(Properties props)
        {
            this.props = props;            
        }  

// TODO RssChannel        
                                
        @Override
        public String getTitle()
        {
            return props.getProperty(PROP_TITLE);
        }
        
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
            state = State.DELETED;
        }         

// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getUri();
        }
        
        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            {
                lkp = Lookups.fixed(this, new IconProviderImpl());              
            }
            return lkp;
        }                           

        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }
        
        @Override
        public HelpCtx getHelp()
        {
            return HelpCtx.DEFAULT_HELP;
        }  

        // TODO IconProvider    
    
        private final class IconProviderImpl implements IconProvider, ChangeSupportProvider, Runnable
        {        
            private Image icon; 
            private boolean isLoading;

            private final ChangeSupport changeSupport = new ChangeSupport(this); 

            @Override
            public synchronized Image getIcon(int type)
            {
                if(icon != null)
                {
                    return icon;
                }
                if(!isLoading)
                {
                    isLoading = true;                
                    RP.post(this);                
                }
                IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
                return provider.getImage(IconsProvider.ICON.RSS_CHANNEL);
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
            public void run() 
            {
                String string = getImage();
                if(string != null)
                {
                    try
                    {
                        URL url = new URL(string);
                        BufferedImage image = ImageIO.read(url);  
                        icon = Utils.resizeImage(image, 16, 16); 
                        changeSupport.fireChange();
                    }
                    catch(MalformedURLException e)
                    {
                        LOG.warning(e.getMessage());
                    }
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    } 
                    finally
                    {
                        isLoading = false;
                    }                
                }
            }                
        } 
    }      
}
