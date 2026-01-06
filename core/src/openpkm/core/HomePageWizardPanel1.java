/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.HomePage;
import openpkm.base.TopicsProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class HomePageWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(HomePageWizardPanel1.class.getName());
    
    private Document document;    
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private HomePageVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public HomePageVisualPanel1 getComponent() {
        if (component == null) {
            component = new HomePageVisualPanel1();
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
        descriptor.putProperty(HomePage.PROP_URL, getComponent().getHomePageUrl()); 
        descriptor.putProperty(TopicsProvider.PROP_TOPICS, getComponent().getHomePageTopics());
        descriptor.putProperty("document", document);         
    }

    @Override
    public void validate() throws WizardValidationException 
    {        
        if (getComponent().getHomePageUrl().equals("")) 
        {
            throw new WizardValidationException(null, "URL can not be empty", null);
        }        
        
        try
        {
            document = Jsoup.connect(getComponent().getHomePageUrl())
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 Firefox/26.0")
                    .get();             
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        }      
    }     
}
