/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCardsProvider 
{
    FileObject getRootDirectory() throws IOException;    
    Collection<TrelloCard> getCards();  
    void addCard(TrelloCard card);
    void removeCard(String cardID);
    Lookup.Provider getProvider();
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener); 
    List<Action> getActions();     
}
