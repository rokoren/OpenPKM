/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.NotFoundException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Action;
import com.julienvey.trello.domain.Argument;
import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.CheckItem;
import com.julienvey.trello.domain.CheckList;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Organization;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import openpkm.utils.DateTimeUtils;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Rok Koren
 */
public class TrelloService 
{
    private static final Logger LOG = Logger.getLogger(TrelloService.class.getName());     
    
    private static TrelloService service;    
    
    private final Accounts accounts = new Accounts();
    
    public void addListener(ChangeListener listener)
    {
        accounts.getChangeSupport().addChangeListener(listener);
    }
    
    public void removeListener(ChangeListener listener)
    {
        accounts.getChangeSupport().removeChangeListener(listener);
    }    
    
    public void addAccount(TrelloAccount account) 
    {
        accounts.getAccounts().put(account.getUsername(), account);
        accounts.getChangeSupport().fireChange();
    }

    public void removeAccount(TrelloAccount account) 
    {
        accounts.getAccounts().remove(account.getUsername());
        accounts.getChangeSupport().fireChange();
    }

    public TrelloAccount getAccount(String username) 
    {
        return accounts.getAccounts().get(username);
    }

    public Collection<TrelloAccount> getAccounts() 
    {
        return accounts.getAccounts().values();
    }

    public void store(TrelloAccount account) 
    {
        // Store the account
        Preferences preferences =  account.getPreferences();                
        preferences.put(TrelloAccount.PROPS_USERNAME, account.getUsername());
        preferences.put(TrelloAccount.PROPS_API_KEY, account.getApiKey());
        preferences.put(TrelloAccount.PROPS_TITLE, account.getTitle());
        preferences.put(TrelloAccount.PROPS_ACCESS_TOKEN, account.getAccessToken());
    }
    
    private static final class Accounts
    {
        private Map<String, TrelloAccount> accounts; 
        private ChangeSupport changeSupport;
        
        public synchronized Map<String, TrelloAccount> getAccounts()
        {
            if(accounts == null)
            {
                accounts = new HashMap<>();  
                try
                {
                    load();                    
                }
                catch(BackingStoreException e)
                {
                    LOG.warning(e.getMessage());
                }
            }  
            return accounts;
        }
        
        public ChangeSupport getChangeSupport() 
        {
            if(changeSupport == null)
            {
                changeSupport = new ChangeSupport(this);            
            }
            return changeSupport;
        }        
        
        private void load() throws BackingStoreException
        {
            // Load the accounts list        
            String[] names = TrelloAccount.PREFERENCES.childrenNames();
            for (String name : names)
            {
                Preferences preferences = TrelloAccount.PREFERENCES.node(name);
                TrelloAccount account = getAccount(preferences);
                if(account != null)
                {
                    accounts.put(account.getUsername(), account);                
                }
            }
        } 
        
        private TrelloAccount getAccount(Preferences preferences)
        {
            String username = preferences.get(TrelloAccount.PROPS_USERNAME, null);
            if(username != null)
            {
                String title = preferences.get(TrelloAccount.PROPS_TITLE, "");
                String apiKey = preferences.get(TrelloAccount.PROPS_API_KEY, "");
                String accessToken = preferences.get(TrelloAccount.PROPS_ACCESS_TOKEN, ""); 
                TrelloAccount account = new TrelloAccountImpl(username, apiKey, accessToken);
                account.setTitle(title);                      
                return account;            
            }
            return null;
        }                 
    } 

    public static final class TrelloAccountImpl implements TrelloAccount
    {
        private final String username;
        private final String apiKey;
        private final String accessToken;   

        private String title;     
        private Map<String, TrelloBoard> boards;   

        public TrelloAccountImpl(String username, String apiKey, String accessToken) 
        {
            this.username = username;
            this.apiKey = apiKey;
            this.accessToken = accessToken;  
        }   

        private synchronized Map<String, TrelloBoard> getMap()
        {
            if(boards == null)
            {
                boards = new HashMap<>();  
                List<TrelloBoard> list = TrelloService.getBoards(this);
                for (TrelloBoard board : list)
                {
                    boards.put(board.getBoardID(), board);
                    LOG.info("Board: " + board.getTitle() + ", Workspace ID: " + board.getWorkspaceID()); 
                }            
            }  
            return boards;
        }    

