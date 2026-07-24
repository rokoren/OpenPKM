/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import openpkm.base.PropertiesProvider;
import openpkm.rss.AbstractRssActionsProvider;
import openpkm.rss.RssChannel;
import openpkm.rss.RssProvider;
import openpkm.utils.FileUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author rok
 */
public class RssActionsProvider extends AbstractRssActionsProvider
{
    private static final Logger LOG = Logger.getLogger(RssActionsProvider.class.getName());     
    
    private final RssProvider provider;  

    public RssActionsProvider(RssProvider provider) 
    {
        this.provider = provider;
    }        
    
    @Override
    public Action addRssChannel() 
    {
        return new AddRssChannel(provider);
    }
    
    private static final class AddRssChannel extends AbstractAction
    {                          
        private final RssProvider provider;            

        public AddRssChannel(RssProvider provider) 
        {
            super("Add Rss Channel");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            NotifyDescriptor descriptor = new NotifyDescriptor.InputLine("URL:", "Add RSS Channel");
            Object retVal = DialogDisplayer.getDefault().notify(descriptor);
            if (retVal == NotifyDescriptor.OK_OPTION) 
            {
                String url = ((NotifyDescriptor.InputLine)descriptor).getInputText();

                try
                {
                    SyndFeedInput input = new SyndFeedInput();
                    SyndFeed feed = input.build(new XmlReader(new URL(url)));   
                    RssChannel channel = provider.getFactory().getRssChannel(feed);
                    if(channel != null)
                    {                      
                        String fileName = FileUtils.getFileName(provider.getRootFolder(), PropertiesProvider.EXTENSION);
                        OutputStream os = provider.getRootFolder().createAndOpen(fileName + "." + PropertiesProvider.EXTENSION);  
                        provider.getFactory().save(channel, os, "New RSS Channel Created by Dialog");
                        os.close();  

                        StatusDisplayer.getDefault().setStatusText("RSS Channel saved with title: " + channel.getTitle());    
                    }
                }
                catch (MalformedURLException e)
                {
                    LOG.warning(e.getMessage());
                }
                catch (IOException e)
                {
                    LOG.warning(e.getMessage());
                }    
                catch (FeedException e)
                {
                    LOG.warning(e.getMessage());
                }              
            }  
        }
    }      
}
