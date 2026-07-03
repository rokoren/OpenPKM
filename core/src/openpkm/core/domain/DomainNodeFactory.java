/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import java.util.logging.Logger;
import openpkm.base.DomainsProvider;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;

/**
 *
 * @author Rok Koren
 */
@NodeFactory.Registration(projectType="openpkm-project", position=200)
public class DomainNodeFactory implements NodeFactory
{
    private static final Logger LOG = Logger.getLogger(DomainNodeFactory.class.getName());

    @Override
    public NodeList createNodes(Project project) 
    {
        assert project != null;
        
        DomainsProvider provider = project.getLookup().lookup(DomainsProvider.class);
        if(provider != null)
        {
            return NodeFactorySupport.fixedNodeList(new DomainsNode(provider));             
        }
                 
        return NodeFactorySupport.fixedNodeList();
    }      
}
