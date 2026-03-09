/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.youtube;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.base.PropertiesProvider;
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
public class YouTubeChannelProjectFactory implements ProjectFactory
{
    public static final String PROJECT_FOLDER = "openpkm-youtube-project"; 
    public static final String PROJECT_FILE   = "project.properties";      
    
    private static final Logger LOG = Logger.getLogger(YouTubeChannelProjectFactory.class.getName());     
    
    @Override
    public boolean isProject(FileObject projectDirectory) 
    { 
        if (projectDirectory.isFolder())
        {
            FileObject projectFolder = projectDirectory.getFileObject(PROJECT_FOLDER);
            if (projectFolder != null)
            {
                return projectFolder.getFileObject(PROJECT_FILE) != null;            
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
            FileObject folder = dir.getFileObject(PROJECT_FOLDER);  
            props.load(folder.getFileObject(PROJECT_FILE).getInputStream());  
            return new YouTubeChannelProject(dir, state, props);   
        }       
        return null;          
    }    

    @Override
    public void saveProject(Project project) throws IOException, ClassCastException 
    {
        if(project instanceof PropertiesProvider)
        {            
            PropertiesProvider provider = (PropertiesProvider)project;
            OutputStream os = new FileOutputStream(project.getProjectDirectory().getFileObject(PROJECT_FOLDER).getFileObject(PROJECT_FILE).getPath());
            provider.getProperties().store(os, "YouTube Channel project updated");
            os.close();   
        }  
    }     
}
