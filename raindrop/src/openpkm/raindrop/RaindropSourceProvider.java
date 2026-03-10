/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.util.Collection;
import java.util.Collections;
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
public abstract class RaindropSourceProvider implements SourceProvider<Raindrop>
{
    protected static final String ROOT_FOLDER = "raindrop";       

    protected Map<String, Raindrop> raindrops; 
    protected FileObject rootDir; 
    
    protected final RaindropProvider provider;

    public RaindropSourceProvider(RaindropProvider provider) 
    {
        this.provider = provider;
    }        
    
    public abstract Map<String, Raindrop> getRaindropsById();   
    
    public Collection<Raindrop> getRaindrops()
    {
        return Collections.unmodifiableCollection(getRaindropsById().values());
    }

    public RaindropProvider getRaindropProvider()
    {
        return provider;
    }
    
    @Override
    public Source getSource(String sourceID) 
    {
        return getRaindropsById().get(sourceID);
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
        return getRaindropsById().containsKey(file.getName());
    }
}
