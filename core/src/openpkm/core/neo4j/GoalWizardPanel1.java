/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.core.neo4j;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeListener;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.Topic;
import org.netbeans.api.project.Project;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class GoalWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    public static final String PROP_GOAL_NAME       = "goal.name";  
    public static final String PROP_GOAL_TAG        = "goal.tag";      
    public static final String PROP_GOAL_LEVEL      = "goal.level"; 
    public static final String PROP_GOAL_START_DATE = "goal.date.start";    
    public static final String PROP_GOAL_END_DATE   = "goal.date.end";     
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GoalVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public GoalVisualPanel1 getComponent() 
    {
        if (component == null) 
        {
            component = new GoalVisualPanel1();
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
    public void validate() throws WizardValidationException 
    {     
        if(getComponent().getGoalName().equals("")) 
        {
            throw new WizardValidationException(null, "Name can not be empty", null);
        } 
    }    

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    
    private Set<String> getSelectedTags(Project project)
    {
        KnowledgeGraphProvider provider = project.getLookup().lookup(KnowledgeGraphProvider.class);
        if(provider != null)
        {
            Collection<Topic> topics = provider.getSelectedTopics();
            if(!topics.isEmpty())
            {
                return provider.getTags(topics);
            }
        }        
        return new HashSet<>();
    }    

    @Override
    public void readSettings(WizardDescriptor wiz) 
    {       
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        descriptor.putProperty(PROP_GOAL_NAME, getComponent().getGoalName());
        descriptor.putProperty(PROP_GOAL_TAG, getComponent().getGoalTag());
        descriptor.putProperty(PROP_GOAL_LEVEL, getComponent().getGoalLevel());
        LocalDate startDate = getComponent().getGoalStartDate();
        if(startDate != null)
        {
            descriptor.putProperty(PROP_GOAL_START_DATE, startDate);
        }
        LocalDate endDate = getComponent().getGoalEndDate();
        if(endDate != null)
        {
            descriptor.putProperty(PROP_GOAL_END_DATE, endDate);
        } 
    }
}
