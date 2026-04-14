/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.Goal;
import openpkm.base.GoalsGraphProvider;
import openpkm.base.VisibilityProvider;
import openpkm.utils.Utils;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rokoren
 */
public class GoalNode extends AbstractNode
{
    private final GoalsGraphProvider provider;
    private final Goal goal;

    public GoalNode(Project project, GoalsGraphProvider provider, Goal goal) 
    {
        super(new GoalChildren(project, provider, goal), Lookups.fixed(project, goal));
        setName(goal.getGoalID());
        setDisplayName(goal.getName());
        this.provider = provider;
        this.goal = goal;
    }  
    
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new SelectGoal(provider, goal),
            new AddGoal(provider, goal)
        };
    }  
    
    private Image getIcon(boolean opened) 
    {
        return Utils.getTreeFolderIcon(opened);
    }

    @Override
    public Image getIcon(int type) 
    {
        return getIcon(false);
    }

    @Override
    public Image getOpenedIcon(int type) 
    {
        return getIcon(true);
    }    
    
    static final class GoalChildren extends Children.Keys<Goal> implements ChangeListener 
    {
        private final Project project;
        private final GoalsGraphProvider provider;
        private final Goal goal;        

        public GoalChildren(Project project, GoalsGraphProvider provider, Goal goal)
        {
            this.project = project;
            this.provider = provider;
            this.goal = goal;
            provider.addChangeListener(this);
        }  

        @Override
        protected void addNotify() 
        {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            EventQueue.invokeLater(new Runnable() 
            {
                @Override
                public void run()
                {                                                
                    SortedSet<Goal> goals = new TreeSet<Goal>(Goal.nameComparator());
                    goals.addAll(provider.getChildrenGoals(goal.getGoalID()));           
                    setKeys(goals);                   
                }
            });
        }        

        @Override
        protected void removeNotify() 
        {
            provider.removeChangeListener(this);                               
            setKeys(Collections.<Goal>emptySet());
        }

        @Override
        protected Node[] createNodes(Goal goal) 
        {
            return new Node[] {new GoalNode(project, provider, goal)};
        }

        @Override
        public void stateChanged(ChangeEvent ev) 
        {
            updateKeys();
        }            
    }  

    private static final class SelectGoal extends AbstractAction
    {
        private final GoalsGraphProvider provider;      
        private final Goal goal;   

        public SelectGoal(GoalsGraphProvider provider, Goal goal) 
        {
            super("Select Goal");
            this.provider = provider;
            this.goal = goal;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            provider.selectGoal(goal);
        }
    }     
    
    private static final class AddGoal extends AbstractAction
    {
        private final GoalsGraphProvider provider;      
        private final Goal goal;   

        public AddGoal(GoalsGraphProvider provider, Goal goal) 
        {
            super("Add Goal");
            this.provider = provider;
            this.goal = goal;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new GoalWizardPanel1());
            panels.add(new GoalWizardPanel2());
            panels.add(new GoalWizardPanel3());
            panels.add(new GoalWizardPanel4());  
            panels.add(new GoalWizardPanel5());
            panels.add(new GoalWizardPanel6());
            panels.add(new GoalWizardPanel7());  
            panels.add(new AccessibilityWizardPanel2()); 
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
            wiz.setTitle("Add Goal");        
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {
                String name = (String) wiz.getProperty(GoalWizardPanel1.PROP_GOAL_NAME);
                String tag = (String) wiz.getProperty(GoalWizardPanel1.PROP_GOAL_TAG);
                Goal.Level level = (Goal.Level) wiz.getProperty(GoalWizardPanel1.PROP_GOAL_LEVEL);
                LocalDate startDate = (LocalDate) wiz.getProperty(GoalWizardPanel1.PROP_GOAL_START_DATE);
                LocalDate endDate = (LocalDate) wiz.getProperty(GoalWizardPanel1.PROP_GOAL_END_DATE);
                String vision = (String) wiz.getProperty(GoalWizardPanel2.PROP_GOAL_VISION);
                String accountability = (String) wiz.getProperty(GoalWizardPanel3.PROP_GOAL_ACCOUNTABILITY);
                String rewards = (String) wiz.getProperty(GoalWizardPanel4.PROP_GOAL_REWARDS);
                String obstacles = (String) wiz.getProperty(GoalWizardPanel5.PROP_GOAL_OBSTACLES);    
                String support = (String) wiz.getProperty(GoalWizardPanel6.PROP_GOAL_SUPPORT);
                String brainstorming = (String) wiz.getProperty(GoalWizardPanel7.PROP_GOAL_BRAINSTORMING);                       
                VisibilityProvider.Modifier visibilityModifier = (VisibilityProvider.Modifier) wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
                provider.addChildrenGoal(goal.getGoalID(), name, tag, level, startDate, endDate, vision, accountability, rewards, obstacles, support, brainstorming, visibilityModifier);
            }
        }
    }     
}
