/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.awt.Color;
import java.util.Comparator;

/**
 *
 * @author Rok Koren
 */
public interface TrelloBoard 
{
    String getWorkspaceID();     
    TrelloAccount getAccount();
    String getBoardID();
    String getName();
    String getDescription();    
    String getUrl();
    String getShortUrl();  
    Color getBackground();
    void setBackground(Color color);
    
    public static Comparator<TrelloBoard> nameComparator() 
    {
        return new Comparator<TrelloBoard>() 
        {
            @Override
            public int compare(TrelloBoard board1, TrelloBoard board2) 
            {
                return board1.getName().compareTo(board2.getName());
            }
        };
    }     
}
