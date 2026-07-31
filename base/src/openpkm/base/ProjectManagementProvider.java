/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface ProjectManagementProvider
{
    FileObject getRootDirectory() throws IOException;    
    Collection<ProjectManagement> getProjects();  
    void addProject(ProjectManagement project);
    void removeProject(String projectID);
    Lookup.Provider getProvider();
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener); 
    List<Action> getActions();
    String getName();
    String getDisplayName();
    Image getIcon(boolean hasChildren);     
}
