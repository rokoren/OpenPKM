/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javax.swing.event.ChangeListener;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.trello.TrelloBoard;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public class TrelloBoardWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>
{
    private static final Logger LOG = Logger.getLogger(TrelloBoardWizardPanel1.class.getName());
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private TrelloBoardVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public TrelloBoardVisualPanel1 getComponent() {
        if (component == null) {
            component = new TrelloBoardVisualPanel1();
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
    public void readSettings(WizardDescriptor descriptor) 
    {
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
        TrelloBoard board = getComponent().getTrelloBoard();
        if(board != null)
        {              
            descriptor.putProperty(TrelloProject.PROP_ACCOUNT_USERNAME, board.getAccountUsername());  
            descriptor.putProperty(TrelloProject.PROP_WORKSPACE_ID, board.getWorkspaceID());   
            descriptor.putProperty(TrelloProject.PROP_BOARD_ID, board.getBoardID());    
            descriptor.putProperty(TrelloProject.PROP_BOARD_NAME, board.getBoardName());               
            descriptor.putProperty(TrelloProject.PROP_BOARD_DESCRIPTION, board.getBoardDescription()); 
            descriptor.putProperty(TrelloProject.PROP_BOARD_URL, board.getBoardUrl()); 
            descriptor.putProperty(TrelloProject.PROP_BOARD_SHORT_URL, board.getBoardShortUrl()); 
        }
        
        Color background = getComponent().getBoardBackground();
        if(background != null)
        {
            descriptor.putProperty(TrelloProject.PROP_BOARD_BACKGROUND, background.toString());
        }
        
        Lookup.Provider provider = (Lookup.Provider)descriptor.getProperty("provider");
        if(provider != null)
        { 
            List<Topic> topics = getComponent().getBoardTopics();
            if(topics != null)
            {
                KnowledgeGraphProvider knowledgeGraphProvider = provider.getLookup().lookup(KnowledgeGraphProvider.class);
                if(knowledgeGraphProvider != null)
                {
                    StringJoiner joiner = new StringJoiner(",");
                    for(Topic topic : topics)
                    {
                        joiner.add(knowledgeGraphProvider.getTreeID(topic));
                    }
                    descriptor.putProperty(TopicsProvider.PROP_TOPICS, joiner.toString());                    
                }
            } 
        }                 
    }

    @Override
    public void validate() throws WizardValidationException 
    {          
        if (getComponent().getTrelloBoard() == null) 
        {
            throw new WizardValidationException(null, "No Trello Board is selected", null);
        }        
    }     
}
