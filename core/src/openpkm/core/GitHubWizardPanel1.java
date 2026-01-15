/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.TopicsProvider;
import openpkm.github.GitHubPasswordProvider;
import openpkm.github.GitHubUser;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class GitHubWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(GitHubWizardPanel1.class.getName());
    
    private GHUser user;
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GitHubVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public GitHubVisualPanel1 getComponent() {
        if (component == null) {
            component = new GitHubVisualPanel1();
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
        descriptor.putProperty(GitHubUser.PROP_USER_NAME, getComponent().getGitHubUsername());  
        descriptor.putProperty(TopicsProvider.PROP_TOPICS, getComponent().getGitHubTopics());
        descriptor.putProperty("user", user);         
    }

    @Override
    public void validate() throws WizardValidationException 
    {  
        GitHubPasswordProvider provider = Lookup.getDefault().lookup(GitHubPasswordProvider.class);
        if(provider == null) 
        {
            throw new WizardValidationException(getComponent(), "GitHub Personal access token not found.", null);
        }          
        if (getComponent().getGitHubUsername().equals("")) 
        {
            throw new WizardValidationException(null, "Username can not be empty", null);
        }        

        try
        {
            LOG.info("Personal access token: " + provider.getPersonalAccessToken());
            GitHub github = new GitHubBuilder().withOAuthToken(provider.getPersonalAccessToken()).build(); 
            user = github.getUser(getComponent().getGitHubUsername());
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
            throw new WizardValidationException(component, e.getMessage(), e.getLocalizedMessage());
        } 
    }     
}
