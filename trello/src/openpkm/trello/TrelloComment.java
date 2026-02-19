/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.time.LocalDateTime;
import openpkm.base.Source;

/**
 *
 * @author Rok Koren
 */
public interface TrelloComment extends Source
{
    String getActionID();
    String getCardID();
    String getText();
    LocalDateTime getDate();    
}
