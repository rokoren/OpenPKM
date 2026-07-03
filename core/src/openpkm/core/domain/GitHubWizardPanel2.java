/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.DescriptionProvider;
import openpkm.base.TitleProvider;
import openpkm.github.GitHubUser;
import org.kohsuke.github.GHUser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Rok Koren
 */
public class GitHubWizardPanel2 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(GitHubWizardPanel2.class.getName());
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GitHubVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public GitHubVisualPanel2 getComponent() {
        if (component == null) {
            component = new GitHubVisualPanel2();
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
        GHUser user = (GHUser)descriptor.getProperty("user");
        try
        {
            getComponent().setGitHubName(user.getName());
            getComponent().setGitHubBio(user.getBio()); 
            descriptor.putProperty(GitHubUser.PROP_FOLLOWERS_COUNT, getComponent().getGitHubName());
            descriptor.putProperty(GitHubUser.PROP_PUBLIC_REPOS_COUNT, getComponent().getGitHubName());
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        descriptor.putProperty(TitleProvider.PROP_TITLE, getComponent().getGitHubName());
        descriptor.putProperty(DescriptionProvider.PROP_DESCRIPTION, getComponent().getGitHubBio());        
    }

    @Override
    public void validate() throws WizardValidationException 
    {        
        if (getComponent().getGitHubName().equals("")) 
        {
            throw new WizardValidationException(null, "Name can not be empty", null);
        }              
    }       
}
