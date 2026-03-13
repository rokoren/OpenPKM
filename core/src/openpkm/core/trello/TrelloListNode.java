/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.DataGroupProvider;
import openpkm.base.NodePositionProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class TrelloListNode implements NodePositionProvider
{
    @StaticResource()
    private static final String ICON = "openpkm/core/resources/application_view_list.png";  
    
    private static final Logger LOG = Logger.getLogger(TrelloListNode.class.getName());     

    private Lookup lkp; 
    
    private final DataGroupProvider provider;        

    public TrelloListNode(DataGroupProvider provider)
    {
        this.provider = provider;
    }           

    @Override
    public String getName() 
    {
        return provider.getName();
    }
    
    @Override
    public Lookup getLookup() 
    {
        if (lkp == null) 
        {          
            lkp = Lookups.fixed(this, provider.getDisplayNameProvider(), provider.getIconProvider(), provider.getActionsProvider());              
        }
        return lkp;
    }     

    @Override
    public Children getChildren() 
    {
        return new ChildrenImpl(provider);
    }    

    @Override
    public HelpCtx getHelp()
    {
        return HelpCtx.DEFAULT_HELP;
    }      

    @Override
    public int getPosition() 
    {
        Integer position = provider.getPosition();
        if(position != null)
        {
            return position.intValue();
        }
        return -1;
    }  
    
    private static final class ChildrenImpl extends Children.Keys<DataObject> implements ChangeListener 
    {
        private final DataGroupProvider provider;            

        public ChildrenImpl(DataGroupProvider provider)
        {
            this.provider = provider;   
            provider.addChangeListener(this);             
        }  

        @Override
        protected void addNotify() 
        {
            updateKeys();
        }

        private void updateKeys() 
        { 
            SortedSet<DataObject> sorted = new TreeSet<DataObject>(provider.getComparator());
            try
            {
                for(FileObject file : provider.getRootFolder().getChildren())
                {
                    DataObject data = null;
                    if(file.isData())
                    {
                        try
                        {
                            data = DataObject.find(file);                    
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.warning(e.getMessage());
                        }
                    }
                    else if(file.isFolder())
                    {
                        data = DataFolder.findFolder(file);
                    }                     

                    if(provider.contains(data))
                    {
                        sorted.add(data);
                    }
                }                
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }              
            setKeys(sorted); 
            
            /*
            SwingUtilities.invokeLater(() -> 
            {
                SortedSet<OpenPkmDataObject> data = new TreeSet<OpenPkmDataObject>(OpenPkmData.titleComparator());
                data.addAll(getData(provider));     
                setKeys(data);    
            });
            */
        }

        @Override
        protected void removeNotify()
        {
            provider.removeChangeListener(this);            
            setKeys(Collections.<DataObject>emptySet());
        }

        @Override
        protected Node[] createNodes(DataObject data) 
        {
            return new Node[] {new FilterNode(data.getNodeDelegate())};
        }          

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            updateKeys();
        }                 
    }         
}
