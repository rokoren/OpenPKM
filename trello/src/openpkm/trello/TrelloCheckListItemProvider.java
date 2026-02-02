/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.CheckItem;
import java.util.Properties;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCheckListItemProvider 
{
    TrelloCheckListItem getCheckListItem(Properties props);
    TrelloCheckListItem createCheckListItem(CheckItem item);     
}
