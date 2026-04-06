/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.jcef;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import me.friwi.jcefmaven.EnumProgress;
import me.friwi.jcefmaven.IProgressHandler;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;

/**
 *
 * @author rokor
 */
public class ProgressHandlerImpl extends AbstractAction implements IProgressHandler, Cancellable
{
    private final ProgressHandle handle;

    public ProgressHandlerImpl() 
    {
        handle = ProgressHandleFactory.createSystemUIHandle("JCEF INITIALIZING", this, this);
        handle.start();
    }         
    
    @Override
    public void handleProgress(EnumProgress progress, float units) 
    {
        handle.setDisplayName("JCEF " + progress.name());
        if(units == -1)
        {
            handle.switchToIndeterminate();           
        }
        else
        {
            handle.switchToDeterminate(100);
            handle.progress((int)units);
        }        
             
        if(progress == EnumProgress.INITIALIZED)
        {
            handle.finish();
        }     
    }   

    @Override
    public boolean cancel() 
    {
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
    }
}
