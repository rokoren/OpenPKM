/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.reference;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.FileTypeProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.TagsProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class FileWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(FileWizardPanel1.class.getName());    
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private FileVisualPanel1 component;
    
    private final AbstractFilesProvider provider;

    public FileWizardPanel1(AbstractFilesProvider provider) 
    {
        this.provider = provider;
    }        

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public FileVisualPanel1 getComponent() {
        if (component == null) 
        {
            try
            {
                component = new FileVisualPanel1(provider);                
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
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
    public void validate() throws WizardValidationException 
    { 
        if (getComponent().getReferenceFileType() == null) 
        {
            throw new WizardValidationException(null, "File Type can not be empty", null);
        }          
        if (getComponent().getReferenceFileName() == null) 
        {
            throw new WizardValidationException(getComponent(), "File can not be empty", null);
        }        
        if (getComponent().getReferenceTitle().equals("")) 
        {
            throw new WizardValidationException(getComponent(), "Title can not be empty", null);
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
    public void readSettings(WizardDescriptor descriptor) 
    {
        Lookup.Provider provider = (Lookup.Provider)descriptor.getProperty("provider");
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
            getComponent().setTopics(provider);
        }           
    }

    @Override
    public void storeSettings(WizardDescriptor descriptor) 
    {
        descriptor.putProperty(FileTypeProvider.PROP_FILE_TYPE, getComponent().getReferenceFileType()); 
        descriptor.putProperty(ReferenceProvider.PROP_TITLE, getComponent().getReferenceTitle());        
        descriptor.putProperty(TagsProvider.PROP_TAGS, getComponent().getReferenceTags());        
        descriptor.putProperty(Reference.PROP_FILE_NAME, getComponent().getReferenceFileName());        
        descriptor.putProperty(Reference.PROP_FILE_PATH, getComponent().getReferenceFileRelativePath());        
        descriptor.putProperty(Reference.PROP_FILE_EXT, getComponent().getReferenceFileExt()); 
        descriptor.putProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, getComponent().getReferenceVisibilityModifier());   
        
        List<Topic> topics = getComponent().getReferenceTopics();
        if(topics != null)
        {
            descriptor.putProperty(TopicsProvider.PROP_TOPICS, topics);
        }        
    }    
}
