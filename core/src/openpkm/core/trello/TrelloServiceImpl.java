/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloBoard;
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
    public List<TrelloBoard> getBoards(TrelloAccount account)
    {
        List<TrelloBoard> list = new ArrayList();
        Trello trelloApi = new TrelloImpl(account.getApiKey(), account.getAccessToken(), new JDKTrelloHttpClient());
        List<Board> boards = trelloApi.getMemberBoards(account.getUsername());
        for(Board board : boards)
        {
            list.add(new TrelloBoardImpl(account, board));
        }                    
        return list;        
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
