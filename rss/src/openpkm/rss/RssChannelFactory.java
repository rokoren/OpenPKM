/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.rss;

import com.rometools.rome.feed.synd.SyndFeed;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.base.SourceFactory;

/**
 *
 * @author Rok Koren
 */
public interface RssChannelFactory extends SourceFactory<RssChannel>
{ 
    String PROP_FEED_URL        = "feed.url"; 
    String PROP_FILE_NAME       = "file.name"; 
    String PROP_TITLE           = "title"; 
    String PROP_DESCRIPTION     = "description";    
    String PROP_LINK            = "link"; 
    String PROP_IMAGE           = "image";     
    String PROP_ICON            = "icon";
    String PROP_URI             = "uri";
    String PROP_AUTHOR          = "author";
    String PROP_COPYRIGHT       = "copyright";
    String PROP_PUBLISHED_DATE  = "published.date"; 
    String PROP_GENERATOR       = "generator";     
    String PROP_LANGUAGE        = "language";
    String PROP_MANAGING_EDITOR = "managing.editor";    
    String PROP_CATEGORY        = "category";     
    
    Logger LOG = Logger.getLogger(RssChannelFactory.class.getName());
    
    RssChannel getChannel(Properties props);    
    Properties getProperties(String feedUrl, SyndFeed feed, String fileName);
}
