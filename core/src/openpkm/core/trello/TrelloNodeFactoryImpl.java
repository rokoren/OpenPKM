/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import openpkm.base.NodeGroup;
import openpkm.core.GroupNode;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Rok Koren
 */
@NodeFactory.Registration(projectType="openpkm-trello-project", position=100)
public class TrelloNodeFactoryImpl implements NodeFactory
{
    private static final Logger LOG = Logger.getLogger(TrelloNodeFactoryImpl.class.getName());

    @Override
    public NodeList createNodes(Project project) 
    {
        assert project != null;
        
        Collection<? extends NodeGroup> nodeGroups = project.getLookup().lookupAll(NodeGroup.class);
        if(!nodeGroups.isEmpty())
        {
            SortedSet<NodeGroup> sorted = new TreeSet<NodeGroup>(NodeGroup.positionComparator());
            sorted.addAll(nodeGroups);
            
            List<AbstractNode> list = new ArrayList();            
            
            for(NodeGroup nodeGroup : sorted)
            {
                list.add(new GroupNode(nodeGroup)); 
            }         

            AbstractNode[] nodes = new AbstractNode[list.size()];
            list.toArray(nodes);
            return NodeFactorySupport.fixedNodeList(nodes);              
        }          

        return NodeFactorySupport.fixedNodeList();
    }     
}
