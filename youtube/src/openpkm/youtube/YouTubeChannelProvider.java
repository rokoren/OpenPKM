/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.youtube;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.Icon;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public abstract class YouTubeChannelProvider implements SourceProvider<YouTubeChannel>
{
    protected static final String ROOT_FOLDER = "youtube-channel";       

    protected Map<String, YouTubeChannel> channels; 
    protected FileObject rootDir; 

    protected final YouTubeChannelFactory factory;

    public YouTubeChannelProvider(YouTubeChannelFactory factory) 
    {
        this.factory = factory;
    } 
    
    @Override
    public YouTubeChannelFactory getFactory()
    {
        return factory;
    }
    
    protected abstract Map<String, YouTubeChannel> getChannelsById();
    
    public Collection<YouTubeChannel> getChannels()
    {
        return Collections.unmodifiableCollection(getChannelsById().values());
    }

    @Override
    public Source getSource(String sourceID) 
    {
        return getChannelsById().get(sourceID);
    }  
    
    @Override
    public void deleteSource(String sourceID) throws IOException
    {
        FileObject root = getRootFolder();
        if(root != null)
        {
            FileObject file = root.getFileObject(sourceID, PropertiesProvider.EXTENSION);
            if(file != null)
            {  
                file.delete();
            }              
        }  
    }     

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "YouTube Channel";
    }

    @Override
    public Icon getIcon(boolean bln) 
    {
        IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
        return provider.getIcon(IconsProvider.ICON.YOUTUBE_CHANNEL);
    }

    @Override
    public boolean contains(FileObject file) 
{
        if(file.isData())
        {
            return getChannelsById().containsKey(file.getName());                
        }
        return false;        
    }      
}
