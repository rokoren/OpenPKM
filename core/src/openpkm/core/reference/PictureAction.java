/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.reference;

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
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.FileTypeProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.reference.AbstractFilesProvider;
import openpkm.reference.FileWizardPanel1;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import openpkm.reference.ReferenceSourceProvider;
import openpkm.utils.Utils;
import org.openide.DialogDisplayer;
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
        category = "OpenPKM/Picture",
        id = "openpkm.core.reference.PictureAction"
)
@ActionRegistration(
        iconBase = "openpkm/reference/resources/link.png",
        displayName = "#CTL_PictureAction"
)
@Messages("CTL_PictureAction=Add Picture Reference")
public class PictureAction implements ActionListener 
{
    private static final Logger LOG = Logger.getLogger(PictureAction.class.getName());     
    
    private final ReferenceSourceProvider provider;

    public PictureAction(ReferenceSourceProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new FileWizardPanel1(AbstractFilesProvider.PICTURES));
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
        wiz.setTitle("Add Picture Reference");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getLookupProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
            
            String fileName = (String)wiz.getProperty(Reference.PROP_FILE_NAME);
            String fileExt = (String)wiz.getProperty(Reference.PROP_FILE_EXT);
            String filePath = (String)wiz.getProperty(Reference.PROP_FILE_PATH);
            String title = (String)wiz.getProperty(TitleProvider.PROP_TITLE);
            List<String> tags = (List<String>) wiz.getProperty(TagsProvider.PROP_TAGS);   
            List<Topic> topics = (List<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);

            Properties props = new Properties();
            props.setProperty(Reference.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
            props.setProperty(ReferenceProvider.PROP_TYPE, ReferenceProvider.Type.PICTURE.getName());
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
            props.setProperty(Reference.PROP_APP_ID, Utils.getAppID());             
            VisibilityProvider.Modifier visibiltyModifier = (VisibilityProvider.Modifier) wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
            props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, visibiltyModifier.toString());            
            props.setProperty(TitleProvider.PROP_TITLE, title);  
            props.setProperty(Reference.PROP_FILE_NAME, fileName); 
            props.setProperty(Reference.PROP_FILE_EXT, fileExt);
            props.setProperty(Reference.PROP_FILE_PATH, filePath);         

            if(tags != null)
            {
                StringJoiner joiner = new StringJoiner(",");
                for(String tag : tags)
                {
                    joiner.add(tag);
                }
                props.setProperty(TagsProvider.PROP_TAGS, joiner.toString());
            } 
            
            if(topics != null)
            {
                KnowledgeGraphProvider knowledgeGraphProvider = provider.getLookupProvider().getLookup().lookup(KnowledgeGraphProvider.class);
                if(knowledgeGraphProvider != null)
                {
                    StringJoiner joiner = new StringJoiner(",");
                    for(Topic topic : topics)
                    {
                        joiner.add(knowledgeGraphProvider.getTreeID(topic));
                    }
                    props.setProperty(TopicsProvider.PROP_TOPICS, joiner.toString());                    
                }
            }              

            FileObject root = provider.getRootFolder();
            if(root != null)
            {
                Reference reference = provider.getReferenceProvider().getReference(props);
                try
                {
                    FileObject file = provider.createData(reference, fileType); 
                    OutputStream os = root.createAndOpen(reference.getSourceID() + "." + PropertiesProvider.EXTENSION);  
                    reference.save(os, "New picture Created by Wizard");
                    os.close();  

                    StatusDisplayer.getDefault().setStatusText("Picture saved with title: " + title);                        

                    DataObject data = DataObject.find(file);
                    OpenCookie open = data.getCookie(OpenCookie.class);
                    open.open();                         
                }
                catch(DataObjectNotFoundException e)
                {
                    LOG.warning(e.getMessage());
                }                       
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                                      
            }             
                                             
        }        
    }     
}
