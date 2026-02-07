/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.Color;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.utils.Utils;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Rok Koren
 */
public class TrelloBoardWizardPanel2 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(TrelloBoardWizardPanel2.class.getName());
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private TrelloBoardVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public TrelloBoardVisualPanel2 getComponent() {
        if (component == null) {
            component = new TrelloBoardVisualPanel2();
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
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {  
        Color background = getComponent().getBackgroundColor();
        if(background != null)
        {              
            descriptor.putProperty(TrelloProject.PROP_BOARD_BACKGROUND, Utils.getHex(background)); 
        }
    }

    @Override
    public void validate() throws WizardValidationException 
    {          
        if (getComponent().getBackgroundColor() == null) 
        {
            throw new WizardValidationException(null, "No color is selected", null);
        }        
    }     
}
