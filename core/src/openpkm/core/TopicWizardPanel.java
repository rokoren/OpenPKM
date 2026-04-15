/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeListener;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public class TopicWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, ActionListener
{
    private static final Logger LOG = Logger.getLogger(TopicWizardPanel.class.getName());    
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private TopicVisualPanel component;
    
    private final JButton button;
    private final Set<Topic> topics;

    public TopicWizardPanel() 
    {
        topics = new HashSet<>();
        button = new JButton("Add");
        button.addActionListener(this);
    }        

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public TopicVisualPanel getComponent() 
    {
        if (component == null) 
        {            
            component = new TopicVisualPanel();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() 
    {
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
    public void readSettings(WizardDescriptor descriptor) 
    {
        Object[] options = {button};
        descriptor.setAdditionalOptions(options);
        // use wiz.getProperty to retrieve previous panel state
        Lookup.Provider provider = (Lookup.Provider)descriptor.getProperty("provider");
        if(provider != null)
        { 
            getComponent().setTopics(provider);
        }          
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {  
        descriptor.putProperty(TopicsProvider.PROP_TOPICS, topics);           
        descriptor.setAdditionalOptions(new Object[0]);
    }    

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        topics.addAll(getComponent().getTopics());
        getComponent().setSelectedTopics(topics);
    }
}
