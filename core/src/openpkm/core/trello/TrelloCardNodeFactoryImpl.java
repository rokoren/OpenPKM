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
import openpkm.base.GroupProvider;
import openpkm.core.GroupNode;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.AbstractNode;
import openpkm.base.SourceGroupProvider;

/**
 *
 * @author Rok Koren
 */
@NodeFactory.Registration(projectType="openpkm-trello-card-project", position=100)
public class TrelloCardNodeFactoryImpl implements NodeFactory
{
    private static final Logger LOG = Logger.getLogger(TrelloCardNodeFactoryImpl.class.getName());

    @Override
    public NodeList createNodes(Project project) 
    {
        assert project != null;
        
        Collection<? extends SourceGroupProvider> nodeGroups = project.getLookup().lookupAll(SourceGroupProvider.class);
        if(!nodeGroups.isEmpty())
        {
            SortedSet<SourceGroupProvider> sorted = new TreeSet<SourceGroupProvider>(GroupProvider.positionComparator());
            sorted.addAll(nodeGroups);
            
            List<AbstractNode> list = new ArrayList();            
            
            for(SourceGroupProvider nodeGroup : sorted)
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
