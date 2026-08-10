/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.Goal;
import openpkm.base.GoalsGraphProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.Topic;
import openpkm.core.raindrop.TopicRaindropWizardPanel1;
import openpkm.raindrop.RaindropChildrenCollection;
import openpkm.raindrop.RaindropCollection;
import openpkm.raindrop.RaindropCollectionProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rok
 */
@NodeFactory.Registration(projectType="openpkm-project", position=100)
public class GraphNodeFactory implements NodeFactory
{
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/tree_list.png";     
    
    private static final Logger LOG = Logger.getLogger(GraphNodeFactory.class.getName());

    @Override
    public NodeList createNodes(Project project)
    {
        assert project != null; 
        
        List<AbstractNode> list = new ArrayList();

        KnowledgeGraphProvider knowledgeProvider = project.getLookup().lookup(KnowledgeGraphProvider.class);
        if(knowledgeProvider != null)
        {
            list.add(new TopicsNode(project, knowledgeProvider));
        }   

        GoalsGraphProvider goalsProvider = project.getLookup().lookup(GoalsGraphProvider.class);
        if(goalsProvider != null)
        {
            list.add(new GoalsNode(project, goalsProvider));
        }               

        AbstractNode[] nodes = new AbstractNode[list.size()];
        list.toArray(nodes);        
        
        if(list.isEmpty())
        {
            return NodeFactorySupport.fixedNodeList();                                   
        }                     
        return NodeFactorySupport.fixedNodeList(nodes); 
    }   
        
    static final class TopicsNode extends AbstractNode implements ChangeListener 
    {        
        private final KnowledgeGraphProvider topicProvider;

        TopicsNode(Project project, KnowledgeGraphProvider topicProvider)
        {
            super(new TopicsChildren(project, topicProvider), Lookups.fixed(project));
            this.topicProvider = topicProvider;
            setName("topics"); // NOI18N
            
            if(topicProvider instanceof ChangeSupportProvider provider)
            {
                provider.addChangeListener(this);                    
            }             
        } 
        
        private String getTopicsName(Collection<Topic> topics)
        {
            if(!topics.isEmpty())
            {
                StringJoiner joiner = new StringJoiner(", ");
                Iterator<Topic> iterator = topics.iterator();
                while(iterator.hasNext())
                {
                    joiner.add(iterator.next().getName());
                } 

                LOG.info("Topics: " + joiner.toString());

                return joiner.toString();                
            }
            return null;
        }        

        @Override
        public Action[] getActions(boolean context) 
        {
            return new Action[]
            {
                new AddNode(topicProvider),
                new AddNodeRaindrop(topicProvider),
                new ClearSelectedNodes(topicProvider)
            };
        }
        
        @Override
        public String getDisplayName() 
        {
            String topicsName = getTopicsName(topicProvider.getSelectedTopics());
            if(topicsName != null)
            {
                return "Topics [" + topicsName + "]";      
            }
            return "Topics";
        }         

        private Image getIcon(boolean opened) 
        {
            return ImageUtilities.loadImage(ICON);
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

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            fireIconChange();
        }

        static final class TopicsChildren extends Children.Keys<Topic> implements ChangeListener 
        {
            private final Project project;
            private final KnowledgeGraphProvider topicProvider;

            public TopicsChildren(Project project, KnowledgeGraphProvider topicProvider)
            {
                this.project = project;
                this.topicProvider = topicProvider;
                
                if(topicProvider instanceof ChangeSupportProvider provider)
                {
                    provider.addChangeListener(this);                    
                }                 
            }  
                        
            protected @Override void addNotify() 
            {
                updateKeys();                             
            }

            private void updateKeys() 
            {
                EventQueue.invokeLater(new Runnable() 
                {
                    public void run()
                    {                                                
                        SortedSet<Topic> topics = new TreeSet<Topic>(Topic.nameComparator());
                        topics.addAll(topicProvider.getRootTopics());           
                        setKeys(topics);                   
                    }
                });
            }        

            @Override
            protected void removeNotify() 
            {
                if(topicProvider instanceof ChangeSupportProvider provider)
                {
                    provider.removeChangeListener(this);                    
                }                  
                                             
                setKeys(Collections.<Topic>emptySet());
            }

            @Override
            protected Node[] createNodes(Topic topic) 
            {
                return new Node[] {new TopicNode(project, topicProvider, topic)};
            }

            @Override
            public void stateChanged(ChangeEvent ev) 
            {
                updateKeys();
            }            
        }  
        
        private static final class AddNode extends AbstractAction
        {
            private final KnowledgeGraphProvider provider;            
            
            public AddNode(KnowledgeGraphProvider provider) 
            {
                super("Add Root Topic");
                this.provider = provider;
            }

            @Override
            public void actionPerformed(ActionEvent evt) 
            {
                List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
                panels.add(new TopicWizardPanel1());
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
                wiz.setTitle("Add Root Topic");        
                if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
                {
                    String name = (String) wiz.getProperty("name");
                    String tag = (String) wiz.getProperty("tag");
                    provider.addRootTopic(name, tag);
                }
            }
        }  
        
        private static final class AddNodeRaindrop extends AbstractAction
        {
            private final KnowledgeGraphProvider provider; 
            
            private List<RaindropChildrenCollection> collections;             
            
