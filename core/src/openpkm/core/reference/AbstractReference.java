/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.reference;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
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
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.event.ChangeListener;
import openpkm.base.IconsProvider;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.reference.AbstractFilesProvider;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rokor
 */
public abstract class AbstractReference implements Reference, PropertiesProvider, TagsProvider, TopicsProvider, VisibilityProvider
{
    public static final String EXT_GIF = "gif";
    public static final String EXT_JPG = "jpg";
    public static final String EXT_PNG = "png";    
    public static final String EXT_PDF = "pdf";
    public static final String EXT_MP4 = "mp4";    

    protected static final Logger LOG = Logger.getLogger(AbstractReference.class.getName());     

    protected final Properties props; 
    protected final PropertyChangeSupport propertyChangeSupport;

    private Lookup lkp;  
    private boolean isDeleted;

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
            lkp = Lookups.fixed(this, new NodeProviderImpl());              
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
    public void setDeleted(boolean newValue)
    {
        boolean oldValue = isDeleted;
        this.isDeleted = newValue;
        propertyChangeSupport.firePropertyChange(PROP_DELETED, oldValue, newValue);        
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
    
    public String getTitle()
    {
        return props.getProperty(ReferenceProvider.PROP_TITLE);
    }

    public void setTitle(String title)
    {
        if(title == null)
        {
            Object oldValue = props.remove(ReferenceProvider.PROP_TITLE);
            propertyChangeSupport.firePropertyChange(ReferenceProvider.PROP_TITLE, oldValue, title);
        }
        else
        {
            Object oldValue = props.setProperty(ReferenceProvider.PROP_TITLE, title);
            propertyChangeSupport.firePropertyChange(ReferenceProvider.PROP_TITLE, oldValue, title);
        }
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

    private final class NodeProviderImpl implements NodeProvider, PropertyChangeListener
    {                
        private final ChangeSupport changeSupport = new ChangeSupport(this); 

        public NodeProviderImpl() 
        {
            addPropertyChangeListener(this);
        }                

        @Override
        public Image getImage()
        { 
            String nameExt = props.getProperty(PROP_FILE_EXT);
            if(nameExt != null)
            {
                IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
                if(nameExt.equalsIgnoreCase(EXT_GIF))
                {
                    return provider.getImage(IconsProvider.ICON.FILE_GIF);                
                }
                else if(nameExt.equalsIgnoreCase(EXT_JPG))
                {
                    return provider.getImage(IconsProvider.ICON.FILE_JPG); 
                } 
                else if(nameExt.equalsIgnoreCase(EXT_PNG))
                {
                    return provider.getImage(IconsProvider.ICON.FILE_PNG);                 
                }             
                else if(nameExt.equalsIgnoreCase(EXT_MP4))
                {
                    return provider.getImage(IconsProvider.ICON.FILE_MP4);                 
                }  
                else if(nameExt.equalsIgnoreCase(EXT_PDF))
                {
                    return provider.getImage(IconsProvider.ICON.FILE_PDF);                 
                }             
            }    
            return null;
        }        
        
        @Override
        public Icon getIcon()
        { 
            String nameExt = props.getProperty(PROP_FILE_EXT);
            if(nameExt != null)
            {
                IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
                if(nameExt.equalsIgnoreCase(EXT_GIF))
                {
                    return provider.getIcon(IconsProvider.ICON.FILE_GIF);                
                }
                else if(nameExt.equalsIgnoreCase(EXT_JPG))
                {
                    return provider.getIcon(IconsProvider.ICON.FILE_JPG); 
                } 
                else if(nameExt.equalsIgnoreCase(EXT_PNG))
                {
                    return provider.getIcon(IconsProvider.ICON.FILE_PNG);                 
                }             
                else if(nameExt.equalsIgnoreCase(EXT_MP4))
                {
                    return provider.getIcon(IconsProvider.ICON.FILE_MP4);                 
                }  
                else if(nameExt.equalsIgnoreCase(EXT_PDF))
                {
                    return provider.getIcon(IconsProvider.ICON.FILE_PDF);                 
                }             
            }    
            return null;
        }                     

        @Override
        public String getName() 
        {
            return getSourceID();
        }

        @Override
        public Image getIcon(int type) 
        {
            return getImage();
        }

        @Override
        public Image getOpenedIcon(int type) 
        {
            return getImage();
        }

        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }

        @Override
        public String getDisplayName() 
        {
            return getTitle();
        }

        @Override
        public String getHtmlDisplayName()
        {
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

        @Override
        public List<Action> getActions() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
            changeSupport.fireChange();
        }
    }     
}
