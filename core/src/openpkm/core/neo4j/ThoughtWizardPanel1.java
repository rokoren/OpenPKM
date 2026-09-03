/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import javax.swing.event.ChangeListener;
import openpkm.base.TagsProvider;
import openpkm.base.Thought;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public class ThoughtWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, WizardDescriptor.FinishablePanel<WizardDescriptor>
{
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ThoughtVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public ThoughtVisualPanel1 getComponent() {
        if (component == null) {
            component = new ThoughtVisualPanel1();
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
        Thought.Type type = getComponent().getSelectedType();
        if(type == null) 
        {
            throw new WizardValidationException(null, "Type can not be empty", null);
        }  

        String text = getComponent().getText();
        if(text.isBlank()) 
        {
            throw new WizardValidationException(null, "Text can not be empty", null);
        }  
    }  
    
    @Override
    public boolean isFinishPanel() 
    {
        return true;
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
        // use wiz.getProperty to retrieve previous panel state   
        Lookup.Provider provider = (Lookup.Provider)wiz.getProperty("provider");
        if(provider != null)
        { 
            getComponent().setTags(provider);
        }          
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) 
    {
        // use wiz.putProperty to remember current panel state

        wiz.putProperty("text", getComponent().getText());         
        
        Thought.Type type = getComponent().getSelectedType();
        wiz.putProperty("type", type); 
             
        wiz.putProperty(TagsProvider.PROP_TAGS, getComponent().getSelectedTags());        
    }    
}
