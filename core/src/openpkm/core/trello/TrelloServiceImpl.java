/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.TList;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloBoard;
import openpkm.trello.TrelloList;
import openpkm.trello.TrelloListProvider;
import openpkm.trello.TrelloService;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloService.class)
public class TrelloServiceImpl implements TrelloService
{
    @Override
    public List<TrelloBoard> getBoards(TrelloAccount account, Trello trello)
    {
        List<TrelloBoard> list = new ArrayList();      
        List<Board> boards = trello.getMemberBoards(account.getUsername());
        for(Board board : boards)
        {
            list.add(new TrelloBoardImpl(account, board));
        }                    
        return list;        
    }
    
    @Override
    public List<TrelloList> getLists(TrelloBoard board, TrelloListProvider provider, Trello trello)
    {
        List<TrelloList> all = new ArrayList();   
        List<TList> lists = trello.getBoardLists(board.getBoardID());
        for(TList list : lists)                
        {
            all.add(provider.createList(list));
        }                     
        return all;        
    }     

    private static final class TrelloBoardImpl implements TrelloBoard
    {
        private final TrelloAccount account;
        private final Board board;        

        public TrelloBoardImpl(TrelloAccount account, Board board) 
        {
            this.account = account;
            this.board = board;       
        }     

        @Override
        public String getAccountUsername() 
        {
            return account.getUsername();
        }      

        @Override
        public String getBoardID() 
        {
            return board.getId();
        }

        @Override
        public String getBoardUrl()
        {
            return board.getUrl();
        }
        
        @Override
        public String getBoardShortUrl()
        {
            return board.getShortUrl();
        }        

        @Override
        public String getBoardName() 
        {
            return board.getName();
        }

        @Override
        public String getBoardDescription() 
        {
            return board.getDesc();
        }

        @Override
        public String getWorkspaceID()
        {
            return board.getIdOrganization();
        }  
        
        @Override
        public Color getBoardBackground()
        {
            return Color.CYAN;
        }
        
        @Override
        public void setBoardBackground(Color color)
        {
            throw new UnsupportedOperationException("Color not found");
        }

        @Override
        public String toString()
        {
            return getBoardName();
        }         
    }  
}
