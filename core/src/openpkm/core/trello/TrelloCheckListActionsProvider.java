/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import openpkm.trello.AbstractCheckListActionsProvider;
import openpkm.trello.TrelloCheckListsProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author rokor
 */
public class TrelloCheckListActionsProvider extends AbstractCheckListActionsProvider
{
    private final TrelloCheckListsProvider provider;     

    public TrelloCheckListActionsProvider(TrelloCheckListsProvider provider) 
    {
        this.provider = provider;
    }        
    
    @Override
    public Action addCheckList() 
    {
        return new AddCheckList(provider);
    }  
    
    private static final class AddCheckList extends AbstractAction
    {                  
        private final TrelloCheckListsProvider provider;             

        public AddCheckList(TrelloCheckListsProvider provider) 
        {
            super("Add Checklist");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            NotifyDescriptor d = new NotifyDescriptor.InputLine("Name:", "Add Checklist");
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.OK_OPTION) 
            {
                String name = ((NotifyDescriptor.InputLine) d).getInputText();
                provider.createCheckList(name);
            }
        }
    }     
}
