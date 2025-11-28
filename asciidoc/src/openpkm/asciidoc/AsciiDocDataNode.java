/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.IconProvider;
import openpkm.base.TitleProvider;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

/**
 *
 * @author Rok Koren
 */
public class AsciiDocDataNode extends DataNode implements PropertyChangeListener, ChangeListener
{
    private static final Logger LOG = Logger.getLogger(AsciiDocDataNode.class.getName());    
    
    public AsciiDocDataNode(AsciiDocDataObject data) 
    {
        super(data, Children.LEAF, data.getLookup());
        data.addPropertyChangeListener(this);
        data.addChangeListener(this);
    }
    
    @Override    
    public Action[] getActions(boolean context) 
    {
        List<Action> actions = new ArrayList();         
        
        for(Action action : super.getActions(context))
        {
            actions.add(action);
        }
        
        AsciiDocDataObject data = (AsciiDocDataObject)getDataObject();  
        //actions.add(new PdfAction(data));            
        
        return actions.toArray(new Action[actions.size()]);
    }    
    
    @Override
    public Image getIcon(int i) 
    {
        IconProvider provider = getLookup().lookup(IconProvider.class);  
        if(provider != null)
        {
            return provider.getIcon();
        }
        return super.getIcon(i);
    }     
    
    @Override
    public String getDisplayName() 
    {
        TitleProvider provider = getLookup().lookup(TitleProvider.class);  
        if(provider != null)
        {
            return provider.getTitle();          
        }       
        return super.getDisplayName();
    }     

    @Override
    public void propertyChange(PropertyChangeEvent evt) 
    {
        if(evt.getPropertyName().equals(TitleProvider.PROP_TITLE))
        {
            fireDisplayNameChange(evt.getOldValue().toString(), evt.getNewValue().toString());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) 
    {
        fireIconChange();
    }
}
