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
public abstract class TrelloActionsProvider implements SourceGroup
{    
    protected static final String ROOT_FOLDER = "actions";       

    protected Map<String, TrelloAction> activity; 
    protected FileObject rootDir; 

    protected final TrelloActionProvider provider;
    protected final ChangeSupport changeSupport; 

    public TrelloActionsProvider(TrelloActionProvider provider) 
    {
        this.provider = provider;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloAction> getActivity();
    
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }     

    public TrelloAction getAction(String actionID) 
    {
        return getActivity().get(actionID);
    }                                  

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Activity";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getActivity().containsKey(file.getName());
    }     
}
