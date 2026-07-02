/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/contextAction.java to edit this template
 */
package openpkm.raindrop;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.LinkProvider;
import openpkm.base.TagsProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "OpenPKM",
        id = "openpkm.raindrop.RaindropAction"
)
@ActionRegistration(
        iconBase = "openpkm/raindrop/resources/raindrop.png",
        displayName = "#CTL_RaindropAction"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/OpenPKM", position = 100),
    @ActionReference(path = "Shortcuts", name = "D-R")
})
@Messages("CTL_RaindropAction=Create Raindrop")
public final class RaindropAction implements ActionListener 
{
    @StaticResource()
    public static final String BANNER = "openpkm/raindrop/resources/banner.png";  
    
    private static final Logger LOG = Logger.getLogger(RaindropAction.class.getName());    
    
    private final LinkProvider context;

    public RaindropAction(LinkProvider context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new CreateRaindropWizardPanel1());
        panels.add(new CreateRaindropWizardPanel2());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
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
        wiz.setTitle("Create Raindrop");
        wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));  
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        {
            RaindropProject project = (RaindropProject)wiz.getProperty("project");
            RaindropSourceProvider provider = project.getLookup().lookup(RaindropSourceProvider.class);
            if(provider != null)
            {
                String note = (String) wiz.getProperty(Raindrop.PROPS_NOTE);
                List<String> tags = (List<String>) wiz.getProperty(TagsProvider.PROP_TAGS);
                Boolean important = (Boolean) wiz.getProperty(Raindrop.PROPS_IMPORTANT);
                
                FileObject file = provider.createRaindrop(context.getLink(), important.booleanValue(), tags, note);
                if(file != null)
                {
                    StatusDisplayer.getDefault().setStatusText("Raindrop saved");                         

                    NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to open Raindrop in editor?", "Raindrop", NotifyDescriptor.YES_NO_OPTION);
                    if(DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION)
                    {
                        try
                        {
                            DataObject data = DataObject.find(file);
                            OpenCookie open = data.getCookie(OpenCookie.class);
                            open.open();                            
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.warning(e.getMessage());
                        }
                    }                                         
                }                
            }
        }
    }
}
