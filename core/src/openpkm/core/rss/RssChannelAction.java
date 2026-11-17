/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.rss;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.FileTypeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.rss.RssChannel;
import openpkm.rss.RssChannelProvider;
import openpkm.utils.FileUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/Channel",
        id = "openpkm.core.rss.RssChannelAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/rss.png",
        displayName = "#CTL_RssChannelAction"
)
@Messages("CTL_RssChannelAction=Add RSS Channel")
public class RssChannelAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(RssChannelAction.class.getName());     
    
    private final RssChannelProvider provider;

    public RssChannelAction(RssChannelProvider provider)
    {
        this.provider = provider;
    }   
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new RssChannelWizardPanel1());
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
        wiz.setTitle("Add RSS Channel");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
            
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
            String title = (String) wiz.getProperty(TitleProvider.PROP_TITLE);      
            Set<String> tags = (Set<String>) wiz.getProperty(TagsProvider.PROP_TAGS);
            Set<Topic> topics = (Set<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);
            Set<Goal> goals = (Set<Goal>) wiz.getProperty(GoalsProvider.PROP_GOALS);

            Properties props = new Properties();
            props.setProperty(Content.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
            props.setProperty(ContentFactory.PROP_TYPE, ContentFactory.Type.ARTICLE.getName());
            props.setProperty(Content.PROP_APP_ID, Utils.getAppID());          
            VisibilityProvider.Modifier visibiltyModifier = (VisibilityProvider.Modifier)wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
            if(visibiltyModifier != null)
            {
                props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, visibiltyModifier.toString());                  
            }          
            props.setProperty(TitleProvider.PROP_TITLE, title);

            String publisher = (String)wiz.getProperty(Article.PROP_PUBLISHER);
            String language = (String)wiz.getProperty(Article.PROP_LANGUAGE);     

            props.setProperty(Article.PROP_PUBLISHER, publisher);
            props.setProperty(Article.PROP_LANGUAGE, language);       

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

            FileObject root = provider.getRootFolder();
            if(root != null)
            {
                Content content = provider.getFactory().getContent(props);
                try
                {
                    FileObject file = provider.createData(content, fileType); 
                    OutputStream os = root.createAndOpen(content.getSourceID() + "." + PropertiesProvider.EXTENSION);  
                    provider.getFactory().save(content, os, "New Article Content created by Wizard");
                    os.close();  

                    StatusDisplayer.getDefault().setStatusText("Article content saved with title: " + title);   
                    
                    NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to open article in editor?", title, NotifyDescriptor.YES_NO_OPTION);
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
    
    @Override
    public void actionPerformed(ActionEvent evt) 
    {
        NotifyDescriptor descriptor = new NotifyDescriptor.InputLine("URL:", "Add RSS Channel");
        Object retVal = DialogDisplayer.getDefault().notify(descriptor);
        if (retVal == NotifyDescriptor.OK_OPTION) 
        {
            String feedUrl = ((NotifyDescriptor.InputLine)descriptor).getInputText();

            try
            {
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(new URL(feedUrl)));  
                String fileName = FileUtils.getFileName(provider.getRootFolder(), PropertiesProvider.EXTENSION);
                RssChannel channel = provider.getFactory().getChannel(feedUrl, fileName, feed);
                if(channel != null)
                {                      
                    OutputStream os = provider.getRootFolder().createAndOpen(fileName + "." + PropertiesProvider.EXTENSION);  
                    provider.getFactory().save(channel, os, "New RSS Channel Created by Dialog");
                    os.close();  

                    StatusDisplayer.getDefault().setStatusText("RSS Channel saved with title: " + channel.getTitle());    
                }
            }
            catch (MalformedURLException e)
            {
                LOG.warning(e.getMessage());
            }
            catch (IOException e)
            {
                LOG.warning(e.getMessage());
            }    
            catch (FeedException e)
            {
                LOG.warning(e.getMessage());
            }              
        }  
    }    
}
