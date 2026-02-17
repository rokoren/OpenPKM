/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.AbstractNode;
import openpkm.base.DataGroupProvider;
import openpkm.base.GroupProvider;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;

/**
 *
 * @author Rok Koren
 */
@NodeFactory.Registration(projectType="openpkm-project", position=100)
public class NodeFactoryImpl implements NodeFactory
{
    private static final Logger LOG = Logger.getLogger(NodeFactoryImpl.class.getName());

    @Override
    public NodeList createNodes(Project project) 
    {
        assert project != null;
        
        Collection<? extends DataGroupProvider> providers = project.getLookup().lookupAll(DataGroupProvider.class);
        if(!providers.isEmpty())
        {
            SortedSet<DataGroupProvider> sorted = new TreeSet<DataGroupProvider>(GroupProvider.positionComparator());
            sorted.addAll(providers);
            
            List<AbstractNode> list = new ArrayList();            
            
            for(DataGroupProvider provider : sorted)
            {
                list.add(new DataGroupNode(provider)); 
            }         

            AbstractNode[] nodes = new AbstractNode[list.size()];
            list.toArray(nodes);
            return NodeFactorySupport.fixedNodeList(nodes);              
        }          

        return NodeFactorySupport.fixedNodeList();
    }            
}
