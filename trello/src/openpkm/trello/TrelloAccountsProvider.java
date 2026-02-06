/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Rok Koren
 */
public interface TrelloAccountsProvider 
{
    Collection<TrelloAccount> getAccounts(); 
    TrelloAccount getAccount(String username);
    void addAccount(TrelloAccount account);
    void removeAccount(TrelloAccount account);
    void store(TrelloAccount account);
    void addListener(ChangeListener listener);
    void removeListener(ChangeListener listener);    
}
