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
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public abstract class TrelloCheckListProvider implements SourceGroup
{
    protected static final String ROOT_FOLDER = "check_lists";       

    protected Map<String, TrelloCheckList> checkLists; 
    protected FileObject rootDir; 

    protected final TrelloCheckListFactory factory;
    protected final ChangeSupport changeSupport; 

    public TrelloCheckListProvider(TrelloCheckListFactory factory) 
    {
        this.factory = factory;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloCheckList> getCheckLists();
    
    public abstract void createCheckList(String name);
    public abstract TrelloAccount getAccount(); 
    public abstract Lookup.Provider getProvider();
    
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }     

    public TrelloCheckList getCheckList(String checkListID) 
    {
        return getCheckLists().get(checkListID);
    }     

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Checklists";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getCheckLists().containsKey(file.getName());
    }      
}
