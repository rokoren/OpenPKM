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
import openpkm.trello.AbstractAttachmentActionsProvider;
import openpkm.trello.TrelloAttachmentProvider;
import openpkm.trello.TrelloAttachmentsProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author rokor
 */
public class TrelloAttachmentActionsProvider extends AbstractAttachmentActionsProvider
{
    private final TrelloAttachmentsProvider provider;

    public TrelloAttachmentActionsProvider(TrelloAttachmentsProvider provider) 
    {
        this.provider = provider;
    }        
    
    @Override
    public Action addAttachment() 
    {
        return new AddAttachmentLink(provider);
    }
    
    private static final class AddAttachmentLink extends AbstractAction
    {  
        @StaticResource()
        public static final String BANNER = "openpkm/core/resources/banner.png";          
        
        private final TrelloAttachmentsProvider provider;            

        public AddAttachmentLink(TrelloAttachmentsProvider provider) 
        {
            super("Add Link Attachment");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new AttachmentLinkWizardPanel1());
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
            wiz.setTitle("Add Link Attachment");  
            wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            { 
                String url = (String)wiz.getProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_URL);
                String name = (String)wiz.getProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_NAME);
                provider.createAttachmentLink(url, name);
            }
        }
    }     
}