        @Override
        public Collection<TrelloBoard> getBoards()
        {
            return Collections.unmodifiableCollection(getMap().values());
        }  

        @Override
        public TrelloBoard getBoard(String boardID)
        {      
            return getMap().get(boardID);
        }

        @Override
        public String getUsername() 
        {
            return username;
        }

        @Override
        public String getTitle()
        {
            return title;
        }

        @Override
        public void setTitle(String title) 
        {
            this.title = title;
        }   

        @Override
        public String getApiKey() 
        {
            return apiKey;
        }

        @Override
        public String getAccessToken() 
        {
            return accessToken;
        }

        @Override
        public Preferences getPreferences() 
        {
            return PREFERENCES.node(username);
        }         
    }
    
    private static final class TrelloBoardImpl implements TrelloBoard
    {
        private static final Color DEFAULT_BACKGROUND = Color.MAGENTA;

        private static final RequestProcessor RP = new RequestProcessor(TrelloBoard.class);             

        private final TrelloAccount account;
        private final Board board;

        private Color background;           

        public TrelloBoardImpl(TrelloAccount account, Board board) 
        {
            this.account = account;
            this.board = board;       
        }

        @Override
        public RequestProcessor getRequestProcessor()
        {
            return RP;
        }      

        @Override
        public TrelloAccount getAccount() 
        {
            return account;
        }      

        @Override
        public String getBoardID() 
        {
            return board.getId();
        }

        @Override
        public String getUrl()
        {
            return board.getUrl();
        }

        @Override
        public String getTitle() 
        {
            return board.getName();
        }

        @Override
        public void setTitle(String title) 
        {
            board.setName(title);
        }

        @Override
        public String getDescription() 
        {
            return board.getDesc();
        }

        @Override
        public void setDescription(String desc) 
        {
            board.setDesc(desc);
        }

        @Override
        public String getWorkspaceID()
        {
            return board.getIdOrganization();
        }

        @Override
        public Color getBackground()
        {
            if(background == null)
            {
                return DEFAULT_BACKGROUND;
            }
            return background;
        }

        @Override
        public void setBackground(Color color) 
        {
            background = color;
        }    

        @Override
        public String toString()
        {
            return getTitle();
        }         
    }
    
    private static final class TrelloListImpl implements TrelloList
    {
        private static final Logger LOG = Logger.getLogger(TrelloListImpl.class.getName());        

        private final TList list;

        private Map<String, TrelloCard> cards;  

        public TrelloListImpl(TList list) 
        {
            this.list = list;
        }     

        @Override
        public String getTitle() 
        {
            return list.getName();
        }

        @Override
        public String getListID() 
        {
            return list.getId();
        }  

        @Override
        public String getBoardID() 
        {
            return list.getIdBoard();
        }

        @Override
        public int getPosition() 
        {
            return list.getPos();
        }

        private synchronized Map<String, TrelloCard> getCardsMap()
        {
            if (cards == null)
            {
                cards = new HashMap<>();
                List<TrelloCard> list = TrelloService.getCards(this);
                for (TrelloCard trelloCard : list)
                {
                    cards.put(trelloCard.getCardID(), trelloCard);
                }  
            }
            return cards;
        }      

        @Override
        public Collection<TrelloCard> getCards()
        {
            return getCardsMap().values();
        }  

        @Override
        public TrelloCard getCard(String cardID)
        {      
            return getCardsMap().get(cardID);
        } 

        @Override
        public TrelloCard addLink(String url)
        {
            try
            {
                Card card = TrelloService.createLink(getListID(), url);
                TrelloCard trelloCard = new TrelloCardImpl(card);
                getCardsMap().put(trelloCard.getCardID(), trelloCard);
                return trelloCard;    
            }
            catch(UnirestException e)
            {
                LOG.warning(e.getMessage());
            }
            return null;
        }   

