/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Get2UsersByUsernameUsernameResponse;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.TopicsProvider;
import openpkm.twitter.TwitterPasswordProvider;
import openpkm.twitter.TwitterUser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class TwitterWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(TwitterWizardPanel1.class.getName());
    
    private Get2UsersByUsernameUsernameResponse user;
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private TwitterVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public TwitterVisualPanel1 getComponent() {
        if (component == null) {
            component = new TwitterVisualPanel1();
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
        descriptor.putProperty(TwitterUser.PROP_USER_NAME, getComponent().getTwitterUsername());  
        descriptor.putProperty(TopicsProvider.PROP_TOPICS, getComponent().getTwitterTopics());
        descriptor.putProperty("user", user);         
    }

    @Override
    public void validate() throws WizardValidationException 
    {  
        TwitterPasswordProvider provider = Lookup.getDefault().lookup(TwitterPasswordProvider.class);
        if(provider == null) 
        {
            throw new WizardValidationException(getComponent(), "Twitter bearer token not found.", null);
        }          
        if (getComponent().getTwitterUsername().equals("")) 
        {
            throw new WizardValidationException(null, "Username can not be empty", null);
        }        

        try
        {
            LOG.info("Bearer token: " + provider.getBearerToken());            
            TwitterApi api = new TwitterApi(new TwitterCredentialsBearer(provider.getBearerToken()));
            user = api.users().findUserByUsername(getComponent().getTwitterUsername()).execute();
            LOG.info("User: " + user.getData().getId());
        }
        catch(ApiException e)
        {
            LOG.warning(e.getMessage());
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        } 
    }     
}
