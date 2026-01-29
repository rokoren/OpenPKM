/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.util.logging.Logger;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import openpkm.base.NodeSupport;

/**
 *
 * @author Rok Koren
 */
public class RaindropProjectLogicalView implements LogicalViewProvider
{
    private static final Logger LOG = Logger.getLogger(RaindropProjectLogicalView.class.getName());      
    
    private final RaindropProject project;

    public RaindropProjectLogicalView(RaindropProject project)
    {
        this.project = project;
    }

    @Override
    public Node createLogicalView() 
    {
        try 
        {
            //Obtain the project directory's node:
            FileObject projectDirectory = project.getProjectDirectory();
            DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
            Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
            //Decorate the project directory's node:
            return new RaindropProjectNode(nodeOfProjectFolder, project);
        } 
        catch (DataObjectNotFoundException e)        
        {
            LOG.warning(e.getMessage());
            //Fallback-the directory couldn't be created -
            //read-only filesystem or something evil happened
            return new AbstractNode(Children.LEAF);
        }
    }

    @Override
    public Node findPath(Node root, Object target) 
    {
        for(Node child : root.getChildren().getNodes(true))
        {
            NodeSupport provider = child.getLookup().lookup(NodeSupport.class);
            if(provider != null)
            {
                Node node = provider.findNode(child, target);
                if(node != null)
                {
                    return node;
                }
            }         
        }
        return null;
    }     
}