        @Override
        public TrelloCard addCard(String title, String desc, LocalDate dueDate, int position)
        {
            Card card = new Card();
            card.setName(title);
            if(desc != null)
            {
                card.setDesc(desc);            
            }
            card.setIdList(getListID());
            card.setIdBoard(getBoardID());
            card.setPos(position);
            if(dueDate != null)
            {
                card.setDue(Date.from(dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            Card card1 = TrelloService.createCard(getListID(), card);
            TrelloCard trelloCard = new TrelloCardImpl(card1);
            getCardsMap().put(trelloCard.getCardID(), trelloCard);
            return trelloCard;
        } 

        @Override    
        public void deleteCard(String cardID)
        {
            TrelloCard card = getCardsMap().remove(cardID);
            if(card != null)
            {
                card.delete();
            }
        }
    }  
    
    private static final class TrelloCardImpl implements TrelloCard
    {
        private final Card card; 

        private boolean dueComplete;

        public TrelloCardImpl(Card card) 
        {
            this.card = card;
        }      

        @Override
        public String getBoardID() 
        {
            return card.getIdBoard();
        }    

        @Override
        public String getListID() 
        {
            return card.getIdList();
        }

        @Override
        public String getCardID() 
        {
            return card.getId();
        }

        @Override
        public String getTitle() 
        {
            return card.getName();
        }

        @Override
        public String getDescription()
        {
            return card.getDesc();
        }

        @Override
        public int getPosition() 
        {
            return card.getPos();
        }  

        @Override
        public boolean isClosed()
        {
            return card.isClosed();
        }

        @Override
        public boolean isSubscribed()
        {
            return card.isSubscribed();
        }

        @Override
        public boolean isDueComplete()
        {
            return dueComplete;
        }

        @Override
        public void setDueComplete(boolean complete)
        {
            dueComplete = complete;
        }

        @Override
        public LocalDate getDueDate()
        {
            Date date = card.getDue();
            if(date != null)
            {
                return DateTimeUtils.convertToLocalDate(date);
            }
            return null;
        }

        @Override
        public List<TrelloLabel> getLabels()
        {
            List<TrelloLabel> list = new ArrayList();
            for(Label label : card.getLabels())
            {
                TrelloLabel trelloLabel = new TrelloLabelImpl(label);
                list.add(trelloLabel);
            }
            return list;
        }

        @Override
        public void delete()
        {
            card.delete();
        }
    } 
    
    private static final class TrelloCheckListImpl implements TrelloCheckList
    {
        private final CheckList checkList;

        private Map<String, TrelloCheckListItem> items = new HashMap<>();;

        public TrelloCheckListImpl(CheckList checkList) 
        {
            this.checkList = checkList;
            for(CheckItem item : checkList.getCheckItems())
            {
                TrelloCheckListItem trelloCheckListItem = new TrelloCheckListItemImpl(this, item);            
                items.put(trelloCheckListItem.getCheckListItemID(), trelloCheckListItem);
            }        
        }

        @Override
        public String getCardID() 
        {
            return checkList.getIdCard();
        }

        @Override
        public String getCheckListID()
        {
            return checkList.getId();
        }   

        @Override
        public String getTitle() 
        {
            return checkList.getName();
        }

        @Override
        public int getPosition() 
        {
            return checkList.getPos();
        }

        @Override
        public Collection<TrelloCheckListItem> getItems() 
        {
            return items.values();
        }

        @Override
        public TrelloCheckListItem getItem(String itemID) 
        {
            return items.get(itemID);
        }

        @Override
        public TrelloCheckListItem addItem(String title, int position)
        {
            CheckItem item = new CheckItem();
            item.setId(System.currentTimeMillis() + "");
            item.setName(title);
            item.setState(TrelloCheckListItem.State.INCOMPLETE.toString().toLowerCase());
            item.setPos(position);
            CheckItem item1 = TrelloService.createCheckListItem(getCheckListID(), item);
            TrelloCheckListItem trelloItem = new TrelloCheckListItemImpl(this, item1);
            items.put(trelloItem.getCheckListItemID(), trelloItem);
            return trelloItem;
        }   
    } 
    
    private static final class TrelloCheckListItemImpl implements TrelloCheckListItem
    {
        private final TrelloCheckList checkList;
        private final CheckItem item;

        public TrelloCheckListItemImpl(TrelloCheckList checkList, CheckItem item) 
        {
            this.checkList = checkList;
            this.item = item;
        }

        @Override
        public TrelloCheckList getCheckList() 
        {
            return checkList;
        }

        @Override
        public String getCheckListItemID() 
        {
            return item.getId();
        }

        @Override
        public String getTitle() 
        {
            return item.getName();
        }

        @Override
        public int getPosition() 
        {
            return item.getPos();
        }  

        @Override
        public State getState() 
        {
            Optional<State> optional = State.get(item.getState());
            if(optional.isPresent())
            {
                return optional.get();
            }
            return null;
        }

        @Override
        public void setState(State state)
        {
            item.setState(state.toString().toLowerCase());
        }    
    } 
    
    private static final class TrelloAttachmentImpl implements TrelloAttachment
    {
        private final Attachment attachment;

        public TrelloAttachmentImpl(Attachment attachment) 
        {
            this.attachment = attachment;
        }

        @Override
        public String getAttachmentID() 
        {
            return attachment.getId();
        }   

        @Override
        public String getUrl() 
        {
            return attachment.getUrl();
        }

        @Override
        public String getName() 
        {
            return attachment.getName();
        }   
    }    
    
    public static Organization getOrganization(TrelloBoard board)
    {
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        return trelloApi.getBoardOrganization(board.getBoardID());     
    }        
    
    public static List<TrelloBoard> getBoards(TrelloAccount account)
    {
        List<TrelloBoard> list = new ArrayList();
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        List<Board> boards = trelloApi.getMemberBoards(account.getUsername());
        for(Board board : boards)
        {
            list.add(new TrelloBoardImpl(account, board));
        }                    
        return list;        
    }
    
    public static List<TrelloList> getLists(TrelloBoard trelloBoard)
    {
        List<TrelloList> list = new ArrayList();
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        List<TList> lists = trelloApi.getBoardLists(trelloBoard.getBoardID());
        for(TList trelloList : lists)
        {
            list.add(new TrelloListImpl(trelloList));
        }                    
        return list;        
    } 
    
    public static List<TrelloCard> getCards(TrelloList trelloList)
    {
        List<TrelloCard> list = new ArrayList();
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        List<Card> cards = trelloApi.getListCards(trelloList.getListID());
        for(Card card : cards)
        {
            list.add(new TrelloCardImpl(card));
        }                    
        return list;        
    }     
    
    public static List<TrelloCard> getCards(TrelloBoard trelloBoard)
    {
        List<TrelloCard> list = new ArrayList();
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        List<Card> cards = trelloApi.getBoardCards(trelloBoard.getBoardID());
        for(Card card : cards)
        {
            list.add(new TrelloCardImpl(card));
        }                    
        return list;        
    }  
    
    public static List<TrelloAction> getActions(TrelloBoard trelloBoard)
    {
        List<TrelloAction> list = new ArrayList();
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        List<Action> actions = trelloApi.getBoardActions(trelloBoard.getBoardID());
        for(Action action : actions)
        {
            AbstractTrelloAction trelloAction = AbstractTrelloAction.getTrelloAction(action);
            if(trelloAction != null)
            {
                list.add(trelloAction); 
            }
        }                  
        return list;        
    }        
    
    public static List<TrelloAction> getActions(TrelloBoard board, LocalDateTime after)
    {
        List<TrelloAction> list = new ArrayList();        
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());        
        if(after == null)
        {
            List<Action> actions = trelloApi.getBoardActions(board.getBoardID());
            for(Action action : actions)
            {
                LOG.info("Action: " + action.getType() + ", Member: " + action.getIdMemberCreator());
                AbstractTrelloAction trelloAction = AbstractTrelloAction.getTrelloAction(action);
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
            List<Action> actions = trelloApi.getBoardActions(board.getBoardID(), argument);
            for(Action action : actions)
            {
                LOG.info("Action: " + action.getType() + ", Member: " + action.getIdMemberCreator());
                AbstractTrelloAction trelloAction = AbstractTrelloAction.getTrelloAction(action);
                if(trelloAction != null)
                {
                    list.add(trelloAction);                                    
                }
            }             
        }                 
        return list;        
    }      
    
    public static List<TrelloCheckList> getCheckLists(String cardID)
    {
        List<TrelloCheckList> list = new ArrayList();
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        List<CheckList> checkLists = trelloApi.getCardChecklists(cardID);
        for(CheckList checkList : checkLists)
        {
            TrelloCheckList trelloCheckList = new TrelloCheckListImpl(checkList);
            list.add(trelloCheckList);
        }                  
        return list;        
    }  
    
    public static List<TrelloAttachment> getAttachments(String cardID)
    {
        List<TrelloAttachment> list = new ArrayList();
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        List<Attachment> attachments = trelloApi.getCardAttachments(cardID);
        for(Attachment attachment : attachments)
        {
            TrelloAttachment trelloAttachment = new TrelloAttachmentImpl(attachment);
            list.add(trelloAttachment);
        }                  
        return list;        
    }     
    
    /*
    public static void updateCard(Card card)
    {
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        trelloApi.updateCard(card);          
    }
    */
    
    public static Card createCard(String sectionID, Card card)
    {
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        return trelloApi.createCard(sectionID, card);  
    }  
    
    public static boolean deleteCard(String cardID)
    {
        try
        {
            Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
            trelloApi.deleteCard(cardID); 
            return true;            
        }
        catch(NotFoundException e)
        {
            LOG.warning(e.getMessage());
            return true;
        }        
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        return false;
    } 
    
    public static void updateCardDesc(String cardID, String desc)
    {
        HttpResponse<JsonNode> response = Unirest.put("https://api.trello.com/1/cards/" + cardID)
          .header("Accept", "application/json")
          .queryString("key", "5838d7104aeb61ec0d492f11a26e5921")
          .queryString("token", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38")
          .queryString("desc", desc)
          .asJson();  
        
        LOG.info(response.getStatusText());
    }
    
    public static void updateCardDueComplete(String cardID, boolean complete)
    {
        HttpResponse<JsonNode> response = Unirest.put("https://api.trello.com/1/cards/" + cardID)
          .header("Accept", "application/json")
          .queryString("key", "5838d7104aeb61ec0d492f11a26e5921")
          .queryString("token", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38")
          .queryString("dueComplete", Boolean.toString(complete))
          .asJson();  
        
        LOG.info(response.getStatusText());
    }    
    
    public static void updateComment(String cardID, String actionID, String text)
    {
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        trelloApi.updateComment(cardID, actionID, text);         
    }

    public static void deleteAttachment(String cardID, String attachmentID)
    {
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        trelloApi.deleteAttachment(cardID, attachmentID);  
    }   
    
    public static void deleteAction(String actionID)
    {
        HttpResponse<String> response = Unirest.delete("https://api.trello.com/1/actions/" + actionID)
          .queryString("key", "5838d7104aeb61ec0d492f11a26e5921")
          .queryString("token", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38")
          .asString();
        LOG.info(response.getStatusText());        
    }      
    
    public static Card createLink(String listID, String url) throws UnirestException
    {
        HttpResponse<JsonNode> response = Unirest.post("https://api.trello.com/1/cards")
          .header("Accept", "application/json")
          .queryString("idList", listID)
          .queryString("urlSource", url)
          .queryString("key", "5838d7104aeb61ec0d492f11a26e5921")
          .queryString("token", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38")
          .asJson();  
        
        JSONObject json = response.getBody().getObject();
        Card card = new Card();
        card.setId(json.getString("id"));
        card.setPos(json.getInt("pos"));
        return card;
    }   
    
    public static Attachment createAttachment(String cardID, String name, String url)
    {
        HttpResponse<JsonNode> response = Unirest.post("https://api.trello.com/1/cards/" + cardID + "/attachments")
          .header("Accept", "application/json")
          .queryString("name", name)
          .queryString("url", url)
          .queryString("key", "5838d7104aeb61ec0d492f11a26e5921")
          .queryString("token", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38")
          .asJson();  
        
        JSONArray json1 = response.getBody().getArray();
        JSONObject json = json1.getJSONObject(0);
        Attachment attachment = new Attachment(url);
        attachment.setName(name);
        attachment.setId(json.getString("id"));
        return attachment;        
    }        
    
    public static TrelloCard getCard(String cardID) throws UnirestException
    {
        HttpResponse<JsonNode> response = Unirest.get("https://api.trello.com/1/cards/" + cardID)
          .header("Accept", "application/json")
          .queryString("key", "5838d7104aeb61ec0d492f11a26e5921")
          .queryString("token", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38")
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
                
        Card card = new Card();
        card.setId(json.getString("id"));
        card.setIdBoard(json.getString("idBoard"));
        card.setIdList(json.getString("idList"));
        card.setName(json.getString("name"));
        card.setDesc(json.getString("desc"));
        card.setPos(json.getInt("pos"));
        card.setClosed(json.getBoolean("closed"));
        card.setSubscribed(json.getBoolean("subscribed"));
        card.setLabels(labels);
        
        if(json.has("due") && !json.isNull("due"))
        {
            String dueDate = json.getString("due");
            OffsetDateTime odt = OffsetDateTime.parse(dueDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            LOG.info("Due Date: " + odt.toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE));
            card.setDue(DateTimeUtils.asDate(odt.toLocalDate()));
        }
        
        TrelloCard trelloCard = new TrelloCardImpl(card);
        trelloCard.setDueComplete(json.getBoolean("dueComplete"));
        
        return trelloCard;
    }      

    public static CheckList createCheckList(String cardID, CheckList checkList)
    {
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        return trelloApi.createCheckList(cardID, checkList);  
    }  
    
    public static void deleteCheckList(String checkListID)
    {
        HttpResponse<String> response = Unirest.delete("https://api.trello.com/1/checklists/" + checkListID)
          .queryString("key", "5838d7104aeb61ec0d492f11a26e5921")
          .queryString("token", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38")
          .asString();

        LOG.info(response.getBody());      
    }
    
    public static void deleteCheckListItem(String checkListID, String checkListItemID)
    {
        HttpResponse<String> response = Unirest.delete("https://api.trello.com/1/checklists/" + checkListID + "/checkItems/" + checkListItemID)
          .queryString("key", "5838d7104aeb61ec0d492f11a26e5921")
          .queryString("token", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38")
          .asString();

        LOG.info(response.getBody());      
    }  
    
    public static void setCheckListItemState(String cardID, String itemID, boolean complete)
    {
        String state = "incomplete";
        if(complete)
        {
            state = "complete";
        }
        HttpResponse<String> response = Unirest.put("https://api.trello.com/1/cards/" + cardID + "/checkItem/" + itemID)
          .queryString("key", "5838d7104aeb61ec0d492f11a26e5921")
          .queryString("token", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38")
          .queryString("state", state)                
          .asString();   
        
        LOG.info(response.getBody()); 
    }
    
    public static CheckItem createCheckListItem(String checkListID, CheckItem item)
    {
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        try
        {
            trelloApi.createCheckItem(checkListID, item); 
        }
        catch(Exception e)
        {
            Exceptions.printStackTrace(e);
        }
        CheckList checkList = trelloApi.getCheckList(checkListID);
        return getCheckListItem(checkList, item.getPos());        
    } 
    
    public static void createComment(String cardID, String text)
    {
        Trello trelloApi = new TrelloImpl("5838d7104aeb61ec0d492f11a26e5921", "ATTA534741a8f5192c1ad712004f1438da8f8ae057fc47fcea44e4e25e99a0b96de2D7B7FC38", new JDKTrelloHttpClient());
        trelloApi.addCommentToCard(cardID, text);
    }     
    
    private static CheckItem getCheckListItem(CheckList checkList, int position)
    {
        for(CheckItem item : checkList.getCheckItems())
        {
            if(item.getPos() == position)
            {
                return item;
            }
        }
        return null;
    } 
    
    public static final CheckList getCheckList(String boardID, String cardID, String name, int position)
    {
        CheckList checkList = new CheckList();
        checkList.setName(name);
        checkList.setIdCard(cardID);
        checkList.setPos(position);
        checkList.setIdBoard(boardID); 
        return checkList;
    }
    
    public static final CheckItem getCheckListItem(String name, int position)
    {
        CheckItem item = new CheckItem();
        item.setId(System.currentTimeMillis() + "");
        item.setName(name);
        item.setState(TrelloCheckListItem.State.INCOMPLETE.toString().toLowerCase());
        item.setPos(position);
        return item;
    }     
    
    public static synchronized TrelloService getDefault()
    {
        if(service == null)
        {
            service = new TrelloService();
        }
        return service;
    }       
}
