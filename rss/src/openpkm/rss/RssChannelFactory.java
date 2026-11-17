/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.rss;

import com.rometools.rome.feed.synd.SyndFeed;
import java.util.Properties;
import openpkm.base.SourceFactory;

/**
 *
 * @author Rok Koren
 */
public interface RssChannelFactory extends SourceFactory<RssChannel>
{    
    RssChannel getChannel(Properties props);       
    RssChannel getChannel(String feedUrl, String fileName, SyndFeed feed);
}
