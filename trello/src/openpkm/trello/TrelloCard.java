/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.time.LocalDateTime;
import java.util.List;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCard extends Source, PropertiesProvider
{
    String getAccountUsername();    
    String getBoardID();
    String getListID();
    String getCardID();
    String getCardName();
    Integer getCardPosition();
    Boolean isCardClosed();
    Boolean isCardSubsribed();
    Boolean isCardPinned();    
    Boolean isCardDueComplete(); 
    void setCardDueComplete(Boolean complete); 
    Boolean isCardTemplate(); 
    LocalDateTime getDateLastActivity();
    String getCardRole();
    List<String> getCardLabelsID();
    void setCardLabelsID(List<String> ids);
    boolean isCardLink();
    /*
    String getCardDescription();    
    LocalDate getDueDate();
    List<TrelloLabel> getLabels();    
    boolean isClosed();
    boolean isSubscribed();
    boolean isDueComplete();
    void setDueComplete(boolean complete); 
    void delete();    
    */
}
