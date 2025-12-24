/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.base.PropertiesProvider;
import openpkm.rss.Domain;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=ProjectFactory.class)
public class DomainProjectFactory implements ProjectFactory
{    
    private static final Logger LOG = Logger.getLogger(DomainProjectFactory.class.getName());     
    
    @Override
    public boolean isProject(FileObject projectDirectory) 
    { 
        if (projectDirectory.isFolder())
        {
            FileObject projectFolder = projectDirectory.getFileObject(Domain.PROJECT_FOLDER);
            if (projectFolder != null)
            {
                return projectFolder.getFileObject(Domain.PROJECT_FILE) != null;            
            }            
        }                
        return false;
    }
    
    @Override
    public Project loadProject(FileObject dir,  ProjectState state) throws IOException 
    {
        if (isProject(dir))
        {
            Properties props = new Properties();
            FileObject folder = dir.getFileObject(Domain.PROJECT_FOLDER);  
            props.load(folder.getFileObject(Domain.PROJECT_FILE).getInputStream());  
            return new DomainProject(dir, state, props);   
        }       
        return null;          
    }    

    @Override
    public void saveProject(Project project) throws IOException, ClassCastException 
    {
        if(project instanceof PropertiesProvider)
        {            
            PropertiesProvider provider = (PropertiesProvider)project;
            OutputStream os = new FileOutputStream(project.getProjectDirectory().getFileObject(Domain.PROJECT_FOLDER).getFileObject(Domain.PROJECT_FILE).getPath());
            provider.getProperties().store(os, "Domain project updated");
            os.close();   
        }  
    }      
}
