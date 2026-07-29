/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.rss;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;

/**
 *
 * @author rok
 */
public abstract class RssProvider implements SourceGroup
{
    protected static final String ROOT_FOLDER = "rss";       

    protected FileObject rootDir; 
    protected Channels channels;

    protected final RssFactory factory;
    protected final ChangeSupport changeSupport; 

    public RssProvider(RssFactory factory) 
    {
        this.factory = factory;
        changeSupport = new ChangeSupport(this); 
    } 
    
    public RssFactory getFactory()
    {
        return factory;
    }
    
    public abstract Channels getChannels();
    
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }                                     

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Feeds";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        if(file.isData())
        {
            return getChannels().hasChannel(file.getName());                
        }
        return false;            
    }   
    
    public static final class Channels
    {
        private final Map<String, RssChannel> channelsByUrl = new HashMap<>();         
        private Map<String, RssChannel> channelsByFile = new HashMap<>();  
        
        public Collection<RssChannel> getChannels()
        {
            return Collections.unmodifiableCollection(channelsByUrl.values());
        }   
        
        public void addChannel(RssChannel channel)
        {
            channelsByUrl.put(channel.getFeedUrl(), channel);
            channelsByFile.put(channel.getFileName(), channel);
        }
        
        public RssChannel removeChannel(String fileName)
        {
            RssChannel channel = channelsByFile.remove(fileName);
            if(channel != null)
            {
                channelsByUrl.remove(channel.getFeedUrl());
            }
            return channel;
        }
        
        public boolean hasChannel(String fileName)
        {
            return channelsByFile.containsKey(fileName);
        }
    }
}
