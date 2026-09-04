/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/contextAction.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import openpkm.base.Goal;
import openpkm.base.GoalsProvider;
import openpkm.base.TagsProvider;
import openpkm.base.Thought;
import openpkm.base.ThoughtsGraphProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "OpenPKM/Thought",
        id = "openpkm.core.neo4j.RootThoughtAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/comments.png",
        displayName = "#CTL_RootThoughtAction"
)
@ActionReference(path = "Toolbars/OpenPKM", position = 55)
@Messages("CTL_RootThoughtAction=Add Root Thought")
public final class RootThoughtAction implements ActionListener {

    private final ThoughtsGraphProvider context;

    public RootThoughtAction(ThoughtsGraphProvider context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) 
    {
        // TODO use context
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new ThoughtWizardPanel1());
        panels.add(new TopicWizardPanel());
        panels.add(new GoalWizardPanel());
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
        wiz.setTitle("Add Root Thought");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", context.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            String text = (String) wiz.getProperty("text");  
            Thought.Type type = (Thought.Type) wiz.getProperty("type");  
            Set<String> tags = (Set<String>) wiz.getProperty(TagsProvider.PROP_TAGS);
            Set<Topic> topics = (Set<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);
            Set<Goal> goals = (Set<Goal>) wiz.getProperty(GoalsProvider.PROP_GOALS);  
            Thought thought = context.addRootThought(text, type, tags, topics, goals);
            if(thought != null)
            {
                StatusDisplayer.getDefault().setStatusText("Thought saved with text: " + text);                
            }
        }          
    }
}
