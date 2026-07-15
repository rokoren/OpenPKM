/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.time.LocalDateTime;
import openpkm.base.PropertiesProvider;

/**
 *
 * @author Rok Koren
 */
public interface TrelloAction extends PropertiesProvider
{
    String getActionID();
    String getActionType();
    LocalDateTime getActionDate();
    String getMemberID();     
    String getMemberFullName(); 
}
