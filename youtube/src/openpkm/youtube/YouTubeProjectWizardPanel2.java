/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.youtube;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class YouTubeProjectWizardPanel2 implements WizardDescriptor.Panel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(YouTubeProjectWizardPanel2.class.getName());
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private YouTubeProjectVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public YouTubeProjectVisualPanel2 getComponent() {
        if (component == null) {
            component = new YouTubeProjectVisualPanel2();
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
        String title = (String) descriptor.getProperty(YouTubeChannel.PROP_TITLE);
        String description = (String) descriptor.getProperty(YouTubeChannel.PROP_DESCRIPTION);         
        String thumbnail = (String) descriptor.getProperty(YouTubeChannel.PROP_THUMBNAIL);
        getComponent().setChannelTitle(title);
        getComponent().setChannelDescription(description);
        
        try
        {                
            URL url = new URL(thumbnail);
            BufferedImage image = ImageIO.read(url); 

            int spaceWidth = image.getWidth() / 2;
            int spaceHeight = image.getWidth() / 2;

            int newWidth = image.getWidth() + 2 * spaceWidth;
            int newHeight = image.getHeight() + 2 * spaceHeight;
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

            // Get the graphics context to draw on the new image
            Graphics2D g2d = newImage.createGraphics();

            // Fill the new image with a white background (or any other color)
            g2d.setColor(new Color(0, 0, 0, 0)); 
            g2d.fillRect(0, 0, newWidth, newHeight);

            // Draw the original image onto the new image with the desired padding
            int x = spaceWidth;
            int y = spaceHeight;
            g2d.drawImage(image, x, y, null);            

            descriptor.putProperty("WizardPanel_image", newImage);                                            
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }         
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        // use wiz.putProperty to remember current panel state      
    }
}
