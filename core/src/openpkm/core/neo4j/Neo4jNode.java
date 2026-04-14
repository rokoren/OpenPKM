/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
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
import openpkm.neo4j.Neo4jInstance;
import openpkm.neo4j.Neo4jInstance.Type;
import openpkm.neo4j.Neo4jProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
@ServicesTabNodeRegistration(name="neo4j", displayName="Neo4j", iconResource = "openpkm/core/resources/logo16.png")
public class Neo4jNode extends AbstractNode
{
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/logo16.png"; 
    
    @StaticResource()
    public static final String BANNER = "openpkm/core/resources/logo256.png";      
    
    public Neo4jNode() 
    {
        super(new Neo4jChildren());
        setName("neo4j");
        setDisplayName("Neo4j");
        setShortDescription("Neo4j Graph Database");
        setIconBaseWithExtension(ICON);
    } 
        
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new AddInstance()
        };
    }  
    
    static final class Neo4jChildren extends Children.Keys<Neo4jInstance> implements ChangeListener 
    {        
        public Neo4jChildren()
        {
            Neo4jProvider provider = Lookup.getDefault().lookup(Neo4jProvider.class);  
            provider.getChangeSupport().addChangeListener(this);
        }  

        protected @Override void addNotify() {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            EventQueue.invokeLater(new Runnable() 
            {
                @Override
                public void run() 
                {  
                    Neo4jProvider provider = Lookup.getDefault().lookup(Neo4jProvider.class);                    
                    SortedSet<Neo4jInstance> subModules = new TreeSet<Neo4jInstance>(Neo4jInstance.nameComparator());
                    subModules.addAll(provider.getInstances());           
                    setKeys(subModules);                   
                }
            });
        }        

        protected @Override void removeNotify() 
        {
            Neo4jProvider provider = Lookup.getDefault().lookup(Neo4jProvider.class); 
            provider.getChangeSupport().removeChangeListener(this);                              
            setKeys(Collections.<Neo4jInstance>emptySet());
        }

        @Override
        protected Node[] createNodes(Neo4jInstance instance) {
            return new Node[] {new Neo4jInstanceNode(instance)};
        }

        @Override
        public void stateChanged(ChangeEvent ev) {
            updateKeys();
        }            
    }      
    
    private static final class AddInstance extends AbstractAction
    {
        public AddInstance() 
        {
            super("Add Instance");
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new Neo4jWizardPanel1());
            panels.add(new Neo4jWizardPanel2());
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
            wiz.setTitle("Add Neo4j Instance");
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));            
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {
                String instanceID = (String) wiz.getProperty(Neo4jInstance.PROP_INSTANCE_ID);
                String neo4jUri = (String) wiz.getProperty(Neo4jInstance.PROP_NEO4J_URI);
                String neo4Username = (String) wiz.getProperty(Neo4jInstance.PROP_NEO4J_USERNAME);
                String neo4jPassword = (String) wiz.getProperty(Neo4jInstance.PROP_NEO4J_PASSWORD);
                String neo4jDatabase = (String) wiz.getProperty(Neo4jInstance.PROP_NEO4J_DATABASE);
                String instanceName = (String) wiz.getProperty(Neo4jInstance.PROP_INSTANCE_NAME);
                Type neo4jType = (Type) wiz.getProperty(Neo4jInstance.PROP_NEO4J_TYPE);
                Neo4jInstance instance = new Neo4jInstanceImpl(instanceID);  
                instance.setNeo4jUri(neo4jUri);
                instance.setNeo4jUsername(neo4Username);
                instance.setNeo4jPassword(neo4jPassword);
                instance.setNeo4jDatabase(neo4jDatabase);
                instance.setInstanceName(instanceName);
                instance.setNeo4jType(neo4jType);
                Neo4jProvider provider = Lookup.getDefault().lookup(Neo4jProvider.class);
                provider.addInstance(instance);
                provider.store(instance);                
            }
        }
    }     
}
