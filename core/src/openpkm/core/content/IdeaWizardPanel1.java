/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/wizardPanel.java to edit this template
 */
package openpkm.core.content;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeListener;
import openpkm.base.FileTypeProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.SomedayMaybeProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import org.netbeans.api.project.Project;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public class IdeaWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private IdeaVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public IdeaVisualPanel1 getComponent() {
        if (component == null) {
            component = new IdeaVisualPanel1();
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
    public void validate() throws WizardValidationException 
    {
        if (getComponent().getThoughtFileType() == null) 
        {
            throw new WizardValidationException(null, "File Type can not be empty", null);
        }          
        if (getComponent().getThoughtTitle().equals("")) 
        {
            throw new WizardValidationException(null, "Title can not be empty", null);
        }      
    }     

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    private Set<String> getSelectedTags(Lookup.Provider provider)
    {
        KnowledgeGraphProvider knowledgeGraph = provider.getLookup().lookup(KnowledgeGraphProvider.class);
        if(knowledgeGraph != null)
        {
            Collection<Topic> topics = knowledgeGraph.getSelectedTopics();
            if(!topics.isEmpty())
            {
                return knowledgeGraph.getTags(topics);
            }
        }        
        return new HashSet<>();
    }
    
    @Override
    public void readSettings(WizardDescriptor wiz) 
    {
        Lookup.Provider provider = (Project)wiz.getProperty("provider");
        if(provider != null)
        {
            Set<String> tags = getSelectedTags(provider);
            if(tags.isEmpty())
            {
                Collection<TagsProvider> providers = (Collection<TagsProvider>)provider.getLookup().lookupAll(TagsProvider.class);
                if(!providers.isEmpty())
                {
                    Iterator<TagsProvider> iterator = providers.iterator();
                    while(iterator.hasNext())
                    {
                        tags.addAll(iterator.next().getTags());  
                    }                     
                }                
            }
            getComponent().setTags(tags);            
        }
    }        

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        // use wiz.putProperty to remember current panel state
        descriptor.putProperty(FileTypeProvider.PROP_FILE_TYPE, getComponent().getThoughtFileType()); 
        descriptor.putProperty(TitleProvider.PROP_TITLE, getComponent().getThoughtTitle()); 
        LocalDate tickleDate = getComponent().getThoughtTickleDate();
        if(tickleDate != null)
        {
            descriptor.putProperty(SomedayMaybeProvider.PROP_TICKLE_DATE, getComponent().getThoughtTickleDate()); 
        }
        descriptor.putProperty(TagsProvider.PROP_TAGS, getComponent().getThoughtTags()); 
    }

}
