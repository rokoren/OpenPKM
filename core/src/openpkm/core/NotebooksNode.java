/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.NodeSupport;
import openpkm.base.TitleProvider;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import openpkm.base.ProjectManagementProvider;
import openpkm.base.ProjectManagement;

/**
 *
 * @author Rok Koren
 */
public class NotebooksNode extends AbstractNode implements NodeSupport
{
    private static final Logger LOG = Logger.getLogger(NotebooksNode.class.getName());    
    
    private final ProjectManagementProvider provider;
    
    public NotebooksNode(ProjectManagementProvider provider) 
    {
        super(new ChildrenImpl(provider), Lookups.proxy(provider.getProvider()));
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

    private Image getIcon(boolean opened) 
    {
        return provider.getIcon(true);
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
    
    private static final class ChildrenImpl extends Children.Keys<ProjectManagement> implements ChangeListener 
    {
        private final ProjectManagementProvider provider;            

        public ChildrenImpl(ProjectManagementProvider provider)
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
            SortedSet<ProjectManagement> sorted = new TreeSet<ProjectManagement>(titleComparator());
            sorted.addAll(provider.getProjects());
            setKeys(sorted); 
            
            /*
            SwingUtilities.invokeLater(() -> 
            {
                SortedSet<OpenPkmDataObject> data = new TreeSet<OpenPkmDataObject>(OpenPkmData.titleComparator());
                data.addAll(getData(provider));     
                setKeys(data);    
            });
            */
        }

        @Override
        protected void removeNotify()
        {
            provider.removeChangeListener(this);            
            setKeys(Collections.<ProjectManagement>emptySet());
        }

        @Override
        protected Node[] createNodes(ProjectManagement board) 
        {
            return new Node[] {new ProjectNode(board)};
        }          

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            updateKeys();
        }                 
    }  
    
    private static Comparator<ProjectManagement> titleComparator() 
    {
        return new Comparator<ProjectManagement>() 
        {
            @Override
            public int compare(ProjectManagement board1, ProjectManagement board2) 
            {
                TitleProvider provider1 = board1.getLookup().lookup(TitleProvider.class);
                TitleProvider provider2 = board2.getLookup().lookup(TitleProvider.class);
                if(provider1 != null && provider2 != null)
                {
                    return provider1.getTitle().compareTo(provider2.getTitle());                    
                }
                return -1;
            }
        };
    }     
}
