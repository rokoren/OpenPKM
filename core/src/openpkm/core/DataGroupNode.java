/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.DataGroupProvider;
import openpkm.base.NodeProvider;
import openpkm.base.TitleProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class DataGroupNode extends AbstractNode implements NodeProvider
{    
    private static final Logger LOG = Logger.getLogger(DataGroupNode.class.getName());    
    
    private final DataGroupProvider provider;
    
    public DataGroupNode(DataGroupProvider provider) 
    {
        super(new ChildrenImpl(provider), Lookups.proxy(provider.getProvider()));
        setName(provider.getName());
        setDisplayName(provider.getDisplayName());
        this.provider = provider;
    }      

    @Override    
    public Action[] getActions(boolean context) 
    {
        List<Action> actions = new ArrayList();
        actions.addAll(provider.getActions());
        return actions.toArray(new Action[actions.size()]);
    }

    private Image getIcon(boolean opened) 
    {
        return provider.getIcon(opened);
    } 
    
    @Override    
    public Image getIcon(int type)     
    {
        return getIcon(false);
    }

    @Override
    public Image getOpenedIcon(int type) 
    {
        return getIcon(true);
    }     
    
    @Override
    public Node findNode(Node root, Object target) 
    {
        FileObject file = (FileObject)target;
        return root.getChildren().findChild(file.getName());
        /*
        LOG.info("Finding node: " + target.toString());
        
        for(Node node : root.getChildren().getNodes())
        {
            DataObject data = node.getLookup().lookup(DataObject.class);                                          
            if(data instanceof OpenPkmDataObject && data.getPrimaryFile().equals(target))
            {
                OpenPkmDataObject opdo = (OpenPkmDataObject)data;
                LOG.info("Data found: " + opdo.getDataID());
                LOG.info("Node found: " + node.getName());
                return node;
            }
        }           
        return null;
        */
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
            SortedSet<DataObject> sorted = new TreeSet<DataObject>(titleComparator());
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
    
    private static Comparator<DataObject> titleComparator() 
    {
        return new Comparator<DataObject>() 
        {
            @Override
            public int compare(DataObject data1, DataObject data2) 
            {
                TitleProvider provider1 = data1.getLookup().lookup(TitleProvider.class);
                TitleProvider provider2 = data1.getLookup().lookup(TitleProvider.class);
                return provider1.getTitle().compareTo(provider2.getTitle());
            }
        };
    }    
}
