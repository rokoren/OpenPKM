/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/contextAction.java to edit this template
 */
package openpkm.core.content;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import openpkm.base.LiteratureNoteFactory;
import openpkm.core.neo4j.GoalWizardPanel;
import openpkm.core.neo4j.ThoughtWizardPanel;
import openpkm.core.neo4j.TopicWizardPanel;
import openpkm.utils.SummaryWizardPanel;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "OpenPKM/Note",
        id = "openpkm.core.content.LiteratureNoteAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/pencil.png",
        displayName = "#CTL_LiteratureNoteAction"
)
@ActionReference(path = "Toolbars/OpenPKM", position = 77)
@Messages("CTL_LiteratureNoteAction=Create Literature Note")
public final class LiteratureNoteAction implements ActionListener {

    private final LiteratureNoteFactory context;

    public LiteratureNoteAction(LiteratureNoteFactory context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) 
    {
        // TODO use context
        
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new NoteWizardPanel1());
        panels.add(new TopicWizardPanel());
        panels.add(new GoalWizardPanel());
        panels.add(new ThoughtWizardPanel());
        panels.add(new SummaryWizardPanel());        
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
        
        context.createLiteratureNote(panels);
    }
}
