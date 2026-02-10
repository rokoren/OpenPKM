/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

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
public abstract class TrelloListsProvider implements SourceGroup
{    
    protected static final String ROOT_FOLDER = "lists";       

    protected Map<String, TrelloList> lists; 
    protected FileObject rootDir; 

    protected final TrelloListProvider provider;
    protected final ChangeSupport changeSupport; 

    public TrelloListsProvider(TrelloListProvider provider) 
    {
        this.provider = provider;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloList> getListsById();
    
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }     

    public Collection<TrelloList> getLists()
    {
        return Collections.unmodifiableCollection(getListsById().values());
    }
    
    public TrelloList getList(String listID) 
    {
        return getListsById().get(listID);
    }                                  

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Lists";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getListsById().containsKey(file.getName());
    }     
}
