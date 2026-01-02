/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.youtube.YouTubeSourceProvider;
import openpkm.youtube.YouTubeVideo;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/WatchLater",
        id = "openpkm.core.WatchLaterAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/eye.png",
        displayName = "#CTL_WatchLaterAction"
)
@Messages("CTL_WatchLaterAction=Watch YouTube Videos")
public class WatchLaterAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(WatchLaterAction.class.getName());     
    
    private final YouTubeSourceProvider provider;

    public WatchLaterAction(YouTubeSourceProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        for(YouTubeVideo video : provider.getVideos())
        {
            if(video.isWatchLater())
            {
                panels.add(new WatchLaterWizardPanel(video));                  
            }          
        }
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) 
        {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()  
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Watch YouTube Videos");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        boolean isFinish = DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION;
        for(WizardDescriptor.Panel panel : panels)
        {
            if(panel instanceof WatchLaterWizardPanel watchLaterPanel)
            {
                watchLaterPanel.finish(isFinish);
            }           
        }                                                             
    }      
}
