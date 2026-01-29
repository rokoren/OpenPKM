/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Date;

/**
 *
 * @author Rok Koren
 */
public interface TrelloAction 
{
    String getActionID();
    String getType();
    Date getDate();
    String getMemberID();     
}
