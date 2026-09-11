/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.Goal;
import openpkm.base.GoalsProvider;
import openpkm.base.IconsProvider;
import openpkm.base.TagsProvider;
import openpkm.base.Thought;
import openpkm.base.ThoughtProvider;
import openpkm.base.ThoughtsGraphProvider;
import openpkm.base.ThoughtsProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rok
 */
public class TreeOfThoughtsNode extends AbstractNode
{
    private static final Logger LOG = Logger.getLogger(TreeOfThoughtsNode.class.getName());  
    
    private final ThoughtProvider thoughtProvider;

    public TreeOfThoughtsNode(ThoughtProvider thoughtProvider) 
    {
        super(new ThoughtChildren(thoughtProvider), Lookups.singleton(thoughtProvider));
        setName(thoughtProvider.getThought().getThoughtID());
        setDisplayName(thoughtProvider.getThought().getText());
        this.thoughtProvider = thoughtProvider;
    }  
    
    public ThoughtProvider getThoughtProvider()
    {
        return thoughtProvider;
    }
    
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            //new SelectThought(provider, thought),
            new AddThought(thoughtProvider)
        };
    }  
    
    private Image getIcon(boolean opened) 
    {
        IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
        return provider.getImage(thoughtProvider.getThought().getType().getIcon());        
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


    
    public static class ThoughtProviderImpl implements ThoughtProvider, ThoughtsProvider
    {
        private final Thought thought;
        private final ThoughtsGraphProvider provider;
        
        private final ChangeSupport changeSupport = new ChangeSupport(this);

        public ThoughtProviderImpl(Thought thought, ThoughtsGraphProvider provider) 
        {
            this.thought = thought;
            this.provider = provider;
        }                

        @Override
        public Thought getThought() 
        {
            return thought;
        }

        @Override
        public ThoughtsGraphProvider getProvider() 
        {
            return provider;
        } 
        
        @Override
        public Thought addChildrenThought(String text, Thought.Type type, Set<String> tags, Set<Topic> topics, Set<Goal> goals) 
        {
            Thought child = provider.addChildrenThought(thought, text, type, tags, topics, goals);
            if(child != null)
            {
                changeSupport.fireChange();
            }
            return child;
        }        
        
        @Override
        public Set<Thought> getThoughts() 
        {
            List<Thought> thoughts = provider.getChildrenThoughts(thought.getThoughtID());
            if(thoughts.isEmpty())
            {
                return Collections.EMPTY_SET;
            }
            return new HashSet<>(thoughts);
        }        

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }
    }
    
    static final class ThoughtChildren extends Children.Keys<Thought> implements ChangeListener 
    {
        private final ThoughtProvider provider;      

        public ThoughtChildren(ThoughtProvider provider)
        {
            this.provider = provider;
            provider.addChangeListener(this);           
        }  

        @Override
        protected void addNotify() 
        {
            updateKeys();                             
        }

        private void updateKeys() 
        { 
            SortedSet<Thought> thoughts = new TreeSet<>(Thought.textComparator());
            thoughts.addAll(provider.getProvider().getChildrenThoughts(provider.getThought().getThoughtID()));           
            setKeys(thoughts);                 
            
            /*
            EventQueue.invokeLater(new Runnable() 
            {
                @Override
                public void run()
                {                                                
                    SortedSet<Thought> thoughts = new TreeSet<>(Thought.textComparator());
                    thoughts.addAll(thoughtsProvider.getChildrenThoughts(thought.getThoughtID()));           
                    setKeys(thoughts);                   
                }
            });
            */
        }        

        @Override
        protected void removeNotify() 
        {
            provider.removeChangeListener(this);
            setKeys(Collections.<Thought>emptySet());
        }

        @Override
        protected Node[] createNodes(Thought thought) 
        {
            ThoughtProvider thoughtProvider = new TreeOfThoughtsNode.ThoughtProviderImpl(thought, provider.getProvider());
            return new Node[] {new TreeOfThoughtsNode(thoughtProvider)};
        }

        @Override
        public void stateChanged(ChangeEvent ev) 
        {
            updateKeys();
        }            
    }  

    private static final class SelectThought extends AbstractAction
    {
        private final ThoughtsGraphProvider thoughtsProvider;
        private final Thought thought;   

        public SelectThought(ThoughtsGraphProvider thoughtsProvider, Thought thought) 
        {
            super("Select Thought");
            this.thoughtsProvider = thoughtsProvider;
            this.thought = thought;
            setEnabled(!thoughtsProvider.getSelectedThoughts().contains(thought));
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            thoughtsProvider.selectThought(thought);
        }
    }     
    
    private static final class AddThought extends AbstractAction
    {
        private final ThoughtProvider provider;

        public AddThought(ThoughtProvider provider) 
        {
            super("Add Thought");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
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
            wiz.setTitle("Add Thought");  
            //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            wiz.putProperty("provider", provider.getProvider());
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            { 
                String text = (String) wiz.getProperty("text");  
                Thought.Type type = (Thought.Type) wiz.getProperty("type");  
                Set<String> tags = (Set<String>) wiz.getProperty(TagsProvider.PROP_TAGS);
                Set<Topic> topics = (Set<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);
                Set<Goal> goals = (Set<Goal>) wiz.getProperty(GoalsProvider.PROP_GOALS);  
                Thought thought = provider.addChildrenThought(text, type, tags, topics, goals);
                if(thought != null)
                {
                    StatusDisplayer.getDefault().setStatusText("Thought saved with text: " + text);                
                }
            } 
        }
    }     
}
