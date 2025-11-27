/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.raindrop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class RaindropWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor> 
{
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private RaindropVisualPanel1 component;
    private RaindropUser user;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public RaindropVisualPanel1 getComponent() {
        if (component == null) {
            component = new RaindropVisualPanel1();
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
        String title = getComponent().getRaindropTitle();
        String token = getComponent().getRaindropToken();
        descriptor.putProperty(RaindropAccount.PROP_TITLE, title); 
        descriptor.putProperty(RaindropAccount.PROP_TOKEN, token);   
        descriptor.putProperty("user", user);   
    }

    @Override
    public void validate() throws WizardValidationException 
    {        
        if (getComponent().getRaindropTitle().equals("")) 
        {
            throw new WizardValidationException(null, "Title can not be empty", null);
        }
        if (getComponent().getRaindropToken().equals("")) 
        {
            throw new WizardValidationException(null, "Token can not be empty", null);
        }  
        
        String title = getComponent().getRaindropTitle();        
        String token = getComponent().getRaindropToken();
        try {
            // Construct the URL for the Raindrop.io API endpoint
            String apiUrl = "https://api.raindrop.io/rest/v1/user";
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the API key in the request header
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) 
            {                
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                user = RaindropUtils.getUser(response.toString());             
            } 
            else
            {
                throw new WizardValidationException(getComponent(), "API Request failed. Response Code: " + responseCode, null);
            }

            // Close the connection
            connection.disconnect();

        }
        catch (Exception e) 
        {
            Exceptions.printStackTrace(e);
            throw new WizardValidationException(null, "API Request failed", null);
        }        
    }  
}
