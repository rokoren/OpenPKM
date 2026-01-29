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
public abstract class TrelloLabelSourceGroup implements SourceGroup
{
    @StaticResource()
    private static final String ICON = "openpkm/trello/resources/layer_label.png";      
    
    protected static final String ROOT_FOLDER = "labels";       

    protected Map<String, TrelloLabel> labels; 
    protected FileObject rootDir; 

    protected final TrelloLabelProvider provider;
    protected final ChangeSupport changeSupport; 

    public TrelloLabelSourceGroup(TrelloLabelProvider provider) 
    {
        this.provider = provider;
        changeSupport = new ChangeSupport(this); 
    } 
    
    protected abstract Map<String, TrelloLabel> getLabels();
    
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
        return getLabels().get(labelID);
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
    public Icon getIcon(boolean bln) 
    {
        return new ImageIcon(ImageUtilities.loadImage(ICON));
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getLabels().containsKey(file.getName());
    }     
}
