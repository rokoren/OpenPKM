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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.Book;
import openpkm.base.FileTypeProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.core.TopicWizardPanel;
import openpkm.reference.AbstractFilesProvider;
import openpkm.reference.BookWizardPanel2;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import openpkm.utils.FileWizardPanel1;
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
import openpkm.reference.ReferenceFactory;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/Book",
        id = "openpkm.core.reference.BookAction"
)
@ActionRegistration(
        iconBase = "openpkm/reference/resources/link.png",
        displayName = "#CTL_BookAction"
)
@Messages("CTL_BookAction=Add Book Reference")
public class BookAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(BookAction.class.getName());     
    
    private final ReferenceProvider provider;

    public BookAction(ReferenceProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new FileWizardPanel1(AbstractFilesProvider.BOOKS));
        panels.add(new TopicWizardPanel());
        panels.add(new BookWizardPanel2());
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
        wiz.setTitle("Add Book Reference");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
            
            String fileName = (String)wiz.getProperty(Reference.PROP_FILE_NAME);
            String fileExt = (String)wiz.getProperty(Reference.PROP_FILE_EXT);
            String filePath = (String)wiz.getProperty(Reference.PROP_FILE_PATH);
            String title = (String)wiz.getProperty(TitleProvider.PROP_TITLE);
            Set<String> tags = (Set<String>) wiz.getProperty(TagsProvider.PROP_TAGS);    
            Set<Topic> topics = (Set<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);

            Properties props = new Properties();
            props.setProperty(Reference.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME)); 
            props.setProperty(ReferenceFactory.PROP_TYPE, ReferenceFactory.Type.BOOK.getName());
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
            props.setProperty(Reference.PROP_APP_ID, Utils.getAppID());          
            VisibilityProvider.Modifier visibiltyModifier = (VisibilityProvider.Modifier) wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
            props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, visibiltyModifier.toString());
            props.setProperty(TitleProvider.PROP_TITLE, title);  
            props.setProperty(Reference.PROP_FILE_NAME, fileName); 
            props.setProperty(Reference.PROP_FILE_EXT, fileExt);
            props.setProperty(Reference.PROP_FILE_PATH, filePath); 

            String subtitle = (String)wiz.getProperty(Book.PROP_SUBTITLE);
            String authors = (String)wiz.getProperty(Book.PROP_AUTHORS);
            String publisher = (String)wiz.getProperty(Book.PROP_PUBLISHER);
            LocalDate publishDate = (LocalDate)wiz.getProperty(Book.PROP_PUBLISH_DATE);
            String language = (String)wiz.getProperty(Book.PROP_LANGUAGE);
            String isbn = (String)wiz.getProperty(Book.PROP_ISBN);          

            props.setProperty(Book.PROP_SUBTITLE, subtitle);
            props.setProperty(Book.PROP_AUTHORS, authors);
            props.setProperty(Book.PROP_PUBLISHER, publisher);
            if(publishDate != null)
            {
                props.setProperty(Book.PROP_PUBLISH_DATE, publishDate.format(DateTimeFormatter.ISO_DATE));                
            }
            props.setProperty(Book.PROP_LANGUAGE, language);
            props.setProperty(Book.PROP_ISBN, isbn); 

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
                KnowledgeGraphProvider knowledgeGraphProvider = provider.getProvider().getLookup().lookup(KnowledgeGraphProvider.class);
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
                Reference reference = provider.getFactory().getReference(props);
                try
                {
                    FileObject file = provider.createData(reference, fileType); 
                    OutputStream os = root.createAndOpen(reference.getSourceID() + "." + PropertiesProvider.EXTENSION);  
                    provider.getFactory().save(reference, os, "New Book Reference created by Wizard");
                    os.close();  

                    StatusDisplayer.getDefault().setStatusText("Book reference saved with title: " + title);    

                    NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to open book in editor?", title, NotifyDescriptor.YES_NO_OPTION);
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
