/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;
import java.util.Comparator;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Rok Koren
 */
public interface TrelloAccount
{
    Preferences PREFERENCES = NbPreferences.forModule(TrelloAccount.class);  
    
    String PROPS_API_KEY      = "api.key";     
    String PROPS_ACCESS_TOKEN = "access.token";   
    String PROPS_TITLE        = "title";  
    String PROPS_USERNAME     = "username";  
    
    String getUsername();
    String getTitle();
    void setTitle(String title);
    String getApiKey();
    String getAccessToken();
    Collection<TrelloBoard> getBoards();
    TrelloBoard getBoard(String boardID);   
    Preferences getPreferences();
    
    public static Comparator<TrelloAccount> titleComparator() 
    {
        return new Comparator<TrelloAccount>() 
        {
            @Override
            public int compare(TrelloAccount account1, TrelloAccount account2) 
            {
                String title1 = account1.getTitle();
                String title2 = account2.getTitle();
                return title1.compareTo(title2);
            }
        };
    }    
}
