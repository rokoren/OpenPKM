/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.time.LocalDate;
import java.util.List;
import openpkm.base.Source;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCard extends Source
{
    String getBoardID();
    String getListID();
    String getCardID();
    String getCardName();
    Integer getCardPosition();
    Boolean isCardClosed();
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
