/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionProvider;
import openpkm.base.BacklinksProvider;
import openpkm.base.Content;
import openpkm.base.ContentFactory;
import openpkm.base.FileTypeProvider;
import openpkm.base.Goal;
import openpkm.base.GoalsGraphProvider;
import openpkm.base.GoalsProvider;
import openpkm.base.LinkProvider;
import openpkm.base.LiteratureNote;
import openpkm.base.LiteratureNoteFactory;
import openpkm.base.Note;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.StateSupport;
import openpkm.base.SummaryProvider;
import openpkm.base.TagsProvider;
import openpkm.base.Thought;
import openpkm.base.ThoughtsGraphProvider;
import openpkm.base.ThoughtsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsGraphProvider;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.ChangeSupport;

/**
 *
 * @author rok
 */
public class SourceProviderWrapperImpl implements SourceProviderWrapper
{
    private static final Logger LOG = Logger.getLogger(SourceProviderWrapper.class.getName());    
    
    private final String sourceID;
    private final SourceProvider provider;
    private final ChangeSupport changeSupport;

    public SourceProviderWrapperImpl(String sourceID, SourceProvider provider) {
        this.sourceID = sourceID;
        this.provider = provider;
        changeSupport = new ChangeSupport(this);    
    }

    @Override
    public Source getSource() 
    {
        return provider.getSource(sourceID);
    }
    
    @Override
    public void deleteSource() throws IOException
    {
        FileObject root = provider.getRootFolder();
        if(root != null)
        {
            FileObject fo = root.getFileObject(sourceID);
            if(fo == null)
            {
                fo = root.getFileObject(sourceID, PropertiesProvider.EXTENSION);
            }
            if(fo != null)
            {  
                fo.delete();
            }              
        }          
    }

    @Override
    public SourceProvider getProvider() 
    {
        return provider;
    } 
    
    @Override
    public Set<String> getTags() 
    {
        Source source = provider.getSource(sourceID);
        if(source != null)
        {
            TagsProvider tagsProvider = source.getLookup().lookup(TagsProvider.class);
            if(tagsProvider != null)
            {
                return tagsProvider.getTags();
            }
        }
        return Collections.EMPTY_SET;
    }  
    
    @Override
    public Set<Thought> getThoughts()
    {
        ThoughtsGraphProvider thoughtsProvider = getProvider().getProvider().getLookup().lookup(ThoughtsGraphProvider.class);
        Source source = provider.getSource(sourceID);
        if(thoughtsProvider != null && source instanceof PropertiesProvider propsProvider)
        {
            String string = propsProvider.getProperties().getProperty(ThoughtsProvider.PROP_THOUGHTS);
            if(string != null && !string.isBlank())
            {
                List<Thought> thoughts = new ArrayList<>();
                for(String thoughtID : Set.of(string.split(",")))
                {
                    Thought thought = thoughtsProvider.getThought(thoughtID);
                    if(thought != null)
                    {
                        thoughts.add(thought);
                    }
                }
                return new HashSet<>(thoughts);
            }
        }
        return Collections.EMPTY_SET;        
    }
    
    @Override
    public Set<String> getBacklinks() 
    {
        Source source = provider.getSource(sourceID);
        if(source != null)
        {
            BacklinksProvider backlinksProvider = source.getLookup().lookup(BacklinksProvider.class);
            if(backlinksProvider != null)
            {
                return backlinksProvider.getBacklinks();
            }
        }
        return Collections.EMPTY_SET;
    }  
    
    @Override
    public void addBacklink(String link)
    {
        Source source = provider.getSource(sourceID);
        if(source instanceof PropertiesProvider propertiesProvider)
        {
            Set<String> backlinks = new HashSet<>(getBacklinks());
            backlinks.add(link);
            StringJoiner joiner = new StringJoiner(",");
            for(String backlink : backlinks)
            {
                joiner.add(backlink);
            }
            propertiesProvider.getProperties().setProperty(BacklinksProvider.PROP_BACKLINKS, joiner.toString());
            if(source instanceof StateSupport state)
            {
                state.markModified();;
            }
        }        
    }
    
