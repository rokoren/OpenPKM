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
import openpkm.base.DataProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCardsProvider extends DataProvider
{
    TrelloCardProvider getCardProvider();
    FileObject getRootDirectory() throws IOException;    
    Collection<TrelloCard> getCards();  
    void addCard(TrelloCard card);
    void removeCard(String cardID);
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener); 
    List<Action> getActions();     
}
