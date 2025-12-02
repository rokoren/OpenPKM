/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.youtube;

import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public abstract class YouTubeSourceProvider implements SourceProvider
{
    protected static final String ROOT_FOLDER = "youtube";       

    protected Map<String, YouTubeVideo> videos; 
    protected FileObject rootDir; 

    protected final YouTubeVideoProvider provider;

    public YouTubeSourceProvider(YouTubeVideoProvider provider) 
    {
        this.provider = provider;
    } 
    
    public abstract Map<String, YouTubeVideo> getVideos();

    @Override
    public Source getSource(String sourceID) 
    {
        return getVideos().get(sourceID);
    }                                  

    @Override
    public String getName() 
    {
        return YouTubeSourceProvider.class.getName();
    }

    @Override
    public String getDisplayName() 
    {
        return "YouTube";
    }

    @Override
    public Icon getIcon(boolean bln) 
    {
        return new ImageIcon(ImageUtilities.loadImage(YouTubeVideo.ICON));
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getVideos().containsKey(file.getName());
    }    
}
