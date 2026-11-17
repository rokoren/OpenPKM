/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.rss;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public abstract class RssChannelProvider implements SourceProvider<RssChannel>
{
    protected static final String ROOT_FOLDER = "rss-channel"; 
    
    protected FileObject rootDir; 
    protected Channels channels; 

    protected final RssChannelFactory factory;    

    public RssChannelProvider(RssChannelFactory factory) 
    {
        this.factory = factory;
    } 
    
    @Override
    public boolean isLiteratureNoteProvider()
    {
        return false;
    }     
    
    @Override
    public RssChannelFactory getFactory()
    {
        return factory;
    }
    
    public abstract Channels getChannels();
    
    @Override
    public RssChannel getSource(String sourceID) 
    {
        return getChannels().getChannelsByFile().get(sourceID);
    }      

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "RSS";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        if(file.isData())
        {
            return getChannels().getChannelsByFile().containsKey(file.getName()); 
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
        
        public Map<String, RssChannel> getChannelsByUrl()
        {
            return channelsByUrl;
        }
        
        public Map<String, RssChannel> getChannelsByFile()
        {
            return channelsByFile;
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
    }
}
