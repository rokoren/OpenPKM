/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import openpkm.neo4j.Neo4jInstance;
import openpkm.neo4j.Neo4jProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class Neo4jInstanceNode extends AbstractNode
{
    private static final Logger LOG = Logger.getLogger(Neo4jInstanceNode.class.getName());
    
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/database.png";        
    
    private final Neo4jInstance instance;
    
    public Neo4jInstanceNode(Neo4jInstance instance) 
    {
        super(Children.LEAF, Lookups.fixed(instance));
        setName(instance.getInstanceID());
        setDisplayName(instance.getName());   
        setShortDescription(instance.getType().toString());
        setIconBaseWithExtension(ICON);
        this.instance = instance;
    }   
    
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new RemoveInstance(instance)
        };
    }     
    
    private static final class RemoveInstance extends AbstractAction
    {
        private final Neo4jInstance instance;

        public RemoveInstance(Neo4jInstance instance) 
        {
            super("Remove Instance");
            this.instance = instance;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            NotifyDescriptor d = new NotifyDescriptor(
                    "Do you want to remove instance?", // message
                    instance.getName(), // title
                    NotifyDescriptor.YES_NO_OPTION, // option type
                    NotifyDescriptor.QUESTION_MESSAGE, // message type
                    null, // custom buttons (as Object[])
                    null); // default value
            if(DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION)
            {
                Neo4jProvider provider = Lookup.getDefault().lookup(Neo4jProvider.class);
                try
                {
                    instance.getPreferences().removeNode(); 
                    instance.getPreferences().flush();
                    provider.removeInstance(instance);
                }
                catch(BackingStoreException e)
                {
                    LOG.warning(e.getMessage());
                }
            }
        }
    }     
}
