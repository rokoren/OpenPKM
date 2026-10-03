/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.drawboard.pdf;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import openpkm.base.BacklinksProvider;
import openpkm.base.DrawboardPdf;
import openpkm.base.GoalsProvider;
import openpkm.base.IconProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.TopicsProvider;
import openpkm.utils.DisplayNameProviderImpl;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rok
 */
public abstract class AbstractDrawboardPdf implements DrawboardPdf, TitleProvider, IconProvider, TagsProvider, BacklinksProvider, TopicsProvider, GoalsProvider
{
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/drawboard-pdf.png";     
    
    protected final Properties props; 
    protected final PropertyChangeSupport propertyChangeSupport;

    protected Lookup lkp;  
    protected State state;

    public AbstractDrawboardPdf(Properties props) 
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
    public String getLink()
    {
        return props.getProperty(PROP_LINK);        
    }

    @Override
    public Set<String> getTags() 
    {
        String tags = props.getProperty(PROP_TAGS);
        if(tags != null&& !tags.isBlank())
        {
            return Set.of(tags.split(","));                   
        }                
        return Collections.EMPTY_SET;
    }
    
    @Override
    public Set<String> getBacklinks() 
    {
        String backlinks = props.getProperty(PROP_BACKLINKS);
        if(backlinks != null && !backlinks.isBlank())
        {
            return Set.of(backlinks.split(","));                   
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
    public Set<String> getGoals()
    {
        String goals = props.getProperty(PROP_GOALS);
        if(goals != null)
        {
            return Set.of(goals.split(","));                   
        }                
        return Collections.EMPTY_SET;
    }        

    @Override
    public Image getIcon(int type) 
    {  
        return ImageUtilities.loadImage(ICON);
    }    
}
