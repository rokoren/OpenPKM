/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/actionListener.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "OpenPKM",
        id = "openpkm.core.neo4j.ThoughtAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/comments.png",
        displayName = "#CTL_ThoughtAction"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/OpenPKM", position = 10),
    @ActionReference(path = "Shortcuts", name = "D-T")
})
@Messages("CTL_ThoughtAction=Add Thought")
public final class ThoughtAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new ThoughtWizardPanel1());
        //panels.add(new CreateRaindropWizardPanel2());
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
        wiz.setTitle("Add Thought");
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));  
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        {
            /*
            RaindropProject project = (RaindropProject)wiz.getProperty("project");
            RaindropProvider provider = project.getLookup().lookup(RaindropProvider.class);
            if(provider != null)
            {
                RaindropCollection collection = (RaindropCollection)wiz.getProperty("collection");
                String note = (String) wiz.getProperty(Raindrop.PROPS_NOTE);
                List<String> tags = (List<String>) wiz.getProperty(TagsProvider.PROP_TAGS);
                Boolean important = (Boolean) wiz.getProperty(Raindrop.PROPS_IMPORTANT);                
                
                FileObject file = collection == null ? provider.createRaindrop(context.getLink(), important.booleanValue(), tags, note) : provider.createRaindrop(collection, context.getLink(), important.booleanValue(), tags, note);
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
            */
        }        
    }
}
