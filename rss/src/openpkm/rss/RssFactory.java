/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.rss;

import com.rometools.rome.feed.synd.SyndFeed;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author rok
 */
public interface RssFactory
{
    RssChannel getRssChannel(Properties props);
    RssChannel getRssChannel(String feedUrl, SyndFeed feed);
    void save(RssChannel channel, OutputStream os, String comments) throws IOException;
}
