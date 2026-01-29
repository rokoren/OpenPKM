/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public abstract class TrelloMemberSourceGroup implements SourceGroup
{
    @StaticResource()
    private static final String ICON = "openpkm/trello/resources/user.png";      
    
    protected static final String ROOT_FOLDER = "members";       

    protected Map<String, TrelloMember> members; 
    protected FileObject rootDir; 

    protected final TrelloMemberProvider provider;
    protected final ChangeSupport changeSupport; 

    public TrelloMemberSourceGroup(TrelloMemberProvider provider) 
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
    public Icon getIcon(boolean bln) 
    {
        return new ImageIcon(ImageUtilities.loadImage(ICON));
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getMembers().containsKey(file.getName());
    }      
}
