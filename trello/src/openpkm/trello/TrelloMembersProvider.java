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
public abstract class TrelloMembersProvider implements SourceGroup
{    
    protected static final String ROOT_FOLDER = "members";       

    protected Map<String, TrelloMember> members; 
    protected FileObject rootDir; 

    protected final TrelloMemberProvider provider;
    protected final ChangeSupport changeSupport; 

    public TrelloMembersProvider(TrelloMemberProvider provider) 
    {
        this.provider = provider;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloMember> getMembers();
    
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }     

    public TrelloMember getMember(String memberID) 
    {
        return getMembers().get(memberID);
    }                                  

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Members";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getMembers().containsKey(file.getName());
    }      
}
