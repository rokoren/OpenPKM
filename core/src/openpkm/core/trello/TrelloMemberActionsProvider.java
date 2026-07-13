/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import openpkm.trello.AbstractMemberActionsProvider;
import openpkm.trello.TrelloMemberProvider;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

/**
 *
 * @author rokor
 */
public class TrelloMemberActionsProvider extends AbstractMemberActionsProvider
{
    private final TrelloMemberProvider provider;  

    public TrelloMemberActionsProvider(TrelloMemberProvider provider) 
    {
        this.provider = provider;
    }        
    
    @Override
    public Action addMember() 
    {
        return new AddMember(provider);
    }
    
    private static final class AddMember extends AbstractAction
    {                          
        private final TrelloMemberProvider provider;            

        public AddMember(TrelloMemberProvider provider) 
        {
            super("Add Member");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            //panels.add(new MemberWizardPanel1());
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
            wiz.setTitle("Add Member");  
            /*
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            wiz.putProperty("project", provider.getProject());
            */
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {  

            }
        }
    }     
}