    @Override
    public void removeBacklink(String link)
    {
        Source source = provider.getSource(sourceID);
        if(source instanceof PropertiesProvider propertiesProvider)
        {
            Set<String> backlinks = new HashSet(getBacklinks());
            backlinks.remove(link);
            StringJoiner joiner = new StringJoiner(",");
            for(String backlink : backlinks)
            {
                joiner.add(backlink);
            }
            propertiesProvider.getProperties().setProperty(BacklinksProvider.PROP_BACKLINKS, joiner.toString());
            if(source instanceof StateSupport state)
            {
                state.markModified();;
            }
        }        
    }    

    @Override
    public void addListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public List<Action> getActions() 
    {
        Source source = getSource();
        if(source != null)
        {
            Collection<? extends ActionProvider> providers = source.getLookup().lookupAll(ActionProvider.class);
            if(!providers.isEmpty())
            {
                List<Action> actions = new ArrayList();
                for(ActionProvider actionProvider : providers)
                {
                    actions.add(actionProvider.getAction(provider));
                }
                return actions;                  
            }          
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public LiteratureNoteFactory getLiteratureNoteFactory(DataObject data) 
    {
        if(provider.isLiteratureNoteProvider())
        {
            ContentProvider contentProvider = provider.getProvider().getLookup().lookup(ContentProvider.class);
            Source source = getSource();
            if(contentProvider != null && source != null)
            {
                TitleProvider titleProvider = source.getLookup().lookup(TitleProvider.class);
                LinkProvider linkProvider = source.getLookup().lookup(LinkProvider.class);
                return new LiteratureNoteFactoryImpl(data.getPrimaryFile(), contentProvider, titleProvider, linkProvider);
            }            
        }
        return null;
    }
    
    private static final class LiteratureNoteFactoryImpl implements LiteratureNoteFactory
    {
        private final FileObject primaryFile;
        private final ContentProvider contetntProvider; 
        private final TitleProvider titleProvider;
        private final LinkProvider linkProvider;

        public LiteratureNoteFactoryImpl(FileObject primaryFile, ContentProvider contetntProvider, TitleProvider titleProvider, LinkProvider linkProvider) 
        {
            this.primaryFile = primaryFile;
            this.contetntProvider = contetntProvider;
            this.titleProvider = titleProvider;
            this.linkProvider = linkProvider;
        }
                
        @Override
        public void createLiteratureNote(List<WizardDescriptor.Panel<WizardDescriptor>> panels) 
        {                        
            WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()  
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle("Create Literature Note");  
            //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            wiz.putProperty("provider", contetntProvider.getProvider());
            if(titleProvider != null)
            {
                wiz.putProperty(TitleProvider.PROP_TITLE, titleProvider.getTitle());                
            }          
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {  
                FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
                String title = (String) wiz.getProperty(TitleProvider.PROP_TITLE);     
                String subtitle = (String) wiz.getProperty(LiteratureNote.PROP_SUBTITLE);
                String authorName = (String) wiz.getProperty(LiteratureNote.PROP_AUTHOR_NAME);
                String summary = (String) wiz.getProperty(SummaryProvider.PROP_SUMMARY); 
                Set<String> tags = (Set<String>) wiz.getProperty(TagsProvider.PROP_TAGS);
                Set<Topic> topics = (Set<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);
                Set<Goal> goals = (Set<Goal>) wiz.getProperty(GoalsProvider.PROP_GOALS);
                Set<Thought> thoughts = (Set<Thought>) wiz.getProperty(ThoughtsProvider.PROP_THOUGHTS);

                LocalDateTime now = LocalDateTime.now();

                Properties props = new Properties(); 
                props.setProperty(Content.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
                props.setProperty(ContentFactory.PROP_TYPE, ContentFactory.Type.LITERATURE_NOTE.getName());
                props.setProperty(Content.PROP_APP_ID, Utils.getAppID());           
                VisibilityProvider.Modifier visibiltyModifier = (VisibilityProvider.Modifier)wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
                if(visibiltyModifier != null)
                {
                    props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, visibiltyModifier.toString());                  
                }
                
                props.setProperty(LiteratureNote.PROP_PRIMARY_FILE_NAME, primaryFile.getNameExt());
                if(titleProvider == null)
                {
                    props.setProperty(LiteratureNote.PROP_PRIMARY_TITLE, title);                
                }                 
                else
                {
                    props.setProperty(LiteratureNote.PROP_PRIMARY_TITLE, titleProvider.getTitle());                
                }
                props.setProperty(TitleProvider.PROP_TITLE, title);
                
                if(subtitle != null)
                {
                    props.setProperty(LiteratureNote.PROP_SUBTITLE, subtitle);                    
                }
                
                if(authorName != null)
                {
                    props.setProperty(LiteratureNote.PROP_AUTHOR_NAME, authorName);                    
                }                
                
                if(summary != null)
                {
                    props.setProperty(SummaryProvider.PROP_SUMMARY, summary);
                }                

                if(linkProvider != null)
                {
                    props.setProperty(LiteratureNote.PROP_SOURCE_URL, linkProvider.getLink());                    
                }

                if(tags != null)
                {
                    StringJoiner joiner = new StringJoiner(",");
                    for(String tag : tags)
                    {
                        joiner.add(tag);
                    }
                    props.setProperty(TagsProvider.PROP_TAGS, joiner.toString());
                }             

                if(topics != null)
                {
                    TopicsGraphProvider knowledgeGraphProvider = contetntProvider.getProvider().getLookup().lookup(TopicsGraphProvider.class);
                    if(knowledgeGraphProvider != null)
                    {
                        StringJoiner joiner = new StringJoiner(",");
                        for(Topic topic : topics)
                        {
                            joiner.add(knowledgeGraphProvider.getTreeID(topic));
                        }
                        props.setProperty(TopicsProvider.PROP_TOPICS, joiner.toString());                    
                    }
                }  

                if(goals != null)
                {
                    GoalsGraphProvider goalsGraphProvider = contetntProvider.getProvider().getLookup().lookup(GoalsGraphProvider.class);
                    if(goalsGraphProvider != null)
                    {
                        StringJoiner joiner = new StringJoiner(",");
                        for(Goal goal : goals)
                        {
                            joiner.add(goalsGraphProvider.getTreeID(goal));
                        }
                        props.setProperty(GoalsProvider.PROP_GOALS, joiner.toString());                    
                    }
                }  
                
                if(thoughts != null)
                {
                    ThoughtsGraphProvider thoughtsGraphProvider = contetntProvider.getProvider().getLookup().lookup(ThoughtsGraphProvider.class);
                    if(thoughtsGraphProvider != null)
                    {
                        StringJoiner joiner = new StringJoiner(",");
                        for(Thought thought : thoughts)
                        {
                            joiner.add(thought.getThoughtID());
                        }
                        props.setProperty(ThoughtsProvider.PROP_THOUGHTS, joiner.toString());                    
                    }
                }                 

                String fileName = FileUtils.getFileName(contetntProvider.getRootFolder(), PropertiesProvider.EXTENSION);
                props.setProperty(Note.PROP_FILE_NAME, fileName);  

                Content content = contetntProvider.getFactory().getContent(props);
                try
                {
                    FileObject file = contetntProvider.createData(content, fileType); 
                    OutputStream os = contetntProvider.getRootFolder().createAndOpen(content.getSourceID() + "." + PropertiesProvider.EXTENSION);  
                    contetntProvider.getFactory().save(content, os, "New Literature Note Content created by Wizard");
                    os.close();  

                    StatusDisplayer.getDefault().setStatusText("Literature Note content saved with title: " + title);  

                    NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to open Literature Note in editor?", title, NotifyDescriptor.YES_NO_OPTION);
                    if(DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION)
                    {
                        try
                        {
                            DataObject data = DataObject.find(file);
                            OpenCookie open = data.getCookie(OpenCookie.class);
                            open.open();                            
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.warning(e.getMessage());
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
}
