/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

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
public abstract class RaindropSourceProvider implements SourceProvider
{
    protected static final String ROOT_FOLDER = "raindrop";       

    protected Map<String, Raindrop> raindrops; 
    protected FileObject rootDir;         

    public abstract Map<String, Raindrop> getRaindrops();    

    @Override
    public Source getSource(String sourceID) 
    {
        return getRaindrops().get(sourceID);
    }                    

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Raindrop";
    }

    @Override
    public Icon getIcon(boolean bln) 
    {
        return new ImageIcon(ImageUtilities.loadImage(Raindrop.ICON));
    }

    @Override
    public boolean contains(FileObject file) 
    {                                   
        return getRaindrops().containsKey(file.getName());
    }
}
