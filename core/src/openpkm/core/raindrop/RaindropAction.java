/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/contextAction.java to edit this template
 */
package openpkm.core.raindrop;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import openpkm.base.Domain;
import openpkm.base.DomainsProvider;
import openpkm.base.LinkProvider;
import openpkm.base.TagsProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.core.neo4j.TopicWizardPanel;
import openpkm.raindrop.Raindrop;
import openpkm.raindrop.RaindropCollection;
import openpkm.raindrop.RaindropProvider;
import openpkm.utils.DomainWizardPanel;
import openpkm.utils.TagsWizardPanel;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DropDownButtonFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "OpenPKM",
        id = "openpkm.core.raindrop.RaindropAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/raindrop.png",
        displayName = "#CTL_RaindropAction"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/OpenPKM", position = 100),
    @ActionReference(path = "Shortcuts", name = "D-R")
})
@Messages("CTL_RaindropAction=Add Raindrop")
public final class RaindropAction extends AbstractAction implements LookupListener, Presenter.Toolbar
{
    @StaticResource()
    public static final String BANNER = "openpkm/core/resources/raindrop512.png";  
    
    private static final Logger LOG = Logger.getLogger(RaindropAction.class.getName());            
    
    private Lookup.Result<LinkProvider> result;   
    private LinkProvider linkProvider;
    
    private JRadioButtonMenuItem modeDefault, modeDomain, modeTopic;   

    public RaindropAction() 
    {
        ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();
        modeDefault = new JRadioButtonMenuItem("Default", true);
        modeDomain = new JRadioButtonMenuItem("Domain", false);
        modeTopic = new JRadioButtonMenuItem("Topic", false);
        buttonGroup1.add(modeDefault);        
        buttonGroup1.add(modeDomain);        
        buttonGroup1.add(modeTopic);        
        
        putValue(SMALL_ICON, ImageUtilities.loadIcon("openpkm/core/resources/raindrop24.png"));
        result = Utilities.actionsGlobalContext().lookupResult(LinkProvider.class); 
        result.addLookupListener(this);
    }  
    
    @Override
    public void resultChanged(LookupEvent event) 
    { 
        Collection<LinkProvider> coll = (Collection<LinkProvider>) result.allInstances();
        if(!coll.isEmpty())
        {
            linkProvider = coll.iterator().next();              
        }
    }      

    @Override
    public void actionPerformed(ActionEvent ev) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new CreateRaindropWizardPanel1());
        panels.add(new CreateRaindropWizardPanel2());
        panels.add(new TagsWizardPanel());
        if(!modeDomain.isSelected())
        {
            panels.add(new DomainWizardPanel());            
        }
        if(!modeTopic.isSelected())
        {
            panels.add(new TopicWizardPanel());            
        }
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
        wiz.setTitle("Add Raindrop");
        wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));  
        if(linkProvider != null)
        {
            wiz.putProperty(LinkProvider.PROP_LINK, linkProvider.getLink());
        }
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        {
            RaindropProject project = (RaindropProject)wiz.getProperty("project");
            RaindropProvider provider = project.getLookup().lookup(RaindropProvider.class);
            if(provider != null)
            {
                RaindropCollection collection = (RaindropCollection)wiz.getProperty("collection");
                String link = (String) wiz.getProperty(LinkProvider.PROP_LINK);
                String note = (String) wiz.getProperty(Raindrop.PROPS_NOTE);
                Boolean important = (Boolean) wiz.getProperty(Raindrop.PROPS_IMPORTANT);  
                
                List<String> tags = (List<String>) wiz.getProperty(TagsProvider.PROP_TAGS);  
                Set<Domain> domains = (Set<Domain>) wiz.getProperty(DomainsProvider.PROP_DOMAINS);  
                Set<Topic> topics = (Set<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);                
                                                              
                FileObject file = collection == null ? provider.createRaindrop(link, important.booleanValue(), note, tags, domains, topics) : provider.createRaindrop(collection, link, important.booleanValue(), note, tags, domains, topics);
                if(file != null)
                {
                    StatusDisplayer.getDefault().setStatusText("Raindrop saved");                         

                    NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to open Raindrop in editor?", "Raindrop", NotifyDescriptor.YES_NO_OPTION);
                    if(DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION)
                    {
                        try
                        {
                            DataObject data = DataObject.find(file);
                            OpenCookie open = data.getCookie(OpenCookie.class);
                            open.open();                            
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.warning(e.getMessage());
                        }
                    }                                         
                }                
            }
        }
    }
    
    @Override
    public Component getToolbarPresenter() {

        JPopupMenu popup = new JPopupMenu();
        
        popup.add(modeDefault);
        popup.add(modeDomain);
        popup.add(modeTopic);       

        Icon icon = (Icon) getValue(SMALL_ICON);

        JButton button = DropDownButtonFactory.createDropDownButton(icon, popup);
        button.setAction(this);

        return button;
    }    
}
