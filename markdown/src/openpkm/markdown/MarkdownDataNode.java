/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.NodeProvider;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

/**
 *
 * @author Rok Koren
 */
public class MarkdownDataNode extends DataNode implements ChangeListener
{
    private static final Logger LOG = Logger.getLogger(MarkdownDataNode.class.getName());    
    
    public MarkdownDataNode(MarkdownDataObject data) 
    {
        super(data, Children.LEAF, data.getLookup());
        NodeProvider provider = data.getLookup().lookup(NodeProvider.class);
        if(provider != null)
        {
            provider.addChangeListener(this);
        }      
    }
    
    @Override    
    public Action[] getActions(boolean context) 
    {
        List<Action> actions = new ArrayList();         
        
        for(Action action : super.getActions(context))
        {
            actions.add(action);
        }
        
        MarkdownDataObject data = (MarkdownDataObject)getDataObject();  
        //actions.add(new PdfAction(data));            
        
        return actions.toArray(new Action[actions.size()]);
    }    
    
    @Override
    public Image getIcon(int type) 
    {
        NodeProvider provider = getLookup().lookup(NodeProvider.class);
        if(provider != null)
        {
            return provider.getIcon(type);
        }          
        return super.getIcon(type);
    }    
    
    @Override
    public Image getOpenedIcon(int type) 
    {
        NodeProvider provider = getLookup().lookup(NodeProvider.class);
        if(provider != null)
        {
            return provider.getOpenedIcon(type);
        }          
        return super.getIcon(type);
    }      
    
    @Override
    public String getDisplayName() 
    {
        NodeProvider provider = getLookup().lookup(NodeProvider.class);
        if(provider != null)
        {
            return provider.getDisplayName();
        }     
        return super.getDisplayName();
    }  
    
    @Override
    public String getShortDescription()
    {
        NodeProvider provider = getLookup().lookup(NodeProvider.class);
        if(provider != null)
        {
            return provider.getShortDescription();
        }     
        return super.getShortDescription();        
    }
    
    @Override
    public String getHtmlDisplayName() 
    {
        NodeProvider provider = getLookup().lookup(NodeProvider.class);
        if(provider != null)
        {
            return provider.getHtmlDisplayName();
        }  
        return super.getHtmlDisplayName();
    }     

    @Override
    public void stateChanged(ChangeEvent e) 
    {
        fireIconChange();
    }    
}
