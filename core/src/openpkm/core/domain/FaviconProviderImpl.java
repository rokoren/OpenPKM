/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import openpkm.domain.FaviconProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author rok
 */
@ServiceProvider(service=FaviconProvider.class)
public class FaviconProviderImpl implements FaviconProvider
{
    private static final String GOOGLE_FAVICON_API = "https://www.google.com/s2/favicons?domain=%s&sz=%s";
    
    @Override
    public BufferedImage getFavicon(String domain, int size) throws IOException
    {
        URL url = new URL(String.format(GOOGLE_FAVICON_API, domain, size));
        return ImageIO.read(url);   
    }    
}
