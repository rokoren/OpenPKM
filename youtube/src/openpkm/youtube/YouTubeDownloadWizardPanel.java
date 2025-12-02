/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.youtube;

import java.util.Arrays;
import java.util.Optional;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author Rok Koren
 */
public class YouTubeDownloadWizardPanel implements WizardDescriptor.FinishablePanel<WizardDescriptor>
{
    public static final String PROP_DOWNLOAD_TYPE       = "download.type";
    public static final String PROP_DOWNLOAD_RESOLUTION = "download.resolution";
    public static final String PROP_DOWNLOAD_SUBTITLES  = "download.subtitles";
    
    public enum DownloadType 
    {
        VIDEO("Video"),
        AUDIO("Audio"),
        NONE("None");

        private String name;

        DownloadType(String name) 
        {
            this.name = name;
        }

        @Override
        public String toString() 
        {
            return name;
        }
        
        public static Optional<DownloadType> get(String name) {
            return Arrays.stream(DownloadType.values())
                    .filter(type -> type.name.equalsIgnoreCase(name))
                    .findFirst();
        }     
    }     
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private YouTubeDownloadVisualPanel component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public YouTubeDownloadVisualPanel getComponent() 
    {
        if (component == null) 
        {
            component = new YouTubeDownloadVisualPanel();
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
        descriptor.putProperty(PROP_DOWNLOAD_TYPE, getComponent().getDownloadType()); 
        descriptor.putProperty(PROP_DOWNLOAD_RESOLUTION, getComponent().getDownloadResolution());
        descriptor.putProperty(PROP_DOWNLOAD_SUBTITLES, getComponent().isDownloadSubtitles());
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
    }    

    @Override
    public boolean isFinishPanel()
    {
        return true;
    }    
}
