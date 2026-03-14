/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextArea;
import openpkm.base.MarkdownSupport;
import openpkm.base.PropertiesProvider;
import openpkm.trello.AbstractCommentActionsProvider;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloActionsProvider;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloComment;
import openpkm.trello.TrelloService;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.util.Lookup;

/**
 *
 * @author rokor
 */
public class TrelloCommentActionsProvider extends AbstractCommentActionsProvider
{
    private static final Logger LOG = Logger.getLogger(TrelloCommentActionsProvider.class.getName());          
    
    private final Trello trello;    
    private final TrelloAccount account;       
    private final TrelloActionsProvider provider; 
    private final TrelloCard card;     

    public TrelloCommentActionsProvider(Trello trello, TrelloAccount account, TrelloActionsProvider provider, TrelloCard card) 
    {
        this.trello = trello;
        this.account = account;
        this.provider = provider;
        this.card = card;              
    }        
    
    @Override
    public Action addComment() 
    {
        return new AddComment(card, provider, account, trello);
    }
    
    private static final class AddComment extends AbstractAction implements ActionListener
    { 
        private static final String ACTION_COMMAND_ADD_COMMENT = "Add Comment";
        private static final String ACTION_COMMAND_OK          = "OK";
        
        private final Trello trello;    
        private final TrelloAccount account;   
        private final TrelloActionsProvider provider; 
        private final TrelloCard card;         
        private final JTextArea area;

        public AddComment(TrelloCard card, TrelloActionsProvider provider, TrelloAccount account, Trello trello) 
        {
            super(ACTION_COMMAND_ADD_COMMENT);
            this.card = card;
            this.provider = provider;
            this.account = account;
            this.trello = trello;
            area = new JTextArea();
            area.setFont(area.getFont().deriveFont(18f));
            area.setPreferredSize(new Dimension(400, 200));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {             
            if(evt.getActionCommand().equals(ACTION_COMMAND_ADD_COMMENT))
            {                
                DialogDescriptor d = new DialogDescriptor(
                area, // Component
                "Add Comment", // title
                true, // modality
                this); // ActionListener
                DialogDisplayer.getDefault().createDialog(d).setVisible(true);                  
            }
            else if(evt.getActionCommand().equals(ACTION_COMMAND_OK))
            {
                FileObject root = provider.getRootFolder();                  
                TrelloService service = Lookup.getDefault().lookup(TrelloService.class);
                MarkdownSupport markdown = Lookup.getDefault().lookup(MarkdownSupport.class);  

                if(root != null && service != null && markdown != null)
                {                                                                                                     
                    TrelloComment comment = service.createComment(card.getCardID(), area.getText().trim(), provider.getActionProvider(), provider.getCommentProvider(), account, trello);
                    if(comment != null)
                    {
                        try
                        {                                                                                                                                            
                            provider.createData(comment, markdown);  
                            
                            FileSystem fs = root.getFileSystem();
                            fs.runAtomicAction(() -> {
                                OutputStream os = root.createAndOpen(comment.getActionID() + "." + PropertiesProvider.EXTENSION);
                                comment.save(os, "Saved by Add Comment Action");
                                os.close();  
                            });                                                                                                              
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }
                    }              
                }                 
            }                                                           
        }
    }     
}
