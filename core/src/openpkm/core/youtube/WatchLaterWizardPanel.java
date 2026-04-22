/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.youtube;

import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.WatchLater;
import openpkm.youtube.YouTubeCefClientProvider;
import openpkm.youtube.YouTubeVideo;
import org.cef.browser.CefBrowser;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class WatchLaterWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(WatchLaterWizardPanel.class.getName());     
    
    private final WatchLater watchLater;
    
    private CefBrowser browser;
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private WatchLaterVisualPanel component;

    public WatchLaterWizardPanel(WatchLater watchLater) 
    {
        this.watchLater = watchLater;
    }  
    
    public void finish(boolean isFinish)
    {
        if(isFinish) watchLater.setWatchLater(false);
        if(browser != null)
        {
            browser.close(true);
        }
    }

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public WatchLaterVisualPanel getComponent() 
    {
        if(browser == null)
        {
            if(watchLater instanceof YouTubeVideo video)
            {
                YouTubeCefClientProvider provider = Lookup.getDefault().lookup(YouTubeCefClientProvider.class);
                if(provider != null)
                {
                    try
                    {
                        browser = provider.getBrowser(video);                     
                    }
                    catch(Exception e)
                    {
                        LOG.warning(e.getMessage());
                    }                   
                }                
            }
        }        
        if (component == null) 
        {
            component = new WatchLaterVisualPanel(watchLater, browser);                                   
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
    public void readSettings(WizardDescriptor wiz) {  
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) {
    }     
}
