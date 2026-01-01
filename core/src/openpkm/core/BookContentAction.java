/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.Book;
import openpkm.base.Content;
import openpkm.base.ContentProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.reference.BookWizardPanel2;
import openpkm.utils.ContentSourceProvider;
import openpkm.utils.Utils;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Rok Koren
 */
@ActionID(
        category = "OpenPKM/Book",
        id = "openpkm.core.BookContentAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/book.png",
        displayName = "#CTL_BookContentAction"
)
@Messages("CTL_BookContentAction=Add Book")
public class BookContentAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(BookContentAction.class.getName());     
    
    private final ContentSourceProvider provider;

    public BookContentAction(ContentSourceProvider provider) 
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new NoteWizardPanel1());
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
        wiz.setTitle("Add Book");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
            
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
            String title = (String) wiz.getProperty(TitleProvider.PROP_TITLE);      
            List<String> tags = (List<String>) wiz.getProperty(TagsProvider.PROP_TAGS);
            List<Topic> topics = (List<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);

            Properties props = new Properties();
            props.setProperty(Content.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
            props.setProperty(ContentProvider.PROP_TYPE, ContentProviderImpl.Type.BOOK.getName());
            props.setProperty(Content.PROP_APP_ID, Utils.getAppID());           
            VisibilityProvider.Modifier visibiltyModifier = (VisibilityProvider.Modifier)wiz.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
            if(visibiltyModifier != null)
            {
                props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, visibiltyModifier.toString());                  
            }          
            props.setProperty(TitleProvider.PROP_TITLE, title);

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

            if(provider.createSource(props, fileType) != null)
            {
                StatusDisplayer.getDefault().setStatusText("Book saved with title: " + title);                    
            }                                             
        }        
    }     
}
