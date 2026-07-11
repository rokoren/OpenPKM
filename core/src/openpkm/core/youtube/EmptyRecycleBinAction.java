/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.youtube;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;
import openpkm.base.ArchiveProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionState;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author rok
 */
@ActionID(
        category = "OpenPKM/Archive",
        id = "openpkm.core.youtube.EmptyRecycleBinAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/bin_empty.png",
        displayName = "#CTL_EmptyRecycleBinAction",
        enabledOn = @ActionState(
        type = ArchiveProvider.class,
        property = "notEmpty"
    )
)
@Messages("CTL_EmptyRecycleBinAction=Empty Recycle Bin")
public class EmptyRecycleBinAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(EmptyRecycleBinAction.class.getName());     
    
    private final ArchiveProvider provider;

    public EmptyRecycleBinAction(ArchiveProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to delete all items in Recycle Bin", NotifyDescriptor.YES_NO_OPTION);
        Object retVal = DialogDisplayer.getDefault().notify(d);
        if (retVal == NotifyDescriptor.YES_OPTION) 
        {
            try
            {
                for(FileObject file : provider.getFiles())
                {
                    try
                    {
                        DataObject data = DataObject.find(file);
                        if(provider.contains(data))
                        {
                            data.delete();
                        }
                    }
                    catch(DataObjectNotFoundException e)
                    {
                        LOG.info(e.getMessage());
                    }
                }        
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }
        }                 
    }     
}
