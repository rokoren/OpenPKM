/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeListener;
import openpkm.base.Source;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.SourceProviders;
import openpkm.base.StateSupport;
import openpkm.base.WorkflowProvider;
import openpkm.domain.WebPage;
import openpkm.jcef.CefClientProvider;
import openpkm.youtube.YouTubeCefClientProvider;
import openpkm.youtube.YouTubeVideo;
import org.cef.browser.CefBrowser;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class SourceWizardPanel implements ActionListener, WizardDescriptor.FinishablePanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(SourceWizardPanel.class.getName());     
        
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SourceVisualPanel component;
    
    private final SourceProviderWrapper sourceProvider;    
    private CefBrowser browser;    
    private final JComboBox<WorkflowProvider.Workflow> comboBox;   
    private final JButton button;
    
    private final DefaultComboBoxModel<WorkflowProvider.Workflow> workflows = new DefaultComboBoxModel<>();  

    public SourceWizardPanel(SourceProviderWrapper sourceProvider, WorkflowProvider.Workflow selectedWorkflow) 
    {
        this.sourceProvider = sourceProvider;
        setModifiers(selectedWorkflow);
        comboBox = new JComboBox<>(workflows);  
        button = new JButton("Delete");
        button.addActionListener(this);
    }  
    
    private void setModifiers(WorkflowProvider.Workflow selectedWorkflow)
    {
        workflows.removeAllElements();
        workflows.addAll(Arrays.asList(WorkflowProvider.Workflow.values()));  
        workflows.setSelectedItem(selectedWorkflow);
    }     
    
    public void finish(boolean isFinish)
    {
        if(isFinish) 
        {
            Source source = sourceProvider.getSource();
            if(source instanceof WorkflowProvider workflowProvider)
            {
                WorkflowProvider.Workflow workflow = (WorkflowProvider.Workflow)workflows.getSelectedItem();
                workflowProvider.setWorkflow(workflow);
                if(source instanceof StateSupport state)
                {
                    state.markModified();
                }
                SourceProviders sourceProviders = sourceProvider.getProvider().getProvider().getLookup().lookup(SourceProviders.class);
                if(sourceProviders != null)
                {
                    sourceProviders.sourceModified(new SourceEventImpl(sourceProvider.getProvider(), source));
                }
            }            
        }            

        if(browser != null)
        {
            browser.close(true);
        }
    }

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public SourceVisualPanel getComponent() 
    {
        if(browser == null)
        {
            Source source = sourceProvider.getSource();
            if(source instanceof WebPage page)
            {
                CefClientProvider provider = Lookup.getDefault().lookup(CefClientProvider.class);
                if(provider != null)
                {
                    try
                    {
                        browser = provider.getCefClient().createBrowser(page.getLinkUrl(), true, false);  
                    }
                    catch(Exception e)
                    {
                        LOG.warning(e.getMessage());
                    }                   
                }                  
            }            
            else if(source instanceof YouTubeVideo video)
            {
                YouTubeCefClientProvider provider = Lookup.getDefault().lookup(YouTubeCefClientProvider.class);
                if(provider != null)
                {
                    try
                    {
                        browser = provider.getBrowser(video);                     
                    }
                    catch(Exception e)
                    {
                        LOG.warning(e.getMessage());
                    }                   
                }                  
            }            
        }        
        if (component == null && browser != null) 
        {
            component = new SourceVisualPanel(sourceProvider.getSource(), browser);   
            if(sourceProvider.getSource() instanceof WebPage)
            {
                component.setPreferredSize(new Dimension(800, 800));
            }              
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }   

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }   

    @Override
    public void readSettings(WizardDescriptor wiz) 
    {
        JLabel label = new JLabel("Workflow:");
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(label);
        panel.add(comboBox);
        panel.add(new JSeparator(JSeparator.VERTICAL));
        panel.add(button);
        Object[] options = {panel};
        wiz.setAdditionalOptions(options);        
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) {
    }     

    @Override
    public boolean isFinishPanel() 
    {
        return true;
    }  
    
    public void actionPerformed(ActionEvent evt)
    {
        workflows.setSelectedItem(WorkflowProvider.Workflow.RECYCLE_BIN);        
    }
}
