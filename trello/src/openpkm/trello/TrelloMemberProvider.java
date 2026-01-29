/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.Member;
import java.util.Properties;

/**
 *
 * @author Rok Koren
 */
public interface TrelloMemberProvider 
{    
    TrelloMember getMember(Properties props);
    TrelloMember createMember(Member member);    
}
