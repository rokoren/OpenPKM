/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Rok Koren
 */
public abstract class TrelloAttachmentProvider implements SourceGroup
{        
    protected static final String ROOT_FOLDER = "attachments";       

    protected Map<String, TrelloAttachment> attachments; 
    protected FileObject rootDir; 

    protected final TrelloAttachmentFactory factory;
    protected final ChangeSupport changeSupport; 

    public TrelloAttachmentProvider(TrelloAttachmentFactory factory) 
    {
        this.factory = factory;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloAttachment> getAttachmentsById();
    public abstract void createAttachmentLink(String url, String name);
    public abstract HttpURLConnection getAttachmentConn(TrelloAttachment attachment) throws MalformedURLException, IOException;
    
    public Collection<TrelloAttachment> getAttachments()
    {
        return Collections.unmodifiableCollection(getAttachmentsById().values());
    }
    
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
        return getAttachmentsById().get(attachmentID);
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
        return getAttachmentsById().containsKey(file.getName());
    }       
}
