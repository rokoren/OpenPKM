/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import openpkm.base.Source;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.OnShowing;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Rok Koren
 */
@OnShowing
public class Installer implements Runnable
{
    @Override
    public void run() 
    {        
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt) 
            {
                if (evt.getPropertyName().equals("opened")) 
                {
                    HashSet<TopComponent> newHashSet = (HashSet<TopComponent>) evt.getNewValue();
                    HashSet<TopComponent> oldHashSet = (HashSet<TopComponent>) evt.getOldValue();
                    for (TopComponent topComponent : newHashSet) 
                    {
                        //if (!oldHashSet.contains(topComponent)) 
                        {
                            DataObject data = topComponent.getLookup().lookup(DataObject.class);
                            if (data != null) 
                            {
                                Project project = data.getLookup().lookup(Project.class);
                                if(project != null)
                                {
                                    Project[] projects = {project};
                                    OpenProjects.getDefault().open(projects, false);                                         
                                }                                 
                                
                                FileObject currentFile = data.getPrimaryFile();
                                MultiViewDescription mvd = data.getLookup().lookup(MultiViewDescription.class);
                                if(mvd != null)
                                {
                                    MultiViewHandler handler = MultiViews.findMultiViewHandler(topComponent);
                                    if(handler != null)
                                    {
                                        MultiViewPerspective[] perspectives = handler.getPerspectives();
                                        if(!hasPerspective(perspectives, mvd.preferredID()))
                                        {
                                            MultiViewPerspective perspective = handler.getSelectedPerspective();
                                            handler.addMultiViewDescription(mvd, 3);
                                            if(perspective != null)
                                            {
                                                handler.requestActive(perspective);
                                            }                                             
                                        }                                      
                                    }                                                                       
                                    
                                    //StatusDisplayer.getDefault().setStatusText("Opened: " + source.getSourceID(), 1);
                                    /*
                                    if (currentFile != null & amp; & amp;
                                    currentFile.getMIMEType().equals("text/x-java")

                                        ) {

                                        currentFile.addFileChangeListener(new FileChangeAdapter() {
                                            @Override
                                            public void fileChanged(FileEvent fe) {
                                                StatusDisplayer.getDefault().setStatusText("Hurray! "
                                                        + "Saved " + fe.getFile().getNameExt(), 1);
                                            }
                                        });
                                    }
                                    */                                    
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private boolean hasPerspective(MultiViewPerspective[] perspectives, String preferredID)
    {
        for(MultiViewPerspective perspective : perspectives)
        {
            if(perspective.preferredID().equals(preferredID))
            {
                return true;
            }
        }
        return false;
    }
}
