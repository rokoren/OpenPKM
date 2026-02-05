/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.time.LocalDate;
import java.util.List;
import openpkm.base.DataSource;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCard extends DataSource
{
    String getBoardID();
    String getListID();
    String getCardID();
    String getCardName();
    Integer getCardPosition(); 
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
