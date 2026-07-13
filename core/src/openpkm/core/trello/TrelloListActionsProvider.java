/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import openpkm.trello.AbstractListActionsProvider;
import openpkm.trello.TrelloListProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author rokor
 */
public class TrelloListActionsProvider extends AbstractListActionsProvider
{
    private final TrelloListProvider provider;

    public TrelloListActionsProvider(TrelloListProvider provider) 
    {
        this.provider = provider;
    }        
    
    @Override
    public Action addList() 
    {
        return new AddList(provider);
    }
    
    private static final class AddList extends AbstractAction
    {                         
        private final TrelloListProvider provider;            

        public AddList(TrelloListProvider provider) 
        {
            super("Add List");
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            NotifyDescriptor d = new NotifyDescriptor.InputLine("Name:", "Add List");
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.OK_OPTION) 
            {
                String name = ((NotifyDescriptor.InputLine) d).getInputText();
                provider.createList(name);
            }
        }
    }      
}
