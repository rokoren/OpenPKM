/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

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
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.MarkdownSupport;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardProvider;
import openpkm.trello.TrelloCardsProvider;
import openpkm.utils.Utils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Trafalgar
 */
@ActionID(
        category = "OpenPKM/Trello/Card",
        id = "openpkm.core.TrelloAddCardAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/panel.png",
        displayName = "#CTL_TrelloAddCardAction"
)
@Messages("CTL_TrelloAddCardAction=Add Card")
public class TrelloAddCardAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(TrelloAddCardAction.class.getName());     
    
    private final TrelloCardsProvider provider;

    public TrelloAddCardAction(TrelloCardsProvider provider)
    {
        this.provider = provider;
    }    

    @Override
    public void actionPerformed(ActionEvent evt) 
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new TrelloCardWizardPanel1());
        panels.add(new TrelloCardWizardPanel2());
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
        wiz.setTitle("Add Card");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
                     
            String cardID = (String) wiz.getProperty(TrelloCardProvider.PROP_CARD_ID);
            String boardID = (String) wiz.getProperty(TrelloCardProvider.PROP_BOARD_ID);
            String listID = (String) wiz.getProperty(TrelloCardProvider.PROP_LIST_ID);
            String name = (String) wiz.getProperty(TrelloCardProvider.PROP_CARD_NAME);
            String description = (String) wiz.getProperty(TrelloCardProvider.PROP_CARD_DESCRIPTION); 
            String position = (String) wiz.getProperty(TrelloCardProvider.PROP_CARD_POSITION); 
            //List<Topic> topics = (List<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);                                  
                        
            Properties props = new Properties();
            props.setProperty(TrelloCardProvider.PROP_APP_ID, Utils.getAppID());
            props.setProperty(TrelloCardProvider.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
            props.setProperty(TrelloCardProvider.PROP_BOARD_ID, boardID);
            props.setProperty(TrelloCardProvider.PROP_LIST_ID, listID);
            props.setProperty(TrelloCardProvider.PROP_CARD_ID, cardID);
            props.setProperty(TrelloCardProvider.PROP_CARD_NAME, name);
            props.setProperty(TrelloCardProvider.PROP_CARD_POSITION, position);          
            /*
            if(topics != null)
            {
                KnowledgeGraphProvider knowledgeGraphProvider = provider.getProvider().getLookup().lookup(KnowledgeGraphProvider.class);
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
            */
            
            MarkdownSupport markdown = Lookup.getDefault().lookup(MarkdownSupport.class);
            if(markdown != null)
            {
                FileObject file = provider.createData(props, markdown);
                if(file != null)  
                {
                    StatusDisplayer.getDefault().setStatusText("OpenPKM Trello Card Project saved: " + name);                     
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
            

            
            try
            {  
                FileObject projectDirectory = FileUtil.createFolder(provider.getRootDirectory(), cardID);           
                FileObject projectFolder = FileUtil.createFolder(projectDirectory, TrelloCardProjectFactory.PROJECT_FOLDER);                   

                OutputStream os = projectFolder.createAndOpen(TrelloCardProjectFactory.PROJECT_FILE);
                props.store(os, "OpenPKM Trello Card Project"); 
                os.close(); 
                                
                StatusDisplayer.getDefault().setStatusText("OpenPKM Trello Card Project saved: " + name); 

                Project project = ProjectManager.getDefault().findProject(projectDirectory);
                if(project != null)
                {
                    TrelloCard card = project.getLookup().lookup(TrelloCard.class);
                    if(card != null)
                    {
                        provider.addCard(card);
                        /*
                        Project[] projects = {domain};
                        OpenProjects.getDefault().open(projects, false);   
                        */
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
