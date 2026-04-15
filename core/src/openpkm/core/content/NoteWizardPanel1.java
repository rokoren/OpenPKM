/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.content;

import javax.swing.event.ChangeListener;
import openpkm.base.FileTypeProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class NoteWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private NoteVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public NoteVisualPanel1 getComponent() 
    {
        if (component == null) 
        {
            component = new NoteVisualPanel1();
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
        if (getComponent().getNoteFileType() == null) 
        {
            throw new WizardValidationException(null, "File Type can not be empty", null);
        }        
        if(getComponent().getNoteTitle().equals("")) 
        {
            throw new WizardValidationException(null, "Title can not be empty", null);
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
        Lookup.Provider lookupProvider = (Lookup.Provider)wiz.getProperty("provider");
        if(lookupProvider != null)
        {
            TagsProvider tagsProvider = lookupProvider.getLookup().lookup(TagsProvider.class);
            if(tagsProvider != null)
            {
                getComponent().setTags(tagsProvider.getTags());                                 
            }            
        }
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        descriptor.putProperty(FileTypeProvider.PROP_FILE_TYPE, getComponent().getNoteFileType()); 
        descriptor.putProperty(TitleProvider.PROP_TITLE, getComponent().getNoteTitle());
        descriptor.putProperty(TagsProvider.PROP_TAGS, getComponent().getNoteTags()); 
    }    
}
