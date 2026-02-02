/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import com.julienvey.trello.domain.Attachment;
import java.util.Properties;

/**
 *
 * @author Rok Koren
 */
public interface TrelloAttachmentProvider 
{
    TrelloAttachment getAttachment(Properties props);
    TrelloAttachment createAttachment(Attachment attachment);     
}
