/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class TrelloWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor> 
{
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private TrelloVisualPanel1 component;
    private Member member;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public TrelloVisualPanel1 getComponent() {
        if (component == null) {
            component = new TrelloVisualPanel1();
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
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        descriptor.putProperty(TrelloAccount.PROPS_TITLE, getComponent().getTrelloTitle()); 
        descriptor.putProperty(TrelloAccount.PROPS_USERNAME, getComponent().getTrelloUsername());          
        descriptor.putProperty(TrelloAccount.PROPS_API_KEY, getComponent().getTrelloApiKey()); 
        descriptor.putProperty(TrelloAccount.PROPS_ACCESS_TOKEN, getComponent().getTrelloAccessToken());                  
        descriptor.putProperty("member", member);  
    }

    @Override
    public void validate() throws WizardValidationException 
    {
        if (getComponent().getTrelloTitle().equals("")) 
        {
            throw new WizardValidationException(null, "Title can not be empty", null);
        }
        if (getComponent().getTrelloUsername().equals("")) 
        {
            throw new WizardValidationException(null, "Username can not be empty", null);
        }  
        if (getComponent().getTrelloApiKey().equals("")) 
        {
            throw new WizardValidationException(null, "API Key can not be empty", null);
        }         
        if (getComponent().getTrelloAccessToken().equals("")) 
        {
            throw new WizardValidationException(null, "Access Token can not be empty", null);
        }          
        
        Trello trelloApi = new TrelloImpl(getComponent().getTrelloApiKey(), getComponent().getTrelloAccessToken(), new JDKTrelloHttpClient());
        member = trelloApi.getMemberInformation(getComponent().getTrelloUsername());        
    }

}
