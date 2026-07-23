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
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import openpkm.base.PropertiesProvider;
import openpkm.rss.RssChannel;
import openpkm.rss.RssProvider;
import openpkm.utils.FileUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/Domain",
        id = "openpkm.core.domain.RssChannelAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/feed.png",
        displayName = "#CTL_RssChannelAction"
)
@Messages("CTL_RssChannelAction=Add RSS Channel")
public class RssChannelAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(RssChannelAction.class.getName());     
    
    private final RssProvider provider;

    public RssChannelAction(RssProvider provider)
    {
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
                    try
                    {
                        String fileName = FileUtils.getFileName(provider.getRootFolder(), PropertiesProvider.EXTENSION);
                        OutputStream os = provider.getRootFolder().createAndOpen(fileName + "." + PropertiesProvider.EXTENSION);  
                        provider.getFactory().save(channel, os, "New RSS Channel Created by Wizard");
                        os.close();  

                        StatusDisplayer.getDefault().setStatusText("RSS Channel saved with title: " + channel.getTitle());                                                                     
                    }                    
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }
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
