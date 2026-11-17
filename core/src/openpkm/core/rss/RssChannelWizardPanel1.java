/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.rss;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.event.ChangeListener;
import openpkm.rss.RssChannelFactory;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Rok Koren
 */
public class RssChannelWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private RssChanneVisualPanel1 component;
    private SyndFeed feed;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public RssChanneVisualPanel1 getComponent() 
    {
        if (component == null) 
        {
            component = new RssChanneVisualPanel1();
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
    public void validate() throws WizardValidationException 
    {     
        if(getComponent().getFeedURL().isBlank()) 
        {
            throw new WizardValidationException(null, "Feed URL can not be empty", null);
        } 
        try
        {
            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(new URL(getComponent().getFeedURL())));  
        }
        catch (MalformedURLException e)
        {
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        }
        catch (IOException e)
        {
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        }    
        catch (FeedException e)
        {
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        }         
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
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        descriptor.putProperty(RssChannelFactory.PROP_FEED_URL, getComponent().getFeedURL()); 
        descriptor.putProperty("feed", feed); 
        
        descriptor.setTitle(feed.getTitle());
    }      
}
