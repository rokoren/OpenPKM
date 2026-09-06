/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.youtube;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.Icon;
import openpkm.base.IconsProvider;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public abstract class YouTubeVideoProvider implements SourceProvider<YouTubeVideo>
{
    protected static final String ROOT_FOLDER = "youtube-video";       

    protected Map<String, YouTubeVideo> videos; 
    protected FileObject rootDir; 

    protected final YouTubeVideoFactory factory;

    public YouTubeVideoProvider(YouTubeVideoFactory factory) 
    {
        this.factory = factory;
    } 
    
    @Override
    public boolean isLiteratureNoteProvider()
    {
        return false;
    }      
    
    @Override
    public YouTubeVideoFactory getFactory()
    {
        return factory;
    }
    
    protected abstract Map<String, YouTubeVideo> getVideosById();    
    
    public Collection<YouTubeVideo> getVideos()
    {
        return Collections.unmodifiableCollection(getVideosById().values());
    }

    @Override
    public YouTubeVideo getSource(String sourceID) 
    {
        return getVideosById().get(sourceID);
    }           

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "YouTube Video";
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
