/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Argument;
import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAttachment;
import openpkm.trello.TrelloAttachmentProvider;
import openpkm.trello.TrelloBoard;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardProvider;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListProvider;
import openpkm.trello.TrelloLabel;
import openpkm.trello.TrelloLabelProvider;
import openpkm.trello.TrelloList;
import openpkm.trello.TrelloListProvider;
import openpkm.trello.TrelloMember;
import openpkm.trello.TrelloMemberProvider;
import openpkm.trello.TrelloService;
import openpkm.utils.Utils;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloService.class)
public class TrelloServiceImpl implements TrelloService
{
    private static final Logger LOG = Logger.getLogger(TrelloService.class.getName());    
    
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
    
    @Override
    public List<TrelloMember> getMembers(TrelloBoard board, TrelloMemberProvider provider, Trello trello)
    {
        List<TrelloMember> list = new ArrayList();   
        List<Member> members = trello.getBoardMembers(board.getBoardID());
        for(Member member : members)                
        {
            list.add(provider.createMember(member));
        }                     
        return list;        
    }     
    
    @Override
    public List<TrelloLabel> getLabels(TrelloBoard board, TrelloLabelProvider provider, Trello trello)
    {
        List<TrelloLabel> list = new ArrayList();   
        List<Label> labels = trello.getBoardLabels(board.getBoardID());
        for(Label label : labels)                
        {
            list.add(provider.createLabel(label));
        }                     
        return list;        
    }      
    
    @Override
    public List<String> getCards(TrelloBoard board, Trello trello)
    {        
        List<String> list = new ArrayList();  
        Argument argument = new Argument("fields", "id");
        List<Card> cards = trello.getBoardCards(board.getBoardID(), argument);
        for(Card card : cards)                
        {            
            list.add(card.getId());
        }                     
        return list;        
    }  
    
    @Override
    public List<TrelloCard> getCards(TrelloList trelloList, TrelloCardProvider provider, Trello trello)
    {
        List<TrelloCard> list = new ArrayList();   
        List<Card> cards = trello.getListCards(trelloList.getListID());
        for(Card card : cards)                
        {
            list.add(provider.createCard(card));
        }                     
        return list;        
    }  
    
    @Override
    public List<TrelloAttachment> getAttachments(TrelloCard card, TrelloAttachmentProvider provider, Trello trello)
    {
        List<TrelloAttachment> list = new ArrayList();   
        List<Attachment> attachments = trello.getCardAttachments(card.getCardID());
        for(Attachment attachment : attachments)                
        {
            list.add(provider.createAttachment(attachment));
        }                     
        return list;        
    } 

    @Override
    public List<TrelloCheckList> getCheckLists(TrelloCard card, TrelloCheckListProvider provider, TrelloAccount account)
    {
        HttpResponse<JsonNode> response = Unirest.get("https://api.trello.com/1/cards/" + card.getCardID() + "/checklists")
          .header("Accept", "application/json")
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asJson();  
        
        List<TrelloCheckList> list = new ArrayList(); 
        
        JSONArray jsons = response.getBody().getArray();
        for(int i=0; i<jsons.length(); i++)
        {
            JSONObject json = jsons.getJSONObject(i);
            Properties props = new Properties();
            props.setProperty(TrelloCheckListProvider.PROP_BOARD_ID, json.getString("idBoard"));
            props.setProperty(TrelloCheckListProvider.PROP_CARD_ID, json.getString("idCard"));            
            props.setProperty(TrelloCheckListProvider.PROP_CHECKLIST_ID, json.getString("id"));
            props.setProperty(TrelloCheckListProvider.PROP_CHECKLIST_NAME, json.getString("name"));
            props.setProperty(TrelloCheckListProvider.PROP_CHECKLIST_POSITION, json.getInt("pos") + "");
            list.add(provider.getCheckList(props));
        }
        return list;        
    } 
    
    @Override
    public TrelloCard getCard(String cardID,  TrelloCardProvider provider, TrelloAccount account) throws UnirestException
    {
        HttpResponse<JsonNode> response = Unirest.get("https://api.trello.com/1/cards/" + cardID)
          .header("Accept", "application/json")
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asJson();  
        
        JSONObject json = response.getBody().getObject();        
        
        JSONArray idLabels = json.getJSONArray("idLabels");
        List<Label> labels = new ArrayList();
        for(int i=0; i<idLabels.length(); i++)
        {
            String id = idLabels.getString(i);
            Label label = new Label();
            label.setId(id);
            labels.add(label);            
            /*            
            label.setColor(jsonLabel.getString("color"));
            label.setName(jsonLabel.getString("name"));
            */
        }
         
        LocalDateTime now = LocalDateTime.now();
        
        Properties props = new Properties();
        props.setProperty(TrelloCardProvider.PROP_APP_ID, Utils.getAppID());
        props.setProperty(TrelloCardProvider.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));        
        props.setProperty(TrelloCardProvider.PROP_ACCOUNT_USERNAME, account.getUsername());
        props.setProperty(TrelloCardProvider.PROP_CARD_ID, json.getString("id"));
        props.setProperty(TrelloCardProvider.PROP_BOARD_ID, json.getString("idBoard"));
        props.setProperty(TrelloCardProvider.PROP_LIST_ID, json.getString("idList"));            
        props.setProperty(TrelloCardProvider.PROP_CARD_NAME, json.getString("name"));
        props.setProperty(TrelloCardProvider.PROP_CARD_DESCRIPTION, json.getString("desc"));
        props.setProperty(TrelloCardProvider.PROP_CARD_POSITION, json.getInt("pos") + "");        
        props.setProperty(TrelloCardProvider.PROP_CARD_CLOSED, Boolean.toString(json.getBoolean("closed")));
        if(!json.isNull("cardRole"))
        {
            props.setProperty(TrelloCardProvider.PROP_CARD_ROLE, json.getString("cardRole"));            
        }
        
        /*
        card.setSubscribed(json.getBoolean("subscribed"));
        card.setLabels(labels);
        
        if(json.has("due") && !json.isNull("due"))
        {
            String dueDate = json.getString("due");
            OffsetDateTime odt = OffsetDateTime.parse(dueDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            LOG.info("Due Date: " + odt.toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE));
            card.setDue(DateUtils.asDate(odt.toLocalDate()));
        }
        */
        
        TrelloCard trelloCard = provider.getCard(props);
        //trelloCard.setDueComplete(json.getBoolean("dueComplete"));
        
        return trelloCard;
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
