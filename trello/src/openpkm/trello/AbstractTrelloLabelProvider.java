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
public abstract class AbstractTrelloLabelProvider implements SourceGroup
{    
    protected static final String ROOT_FOLDER = "labels";       

    protected Map<String, TrelloLabel> labels; 
    protected FileObject rootDir; 

    protected final TrelloLabelFactory factory;
    protected final ChangeSupport changeSupport; 

    public AbstractTrelloLabelProvider(TrelloLabelFactory factory) 
    {
        this.factory = factory;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloLabel> getLabelsById();
    
    public Collection<TrelloLabel> getLabels()
    {
        return Collections.unmodifiableCollection(getLabelsById().values());
    }
    
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }     

    public TrelloLabel getLabel(String labelID) 
    {
        return getLabelsById().get(labelID);
    }                                  

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Labels";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getLabelsById().containsKey(file.getName());
    }     
}
