/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import openpkm.base.NotebooksProvider;

/**
 *
 * @author Rok Koren
 */
@NodeFactory.Registration(projectType="openpkm-project", position=300)
public class BoardNodeFactory implements NodeFactory
{
    private static final Logger LOG = Logger.getLogger(BoardNodeFactory.class.getName());

    @Override
    public NodeList createNodes(Project project) 
    {
        assert project != null;
        
        NotebooksProvider provider = project.getLookup().lookup(NotebooksProvider.class);
        if(provider != null)
        {
            return NodeFactorySupport.fixedNodeList(new NotebooksNode(provider));             
        }
                 
        return NodeFactorySupport.fixedNodeList();
    }     
}
