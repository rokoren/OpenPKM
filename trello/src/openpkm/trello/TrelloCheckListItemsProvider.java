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
public abstract class TrelloCheckListItemsProvider implements SourceGroup
{
    protected static final String ROOT_FOLDER = "items";       

    protected Map<String, TrelloCheckListItem> checkListItems; 
    protected FileObject rootDir; 

    protected final TrelloCheckListItemProvider provider;
    protected final ChangeSupport changeSupport; 

    public TrelloCheckListItemsProvider(TrelloCheckListItemProvider provider) 
    {
        this.provider = provider;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloCheckListItem> getCheckListItems();
    
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }     

    public TrelloCheckListItem getCheckListItem(String itemID) 
    {
        return getCheckListItems().get(itemID);
    }                                  

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Checklist Items";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getCheckListItems().containsKey(file.getName());
    }     
}
