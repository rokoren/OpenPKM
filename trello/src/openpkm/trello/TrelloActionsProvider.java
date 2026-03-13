/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.event.ChangeListener;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Rok Koren
 */
public abstract class TrelloActionsProvider implements SourceProvider<TrelloComment>
{    
    protected static final String ROOT_FOLDER = "actions";       

    protected Map<String, TrelloAction> activity; 
    protected FileObject rootDir; 

    protected final TrelloActionProvider actionProvider;
    protected final TrelloCommentProvider commentProvider;
    protected final ChangeSupport changeSupport; 

    public TrelloActionsProvider(TrelloActionProvider actionProvider, TrelloCommentProvider commentProvider) 
    {
        this.actionProvider = actionProvider;
        this.commentProvider = commentProvider;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloAction> getActionsById();
    
    public Collection<TrelloAction> getActions()
    {
        return Collections.unmodifiableCollection(getActionsById().values());
    }
    
    public TrelloActionProvider getActionProvider()
    {
        return actionProvider;
    }
    
    public TrelloCommentProvider getCommentProvider()
    {
        return commentProvider;
    }    
    
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
        return getActionsById().get(actionID);
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
        return getActionsById().containsKey(file.getName());
    }     
}
