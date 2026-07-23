/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.time.LocalDateTime;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.StateSupport;

/**
 *
 * @author Rok Koren
 */
public interface TrelloComment extends Source, StateSupport, PropertiesProvider
{
    String getActionID();
    String getCardID();
    String getText();
    LocalDateTime getDate();    
}
