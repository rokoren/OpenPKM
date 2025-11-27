/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Rok Koren
 */
public class RaindropRootCollection implements RaindropCollection
{
    private static final Logger LOG = Logger.getLogger(RaindropRootCollection.class.getName());    
    
    private final RaindropAccount account;
    private final int collectionID;  
    
    private String title;
    private String description;
    private String cover;    
    private int count;
    private boolean share;
    private BufferedImage image;

    public RaindropRootCollection(RaindropAccount account, int collectionID, boolean isPublic) 
    {
        this.account = account;
        this.collectionID = collectionID;
        this.share = isPublic;        
    }        

    @Override
    public int getCollectionID() 
    {
        return collectionID;
    }

    @Override
    public String getTitle() 
    {
        return title;
    }
    
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    public String getDescription() 
    {
        return description;
    } 
    
    @Override
    public void setDescription(String desc)
    {
        description = desc;
    }
    
    @Override
    public String getCover() 
    {
        return cover;
    } 

    @Override
    public void setCover(String cover)
    {
        this.cover = cover;
    }
    
    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public void setCount(int count)
    {
        this.count = count;
    }    
    
    @Override
    public synchronized BufferedImage getImage() throws MalformedURLException, IOException
    {
        if(image == null)
        {
            URL url = new URL(cover);
            image = ImageIO.read(url);          
        }
        return image;
    } 

    @Override
    public RaindropAccount getAccount() 
    {
        return account;
    }
    
    @Override
    public List<Raindrop> getRaindrops()
    {
        return RaindropUtils.getRaindrops(account, this);
    }     
    
    @Override
    public String toString()
    {
        return getTitle();
    } 

    @Override
    public boolean isPublic() 
    {
        return share;
    }

    @Override
    public void setPublic(boolean share) 
    {
        this.share = share;
    }     
}
