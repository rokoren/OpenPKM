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
import openpkm.base.Blog;
import openpkm.base.DescriptionProvider;
import openpkm.base.Domain;
import openpkm.base.DomainsProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.TitleProvider;
import openpkm.base.Topic;
import openpkm.base.TopicsProvider;
import openpkm.core.HomePageWizardPanel1;
import openpkm.core.HomePageWizardPanel2;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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
    
    private final DomainsProvider provider;

    public BlogAction(DomainsProvider provider)
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
            String domainID = null;
            
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

                    domainID = hex.toString();                      
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

                    domainID = hex.toString();                      
                } 
                catch(NoSuchAlgorithmException e)
                {
                    LOG.warning(e.getMessage());
                }
            }            

            Properties props = new Properties();
            props.setProperty(Blog.PROP_BLOG_ID, domainID);
            props.setProperty(Domain.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
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

            try
            {  
                FileObject projectDirectory = FileUtil.createFolder(provider.getRootDirectory(), domainID);           
                FileObject projectFolder = FileUtil.createFolder(projectDirectory, BlogProjectFactory.PROJECT_FOLDER);                   

                OutputStream os = projectFolder.createAndOpen(BlogProjectFactory.PROJECT_FILE);
                props.store(os, "OpenPKM Blog Project"); 
                os.close(); 
                                
                StatusDisplayer.getDefault().setStatusText("OpenPKM Blog Project saved: " + title); 

                Project project = ProjectManager.getDefault().findProject(projectDirectory);
                if(project != null)
                {
                    Domain domain = project.getLookup().lookup(Domain.class);
                    if(domain != null)
                    {
                        provider.addDomain(domain);
                        /*
                        Project[] projects = {domain};
                        OpenProjects.getDefault().open(projects, false);   
                        */
                    }
                }                  
            }
            catch(IOException e) 
            {
                LOG.warning(e.getMessage());
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
