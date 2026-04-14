/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.reference;

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
import java.util.Set;
import java.util.logging.Logger;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.ShortDescriptionProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.reference.AbstractFilesProvider;
import openpkm.reference.Reference;
import openpkm.utils.DisplayNameProviderImpl;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rokor
 */
public abstract class AbstractReference implements Reference, TitleProvider, PropertiesProvider, IconProvider, ShortDescriptionProvider, TagsProvider, TopicsProvider, VisibilityProvider
{
    public static final String EXT_GIF = "gif";
    public static final String EXT_JPG = "jpg";
    public static final String EXT_PNG = "png";    
    public static final String EXT_PDF = "pdf";
    public static final String EXT_MP4 = "mp4";    

    protected static final Logger LOG = Logger.getLogger(AbstractReference.class.getName());     

    protected final Properties props; 
    protected final PropertyChangeSupport propertyChangeSupport;

    protected Lookup lkp;  
    protected SourceState state;

    public AbstractReference(Properties props) 
    {
        this.props = props;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    @Override
    public Lookup getLookup() 
    {
        if (lkp == null) 
        {
            lkp = Lookups.fixed(this, new DisplayNameProviderImpl(this));              
        }
        return lkp;
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
    public String getAppID()
    {
        return props.getProperty(PROP_APP_ID);
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

    @Override
    public Image getIcon(int type) 
    {
        String nameExt = props.getProperty(Reference.PROP_FILE_EXT);
        if(nameExt != null)
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            if(nameExt.equalsIgnoreCase(AbstractReference.EXT_GIF))
            {
                return provider.getImage(IconsProvider.ICON.FILE_GIF);                
            }
            else if(nameExt.equalsIgnoreCase(AbstractReference.EXT_JPG))
            {
                return provider.getImage(IconsProvider.ICON.FILE_JPG); 
            } 
            else if(nameExt.equalsIgnoreCase(AbstractReference.EXT_PNG))
            {
                return provider.getImage(IconsProvider.ICON.FILE_PNG);                 
            }             
            else if(nameExt.equalsIgnoreCase(AbstractReference.EXT_MP4))
            {
                return provider.getImage(IconsProvider.ICON.FILE_MP4);                 
            }  
            else if(nameExt.equalsIgnoreCase(AbstractReference.EXT_PDF))
            {
                return provider.getImage(IconsProvider.ICON.FILE_PDF);                 
            }             
        }    
        return null;
    } 
    
    @Override
    public String getShortDescription() 
    {
        try
        {
            return getFile().getPath();            
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }
        return null;
    }     
}
