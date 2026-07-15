/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.swing.JComponent;
import openpkm.base.DescriptionProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.domain.Blog;
import openpkm.domain.BlogProvider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
        category = "OpenPKM/Domain",
        id = "openpkm.core.domain.BlogAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/blogger.png",
        displayName = "#CTL_BlogAction"
)
@Messages("CTL_BlogAction=Add Blog")
public class BlogAction implements ActionListener
{
    private static final String ICON_LINK_SELECTOR = "link[rel~=^(.*\\s|)icon(|\\s.*)$]";    
    
    private static final Logger LOG = Logger.getLogger(BlogAction.class.getName());     
    
    private final BlogProvider provider;

    public BlogAction(BlogProvider provider)
    {
        this.provider = provider;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new HomePageWizardPanel1());
        panels.add(new HomePageWizardPanel2());
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
        wiz.setTitle("Add Blog");  
        //wiz.putProperty("WizardPanel_image", ImageUtilities.loadImage(BANNER, true));                    
        wiz.putProperty("provider", provider.getProvider());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) 
        { 
            LocalDateTime now = LocalDateTime.now();
            String blogID = null;
            
            FileTypeProvider fileType = (FileTypeProvider) wiz.getProperty(FileTypeProvider.PROP_FILE_TYPE);
            String url = (String) wiz.getProperty(Blog.PROP_URL);
            String title = (String) wiz.getProperty(TitleProvider.PROP_TITLE);
            String description = (String) wiz.getProperty(DescriptionProvider.PROP_DESCRIPTION);  
            List<Topic> topics = (List<Topic>) wiz.getProperty(TopicsProvider.PROP_TOPICS);            
            
            Document document = (Document) wiz.getProperty("document"); 
            String canonical = document.select("link[rel=canonical]").attr("href");
            if(canonical != null && !canonical.isBlank())
            {
                String signature = getSignature(document, canonical);  
                
                try
                {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(signature.getBytes(StandardCharsets.UTF_8));

                    StringBuilder hex = new StringBuilder();
                    for (byte b : hash) {
                        hex.append(String.format("%02x", b));
                    }

                    blogID = hex.toString();                      
                } 
                catch(NoSuchAlgorithmException e)
                {
                    LOG.warning(e.getMessage());
                }                                
            }
            else
            { 
                String signature = getSignature(document, url);  
                
                try
                {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(signature.getBytes(StandardCharsets.UTF_8));

                    StringBuilder hex = new StringBuilder();
                    for (byte b : hash) {
                        hex.append(String.format("%02x", b));
                    }

                    blogID = hex.toString();                      
                } 
                catch(NoSuchAlgorithmException e)
                {
                    LOG.warning(e.getMessage());
                }
            }            

            Properties props = new Properties();
            props.setProperty(Blog.PROP_BLOG_ID, blogID);
            props.setProperty(Blog.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
            props.setProperty(Blog.PROP_TITLE, title);       
            props.setProperty(Blog.PROP_DESCRIPTION, description);            
            props.setProperty(Blog.PROP_URL, url);  
            
            String favicon = getFavicon(document);
            if(favicon == null)
            {
                favicon = getFaviconGoogle(document);
            }
            
            if(favicon != null)
            {
                props.setProperty(Blog.PROP_FAVICON, favicon);
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
                Blog blog = provider.getFactory().getBlog(props);
                try
                {
                    FileObject file = provider.createData(blog, fileType); 
                    OutputStream os = root.createAndOpen(blog.getBlogID() + "." + PropertiesProvider.EXTENSION);  
                    provider.getFactory().save(blog, os, "New Blog Created by Wizard");
                    os.close();  

                    StatusDisplayer.getDefault().setStatusText("Blog saved with title: " + title);                         

                    NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to open Blog in editor?", title, NotifyDescriptor.YES_NO_OPTION);
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
    
    private static String getSignature(Document document, String url)
    {
        String normalized = getNormalized(url);
        String title = document.title();
        String description = document.select("meta[name=description]").attr("content");
        String ogUrl = document.select("meta[property=og:url]").attr("content");
        String ogTitle = document.select("meta[property=og:title]").attr("content");   
        String signature = normalized + "|" + title + "|" + description + "|" + ogUrl + "|" + ogTitle; 
        return signature;
    }
    
    private static String getFavicon(Document document)
    {
        //Element element = document.head().select("link[href~=.*\\.(ico|png)]").first();    
        Element element = document.head().select(ICON_LINK_SELECTOR).first(); 
        if(element != null)
        {
            return element.absUrl("href");             
        }
        return null;       
    }
    
    private static String getFaviconGoogle(Document document)
    {
	Element element = document.head().select("meta[itemprop=image]").first();    
        if(element != null)
        {
            return element.attr("content");                   
        }
        return null;
    } 

    private static String getNormalized(String url)
    {
        String normalized = url
                .replaceAll("\\?.*", "")     // odstrani query parametre
                .replaceAll("#.*", "")       // odstrani sidra
                .replaceAll("/$", "")        // odstrani trailing slash
                .trim();
        return normalized;
    }     
}
