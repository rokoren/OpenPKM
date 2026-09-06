/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author rok
 */
public abstract class TrelloCardProvider implements SourceProvider<TrelloCard>
{
    public static final String PROP_TRELLO_SYNC_CARD = "trello.sync.card"; 

    protected static final String ROOT_FOLDER = "cards";          

    protected Map<String, TrelloCard> cards; 
    protected FileObject rootDir;            

    private final TrelloCardFactory factory;    

    public TrelloCardProvider(TrelloCardFactory factory)
    {
        this.factory = factory;                            
    } 
    
    @Override
    public boolean isLiteratureNoteProvider()
    {
        return false;
    }

    @Override
    public TrelloCardFactory getFactory()
    {
        return factory;
    }                

    @Override
    public String getName()
    {
        return ROOT_FOLDER;
    }       

    @Override
    public String getDisplayName() 
    {
        return "Trello Cards";
    }                

    @Override
    public TrelloCard getSource(String sourceID)
    {
        return getCardsById().get(sourceID);
    }                                 

    public abstract Map<String, TrelloCard> getCardsById();
    public abstract TrelloAccount getAccount();
    public abstract void createLink(TrelloList list, String url);
    public abstract void createCard(TrelloList list, String name);

    public Collection<TrelloCard> getCards()
    {
        return Collections.unmodifiableCollection(getCardsById().values());
    }      

    @Override
    public boolean contains(FileObject file) 
    {
        if(file.isData())
        {
            return getCardsById().containsKey(file.getName());                
        }
        return false;
    }              
}
