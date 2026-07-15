/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import openpkm.trello.AbstractCardActionsProvider;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloList;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import openpkm.trello.TrelloService;
import org.openide.util.Lookup;
import openpkm.trello.TrelloCardProvider;

/**
 *
 * @author rokor
 */
public class TrelloCardActionsProvider extends AbstractCardActionsProvider
{
    private static final Logger LOG = Logger.getLogger(TrelloCardActionsProvider.class.getName());     
    
    private final TrelloList list;       
    private final TrelloCardProvider provider;   

    public TrelloCardActionsProvider(TrelloList list, TrelloCardProvider provider) {
        this.list = list;
        this.provider = provider;
    }        
    
    @Override
    public Action addLink() 
    {
        return new AddLink(list, provider);
    }

    @Override
    public Action addCard() 
    {
        return new AddCard(list, provider);
    }
    
    private static final class AddLink extends AbstractAction
    {          
        private final TrelloList list;       
        private final TrelloCardProvider provider;  

        public AddLink(TrelloList list, TrelloCardProvider provider) 
        {
            super("Add Link");
            this.list = list;
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            NotifyDescriptor d = new NotifyDescriptor.InputLine("URL:", "Add Link");
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.OK_OPTION) 
            {
                String url = ((NotifyDescriptor.InputLine) d).getInputText();
                provider.createLink(list, url);
            }            
        }
    }  
    
    private static final class AddCard extends AbstractAction
    {          
        private final TrelloList list;       
        private final TrelloCardProvider provider;  

        public AddCard(TrelloList list, TrelloCardProvider provider) 
        {
            super("Add Card");
            this.list = list;
            this.provider = provider;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            NotifyDescriptor d = new NotifyDescriptor.InputLine("Name:", "Add Card");
            Object retVal = DialogDisplayer.getDefault().notify(d);
            if (retVal == NotifyDescriptor.OK_OPTION) 
            {
                String name = ((NotifyDescriptor.InputLine) d).getInputText();
                provider.createCard(list, name);
            }            
        }
    }   
    
    public static final class CardComplete extends AbstractAction
    {             
        private final TrelloCard card;
        private final TrelloAccount account;

        public CardComplete(TrelloCard card, TrelloAccount account) 
        {
            super(getActionName(card));       
            this.card = card;
            this.account = account;
        }
        
        private static String getActionName(TrelloCard card)
        {
            if(Boolean.TRUE.equals(card.isCardDueComplete()))
            {
                return "Uncomplete";
            }
            return "Complete";
        }
        
        private static boolean getComplete(Boolean dueComplete)
        {
            if(dueComplete != null)
            {
                return !dueComplete;
            }
            return false;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
            if(service == null)
            {
                LOG.warning("No Trello service found");
                NotifyDescriptor descriptor = new NotifyDescriptor.Message("No Trello service found", NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(descriptor);                
            }
            else
            {
                boolean complete = getComplete(card.isCardDueComplete());
                int status = service.setCardDueComplete(card.getCardID(), complete, account);
                card.setCardDueComplete(complete);         
                card.markModified();                  
            }
        }
    }     
}
