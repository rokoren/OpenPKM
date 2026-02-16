/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.NodeGroup;
import openpkm.base.NodeProvider;
import openpkm.base.NodeSupport;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class GroupNode extends AbstractNode implements NodeSupport
{
    private static final Logger LOG = Logger.getLogger(GroupNode.class.getName());    
    
    private final NodeGroup nodeGroup;
    
    public GroupNode(NodeGroup nodeGroup) 
    {
        super(new ChildrenImpl(nodeGroup), Lookups.proxy(nodeGroup.getProvider()));
        setName(nodeGroup.getName());
        setDisplayName(nodeGroup.getDisplayName());
        this.nodeGroup = nodeGroup;
    }      

    @Override    
    public Action[] getActions(boolean context) 
    {
        List<Action> actions = new ArrayList();
        actions.addAll(nodeGroup.getActions());
        return actions.toArray(new Action[actions.size()]);
    }

    private Image getIcon(boolean opened) 
    {
        return nodeGroup.getIcon(true, opened);
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
    public Node findNode(Node root, Object target) 
    {
        FileObject file = (FileObject)target;
        return root.getChildren().findChild(file.getName());
        /*
        LOG.info("Finding node: " + target.toString());
        
        for(Node node : root.getChildren().getNodes())
        {
            DataObject data = node.getLookup().lookup(DataObject.class);                                          
            if(data instanceof OpenPkmDataObject && data.getPrimaryFile().equals(target))
            {
                OpenPkmDataObject opdo = (OpenPkmDataObject)data;
                LOG.info("Data found: " + opdo.getDataID());
                LOG.info("Node found: " + node.getName());
                return node;
            }
        }           
        return null;
        */
    } 
    
    private static final class ChildrenImpl extends Children.Keys<NodeProvider> implements ChangeListener 
    {
        private final NodeGroup nodeGroup;            

        public ChildrenImpl(NodeGroup nodeGroup)
        {
            this.nodeGroup = nodeGroup;   
            nodeGroup.addChangeListener(this);             
        }  

        @Override
        protected void addNotify() 
        {
            updateKeys();
        }

        private void updateKeys() 
        {
            setKeys(nodeGroup.getNodes()); 
        }

        @Override
        protected void removeNotify()
        {
            nodeGroup.removeChangeListener(this);            
            setKeys(Collections.<NodeProvider>emptySet());
        }

        @Override
        protected Node[] createNodes(NodeProvider provider) 
        {
            return new Node[] {new ProviderNode(provider)};
        }          

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            updateKeys();
        }                 
    }  
    
    private static final class ProviderNode extends AbstractNode
    {
        private final NodeProvider provider;

        public ProviderNode(NodeProvider provider) 
        {
            super(provider.getChildren(), Lookups.singleton(provider));
            setName(provider.getName());
            setDisplayName(provider.getDisplayName());
            this.provider = provider;
        } 
        
        @Override    
        public Action[] getActions(boolean context) 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(provider.getActions());
            return actions.toArray(new Action[actions.size()]);
        }

        @Override    
        public Image getIcon(int type)     
        {
            return provider.getIcon(false);
        }

        @Override
        public Image getOpenedIcon(int type) 
        {
            return provider.getIcon(true);
        }        
    }      
}
