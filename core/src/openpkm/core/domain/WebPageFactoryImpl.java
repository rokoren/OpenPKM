/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import com.rometools.rome.feed.synd.SyndEntry;
import java.awt.BorderLayout;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import openpkm.base.Article;
import openpkm.base.DescriptionProvider;
import openpkm.base.IconProvider;
import openpkm.base.Link;
import openpkm.base.PropertiesProvider;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.base.TopicsProvider;
import openpkm.base.VisibilityProvider;
import openpkm.base.WorkflowProvider;
import openpkm.domain.WebPage;
import openpkm.domain.WebPageFactory;
import openpkm.jcef.CefClientProvider;
import openpkm.utils.DateTimeUtils;
import openpkm.utils.DisplayNameProviderImpl;
import org.cef.browser.CefBrowser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=WebPageFactory.class)
public class WebPageFactoryImpl implements WebPageFactory 
{
    private static final Logger LOG = Logger.getLogger(WebPageFactory.class.getName());     
    
    @Override
    public WebPage getWebPage(Properties props) 
    {
        String name = props.getProperty(PROP_TYPE);
        if(name != null)
        {
            Optional<Type> type = Type.get(name);
            if(type.isPresent())
            {
                if(type.get() == Type.LINK)
                {
                    return new LinkImpl(props);
                }
                else if(type.get() == Type.RSS)
                {
                    return new RssImpl(props);
                }                 
                else if(type.get() == Type.ARTICLE)
                {
                    return new ArticleImpl(props);
                }                                 
            }
        }
        return null;
    } 

    @Override
    public WebPage getWebPage(String fileName, SyndEntry syndEntry) 
    {
        LocalDateTime now = LocalDateTime.now();
        Properties props = new Properties();
        props.setProperty(WebPageFactory.PROP_FILE_NAME, fileName); 
        props.setProperty(WebPageFactory.PROP_TYPE, WebPageFactory.Type.RSS.getName());                            
        props.setProperty(WebPage.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
        props.setProperty(WebPageFactory.PROP_URI, syndEntry.getUri());                            
        props.setProperty(WebPageFactory.PROP_LINK_URL, syndEntry.getLink());                                     
        props.setProperty(TitleProvider.PROP_TITLE, syndEntry.getTitle());     
        props.setProperty(DescriptionProvider.PROP_DESCRIPTION, syndEntry.getDescription().getValue());                                                      
        props.setProperty(WebPageFactory.PROP_PUBLISHED_DATE, DateTimeUtils.convertToLocalDateTime(syndEntry.getPublishedDate()).format(DateTimeFormatter.ISO_DATE_TIME)); 
        props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, VisibilityProvider.Modifier.PRIVATE.toString()); 
        props.setProperty(WorkflowProvider.PROP_WORKFLOW, WorkflowProvider.Workflow.READ_LATER.toString()); 
        return getWebPage(props);
    } 
    
    @Override
    public void save(WebPage page, OutputStream os, String comments) throws IOException
    {
        page.getProperties().store(os, comments); 
        LOG.info("Web Page saved");
    }      
    
    private static abstract class AbstractWebPage implements WebPage, TitleProvider, IconProvider, TagsProvider, TopicsProvider, MultiViewDescription
    {  
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/www_page.png";           
        
        protected static final Logger LOG = Logger.getLogger(AbstractWebPage.class.getName());     

        protected final Properties props; 
        protected final PropertyChangeSupport propertyChangeSupport;
        
        private Lookup lkp;  
        protected State state;        

        public AbstractWebPage(Properties props) 
        {
            this.props = props;
            propertyChangeSupport = new PropertyChangeSupport(this);              
        }
        
        @Override
        public String getLinkUrl()
        {
            return props.getProperty(PROP_LINK_URL);
        }
        
        @Override
        public String getFileName()
        {
            return props.getProperty(PROP_FILE_NAME);
        }        
        
        @Override
        public String getSourceID()
        {            
            return getFileName();
        }         
        
        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            { 
                lkp = Lookups.fixed(this, new DisplayNameProviderImpl(this));              
            }
            return lkp;
        }  
        
        @Override
        public String getTitle()
        {
            return props.getProperty(TitleProvider.PROP_TITLE);
        }

        @Override
        public void setTitle(String title)
        {
            if(title == null)
            {
                Object oldValue = props.remove(TitleProvider.PROP_TITLE);
                propertyChangeSupport.firePropertyChange(TitleProvider.PROP_TITLE, oldValue, title);
            }
            else
            {
                Object oldValue = props.setProperty(TitleProvider.PROP_TITLE, title);
                propertyChangeSupport.firePropertyChange(TitleProvider.PROP_TITLE, oldValue, title);
            }
        }  

