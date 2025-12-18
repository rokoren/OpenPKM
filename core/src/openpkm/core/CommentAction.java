/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.Content;
import openpkm.base.ContentProvider;
import openpkm.base.FileTypeIndependent;
import openpkm.base.FileTypeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.VisibilityProvider;
import openpkm.utils.ContentSourceProvider;
import openpkm.utils.Utils;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/Comment",
        id = "openpkm.core.CommentAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/comment.png",
        displayName = "#CTL_CommentAction"
)
@Messages("CTL_CommentAction=Add Comment")
public class CommentAction implements ActionListener
{
    @StaticResource()
    private static final String BANNER = "openpkm/core/resources/comment.png";     
    
    private static final Logger LOG = Logger.getLogger(CommentAction.class.getName());     
    
    private final ContentSourceProvider provider;

    public CommentAction(ContentSourceProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new NoteWizardPanel1());
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
        wiz.setTitle("Add Comment");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        {  
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
            String title = (String) wiz.getProperty(TitleProvider.PROP_TITLE);     
            List<String> tags = (List<String>) wiz.getProperty(TagsProvider.PROP_TAGS);

            LocalDateTime now = LocalDateTime.now();

            Properties props = new Properties(); 
            props.setProperty(Content.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
            props.setProperty(ContentProvider.PROP_TYPE, ContentProviderImpl.Type.COMMENT.getName());
            props.setProperty(Content.PROP_APP_ID, Utils.getAppID());
            props.setProperty(FileTypeIndependent.PROP_DATA_FILE_EXTENSION, fileType.getExtension());            
            VisibilityProvider.Modifier visibiltyModifier = (VisibilityProvider.Modifier)wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
            if(visibiltyModifier != null)
            {
                props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, visibiltyModifier.toString());                  
            }          
            props.setProperty(TitleProvider.PROP_TITLE, title);

            StringJoiner joiner = new StringJoiner(",");
            Iterator<String> iterator = tags.iterator();
            while(iterator.hasNext())
            {
                joiner.add(iterator.next());
            } 
            props.setProperty(TagsProvider.PROP_TAGS, joiner.toString());                                                 

            FileObject folder = provider.getRootFolder();
            if(folder != null)
            {
                try
                { 
                    OutputStream os = folder.createAndOpen(now.getNano() + "." + PropertiesProvider.EXTENSION);
                    props.store(os, "New Comment Created by Wizard"); 
                    os.close();                           
                    StatusDisplayer.getDefault().setStatusText("Comment saved with title: " + title);             
                }
                catch(IOException e) 
                {
                    LOG.warning(e.getMessage());
                }                     
            }                      
        }                                                      
    }    
}
