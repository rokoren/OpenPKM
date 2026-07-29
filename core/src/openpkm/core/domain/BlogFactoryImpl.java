/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.CloseSupport;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.OpenSupport;
import openpkm.base.PropertiesProvider;
import openpkm.base.StateSupport;
import openpkm.domain.Blog;
import openpkm.domain.BlogFactory;
import openpkm.domain.Domain;
import openpkm.domain.FaviconProvider;
import openpkm.domain.MultiViewElementImpl;
import openpkm.utils.DisplayNameProviderImpl;
import openpkm.utils.ShortDescriptionProviderImpl;
import openpkm.utils.Utils;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author rok
 */
@ServiceProvider(service=BlogFactory.class)
public class BlogFactoryImpl implements BlogFactory
{
    private static final Logger LOG = Logger.getLogger(BlogFactory.class.getName());      
    
    @Override
    public Blog getBlog(Properties props) 
    {
        return new BlogImpl(props);
    }
    
    @Override
    public void save(Blog blog, OutputStream os, String comments) throws IOException
    {
        blog.getProperties().store(os, comments);
        LOG.info("Blog saved");      
    }   

    public class BlogImpl implements Blog, Domain, StateSupport, MultiViewDescription
    {
        private final Properties props; 
        private final PropertyChangeSupport propertyChangeSupport;

        private Lookup lkp;  
        private State state;    

        public BlogImpl(Properties props) 
        {
            this.props = props;
            propertyChangeSupport = new PropertyChangeSupport(this);
        }    

    // TODO Domain
        
        @Override
        public String getFileName()
        {
            return props.getProperty(PROP_FILE_NAME);
        }         
        
    // TODO Blog         

        @Override
        public String getUrl() 
        {
            return props.getProperty(PROP_URL);
        }     

        @Override
        public String getFavicon() 
        {
            return props.getProperty(PROP_FAVICON);
        }       

    // TODO Source

        @Override
        public String getAppID()
        {
            return props.getProperty(PROP_APP_ID);
        } 

        @Override
        public String getSourceID()
        {
            return getFileName();
        }  

        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            {
                lkp = Lookups.fixed(this, new DisplayNameProviderImpl(this), new ShortDescriptionProviderImpl(this), new IconProviderImpl());              
            }
            return lkp;
        }  

        @Override
        public boolean isModified() 
        {
            return state == State.MODIFIED;
        }

        @Override
        public void markModified()
        {
            State oldValue = state;
            state = State.MODIFIED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
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
       
// TODO MultiViewDescription        
        
        @Override
        public String preferredID() 
        {
            return "blog";
        }          
        
        @Override
        public MultiViewElement createElement() 
        {
            return new MultiViewElementImpl(this, false);
        }  

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Blog";
        }   
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }  

        @Override
        public Image getIcon() 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.BLOG);
        } 
        
        private final class IconProviderImpl implements IconProvider, OpenSupport, CloseSupport, ChangeSupportProvider, Runnable
        {                
            private final ChangeSupport changeSupport = new ChangeSupport(this); 

            private Image icon; 

            @Override
            public synchronized Image getIcon(int type)
            {
                if(icon != null)
                {
                    return icon;
                }
                IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
                return provider.getImage(IconsProvider.ICON.BLOG);
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
                String favicon = getFavicon();
                try
                {
                    if(favicon != null)
                    {
                        try
                        {
                            URL url = new URL(favicon);
                            BufferedImage image = ImageIO.read(url);  
                            if(image != null)
                            {
                                icon = Utils.resizeImage(image, 16, 16); 
                                changeSupport.fireChange();                                
                            }
                        }
                        catch(MalformedURLException e)
                        {
                            LOG.warning(e.getMessage());
                        }             
                    }  
                    if(icon == null)
                    {
                        FaviconProvider provider = Lookup.getDefault().lookup(FaviconProvider.class);
                        icon = provider.getFavicon(getUrl(), 16);  
                        changeSupport.fireChange(); 
                    }                       
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }  
            }                

            @Override
            public void open() 
            {
                RP.post(this);
            }

            @Override
            public void close() 
            {
                icon = null;
                changeSupport.fireChange();
            }
        }          
    }
}
