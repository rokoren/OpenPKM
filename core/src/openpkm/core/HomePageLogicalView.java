/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import openpkm.base.NodeProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;

/**
 *
 * @author Rok Koren
 */
public class HomePageLogicalView implements LogicalViewProvider
{
    private final HomePageProject project;

    public HomePageLogicalView(HomePageProject project)
    {
        this.project = project;
    }

    @Override
    public Node createLogicalView() 
    {
        //Obtain the project directory's node:
        FileObject projectDirectory = project.getProjectDirectory();
        DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
        Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
        //Decorate the project directory's node:
        return new HomePageNode(nodeOfProjectFolder, project);
    }

    @Override
    public Node findPath(Node root, Object target) 
    {
        for(Node child : root.getChildren().getNodes(true))
        {
            NodeProvider provider = child.getLookup().lookup(NodeProvider.class);
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
