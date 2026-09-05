/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.content;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.Content;
import openpkm.base.FileTypeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.utils.ContentProvider;
import openpkm.utils.Utils;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle.Messages;
import openpkm.base.ContentFactory;
import openpkm.base.Goal;
import openpkm.base.GoalsGraphProvider;
import openpkm.base.GoalsProvider;
import openpkm.base.Note;
import openpkm.core.neo4j.GoalWizardPanel;
import openpkm.core.neo4j.TopicWizardPanel;
import openpkm.utils.FileUtils;
import openpkm.base.TopicsGraphProvider;
import openpkm.core.neo4j.ThoughtWizardPanel;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/Note",
        id = "openpkm.core.content.ThoughtAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/comment.png",
        displayName = "#CTL_ThoughtAction"
)
@Messages("CTL_ThoughtAction=Add Thought")
public class ThoughtAction implements ActionListener
{
    @StaticResource()
    private static final String BANNER = "openpkm/core/resources/comment.png";     
    
    private static final Logger LOG = Logger.getLogger(ThoughtAction.class.getName());     
    
    private final ContentProvider provider;

    public ThoughtAction(ContentProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new NoteWizardPanel1());
        panels.add(new TopicWizardPanel());
        panels.add(new GoalWizardPanel());
        panels.add(new ThoughtWizardPanel());
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
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()  
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Add Thought");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        {  
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
            String title = (String) wiz.getProperty(TitleProvider.PROP_TITLE);     
            Set<String> tags = (Set<String>) wiz.getProperty(TagsProvider.PROP_TAGS);
            Set<Topic> topics = (Set<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);
            Set<Goal> goals = (Set<Goal>) wiz.getProperty(GoalsProvider.PROP_GOALS);

            LocalDateTime now = LocalDateTime.now();

            Properties props = new Properties(); 
            props.setProperty(Content.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
            props.setProperty(ContentFactory.PROP_TYPE, ContentFactory.Type.NOTE.getName());
            props.setProperty(Content.PROP_APP_ID, Utils.getAppID());           
            VisibilityProvider.Modifier visibiltyModifier = (VisibilityProvider.Modifier)wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
            if(visibiltyModifier != null)
            {
                props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, visibiltyModifier.toString());                  
            }          
            props.setProperty(TitleProvider.PROP_TITLE, title);

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
                TopicsGraphProvider knowledgeGraphProvider = provider.getProvider().getLookup().lookup(TopicsGraphProvider.class);
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
                GoalsGraphProvider goalsGraphProvider = provider.getProvider().getLookup().lookup(GoalsGraphProvider.class);
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

            String fileName = FileUtils.getFileName(provider.getRootFolder(), PropertiesProvider.EXTENSION);
            props.setProperty(Note.PROP_FILE_NAME, fileName);  

            Content content = provider.getFactory().getContent(props);
            try
            {
                FileObject file = provider.createData(content, fileType); 
                OutputStream os = provider.getRootFolder().createAndOpen(content.getSourceID() + "." + PropertiesProvider.EXTENSION);  
                provider.getFactory().save(content, os, "New Thought Content created by Wizard");
                os.close();  

                StatusDisplayer.getDefault().setStatusText("Thought content saved with title: " + title);  

                NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to open thought in editor?", title, NotifyDescriptor.YES_NO_OPTION);
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
