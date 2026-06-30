/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.FileTypeProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.VisibilityProvider;
import openpkm.reference.AbstractFilesProvider;
import openpkm.reference.Reference;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class FileWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(FileWizardPanel1.class.getName());    
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private FileVisualPanel1 component;
    
    private final AbstractFilesProvider provider;

    public FileWizardPanel1(AbstractFilesProvider provider) 
    {
        this.provider = provider;
    }        

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public FileVisualPanel1 getComponent() {
        if (component == null) 
        {
            try
            {
                component = new FileVisualPanel1(provider);                
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }
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
        if (getComponent().getReferenceFileType() == null) 
        {
            throw new WizardValidationException(null, "File Type can not be empty", null);
        }          
        if (getComponent().getReferenceFileName() == null) 
        {
            throw new WizardValidationException(getComponent(), "File can not be empty", null);
        }        
        if (getComponent().getReferenceTitle().equals("")) 
        {
            throw new WizardValidationException(getComponent(), "Title can not be empty", null);
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
        Lookup.Provider lookupProvider = (Lookup.Provider)descriptor.getProperty("provider");
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
        descriptor.putProperty(FileTypeProvider.PROP_FILE_TYPE, getComponent().getReferenceFileType()); 
        descriptor.putProperty(TitleProvider.PROP_TITLE, getComponent().getReferenceTitle());        
        descriptor.putProperty(TagsProvider.PROP_TAGS, new HashSet<String>(getComponent().getReferenceTags()));        
        descriptor.putProperty(Reference.PROP_FILE_NAME, getComponent().getReferenceFileName());        
        descriptor.putProperty(Reference.PROP_FILE_PATH, getComponent().getReferenceFileRelativePath());        
        descriptor.putProperty(Reference.PROP_FILE_EXT, getComponent().getReferenceFileExt()); 
        descriptor.putProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, getComponent().getReferenceVisibilityModifier());          
    }    
}