            public AddNodeRaindrop(KnowledgeGraphProvider provider) 
            {
                super("Add Root Topic from Raindrop");
                this.provider = provider;
                RaindropCollectionProvider raindrop = provider.getProvider().getLookup().lookup(RaindropCollectionProvider.class);
                if(raindrop != null)
                {
                    try
                    {
                        collections = raindrop.getRaindropAccount().getChildrenCollections(raindrop.getRaindropCollection().getCollectionID());
                    }
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }                      
                }
                setEnabled(collections != null && !collections.isEmpty());
            }

            @Override
            public void actionPerformed(ActionEvent evt) 
            {
                List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
                panels.add(new TopicRaindropWizardPanel1(collections));
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
                wiz.setTitle("Add Root Topic from Raindrop");        
                if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
                {
                    RaindropCollection collection = (RaindropCollection) wiz.getProperty("collection");
                    String tag = (String) wiz.getProperty("tag");
                    provider.addRootTopic(collection.getCollectionID() + "", collection.getTitle(), tag);
                }
            }
        }          

        private static final class ClearSelectedNodes extends AbstractAction
        {
            private final KnowledgeGraphProvider provider;            
            
            public ClearSelectedNodes(KnowledgeGraphProvider provider) 
            {
                super("Clear Selected Topics");
                this.provider = provider;
            }

            @Override
            public void actionPerformed(ActionEvent evt) 
            {
                provider.clearSelectedTopics();
            }
        } 
    } 
    
    static final class GoalsNode extends AbstractNode implements ChangeListener 
    {        
        private final GoalsGraphProvider goalProvider;

        GoalsNode(Project project, GoalsGraphProvider goalProvider)
        {
            super(new GoalsChildren(project, goalProvider), Lookups.fixed(project));
            this.goalProvider = goalProvider;
            setName("goals"); // NOI18N
            
            if(goalProvider instanceof ChangeSupportProvider provider)
            {
                provider.addChangeListener(this);                    
            }             
        } 
        
        private String getGoalsName(Collection<Goal> goals)
        {
            if(!goals.isEmpty())
            {
                StringJoiner joiner = new StringJoiner(", ");
                Iterator<Goal> iterator = goals.iterator();
                while(iterator.hasNext())
                {
                    joiner.add(iterator.next().getName());
                } 

                LOG.info("Goals: " + joiner.toString());

                return joiner.toString();                
            }
            return null;
        }          

        @Override
        public Action[] getActions(boolean context) 
        {
            return new Action[]
            {
                new AddGoal(goalProvider),
                new ResetSelectedGoals(goalProvider)
            };
        }
        
        @Override
        public String getDisplayName() 
        {
            String goalsName = getGoalsName(goalProvider.getSelectedGoals());
            if(goalsName != null)
            {
                return "Goals [" + goalsName + "]";      
            }
            return "Goals";
        }         

        private Image getIcon(boolean opened) 
        {
            return ImageUtilities.loadImage(ICON);
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

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            fireIconChange();
        }

        static final class GoalsChildren extends Children.Keys<Goal> implements ChangeListener 
        {
            private final Project project;
            private final GoalsGraphProvider goalProvider;

            public GoalsChildren(Project project, GoalsGraphProvider goalProvider)
            {
                this.project = project;
                this.goalProvider = goalProvider;
                
                if(goalProvider instanceof ChangeSupportProvider provider)
                {
                    provider.addChangeListener(this);                    
                }                 
            }  
                        
            protected @Override void addNotify() 
            {
                updateKeys();                             
            }

            private void updateKeys() 
            {
                EventQueue.invokeLater(new Runnable() 
                {
                    public void run()
                    {                                                
                        SortedSet<Goal> goals = new TreeSet<Goal>(Goal.nameComparator());
                        goals.addAll(goalProvider.getRootGoals());           
                        setKeys(goals);                   
                    }
                });
            }        

            @Override
            protected void removeNotify() 
            {
                if(goalProvider instanceof ChangeSupportProvider provider)
                {
                    provider.removeChangeListener(this);                    
                }                  
                                             
                setKeys(Collections.<Goal>emptySet());
            }

            @Override
            protected Node[] createNodes(Goal goal) 
            {
                return new Node[] {new GoalNode(project, goalProvider, goal)};
            }

            @Override
            public void stateChanged(ChangeEvent ev) 
            {
                updateKeys();
            }            
        }  
        
        private static final class AddGoal extends AbstractAction
        {
            private final GoalsGraphProvider provider;            
            
            public AddGoal(GoalsGraphProvider provider) 
            {
                super("Add Root Goal");
                this.provider = provider;
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
                wiz.setTitle("Add Root Goal");        
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
                    provider.addRootGoal(name, tag, level, startDate, endDate, vision, accountability, rewards, obstacles, support, brainstorming);
                }
            }
        }  

        private static final class ResetSelectedGoals extends AbstractAction
        {
            private final GoalsGraphProvider provider;            
            
            public ResetSelectedGoals(GoalsGraphProvider provider) 
            {
                super("Clear Selected Goals");
                this.provider = provider;
            }

            @Override
            public void actionPerformed(ActionEvent evt) 
            {
                provider.clearSelectedGoals();
            }
        } 
    }      
}
