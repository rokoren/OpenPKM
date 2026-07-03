/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.TopicsProvider;
import openpkm.rss.RssChannel;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class RssChannelWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(RssChannelWizardPanel1.class.getName());
    
    private SyndFeed feed;    
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private RssChannelVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public RssChannelVisualPanel1 getComponent() {
        if (component == null) {
            component = new RssChannelVisualPanel1();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor descriptor) 
    {
        // use wiz.getProperty to retrieve previous panel state
        Lookup.Provider provider = (Lookup.Provider)descriptor.getProperty("provider");
        if(provider != null)
        { 
            getComponent().setTopics(provider);
        }          
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {        
        descriptor.putProperty(RssChannel.PROP_RSS_URL, getComponent().getRssUrl());  
        descriptor.putProperty(RssChannelProject.PROP_RSS_FILE, Boolean.toString(getComponent().isRssFile())); 
        descriptor.putProperty(TopicsProvider.PROP_TOPICS, getComponent().getRssChannelTopics());
        descriptor.putProperty("feed", feed);         
    }

    @Override
    public void validate() throws WizardValidationException 
    {        
        if (getComponent().getRssUrl().equals("")) 
        {
            throw new WizardValidationException(null, "URL can not be empty", null);
        }        
        
        try
        {
            URL url = new URL(getComponent().getRssUrl());
            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(url));                                                     
        }
        catch (MalformedURLException e)
        {
            LOG.warning(e.getMessage());
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        }
        catch (IOException e)
        {
            LOG.warning(e.getMessage());
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        }    
        catch (FeedException e)
        {
            LOG.warning(e.getMessage());
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        }              
    }      
}
