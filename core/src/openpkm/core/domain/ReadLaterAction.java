/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.ReadLaterProvider;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.WorkflowProvider;
import openpkm.utils.SourceWizardPanel;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionState;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/ReadLater",
        id = "openpkm.core.domain.ReadLaterAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/watch_window.png",
        displayName = "#CTL_ReadLaterAction",
        enabledOn = @ActionState(
        type = ReadLaterProvider.class,
        property = "notEmpty"
    )
)
@Messages("CTL_ReadLaterAction=Read RSS News")
public class ReadLaterAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(ReadLaterAction.class.getName());     
    
    private final ReadLaterProvider provider;

    public ReadLaterAction(ReadLaterProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<SourceWizardPanel> panels = new ArrayList<SourceWizardPanel>();
        
        try
        {
            for(FileObject file : provider.getFiles())
            {
                DataObject data = null;
                if(file.isData())
                {
                    try
                    {
                        data = DataObject.find(file);                    
                    }
                    catch(DataObjectNotFoundException e)
                    {
                        LOG.warning(e.getMessage());
                    }
                }
                else if(file.isFolder())
                {
                    data = DataFolder.findFolder(file);
                }  
                
                if(data != null)
                {
                    SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                    if(sourceProvider.getSource() instanceof WorkflowProvider workflowProvider)
                    {
                        if(workflowProvider.getWorkflow() == WorkflowProvider.Workflow.READ_LATER)
                        {
                            panels.add(new SourceWizardPanel(sourceProvider));                  
                        }            
                    }                                                                                 
                }                  
            }              
        } 
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }
        
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) 
        {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()  
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Watch RSS News");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        boolean isFinish = DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION;
        for(SourceWizardPanel panel : panels)
        {
            panel.finish(isFinish);          
        }                                                                  
    }     
}
