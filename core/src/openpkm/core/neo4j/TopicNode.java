/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.Topic;
import openpkm.base.VisibilityProvider;
import openpkm.core.raindrop.TopicRaindropWizardPanel1;
import openpkm.raindrop.RaindropChildrenCollection;
import openpkm.raindrop.RaindropCollection;
import openpkm.raindrop.RaindropCollectionProvider;
import openpkm.utils.Utils;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import openpkm.base.TopicsGraphProvider;

/**
 *
 * @author rokoren
 */
public class TopicNode extends AbstractNode
{
    private static final Logger LOG = Logger.getLogger(TopicNode.class.getName());      
    
    private final TopicsGraphProvider provider;
    private final Topic topic;

    public TopicNode(Project project, TopicsGraphProvider provider, Topic topic) 
    {
        super(new TopicChildren(project, provider, topic), Lookups.fixed(project, topic));
        setName(topic.getTopicID());
        setDisplayName(topic.getName());
        this.provider = provider;
        this.topic = topic;
    }  
    
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new SelectTopic(provider, topic),
            new AddTopic(provider, topic),
            new AddTopicRaindrop(provider, topic)
        };
    }  
    
    @Override
    public Action getPreferredAction()
    {
        return new SelectTopic(provider, topic);
    }    
    
    private Image getIcon(boolean opened) 
    {
        RaindropCollectionProvider raindrop = provider.getProvider().getLookup().lookup(RaindropCollectionProvider.class);
        if(raindrop != null)
        {
            try
            {
                RaindropCollection collection = raindrop.getRaindropAccount().getCollection(Integer.parseInt(topic.getTopicID()));  
                if(collection != null)
                {
                    BufferedImage image = collection.getImage();
                    if(image != null)
                    {
                        return Utils.resizeImage(image, 16, 16);
                    }                    
                }
            }
            catch(NumberFormatException e)
            {
                LOG.fine(e.getMessage());
            }             
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }            
        }
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
    
    static final class TopicChildren extends Children.Keys<Topic> implements ChangeListener 
    {
        private final Project project;
        private final TopicsGraphProvider topicProvider;
        private final Topic topic;        

        public TopicChildren(Project project, TopicsGraphProvider topicProvider, Topic topic)
        {
            this.project = project;
            this.topicProvider = topicProvider;
            this.topic = topic;
            
            if(topicProvider instanceof ChangeSupportProvider provider)
            {
                provider.addChangeListener(this);                    
            } 
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
                    SortedSet<Topic> topics = new TreeSet<Topic>(Topic.nameComparator());
                    topics.addAll(topicProvider.getChildrenTopics(topic.getTopicID()));           
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

    private static final class SelectTopic extends AbstractAction
    {
        private final TopicsGraphProvider provider;      
        private final Topic topic;   

        public SelectTopic(TopicsGraphProvider provider, Topic topic) 
        {
            super("Select Topic");
            this.provider = provider;
            this.topic = topic;
            setEnabled(!provider.getSelectedTopics().contains(topic));
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            provider.selectTopic(topic);
        }
    }     
    
    private static final class AddTopic extends AbstractAction
    {
        private final TopicsGraphProvider provider;      
        private final Topic topic;   

        public AddTopic(TopicsGraphProvider provider, Topic topic) 
        {
            super("Add Topic");
            this.provider = provider;
            this.topic = topic;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new TopicWizardPanel1());
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
            wiz.setTitle("Add Topic");        
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {
                String name = (String) wiz.getProperty("name");
                String tag = (String) wiz.getProperty("tag");
                VisibilityProvider.Modifier visibilityModifier = (VisibilityProvider.Modifier) wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
                provider.addChildrenTopic(topic.getTopicID(), name, tag, visibilityModifier);
            }
        }
    } 
    
    private static final class AddTopicRaindrop extends AbstractAction
    {
        private final TopicsGraphProvider provider;      
        private final Topic topic; 
        
        private List<RaindropChildrenCollection> collections;        

        public AddTopicRaindrop(TopicsGraphProvider provider, Topic topic) 
        {
            super("Add Topic from Raindrop");
            this.provider = provider;
            this.topic = topic;
            RaindropCollectionProvider raindrop = provider.getProvider().getLookup().lookup(RaindropCollectionProvider.class);
            if(raindrop != null)
            {
                try
                {
                    RaindropCollection collection = raindrop.getRaindropAccount().getCollection(Integer.parseInt(topic.getTopicID())); 
                    if(collection != null)
                    {
                        collections = raindrop.getRaindropAccount().getChildrenCollections(collection.getCollectionID());                        
                    }
                }
                catch(NumberFormatException e)
                {
                    LOG.fine(e.getMessage());
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
            wiz.setTitle("Add Topic from Raindrop");        
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {
                RaindropCollection collection = (RaindropCollection) wiz.getProperty("collection");
                String tag = (String) wiz.getProperty("tag");
                VisibilityProvider.Modifier visibilityModifier = (VisibilityProvider.Modifier) wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
                provider.addChildrenTopic(topic.getTopicID(), collection.getCollectionID() + "", collection.getTitle(), tag, visibilityModifier);
            }
        }
    }     
}
