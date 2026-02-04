/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCard 
{
    String getBoardID();
    String getListID();
    String getCardID();
    String getCardName();
    String getCardDescription();
    LocalDate getDueDate();
    List<TrelloLabel> getLabels();
    int getPosition();  
    boolean isClosed();
    boolean isSubscribed();
    boolean isDueComplete();
    void setDueComplete(boolean complete); 
    void delete();    
}