        @Override
        public void addTitleListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_TITLE, listener);
        }

        @Override
        public void removeTitleListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_TITLE, listener);
        }         
                
        @Override
        public Properties getProperties()
        {
            return props;
        }  
        
        @Override
        public boolean merge(PropertiesProvider provider)
        {
            if(props.equals(provider.getProperties()))       
            {
                return false;
            }
            props.putAll(provider.getProperties());        
            return true;
        }               
        
        @Override
        public String getAppID()
        {
            return props.getProperty(PROP_APP_ID);
        }           

        @Override
        public boolean isModified() 
        {
            return state == State.MODIFIED;
        }

        @Override
        public void markModified()
        {
            State oldValue = state;
            state = State.MODIFIED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }   

        @Override
        public boolean isDeleted() 
        {
            return state == State.DELETED;
        }        
        
        @Override
        public void notifyDeleted()
        {
            State oldValue = state;
            state = State.DELETED;
            propertyChangeSupport.firePropertyChange(PROP_STATE, oldValue, state);        
        }        

        @Override
        public LocalDateTime getTimeCreated() 
        {
            String created = props.getProperty(PROP_TIME_CREATED);
            if(created != null)
            {
                return LocalDateTime.parse(created, DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;
        }                              

        @Override
        public Set<String> getTags()
        {
            if(props.containsKey(PROP_TAGS))
            {
                String string = props.getProperty(PROP_TAGS);
                return Set.of(string.split(","));
            }   
            return Collections.EMPTY_SET;
        } 

        @Override
        public Set<String> getTopics()
        {
            String topics = props.getProperty(PROP_TOPICS);
            if(topics != null)
            {
                return Set.of(topics.split(","));                   
            }                
            return Collections.EMPTY_SET;
        }   
        
        @Override
        public Image getIcon(int type) 
        {  
            return ImageUtilities.loadImage(ICON);             
        }          
        
        @Override
        public Image getIcon() 
        {  
            return ImageUtilities.loadImage(ICON);             
        }   
        
        @Override
        public String preferredID() 
        {
            return "web_page";
        }         
        
        @Override
        public MultiViewElement createElement() 
        {
            return new MultiViewElementImpl(this);
        } 

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Web Page";
        }   
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }         
    }  
    
    private static final class LinkImpl extends AbstractWebPage implements Link
    {            
        public LinkImpl(Properties props)
        {
            super(props);
        }         
        
        @Override
        public String getLink() 
        {
            return props.getProperty(PROP_LINK);
        }         
        
        @Override
        public Document getDocument(String userAgent) throws IOException
        {
            return Jsoup.connect(getLink()).ignoreContentType(true).userAgent(userAgent).get();   
        }                 
        
        @Override
        public Image getIcon(int type) 
        {  
            return ImageUtilities.loadImage(ICON);             
        }       
    }
    
    private static final class RssImpl extends AbstractWebPage implements DescriptionProvider, Link, WorkflowProvider, VisibilityProvider
    {             
        public RssImpl(Properties props)
        {
            super(props);
        } 
        
        @Override
        public LocalDateTime getTimeCreated() 
        {
            return getPublishedDate();
        }          
        
        public String getUri() 
        {
            return props.getProperty(PROP_URI);
        }         
        
        @Override
        public String getLink() 
        {
            return props.getProperty(PROP_LINK);
        }  
        
        public LocalDateTime getPublishedDate() 
        {
            String string = props.getProperty(PROP_PUBLISHED_DATE);
            if(string != null)
            {
                return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;
        }         
        
        @Override
        public Document getDocument(String userAgent) throws IOException
        {
            return Jsoup.connect(getLink()).ignoreContentType(true).userAgent(userAgent).get();   
        }                                                
        
        @Override
        public String getDescription()
        {
            return props.getProperty(PROP_DESCRIPTION);
        } 
        
        @Override
        public void setDescription(String description)
        {
            if(description == null)
            {
                Object oldValue = props.remove(PROP_DESCRIPTION);
                propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldValue, description);
            }
            else
            {
                Object oldValue = props.setProperty(PROP_DESCRIPTION, description);
                propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldValue, description);
            }
        } 

        @Override
        public void addDescriptionListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_DESCRIPTION, listener);
        }

        @Override
        public void removeDescriptionListener(PropertyChangeListener listener)
        {
            propertyChangeSupport.addPropertyChangeListener(PROP_DESCRIPTION, listener);
        } 
        
        @Override
        public VisibilityProvider.Modifier getModifier()
        {
            String name = props.getProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER);
            if(name != null)
            {
                Optional<VisibilityProvider.Modifier> optional = VisibilityProvider.Modifier.get(name);
                if(optional.isPresent())
                {
                    return optional.get();
                }
            }
            return VisibilityProvider.Modifier.NONE;
        }

        @Override
        public void setModifier(VisibilityProvider.Modifier modifier)
        {
            if(modifier == null)
            {
                props.remove(VisibilityProvider.PROP_VISIBILITY_MODIFIER);         
            }
            else
            {
                props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, modifier.toString());  
            }
        } 
        
        @Override
        public Workflow getWorkflow()
        {
            String name = props.getProperty(WorkflowProvider.PROP_WORKFLOW);
            if(name != null)
            {
                Optional<WorkflowProvider.Workflow> optional = WorkflowProvider.Workflow.get(name);
                if(optional.isPresent())
                {
                    return optional.get();
                }
            }
            return WorkflowProvider.Workflow.DEFAULT;
        }
        
        @Override
        public void setWorkflow(Workflow workflow)
        {
            if(workflow == null)
            {
                props.remove(WorkflowProvider.PROP_WORKFLOW);         
            }
            else
            {
                props.setProperty(WorkflowProvider.PROP_WORKFLOW, workflow.toString());  
            }            
        }                        
    }    
    
    private static final class ArticleImpl extends AbstractWebPage implements Article, Link
    { 
        @StaticResource()
        public static final String ICON = "openpkm/core/resources/www_page.png";        
        
        public ArticleImpl(Properties props)
        {
            super(props);
        }         

        @Override
        public String getLink() 
        {
            return props.getProperty(PROP_LINK);
        }         
        
        @Override
        public Document getDocument(String userAgent) throws IOException
        {
            return Jsoup.connect(getLink()).ignoreContentType(true).userAgent(userAgent).get();   
        }                

        @Override
        public String getPublisher() 
        {
            return props.getProperty(PROP_PUBLISHER);
        }

        @Override
        public void setPublisher(String publisher) 
        {
            if(publisher == null)
            {
                props.remove(PROP_PUBLISHER);
            }
            else
            {
                props.setProperty(PROP_PUBLISHER, publisher);
            }
        }

        @Override
        public String getLanguage() 
        {
            return props.getProperty(PROP_LANGUAGE);
        }

        @Override
        public void setLanguage(String lang)
        {
            if(lang == null)
            {
                props.remove(PROP_LANGUAGE);
            }
            else
            {
                props.setProperty(PROP_LANGUAGE, lang);
            }
        } 

        @Override
        public Image getIcon(int type) 
        {  
            return ImageUtilities.loadImage(ICON);             
        } 
    } 
    
    private static final class MultiViewElementImpl extends JPanel implements MultiViewElement
    {
        private CefBrowser browser; 
        private JToolBar toolbar;
        
        private transient MultiViewElementCallback callback;  
        
        private final WebPage page;

        public MultiViewElementImpl(WebPage page) 
        {
            this.page = page;
            setLayout(new BorderLayout());
        }                
        
        @Override
        public UndoRedo getUndoRedo() 
        {
            return UndoRedo.NONE;
        }

        @Override
        public void setMultiViewCallback(MultiViewElementCallback callback) 
        {
            this.callback = callback;
        }

        @Override
        public CloseOperationState canCloseElement() 
        {
            return CloseOperationState.STATE_OK;
        } 
        
        @Override
        public JComponent getVisualRepresentation() 
        {
            return this;
        }

        @Override
        public JComponent getToolbarRepresentation() 
        {
            if(toolbar == null)
            {
                toolbar = new JToolBar();
            }
            return toolbar;
        }

        @Override
        public Action[] getActions() 
        {
            return new Action[0];
        }

        @Override
        public Lookup getLookup() 
        {
            return Lookup.EMPTY;
        }        

        @Override
        public void componentOpened() 
        {
            if(browser == null)
            {
                CefClientProvider provider = Lookup.getDefault().lookup(CefClientProvider.class);
                if(provider != null)
                {
                    try
                    {
                        browser = provider.getCefClient().createBrowser(page.getLinkUrl(), false, false); 
                        add(browser.getUIComponent(), BorderLayout.CENTER);
                    }
                    catch(Exception e)
                    {
                        LOG.warning(e.getMessage());
                    }
                }
            }            
        }

        @Override
        public void componentClosed() 
        {
            if(browser != null)
            {
                browser.close(true);
            }
        }

        @Override
        public void componentShowing() 
        {
            
        }

        @Override
        public void componentHidden() 
        {
            
        }

        @Override
        public void componentActivated() 
        {
            browser.setFocus(true); 
        }

        @Override
        public void componentDeactivated() 
        {
            browser.setFocus(false);  
        }
    }    
}
