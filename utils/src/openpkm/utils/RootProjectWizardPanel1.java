/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.utils;

import javax.swing.event.ChangeListener;
import openpkm.base.TitleProvider;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

public class RootProjectWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor> 
{
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private RootProjectVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public RootProjectVisualPanel1 getComponent() {
        if (component == null) {
            component = new RootProjectVisualPanel1(true);
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
        if(getComponent().getProjectLocation().equals(""))
        {
            throw new WizardValidationException(null, "Location can not be empty", null);
        }
        if(getComponent().getProjectTitle().equals("")) 
        {
            throw new WizardValidationException(null, "Title can not be empty", null);
        }  
        if(getComponent().getProjectNeo4jInstance() == null) 
        {
            throw new WizardValidationException(null, "Knowledge Graph can not be empty", null);
        }         
    }     

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) 
    {
        String location = NbPreferences.forModule(RootProjectVisualPanel1.class).get("location", "");  
        getComponent().setProjectLocation(location);
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        descriptor.putProperty(TitleProvider.PROP_TITLE, getComponent().getProjectTitle());
        descriptor.putProperty("neo4j", getComponent().getProjectNeo4jInstance());
        descriptor.putProperty("location", getComponent().getProjectLocation());
    }
}
