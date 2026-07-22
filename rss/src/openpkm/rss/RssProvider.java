/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.rss;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.event.ChangeListener;
import openpkm.utils.Utils;
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

    protected Map<String, RssChannel> channels; 
    protected FileObject rootDir; 

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
    
    protected abstract Map<String, RssChannel> getChannelsById();
    
    public Collection<RssChannel> getChannels()
    {
        return Collections.unmodifiableCollection(getChannelsById().values());
    }
    
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }     

    public RssChannel getChannel(String channelID) 
    {
        return getChannelsById().get(channelID);
    }                                  

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "RSS Channel";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        try
        {
            RssChannel channel = factory.getRssChannel(Utils.getProperties(file)); 
            if(getChannelsById().containsKey(channel.getRssID()))
            {
                return true;
            }
        }
        catch(IOException e) {}  
        return false;
    }     
}
