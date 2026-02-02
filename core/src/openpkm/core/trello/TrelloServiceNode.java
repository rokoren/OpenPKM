/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloService;
import openpkm.trello.TrelloWizardPanel1;
import openpkm.trello.TrelloWizardPanel2;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
@ServicesTabNodeRegistration(name="trello", displayName="Trello", iconResource = "openpkm/core/resources/trello.png")
public class TrelloServiceNode extends AbstractNode
{
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/trello.png";    
    
    public TrelloServiceNode() 
    {
        super(new TrelloChildren());
        setName("trello");
        setDisplayName("Trello");
        setShortDescription("Work Management Tool");    
        setIconBaseWithExtension(ICON);
    } 
        
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new AddTrello()
        };
    }  
    
    static final class TrelloChildren extends Children.Keys<TrelloAccount> implements ChangeListener 
    {        
        public TrelloChildren()
        {
            TrelloService.getDefault().addListener(this);
        }  

        protected @Override void addNotify() 
        {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            EventQueue.invokeLater(new Runnable() 
            {
                public void run() 
                {                     
                    SortedSet<TrelloAccount> subModules = new TreeSet<TrelloAccount>(TrelloAccount.titleComparator());
                    subModules.addAll(TrelloService.getDefault().getAccounts());           
                    setKeys(subModules);                   
                }
            });
        }        

        protected @Override void removeNotify() 
        {
            TrelloService.getDefault().removeListener(this);                              
            setKeys(Collections.<TrelloAccount>emptySet());
        }

        @Override
        protected Node[] createNodes(TrelloAccount account) 
        {
            return new Node[] {new TrelloAccountNode(account)};
        }

        @Override
        public void stateChanged(ChangeEvent ev) {
            updateKeys();
        }            
    }      
    
    private static final class AddTrello extends AbstractAction
    {
        public AddTrello() 
        {
            super("Add Account");
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new TrelloWizardPanel1());
            panels.add(new TrelloWizardPanel2());
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) {
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
            wiz.setTitle("Add Trello Account");
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage("openpkm/trello/resources/banner.png", true));            
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {
                String title = (String) wiz.getProperty(TrelloAccount.PROPS_TITLE);
                String username = (String) wiz.getProperty(TrelloAccount.PROPS_USERNAME);
                String apiKey = (String) wiz.getProperty(TrelloAccount.PROPS_API_KEY);
                String accessToken = (String) wiz.getProperty(TrelloAccount.PROPS_ACCESS_TOKEN);
                TrelloAccount account = new TrelloService.TrelloAccountImpl(username, apiKey, accessToken);  
                account.setTitle(title);
                TrelloService.getDefault().addAccount(account);
                TrelloService.getDefault().store(account);
            }
        }
    }     
}
