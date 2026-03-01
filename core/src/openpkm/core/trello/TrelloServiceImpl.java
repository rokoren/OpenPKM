/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Action;
import com.julienvey.trello.domain.Argument;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAction;
import openpkm.trello.TrelloActionProvider;
import openpkm.trello.TrelloAttachment;
import openpkm.trello.TrelloAttachmentProvider;
import openpkm.trello.TrelloBoard;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardProvider;
import openpkm.trello.TrelloCheckList;
import openpkm.trello.TrelloCheckListItem;
import openpkm.trello.TrelloCheckListProvider;
import openpkm.trello.TrelloComment;
import openpkm.trello.TrelloCommentProvider;
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
    public TrelloList createList(String boardID, String name, TrelloListProvider provider, TrelloAccount account)
    {
        HttpResponse<JsonNode> response = Unirest.post("https://api.trello.com/1/lists/")
          .header("Accept", "application/json")
          .queryString("name", name)
          .queryString("idBoard", boardID)
          .queryString("pos", "bottom")
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asJson();  
        
        JSONArray json1 = response.getBody().getArray();
        JSONObject json = json1.getJSONObject(0);

        Properties props = new Properties();
        props.setProperty(TrelloListProvider.PROP_LIST_ID, json.getString("id"));
        props.setProperty(TrelloListProvider.PROP_BOARD_ID, json.getString("idBoard"));           
        props.setProperty(TrelloListProvider.PROP_LIST_NAME, json.getString("name"));
        props.setProperty(TrelloListProvider.PROP_LIST_POSITION, json.getInt("pos") + "");
        return provider.getList(props);
    }    

    @Override
    public TrelloComment createComment(String cardID, String text, TrelloActionProvider actionProvider, TrelloCommentProvider commentProvider, TrelloAccount account, Trello trello)
    {
        HttpResponse<JsonNode> response = Unirest.post("https://api.trello.com/1/cards/" + cardID + "/actions/comments")
          .header("Accept", "application/json")
          .queryString("text", text)
          .queryString("idCard", cardID)
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asJson();  
        
        JSONObject json = response.getBody().getObject();

        Properties props = new Properties();
        props.setProperty(AbstractTrelloAction.PROP_ACTION_ID, json.getString("id"));
        props.setProperty(AbstractTrelloAction.PROP_ACTION_TYPE, json.getString("type"));           
        props.setProperty(AbstractTrelloAction.PROP_ACTION_DATE, json.getString("date"));

        JSONObject member = json.getJSONObject("memberCreator");
        props.setProperty(AbstractTrelloAction.PROP_MEMBER_ID, member.getString("id"));
        props.setProperty(AbstractTrelloAction.PROP_MEMBER_FULL_NAME, member.getString("fullName"));
        
        JSONObject data = json.getJSONObject("data");
        props.setProperty(AbstractTrelloAction.PROP_COMMENT_TEXT, data.getString("text"));         
        
        JSONObject card = data.getJSONObject("card");
        JSONObject list = data.getJSONObject("list");
        props.setProperty(AbstractTrelloAction.PROP_CARD_ID, card.getString("id"));
        props.setProperty(AbstractTrelloAction.PROP_CARD_NAME, card.getString("name"));
        props.setProperty(AbstractTrelloAction.PROP_LIST_ID, list.getString("id"));
        
        TrelloAction action = actionProvider.getAction(props);
        if(action != null)
        {
            return commentProvider.getComment(action, trello, account);
        }
        
        return null;   
    }  
    
    @Override
    public int deleteComment(String cardID, String actionID, TrelloAccount account)
    {
        HttpResponse<String> response = Unirest.delete("https://api.trello.com/1/cards/" + cardID + "/actions/" + actionID + "/comments")
          .header("Accept", "application/json")
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asString();   
        
        return response.getStatus();
    }   
    
    @Override
    public int deleteCheckListItem(TrelloCheckList checkList, TrelloCheckListItem item, TrelloAccount account)
    {
        HttpResponse<String> response = Unirest.delete("https://api.trello.com/1/checklists/" + checkList.getCheckListID() + "/checkItems/" + item.getCheckListItemID())
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asString();

        return response.getStatus(); 
    }  

    @Override
    public int setCheckListItemState(TrelloCheckList checkList, TrelloCheckListItem item, TrelloAccount account)
    {
        HttpResponse<String> response = Unirest.put("https://api.trello.com/1/cards/" + checkList.getCardID() + "/checkItem/" + item.getCheckListItemID())
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .queryString("state", item.getCheckListItemState().toString())                
          .asString();   
        
        return response.getStatus(); 
    } 
    
    @Override
    public int addLabel(TrelloCard card, TrelloLabel label, TrelloAccount account)
    {
        HttpResponse<String> response = Unirest.post("https://api.trello.com/1/cards/" + card.getCardID() + "/idLabels")
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .queryString("value", label.getLabelID())                
          .asString();   
        
        return response.getStatus();         
    }
    
    @Override
    public int removeLabel(TrelloCard card, TrelloLabel label, TrelloAccount account)
    {
        HttpResponse<String> response = Unirest.delete("https://api.trello.com/1/cards/" + card.getCardID() + "/idLabels/" + label.getLabelID())
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())               
          .asString();   
        
        return response.getStatus();         
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
    public List<TrelloAction> getActions(TrelloBoard board, LocalDateTime after, TrelloActionProvider provider, Trello trello) throws Exception
    {
        List<TrelloAction> list = new ArrayList();        
        if(after == null)
        {
            List<Action> actions = trello.getBoardActions(board.getBoardID());
            for(Action action : actions)
            {
                LOG.fine("Action: " + action.getType() + ", Member: " + action.getIdMemberCreator());
                TrelloAction trelloAction = provider.createAction(action);
                if(trelloAction != null)
                {
                    list.add(trelloAction);                                    
                }
            }             
        }
        else
        {
            ZoneId localZone = ZoneId.systemDefault(); // Replace with your ZoneId if needed
            // Step 2: Convert to ZonedDateTime using the local zone
            ZonedDateTime localZonedDateTime = after.atZone(localZone);
            // Step 3: Convert to UTC
            ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
            // Step 4: Format the date as ISO 8601 with 'Z' at the end
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            LOG.info("Since: " + utcZonedDateTime.format(formatter));
            Argument argument = new Argument("since", utcZonedDateTime.format(formatter));
            List<Action> actions = trello.getBoardActions(board.getBoardID(), argument);
            for(Action action : actions)
            {
                LOG.fine("Action: " + action.getType() + ", Member: " + action.getIdMemberCreator());
                TrelloAction trelloAction = provider.createAction(action);
                if(trelloAction != null)
                {
                    list.add(trelloAction);                                    
                }
            }             
        }                 
        return list;        
    }     
    
    @Override
    public List<Card> getCards(TrelloBoard board, Trello trello)
    {        
        Argument argument1 = new Argument("fields", "id");
        Argument argument2 = new Argument("fields", "dateLastActivity");
        return trello.getBoardCards(board.getBoardID(), argument1, argument2);      
    } 
    
    @Override
    public String getCardDescription(String cardID, Trello trello)
    { 
        Argument argument = new Argument("fields", "desc");
        Card card = trello.getCard(cardID, argument);                    
        return card.getDesc();        
    } 

    @Override
    public void setCardDescription(String cardID, String description, TrelloAccount account)
    {
        HttpResponse<JsonNode> response = Unirest.put("https://api.trello.com/1/cards/" + cardID)
          .header("Accept", "application/json")
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .queryString("desc", description)
          .asJson();  
        
        LOG.info(response.getStatusText());
    }
    
    @Override
    public String getCommentText(String actionID, Trello trello)
    { 
        Action action = trello.getAction(actionID);                    
        return action.getData().getText();        
    }   
    
    @Override
    public void setCommentText(String cardID, String actionID, String description, Trello trello)
    {
        trello.updateComment(cardID, actionID, description);          
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
    public List<TrelloAttachment> getAttachments(TrelloCard card, TrelloAttachmentProvider provider, TrelloAccount account)
    {
        HttpResponse<JsonNode> response = Unirest.get("https://api.trello.com/1/cards/" + card.getCardID() + "/attachments")
          .header("Accept", "application/json")
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asJson();  
        
        List<TrelloAttachment> list = new ArrayList(); 
        if(response.isSuccess())
        {
            JSONArray jsons = response.getBody().getArray();
            for(int i=0; i<jsons.length(); i++)
            {
                JSONObject json = jsons.getJSONObject(i);
                Properties props = new Properties();
                props.setProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_ID, json.getString("id"));
                props.setProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_NAME, json.getString("name")); 
                if(!json.isNull("mimeType"))
                {
                    props.setProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_MIME_TYPE, json.getString("mimeType"));                    
                }
                props.setProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_URL, json.getString("url"));
                props.setProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_POSITION, json.getInt("pos") + "");          
                list.add(provider.getAttachment(props));
            }            
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
        if(response.isSuccess())
        {
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
                props.setProperty(TrelloCheckListProvider.PROP_CHECKLIST_ITEMS, json.getJSONArray("checkItems").toString());            
                list.add(provider.getCheckList(props));
            }            
        }
        return list;        
    } 
    
    @Override
    public TrelloCheckList createCheckList(String cardID, String name, TrelloCheckListProvider provider, TrelloAccount account)
    {
        HttpResponse<JsonNode> response = Unirest.post("https://api.trello.com/1/cards/" + cardID + "/checklists")
          .header("Accept", "application/json")
          .queryString("name", name)
          .queryString("idCard", cardID)
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asJson();  
        
        JSONArray json1 = response.getBody().getArray();
        JSONObject json = json1.getJSONObject(0);

        Properties props = new Properties();
        props.setProperty(TrelloCheckListProvider.PROP_CHECKLIST_ID, json.getString("id"));
        props.setProperty(TrelloCheckListProvider.PROP_BOARD_ID, json.getString("idBoard"));
        props.setProperty(TrelloCheckListProvider.PROP_CARD_ID, json.getString("idCard"));            
        props.setProperty(TrelloCheckListProvider.PROP_CHECKLIST_NAME, json.getString("name"));
        props.setProperty(TrelloCheckListProvider.PROP_CHECKLIST_POSITION, json.getInt("pos") + "");
        return provider.getCheckList(props);
    }
    
    @Override
    public JSONObject createCheckListIem(String checkListID, String name, TrelloAccount account)
    {
        HttpResponse<JsonNode> response = Unirest.post("https://api.trello.com/1/checklists/" + checkListID + "/checkItems")
          .header("Accept", "application/json")
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .queryString("name", name)
          .asJson(); 
        
        JSONArray jsons = response.getBody().getArray();
        return jsons.getJSONObject(0);       
    }    
    
    @Override
    public TrelloAttachment createAttachmentLink(String cardID, String name, String url, TrelloAttachmentProvider provider, TrelloAccount account)
    {
        HttpResponse<JsonNode> response = Unirest.post("https://api.trello.com/1/cards/" + cardID + "/attachments")
          .header("Accept", "application/json")
          .queryString("name", name)
          .queryString("url", url)
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asJson();  

        JSONArray json1 = response.getBody().getArray();
        JSONObject json = json1.getJSONObject(0);
        
        Properties props = new Properties();
        props.setProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_ID, json.getString("id"));
        props.setProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_URL, url);        
        props.setProperty(TrelloAttachmentProvider.PROP_ATTACHMENT_NAME, name);     

        return provider.getAttachment(props);        
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
        StringJoiner joiner = new StringJoiner(",");
        for(int i=0; i<idLabels.length(); i++)
        {
            String id = idLabels.getString(i);
            joiner.add(id);
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
        props.setProperty(TrelloCardProvider.PROP_CARD_SUBSCRIBED, Boolean.toString(json.getBoolean("subscribed")));
        props.setProperty(TrelloCardProvider.PROP_CARD_TEMPLATE, Boolean.toString(json.getBoolean("isTemplate")));
        props.setProperty(TrelloCardProvider.PROP_CARD_DUE_COMPLETE, Boolean.toString(json.getBoolean("dueComplete")));
        props.setProperty(TrelloCardProvider.PROP_CARD_DATE_LAST_ACTIVITY, json.getString("dateLastActivity"));
        props.setProperty(TrelloCardProvider.PROP_CARD_LABELS_ID, joiner.toString());
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
        return provider.getCard(props);
    }   
    
    @Override
    public TrelloCard createCard(String listID, String name, TrelloCardProvider provider, TrelloAccount account)
    {
        HttpResponse<JsonNode> response = Unirest.post("https://api.trello.com/1/cards/")
          .header("Accept", "application/json")
          .queryString("name", name)
          .queryString("idList", listID)
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asJson();  
        
        JSONArray json1 = response.getBody().getArray();
        JSONObject json = json1.getJSONObject(0);

        JSONArray idLabels = json.getJSONArray("idLabels");
        StringJoiner joiner = new StringJoiner(",");
        for(int i=0; i<idLabels.length(); i++)
        {
            String id = idLabels.getString(i);
            joiner.add(id);
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
        props.setProperty(TrelloCardProvider.PROP_CARD_SUBSCRIBED, Boolean.toString(json.getBoolean("subscribed")));
        props.setProperty(TrelloCardProvider.PROP_CARD_TEMPLATE, Boolean.toString(json.getBoolean("isTemplate")));
        props.setProperty(TrelloCardProvider.PROP_CARD_DUE_COMPLETE, Boolean.toString(json.getBoolean("dueComplete")));
        props.setProperty(TrelloCardProvider.PROP_CARD_DATE_LAST_ACTIVITY, json.getString("dateLastActivity"));
        props.setProperty(TrelloCardProvider.PROP_CARD_LABELS_ID, joiner.toString());
        if(!json.isNull("cardRole"))
        {
            props.setProperty(TrelloCardProvider.PROP_CARD_ROLE, json.getString("cardRole"));            
        }
        
        return provider.getCard(props);
    } 
    
    @Override
    public TrelloCard createLink(String listID, String url, TrelloCardProvider provider, TrelloAccount account) throws UnirestException
    {
        HttpResponse<JsonNode> response = Unirest.post("https://api.trello.com/1/cards")
          .header("Accept", "application/json")
          .queryString("idList", listID)
          .queryString("urlSource", url)
          .queryString("cardRole", "link")
          .queryString("key", account.getApiKey())
          .queryString("token", account.getAccessToken())
          .asJson();  
        
        JSONArray json1 = response.getBody().getArray();
        JSONObject json = json1.getJSONObject(0);

        JSONArray idLabels = json.getJSONArray("idLabels");
        StringJoiner joiner = new StringJoiner(",");
        for(int i=0; i<idLabels.length(); i++)
        {
            String id = idLabels.getString(i);
            joiner.add(id);
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
        props.setProperty(TrelloCardProvider.PROP_CARD_SUBSCRIBED, Boolean.toString(json.getBoolean("subscribed")));
        props.setProperty(TrelloCardProvider.PROP_CARD_TEMPLATE, Boolean.toString(json.getBoolean("isTemplate")));
        props.setProperty(TrelloCardProvider.PROP_CARD_DUE_COMPLETE, Boolean.toString(json.getBoolean("dueComplete")));
        props.setProperty(TrelloCardProvider.PROP_CARD_DATE_LAST_ACTIVITY, json.getString("dateLastActivity"));
        props.setProperty(TrelloCardProvider.PROP_CARD_LABELS_ID, joiner.toString());
        if(!json.isNull("cardRole"))
        {
            props.setProperty(TrelloCardProvider.PROP_CARD_ROLE, json.getString("cardRole"));            
        }
        
        return provider.getCard(props);
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
