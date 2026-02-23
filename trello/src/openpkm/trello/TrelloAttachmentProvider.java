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
    String MIME_TYPE_PDF  = "application/pdf";
    String MIME_TYPE_PNG  = "image/png";
    String MIME_TYPE_JPEG = "image/jpeg";      
    
    String PROP_ATTACHMENT_ID        = "attachment.id";
    String PROP_ATTACHMENT_URL       = "attachment.url";
    String PROP_ATTACHMENT_NAME      = "attachment.name";    
    String PROP_ATTACHMENT_MIME_TYPE = "attachment.mime.type"; 
    String PROP_ATTACHMENT_POSITION  = "attachment.position";    
    
    TrelloAttachment getAttachment(Properties props);
    TrelloAttachment createAttachment(Attachment attachment);     
}
