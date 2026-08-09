/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.raindrop;

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
import openpkm.raindrop.RaindropAccount;
import openpkm.raindrop.RaindropService;
import openpkm.raindrop.RaindropUser;
import openpkm.raindrop.RaindropWizardPanel1;
import openpkm.raindrop.RaindropWizardPanel2;
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
@ServicesTabNodeRegistration(name="raindrop", displayName="Raindrop.io", iconResource = "openpkm/core/resources/raindrop.png")
public class RaindropNode extends AbstractNode
{
    @StaticResource()
    public static final String BANNER = "openpkm/core/resources/raindrop512.png";      
    
    public RaindropNode() 
    {
        super(new RaindropChildren());
        setName("raindrop");
        setDisplayName("Raindrop.io");
        setShortDescription("All-in-one bookmark manager");
        setIconBaseWithExtension(AbstractRaindrop.ICON);
    } 
        
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new AddRaindrop()
        };
    }  
    
    static final class RaindropChildren extends Children.Keys<RaindropAccount> implements ChangeListener 
    {        
        public RaindropChildren()
        {
            RaindropService.getDefault().addChangeListener(this);
        }  

        @Override        
        protected void addNotify() {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            EventQueue.invokeLater(new Runnable() 
            {
                public void run() 
                {                   
                    SortedSet<RaindropAccount> subModules = new TreeSet<RaindropAccount>(RaindropAccount.titleComparator());
                    subModules.addAll(RaindropService.getDefault().getAccounts());           
                    setKeys(subModules);                   
                }
            });
        }        

        protected @Override void removeNotify() 
        { 
            RaindropService.getDefault().removeChangeListener(this);                              
            setKeys(Collections.<RaindropAccount>emptySet());
        }

        @Override
        protected Node[] createNodes(RaindropAccount account) {
            return new Node[] {new RaindropAccountNode(account)};
        }

        @Override
        public void stateChanged(ChangeEvent ev) {
            updateKeys();
        }            
    }      
    
    private static final class AddRaindrop extends AbstractAction
    {
        public AddRaindrop() 
        {
            super("Add Account");
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new RaindropWizardPanel1());
            panels.add(new RaindropWizardPanel2());
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
            wiz.setTitle("Add Raindrop.io Account");
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));            
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {
                String title = (String) wiz.getProperty(RaindropAccount.PROP_TITLE);
                String token = (String) wiz.getProperty(RaindropAccount.PROP_TOKEN);
                RaindropUser user = (RaindropUser) wiz.getProperty("user");
                RaindropAccount account = new RaindropAccount(token, user);  
                account.setTitle(title);
                RaindropService.getDefault().addAccount(account);
                RaindropService.getDefault().store(account);                
            }
        }
    }     
}
