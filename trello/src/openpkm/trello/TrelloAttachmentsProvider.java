/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import java.util.Map;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Rok Koren
 */
public abstract class TrelloAttachmentsProvider implements SourceGroup
{        
    protected static final String ROOT_FOLDER = "attachments";       

    protected Map<String, TrelloAttachment> attachments; 
    protected FileObject rootDir; 

    protected final TrelloAttachmentProvider provider;
    protected final ChangeSupport changeSupport; 

    public TrelloAttachmentsProvider(TrelloAttachmentProvider provider) 
    {
        this.provider = provider;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloAttachment> getAttachments();
    
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }     

    public TrelloAttachment getAttachment(String attachmentID) 
    {
        return getAttachments().get(attachmentID);
    }                                  

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Attachments";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getAttachments().containsKey(file.getName());
    }       
}
