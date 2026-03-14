/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import openpkm.base.DisplayNameProvider.TextFormat;
import openpkm.base.NodeProvider;
import openpkm.trello.AbstractCardLabelsActionsProvider;
import openpkm.trello.TrelloLabel;
import openpkm.trello.TrelloLabelsProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author rokor
 */
public class TrelloCardLabelsActionsProvider extends AbstractCardLabelsActionsProvider
{
    private final TrelloLabelsProvider provider;     

    public TrelloCardLabelsActionsProvider(TrelloLabelsProvider provider) 
    {
        this.provider = provider;
    }        
    
    @Override
    public Action addLabel() 
    {
        return new AddLabel(provider);
    }

    private static final class AddLabel extends AbstractAction implements ActionListener
    { 
        private static final String ACTION_COMMAND_ADD_LABEL = "Add Label";
        private static final String ACTION_COMMAND_OK        = "OK";        
        
        private final DefaultComboBoxModel<TrelloLabel> labels = new DefaultComboBoxModel<>(); 
        private final JComboBox comboBox;

        private final TrelloLabelsProvider provider;  

        public AddLabel(TrelloLabelsProvider provider) 
        {
            super(ACTION_COMMAND_ADD_LABEL);
            this.provider = provider;
            SortedSet<TrelloLabel> sorted = new TreeSet<TrelloLabel>(NodeProvider.displayNameComparator());
            sorted.addAll(provider.getLabels());
            labels.addAll(sorted);
            comboBox = new JComboBox(labels);
            comboBox.setRenderer(new NodeProvider.ListCellRendererImpl(TextFormat.PLAIN));               
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            if(evt.getActionCommand().equals(ACTION_COMMAND_ADD_LABEL))
            {                
                DialogDescriptor d = new DialogDescriptor(
                comboBox, // Component
                "Add Label", // title
                true, // modality
                this); // ActionListener
                DialogDisplayer.getDefault().createDialog(d).setVisible(true);                  
            }
            else if(evt.getActionCommand().equals(ACTION_COMMAND_OK))
            {
                TrelloLabel label = (TrelloLabel)labels.getSelectedItem();
                if(label != null)
                {
                    provider.addLabel(label);
                }                
            }    
        }
    } 
}
