/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.Domain;
import openpkm.base.DomainsGraphProvider;
import openpkm.core.raindrop.DomainRaindropWizardPanel1;
import openpkm.raindrop.RaindropChildrenCollection;
import openpkm.raindrop.RaindropCollection;
import openpkm.raindrop.RaindropCollectionProvider;
import openpkm.utils.Utils;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rok
 */
public class DomainNode extends AbstractNode
{
    private static final Logger LOG = Logger.getLogger(DomainNode.class.getName());      
    
    private final DomainsGraphProvider provider;
    private final Domain domain;

    public DomainNode(Project project, DomainsGraphProvider provider, Domain domain) 
    {
        super(new DomainChildren(project, provider, domain), Lookups.fixed(project, domain));
        setName(domain.getDomainID());
        setDisplayName(domain.getName());
        this.provider = provider;
        this.domain = domain;
    }  
    
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
            new SelectDomain(provider, domain),
            new AddDomain(provider, domain),
            new AddDomainRaindrop(provider, domain)
        };
    }  
    
    @Override
    public Action getPreferredAction()
    {
        return new SelectDomain(provider, domain);
    }    
    
    private Image getIcon(boolean opened) 
    {
        RaindropCollectionProvider raindrop = provider.getProvider().getLookup().lookup(RaindropCollectionProvider.class);
        if(raindrop != null)
        {
            try
            {
                RaindropCollection collection = raindrop.getRaindropAccount().getCollection(Integer.parseInt(domain.getDomainID()));  
                if(collection != null)
                {
                    BufferedImage image = collection.getImage();
                    if(image != null)
                    {
                        return Utils.resizeImage(image, 16, 16);
                    }                    
                }
            }
            catch(NumberFormatException e)
            {
                LOG.fine(e.getMessage());
            }             
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }            
        }
        return Utils.getTreeFolderIcon(opened);
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
    
    static final class DomainChildren extends Children.Keys<Domain> implements ChangeListener 
    {
        private final Project project;
        private final DomainsGraphProvider domainsProvider;
        private final Domain domain;        

        public DomainChildren(Project project, DomainsGraphProvider domainsProvider, Domain domain)
        {
            this.project = project;
            this.domainsProvider = domainsProvider;
            this.domain = domain;
            
            if(domainsProvider instanceof ChangeSupportProvider provider)
            {
                provider.addChangeListener(this);                    
            } 
        }  

        @Override
        protected void addNotify() 
        {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            EventQueue.invokeLater(new Runnable() 
            {
                @Override
                public void run()
                {                                                
                    SortedSet<Domain> domains = new TreeSet<Domain>(Domain.nameComparator());
                    domains.addAll(domainsProvider.getChildrenDomains(domain.getDomainID()));           
                    setKeys(domains);                   
                }
            });
        }        

        @Override
        protected void removeNotify() 
        {
            if(domainsProvider instanceof ChangeSupportProvider provider)
            {
                provider.removeChangeListener(this);                    
            }  
            
            setKeys(Collections.<Domain>emptySet());
        }

        @Override
        protected Node[] createNodes(Domain domain) 
        {
            return new Node[] {new DomainNode(project, domainsProvider, domain)};
        }

        @Override
        public void stateChanged(ChangeEvent ev) 
        {
            updateKeys();
        }            
    }  

    private static final class SelectDomain extends AbstractAction
    {
        private final DomainsGraphProvider provider;      
        private final Domain domain;   

        public SelectDomain(DomainsGraphProvider provider, Domain domain) 
        {
            super("Select Domain");
            this.provider = provider;
            this.domain = domain;
            setEnabled(!provider.getSelectedDomains().contains(domain));
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            provider.selectDomain(domain);
        }
    }     
    
    private static final class AddDomain extends AbstractAction
    {
        private final DomainsGraphProvider provider;      
        private final Domain domain;   

        public AddDomain(DomainsGraphProvider provider, Domain domain) 
        {
            super("Add Domain");
            this.provider = provider;
            this.domain = domain;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new DomainWizardPanel1());
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
            WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle("Add Domain");        
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {
                String name = (String) wiz.getProperty("name");
                //provider.addChildrenDomain(domain.getDomainID(), name, tag, visibilityModifier);
            }
        }
    } 
    
    private static final class AddDomainRaindrop extends AbstractAction
    {
        private final DomainsGraphProvider provider;      
        private final Domain domain; 
        
        private List<RaindropChildrenCollection> collections;        

        public AddDomainRaindrop(DomainsGraphProvider provider, Domain domain) 
        {
            super("Add Domain from Raindrop");
            this.provider = provider;
            this.domain = domain;
            RaindropCollectionProvider raindrop = provider.getProvider().getLookup().lookup(RaindropCollectionProvider.class);
            if(raindrop != null)
            {
                try
                {
                    RaindropCollection collection = raindrop.getRaindropAccount().getCollection(Integer.parseInt(domain.getDomainID())); 
                    if(collection != null)
                    {
                        collections = raindrop.getRaindropAccount().getChildrenCollections(collection.getCollectionID());                        
                    }
                }
                catch(NumberFormatException e)
                {
                    LOG.fine(e.getMessage());
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                  
            }          
            setEnabled(collections != null && !collections.isEmpty());            
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {            
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new DomainRaindropWizardPanel1(collections));
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
            WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle("Add Domain from Raindrop");        
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
            {
                RaindropCollection collection = (RaindropCollection) wiz.getProperty("collection");
                //provider.addChildrenTopic(domain.getDomainID(), collection.getCollectionID() + "", collection.getTitle(), tag, visibilityModifier);
            }
        }
    }     
}
