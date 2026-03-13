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
    String getAccountUsername();    
    String getWorkspaceID();     
    String getBoardID();
    String getBoardName();
    void setBoardName(String name);
    String getBoardDescription();    
    String getBoardUrl();
    String getBoardShortUrl();  
    Color getBoardBackground();
    void setBoardBackground(Color color);
    
    public static Comparator<TrelloBoard> nameComparator() 
    {
        return new Comparator<TrelloBoard>() 
        {
            @Override
            public int compare(TrelloBoard board1, TrelloBoard board2) 
            {
                return board1.getBoardName().compareTo(board2.getBoardName());
            }
        };
    }     
}
