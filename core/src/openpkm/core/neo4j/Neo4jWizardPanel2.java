/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.core.neo4j;

import javax.swing.event.ChangeListener;
import openpkm.neo4j.Neo4jInstance;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class Neo4jWizardPanel2 implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Neo4jVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public Neo4jVisualPanel2 getComponent() {
        if (component == null) {
            component = new Neo4jVisualPanel2();
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
        descriptor.putProperty(Neo4jInstance.PROP_INSTANCE_ID, getComponent().getInstanceID()); 
        descriptor.putProperty(Neo4jInstance.PROP_INSTANCE_NAME, getComponent().getInstanceName());  
        descriptor.putProperty(Neo4jInstance.PROP_NEO4J_DATABASE, getComponent().getNeo4jDatabase());          
        descriptor.putProperty(Neo4jInstance.PROP_NEO4J_TYPE, getComponent().getNeo4jType());  
    }
}
