/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import javax.swing.event.ChangeListener;
import openpkm.base.FileTypeProvider;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Rok Koren
 */
public class FileTypeWizardPanel implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private FileTypeVisualPanel component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public FileTypeVisualPanel getComponent() 
    {
        if (component == null) 
        {
            component = new FileTypeVisualPanel();
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
        if (getComponent().getFileType() == null) 
        {
            throw new WizardValidationException(null, "File Type can not be empty", null);
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
        descriptor.putProperty(FileTypeProvider.PROP_FILE_TYPE, getComponent().getFileType());  
    }     
}
