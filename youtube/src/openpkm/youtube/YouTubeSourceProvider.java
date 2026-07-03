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
 * @author Rok Koren
 */
public abstract class YouTubeSourceProvider implements SourceProvider<YouTubeVideo>
{
    protected static final String ROOT_FOLDER = "youtube";       

    protected Map<String, YouTubeVideo> videos; 
    protected FileObject rootDir; 

    protected final YouTubeVideoProvider provider;

    public YouTubeSourceProvider(YouTubeVideoProvider provider) 
    {
        this.provider = provider;
    } 
    
    public YouTubeVideoProvider getVideoProvider()
    {
        return provider;
    }
    
    protected abstract Map<String, YouTubeVideo> getVideosById();
    
    public Collection<YouTubeVideo> getVideos()
    {
        return Collections.unmodifiableCollection(getVideosById().values());
    }

    @Override
    public Source getSource(String sourceID) 
    {
        return getVideosById().get(sourceID);
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
        return "YouTube";
    }

    @Override
    public Icon getIcon(boolean bln) 
    {
        IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
        return provider.getIcon(IconsProvider.ICON.YOUTUBE_VIDEO);
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getVideosById().containsKey(file.getName());
    }    
}
