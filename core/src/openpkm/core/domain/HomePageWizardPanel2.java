/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import openpkm.core.domain.DomainVisualPanel2;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.DescriptionProvider;
import openpkm.base.TitleProvider;
import org.jsoup.nodes.Document;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Rok Koren
 */
public class HomePageWizardPanel2 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(HomePageWizardPanel2.class.getName());
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private DomainVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public DomainVisualPanel2 getComponent() {
        if (component == null) {
            component = new DomainVisualPanel2();
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
        Document document = (Document)descriptor.getProperty("document");
        String description = document.select("meta[name=description]").attr("content");
        getComponent().setDomainTitle(document.title());
        getComponent().setDomainDescription(description);                
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        descriptor.putProperty(TitleProvider.PROP_TITLE, getComponent().getDomainTitle());
        descriptor.putProperty(DescriptionProvider.PROP_DESCRIPTION, getComponent().getDomainDescription());        
    }

    @Override
    public void validate() throws WizardValidationException 
    {        
        if (getComponent().getDomainTitle().equals("")) 
        {
            throw new WizardValidationException(null, "Title can not be empty", null);
        }              
    }    
}
