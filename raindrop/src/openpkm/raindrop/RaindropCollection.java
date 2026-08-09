/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.raindrop;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Comparator;

/**
 *
 * @author Rok Koren
 */
public interface RaindropCollection 
{
    int getCollectionID();
    int getCount();
    void setCount(int count);
    String getTitle();
    void setTitle(String title);
    String getDescription();
    void setDescription(String desc);
    String getCover();
    void setCover(String cover);
    BufferedImage getImage() throws MalformedURLException, IOException;
    RaindropAccount getAccount();
    boolean isPublic();
    void setPublic(boolean share);  
    
    public static Comparator<RaindropCollection> titleComparator() 
    {
        return new Comparator<RaindropCollection>() 
        {
            @Override
            public int compare(RaindropCollection collection1, RaindropCollection collection2) 
            {
                String title1 = collection1.getTitle();
                String title2 = collection2.getTitle();
                return title1.compareTo(title2);
            }
        };
    }      
}
