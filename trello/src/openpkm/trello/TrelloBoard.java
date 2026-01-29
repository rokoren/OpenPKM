/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.awt.Color;
import java.util.Comparator;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Rok Koren
 */
public interface TrelloBoard 
{
    TrelloAccount getAccount();
    String getBoardID();
    String getUrl();
    String getTitle();
    void setTitle(String title);
    String getDescription();
    void setDescription(String desc);
    Color getBackground();
    void setBackground(Color color);
    String getWorkspaceID();            
    RequestProcessor getRequestProcessor();  
    
    public static Comparator<TrelloBoard> titleComparator() 
    {
        return new Comparator<TrelloBoard>() 
        {
            @Override
            public int compare(TrelloBoard board1, TrelloBoard board2) 
            {
                String title1 = board1.getTitle();
                String title2 = board2.getTitle();
                return title1.compareTo(title2);
            }
        };
    }     
}
