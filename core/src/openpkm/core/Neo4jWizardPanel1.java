/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.core;

import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.neo4j.Neo4jInstance;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class Neo4jWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(Neo4jWizardPanel1.class.getName());
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Neo4jVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public Neo4jVisualPanel1 getComponent() {
        if (component == null) {
            component = new Neo4jVisualPanel1();
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
        descriptor.putProperty(Neo4jInstance.PROP_URI, getComponent().getNeo4jUri()); 
        descriptor.putProperty(Neo4jInstance.PROP_USERNAME, getComponent().getNeo4jUsername());   
        descriptor.putProperty(Neo4jInstance.PROP_PASSWORD, getComponent().getNeo4jPassword());   
    }

    @Override
    public void validate() throws WizardValidationException 
    {        
        if (getComponent().getNeo4jUri().equals("")) 
        {
            throw new WizardValidationException(null, "URI can not be empty", null);
        }
        if (getComponent().getNeo4jUsername().equals("")) 
        {
            throw new WizardValidationException(null, "Username can not be empty", null);
        }  
        if (getComponent().getNeo4jPassword().equals("")) 
        {
            throw new WizardValidationException(null, "Password can not be empty", null);
        }         
        
        String uri = getComponent().getNeo4jUri();        
        String username = getComponent().getNeo4jUsername();
        String password = getComponent().getNeo4jPassword();
        try 
        {
            Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
            driver.verifyConnectivity();
            driver.close();
        }
        catch (Exception e) 
        {
            LOG.warning(e.getMessage());
            throw new WizardValidationException(null, e.getMessage(), null);
        }        
    }     
}
