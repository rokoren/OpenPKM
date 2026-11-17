/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.rss;

import com.rometools.rome.feed.synd.SyndFeed;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.Domain;
import openpkm.base.DomainsGraphProvider;
import openpkm.base.DomainsProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.rss.RssChannel;
import openpkm.rss.RssChannelFactory;
import openpkm.rss.RssChannelProvider;
import openpkm.utils.FileUtils;
import openpkm.utils.Utils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/Channel",
        id = "openpkm.core.rss.RssChannelAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/rss.png",
        displayName = "#CTL_RssChannelAction"
)
@Messages("CTL_RssChannelAction=Add RSS Channel")
public class RssChannelAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(RssChannelAction.class.getName());     
    
    private final RssChannelProvider provider;

    public RssChannelAction(RssChannelProvider provider)
    {
        this.provider = provider;
    }   
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new RssChannelWizardPanel1());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) 
        {
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
        wiz.setTitle("Add RSS Channel");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
            
            String feedUrl = (String) wiz.getProperty(RssChannelFactory.PROP_FEED_URL);
            SyndFeed feed = (SyndFeed) wiz.getProperty("feed");
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);  
            Set<Domain> domains = (Set<Domain>) wiz.getProperty(DomainsProvider.PROP_DOMAINS);
            String fileName = FileUtils.getFileName(provider.getRootFolder(), PropertiesProvider.EXTENSION);

            Properties props = provider.getFactory().getProperties(feedUrl, feed, fileName);
            props.setProperty(RssChannel.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
            props.setProperty(RssChannel.PROP_APP_ID, Utils.getAppID());                                         

            if(domains != null)
            {
                DomainsGraphProvider domainsProvider = provider.getProvider().getLookup().lookup(DomainsGraphProvider.class);
                if(domainsProvider != null)
                {
                    StringJoiner joiner = new StringJoiner(",");
                    for(Domain domain : domains)
                    {
                        joiner.add(domainsProvider.getTreeID(domain));
                    }
                    props.setProperty(DomainsProvider.PROP_DOMAINS, joiner.toString());                    
                }
            }                         

            FileObject root = provider.getRootFolder();
            if(root != null)
            {
                RssChannel channel = provider.getFactory().getChannel(props);
                try
                {
                    FileObject file = provider.createData(channel, fileType); 
                    OutputStream os = root.createAndOpen(channel.getSourceID() + "." + PropertiesProvider.EXTENSION);  
                    provider.getFactory().save(channel, os, "New RSS Channel created by Wizard");
                    os.close();  

                    StatusDisplayer.getDefault().setStatusText("RSS Channel saved with title: " + feed.getTitle());   
                    
                    NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to open RSS Channel in editor?", feed.getTitle(), NotifyDescriptor.YES_NO_OPTION);
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
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                   
            }             
                                                                  
        }        
    }          
}
