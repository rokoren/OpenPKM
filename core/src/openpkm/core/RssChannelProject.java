/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import openpkm.base.Article;
import openpkm.base.ArticleProvider;
import openpkm.base.AsciiDocSupport;
import openpkm.base.BatchUpdateSupport;
import openpkm.base.Book;
import openpkm.base.BookProvider;
import openpkm.base.BulletIconProvider;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DataGroupProvider;
import openpkm.base.Document;
import openpkm.base.Domain;
import openpkm.base.DomainsProvider;
import openpkm.base.FileTypeProvider;
import openpkm.base.HtmlFilesProvider;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.Link;
import openpkm.base.Picture;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviders;
import openpkm.base.UpdateCookie;
import openpkm.base.Video;
import openpkm.base.WatchLater;
import openpkm.base.WebPage;
import openpkm.base.WebPageProvider;
import openpkm.reference.Reference;
import openpkm.reference.ReferenceProvider;
import openpkm.reference.ReferenceSourceProvider;
import openpkm.rss.Rss;
import openpkm.rss.RssChannel;
import openpkm.utils.DateTimeUtils;
import openpkm.utils.FileUtils;
import openpkm.utils.LogicalViewProviderImpl;
import openpkm.utils.SavableImpl;
import openpkm.utils.Utils;
import openpkm.utils.WebSourceProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ParentProjectProvider;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.RootProjectProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Rok Koren
 */
public class RssChannelProject implements Domain, RssChannel, PropertiesProvider, Sources, SourceProviders, BatchUpdateSupport
{    
    public static final String PROP_RSS_FILE = "rss.file"; 
    
    public static final String RSS_FILE = "rss.xml"; 
    
    private static final String DATA_FOLDER = "data";    
    
    private static final int POSITION_NOTES       = 100;
    private static final int POSITION_DOCUMENTS   = 200;
    private static final int POSITION_ARTICLES    = 300;
    private static final int POSITION_BOOKS       = 400;
    private static final int POSITION_LINKS       = 500;
    private static final int POSITION_PICTURES    = 600;    
    private static final int POSITION_VIDEOS      = 700;
    private static final int POSITION_WATCH_LATER = 800;

    private static final Logger LOG = Logger.getLogger(RssChannelProject.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(RssChannelProject.class);   
    
    private final Map<String, SourceProvider> sources = new HashMap();  
    private final List<UpdateCookie> cookies = new ArrayList();      
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);   
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    
    private final FileObject projectDir;        
    private final ProjectState state;
    private final Properties props;   
    
    private Lookup lkp;  
    private FileObject dataDir;
    private LocalFileSystem fileSystem;
    private Source lastSource;   
    
    public RssChannelProject(FileObject projectDir, ProjectState state, Properties props) 
    {
        this.projectDir = projectDir; 
        this.state = state;
        this.props = props;

        WebPageProvider webPageProvider = Lookup.getDefault().lookup(WebPageProvider.class);
        if(webPageProvider != null)
        {          
            SourceProvider links = new WebSourceProviderImpl(webPageProvider);
            sources.put(links.getName(), links);            
        }        
        
        ReferenceProvider referenceProvider = Lookup.getDefault().lookup(ReferenceProvider.class);
        if(referenceProvider != null)
        {          
            SourceProvider references = new ReferenceSourceProviderImpl(referenceProvider);
            sources.put(references.getName(), references);            
        }

    }
    
    private synchronized LocalFileSystem getFileSystem() throws IOException, PropertyVetoException
    {
        if(fileSystem == null)
        {
            fileSystem = new LocalFileSystem();
            fileSystem.setRootDirectory(FileUtil.toFile(getDataDirectory()));            
        }
        return fileSystem;
    }
    
    @Override
    public SourceProvider getSourceProvider(String folder)
    {
        return sources.get(folder);
    }
    
    @Override
    public synchronized FileObject getDataDirectory() throws IOException
    {
        if(dataDir == null)
        {
            dataDir = getProjectDirectory().getFileObject(DATA_FOLDER);
            if(dataDir == null)
            {
                dataDir = getProjectDirectory().createFolder(DATA_FOLDER);
                LOG.info("Data dir created: " + dataDir.getPath());                        
            }                 
        }                           
        return dataDir;       
    } 
    
    @Override
    public FileObject getFileWithAttrs(FileObject file, boolean refresh)
    {
        try
        {
            if(refresh) getFileSystem().getRoot().refresh();
            return getFileSystem().getRoot().getFileObject(file.getName(), file.getExt());            
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }
        catch(PropertyVetoException e)
        {
            LOG.warning(e.getMessage());
        }  
        return null;
    }

    private Source getLastSource() 
    {
        return lastSource;
    }

    private void setLastSource(Source source) 
    {
        Source oldSource = lastSource;
        lastSource = source;
        propertyChangeSupport.firePropertyChange(PROP_LAST_SOURCE, oldSource, source);
    }  
    
// TODO Sources    
    
    @Override
    public SourceGroup[] getSourceGroups(String string) 
    {
        if(string.equalsIgnoreCase(Sources.TYPE_GENERIC))
        {
            return sources.values().toArray(new SourceGroup[0]);                
        }
        return new SourceGroup[0];
    } 

    @Override
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }      
    
// TODO Project
    
    @Override
    public FileObject getProjectDirectory()
    {
        return projectDir;
    } 
    
    @Override
    public Lookup getLookup() 
    {
        if (lkp == null) 
        { 
            List list = new ArrayList();

            list.add(this);
            list.add(new Info());
            list.add(new IconProviderImpl());
            list.add(new ProjectOpenedHookImpl());   
            list.add(new RootProjectProviderImpl());
            list.add(new ParentProjectProviderImpl());              

            list.add(new LogicalViewProviderImpl(this));
            list.add(new RssChannelCustomizerProvider(this));  

            list.add(new DomainsProviderImpl()); 
            list.add(new HtmlFilesProviderImpl());                                  

            list.addAll(sources.values());

            list.add(new BookDataGroupProviderImpl()); 
            list.add(new ArticleDataGroupProviderImpl()); 
            list.add(new DocumentDataGroupProviderImpl()); 
            list.add(new LinkDataGroupProviderImpl());                
            list.add(new PictureDataGroupProviderImpl()); 
            list.add(new VideoDataGroupProviderImpl());             
            list.add(new WatchLaterDataGroupProviderImpl()); 
            
            lkp = Lookups.fixed(list.toArray(new Object[list.size()]));              
        }
        return lkp;
    }      

// TODO Domain  
    
    @Override
    public String getDomainID() 
    {
        return getRssUrl();
    }    
    
    @Override
    public String getAppID() 
    {
        return props.getProperty(PROP_APP_ID);
    }   
    
    @Override
    public LocalDateTime getTimeCreated() 
    {
        String string = props.getProperty(PROP_TIME_CREATED);
        if(string != null)
        {
            return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
    }
    
// TODO RssChannel    
    
    @Override
    public String getRssUrl() 
    {
        return props.getProperty(PROP_RSS_URL);
    }  
    
    public boolean isRssFile()
    {
        String string = props.getProperty(PROP_RSS_FILE);
        if(string != null)
        {
            return Boolean.parseBoolean(string);
        }
        return false;
    }
    
    public void setRssFile(boolean file)
    {
        Object oldValue = props.setProperty(PROP_RSS_FILE, Boolean.toString(file));
        if(oldValue != null)
        {
            oldValue = Boolean.parseBoolean(oldValue.toString());
        }
        propertyChangeSupport.firePropertyChange(PROP_LINK, oldValue, file);
    }

    @Override
    public String getLink() 
    {
        return props.getProperty(PROP_LINK);
    } 
    
    @Override
    public void setLink(String link) 
    {
        if(link == null)
        {
            Object oldValue = props.remove(PROP_LINK);
            propertyChangeSupport.firePropertyChange(PROP_LINK, oldValue, link);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_LINK, link);  
            propertyChangeSupport.firePropertyChange(PROP_LINK, oldValue, link);
        } 
    }     

    @Override
    public String getImage() 
    {
        return props.getProperty(PROP_IMAGE);
    } 
    
    @Override
    public void setImage(String image) 
    {
        if(image == null)
        {
            Object oldValue = props.remove(PROP_IMAGE);
            propertyChangeSupport.firePropertyChange(PROP_IMAGE, oldValue, image);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_IMAGE, image);  
            propertyChangeSupport.firePropertyChange(PROP_IMAGE, oldValue, image);
        } 
    } 

    @Override
    public String getIcon() 
    {
        return props.getProperty(PROP_ICON);
    } 
    
    @Override
    public void setIcon(String icon) 
    {
        if(icon == null)
        {
            Object oldValue = props.remove(PROP_ICON);
            propertyChangeSupport.firePropertyChange(PROP_ICON, oldValue, icon);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_ICON, icon);  
            propertyChangeSupport.firePropertyChange(PROP_ICON, oldValue, icon);
        } 
    } 
    
    @Override
    public String getUri() 
    {
        return props.getProperty(PROP_URI);
    } 
    
    @Override
    public void setUri(String uri) 
    {
        if(uri == null)
        {
            Object oldValue = props.remove(PROP_URI);
            propertyChangeSupport.firePropertyChange(PROP_URI, oldValue, uri);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_URI, uri);  
            propertyChangeSupport.firePropertyChange(PROP_URI, oldValue, uri);
        } 
    }     
    
    @Override
    public String getAuthor() 
    {
        return props.getProperty(PROP_AUTHOR);
    }  
    
    @Override
    public void setAuthor(String author) 
    {
        if(author == null)
        {
            Object oldValue = props.remove(PROP_AUTHOR);
            propertyChangeSupport.firePropertyChange(PROP_AUTHOR, oldValue, author);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_AUTHOR, author);  
            propertyChangeSupport.firePropertyChange(PROP_AUTHOR, oldValue, author);
        } 
    }   
    
    @Override
    public String getCopyright() 
    {
        return props.getProperty(PROP_COPYRIGHT);
    }  
    
    @Override
    public void setCopyright(String copyright) 
    {
        if(copyright == null)
        {
            Object oldValue = props.remove(PROP_COPYRIGHT);
            propertyChangeSupport.firePropertyChange(PROP_COPYRIGHT, oldValue, copyright);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_COPYRIGHT, copyright);  
            propertyChangeSupport.firePropertyChange(PROP_COPYRIGHT, oldValue, copyright);
        } 
    }      
    
    @Override
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
    public void setPublishedDate(LocalDateTime time)
    {
        if(time == null)
        {
            Object oldValue = props.remove(PROP_PUBLISHED_DATE);   
            propertyChangeSupport.firePropertyChange(PROP_PUBLISHED_DATE, oldValue, time);
        }
        else
        {
            Object oldValue = props.setProperty(PROP_PUBLISHED_DATE, time.format(DateTimeFormatter.ISO_DATE_TIME));  
            if(oldValue != null)
            {
                oldValue = LocalDateTime.parse(oldValue.toString(), DateTimeFormatter.ISO_DATE_TIME);
            }
            propertyChangeSupport.firePropertyChange(PROP_PUBLISHED_DATE, oldValue, time);            
        }
    }  
    
    @Override
    public String getGenerator() 
    {
        return props.getProperty(PROP_GENERATOR);
    }  
    
    @Override
    public void setGenerator(String generator) 
    {
        if(generator == null)
        {
            Object oldValue = props.remove(PROP_GENERATOR);
            propertyChangeSupport.firePropertyChange(PROP_GENERATOR, oldValue, generator);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_GENERATOR, generator);  
            propertyChangeSupport.firePropertyChange(PROP_GENERATOR, oldValue, generator);
        } 
    }  
    
    @Override
    public String getLanguage() 
    {
        return props.getProperty(PROP_LANGUAGE);
    }  
    
    @Override
    public void setLanguage(String language) 
    {
        if(language == null)
        {
            Object oldValue = props.remove(PROP_LANGUAGE);
            propertyChangeSupport.firePropertyChange(PROP_LANGUAGE, oldValue, language);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_LANGUAGE, language);  
            propertyChangeSupport.firePropertyChange(PROP_LANGUAGE, oldValue, language);
        } 
    }  

    @Override
    public String getManagingEditor() 
    {
        return props.getProperty(PROP_MANAGING_EDITOR);
    }  
    
    @Override
    public void setManagingEditor(String managingEditor) 
    {
        if(managingEditor == null)
        {
            Object oldValue = props.remove(PROP_MANAGING_EDITOR);
            propertyChangeSupport.firePropertyChange(PROP_MANAGING_EDITOR, oldValue, managingEditor);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_MANAGING_EDITOR, managingEditor);  
            propertyChangeSupport.firePropertyChange(PROP_MANAGING_EDITOR, oldValue, managingEditor);
        } 
    }   
    
    @Override
    public String getCategory() 
    {
        return props.getProperty(PROP_CATEGORY);
    }  
    
    @Override
    public void setCategory(String category) 
    {
        if(category == null)
        {
            Object oldValue = props.remove(PROP_CATEGORY);
            propertyChangeSupport.firePropertyChange(PROP_CATEGORY, oldValue, category);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_CATEGORY, category);  
            propertyChangeSupport.firePropertyChange(PROP_CATEGORY, oldValue, category);
        } 
    }     
    
// TODO TitleProvider  
    
    @Override
    public String getTitle() 
    {
        return props.getProperty(PROP_TITLE);
    }

    @Override
    public void setTitle(String title) 
    {
        if(title == null)
        {
            Object oldValue = props.remove(PROP_TITLE);
            propertyChangeSupport.firePropertyChange(PROP_TITLE, oldValue, title);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_TITLE, title);  
            propertyChangeSupport.firePropertyChange(PROP_TITLE, oldValue, title);
        } 
    } 
    
// TODO DescriptionProvider  
    
    @Override
    public String getDescription() 
    {
        return props.getProperty(PROP_DESCRIPTION);
    }  

    @Override
    public void setDescription(String desc) 
    {
        if(desc == null)
        {
            Object oldValue = props.remove(PROP_DESCRIPTION);
            propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldValue, desc);
        }
        else        
        {
            Object oldValue = props.setProperty(PROP_DESCRIPTION, desc);  
            propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldValue, desc);
        }   
    }      

// TODO PropertiesProvider
    
    @Override
    public Properties getProperties()
    {
        return props;
    }     
    
    @Override
    public void merge(PropertiesProvider provider)
    {
        props.putAll(provider.getProperties());
    }  
    
    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if(propertyName == null)
        {
            propertyChangeSupport.addPropertyChangeListener(listener);    
        }
        else
        {
            propertyChangeSupport.addPropertyChangeListener(propertyName, listener);            
        }
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if(propertyName == null)
        {
            propertyChangeSupport.removePropertyChangeListener(listener);    
        }
        else
        {
            propertyChangeSupport.removePropertyChangeListener(propertyName, listener);            
        }                        
    }     
    
// TODO BatchUpdateSupport    
    
    @Override
    public void registerUpdateCookie(UpdateCookie cookie) 
    {
        cookies.add(cookie);
    }

    @Override
    public boolean batchUpdate() 
    {
        if(cookies.isEmpty())
        {
            return false;
        }
        for(UpdateCookie cookie : cookies)
        {
            cookie.update();
        }
        cookies.clear();
        return true;
    }        
    
// TODO ProjectOpenedHook    
    
    private final class ProjectOpenedHookImpl extends ProjectOpenedHook implements PropertyChangeListener, Runnable
    {
        private RequestProcessor.Task task;          
        
        @Override
        protected void projectOpened() 
        { 
            task = RP.create(this);    
            propertyChangeSupport.addPropertyChangeListener(this);
            WebSourceProviderImpl provider = getLookup().lookup(WebSourceProviderImpl.class);
            propertyChangeSupport.addPropertyChangeListener(PROP_PUBLISHED_DATE, provider);            
            task.schedule(1000);             
        }

        @Override
        protected void projectClosed() 
        { 
            task.cancel();
            WebSourceProviderImpl provider = getLookup().lookup(WebSourceProviderImpl.class);
            propertyChangeSupport.removePropertyChangeListener(PROP_PUBLISHED_DATE, provider);            
            propertyChangeSupport.removePropertyChangeListener(this);            
        } 
        
        private static void rssFile(SyndFeed feed, FileObject file) throws IOException, FeedException
        {
            Writer writer = new OutputStreamWriter(file.getOutputStream());
            SyndFeedOutput output = new SyndFeedOutput();  
            output.output(feed, writer);
            writer.close();              
        }
        
        @Override
        public void run()
        {  
            try
            {
                URL url = new URL(getRssUrl());
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(url));
                setLink(feed.getLink());
                setUri(feed.getUri());
                setAuthor(feed.getAuthor());
                setCopyright(feed.getCopyright());
                setGenerator(feed.getGenerator());
                setLanguage(feed.getLanguage());
                setManagingEditor(feed.getManagingEditor()); 
                setImage(feed.getImage().getUrl());
                if(feed.getIcon() != null)
                {
                    setIcon(feed.getIcon().getUrl());                    
                }
                
                if(feed.getCategories() != null)
                {
                    StringJoiner joiner = new StringJoiner(",");
                    for(SyndCategory category : feed.getCategories())
                    {
                        joiner.add(category.getLabel());
                    }
                    setCategory(joiner.toString());                    
                }
                
                if(isRssFile())
                {
                    FileObject file = getProjectDirectory().getFileObject(RssChannelProjectFactory.PROJECT_FOLDER).getFileObject(RSS_FILE);
                    if(file == null)
                    {
                        file = getProjectDirectory().getFileObject(RssChannelProjectFactory.PROJECT_FOLDER).createData(RSS_FILE);
                        rssFile(feed, file);
                    }
                    else if(DateTimeUtils.convertToLocalDateTime(feed.getPublishedDate()).isAfter(getPublishedDate()))
                    {
                        rssFile(feed, file);
                    }                   
                }                               
                
                LocalDateTime publishedDate = DateTimeUtils.convertToLocalDateTime(feed.getPublishedDate());
                setPublishedDate(publishedDate);                                                      
            }
            catch (MalformedURLException e)
            {
                LOG.warning(e.getMessage());
            }
            catch (IOException e)
            {
                LOG.warning(e.getMessage());
            }    
            catch (FeedException e)
            {
                LOG.warning(e.getMessage());
            } 
            finally
            {
                task.schedule(100000);
            }           
        }         

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            state.markModified(); 
            if(evt.getPropertyName().equals(PROP_TITLE))
            {
                propertyChangeSupport.firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME, evt.getOldValue(), evt.getNewValue());
            }
        }                   
    }
    
// TODO ProjectInformation 
    
    private final class Info implements ProjectInformation
    {                     
        @Override
        public Icon getIcon()
        {  
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getIcon(IconsProvider.ICON.RSS_CHANNEL);
        }

        @Override
        public String getName() 
        {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() 
        {                       
            return getTitle();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.addPropertyChangeListener(ProjectInformation.PROP_DISPLAY_NAME, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(ProjectInformation.PROP_DISPLAY_NAME, listener);
        }                

        @Override
        public Project getProject() 
        {
            return RssChannelProject.this;
        }
    }     
  
// TODO IconProvider    
    
    private final class IconProviderImpl implements IconProvider, ChangeSupportProvider, Runnable
    {        
        private Image icon; 
        private boolean isLoading;
        
        private final ChangeSupport changeSupport = new ChangeSupport(this); 

        @Override
        public synchronized Image getIcon(int type)
        {
            if(icon != null)
            {
                return icon;
            }
            if(!isLoading)
            {
                isLoading = true;                
                RP.post(this);                
            }
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.RSS_CHANNEL);
        }

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }        
        
        @Override
        public void run() 
        {
            String string = getImage();
            if(string != null)
            {
                try
                {
                    URL url = new URL(string);
                    BufferedImage image = ImageIO.read(url);  
                    icon = Utils.resizeImage(image, 16, 16); 
                    changeSupport.fireChange();
                }
                catch(MalformedURLException e)
                {
                    LOG.warning(e.getMessage());
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                } 
                finally
                {
                    isLoading = false;
                }                
            }
        }                
    }    

// TODO RootProjectProvider     

    private final class RootProjectProviderImpl implements RootProjectProvider
    {
        @Override
        public Project getRootProject() 
        {
            return Utils.getRootProject(RssChannelProject.this);
        }         
    }
    
// TODO ParentProjectProvider     

    private final class ParentProjectProviderImpl implements ParentProjectProvider
    {
        private Project getProject(FileObject dir)
        {
            FileObject parent = dir.getParent();
            if(parent != null && parent.isFolder())
            {
                if(ProjectManager.getDefault().isProject(parent))
                {
                    try
                    {
                        Project project = ProjectManager.getDefault().findProject(parent);
                        DomainsProvider provider = project.getLookup().lookup(DomainsProvider.class);
                        if(provider != null)
                        {
                            return project;                        
                        }
                        else
                        {
                            return getProject(parent);
                        }
                    }
                    catch(IOException e)
                    {
                        LOG.warning(e.getMessage());
                    }
                }
                else
                {
                    return getProject(parent);
                }
            }
            return null;         
        }

        @Override
        public Project getPartentProject() 
        {
            return getProject(getProjectDirectory());
        }          
    }  
    
// TODO DomainsProvider

    private final class DomainsProviderImpl implements DomainsProvider
    {                        
        private static final String ROOT_FOLDER = "domain";          
        
        private Map<String, Domain> domains; 
        private FileObject rootDir;            
        
        private final ChangeSupport changeSupport;              

        public DomainsProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
        } 
        
        @Override
        public synchronized FileObject getRootDirectory() throws IOException
        {
            if(rootDir == null)
            {
                rootDir = getProjectDirectory().getFileObject(ROOT_FOLDER);
                if(rootDir == null)
                {
                    rootDir = getProjectDirectory().createFolder(ROOT_FOLDER);
                    LOG.info("Domain dir created: " + rootDir.getPath());                        
                }                 
            }                           
            return rootDir;       
        }         
        
        private synchronized Map<String, Domain> getDomainsById()
        {
            if(domains == null)
            {
                domains = new HashMap<>();
                try
                {
                    for (FileObject fo : getRootDirectory().getChildren()) 
                    {
                        if(fo.isFolder())
                        {
                            Project project = ProjectManager.getDefault().findProject(fo);
                            if(project instanceof Domain domain)
                            {
                                domains.put(domain.getDomainID(), domain);
                            }                                                                                    
                        }
                        else
                        {
                            DataObject data = DataObject.find(fo);
                            Domain domain = data.getLookup().lookup(Domain.class);
                            if(domain != null)
                            {
                                domains.put(domain.getDomainID(), domain);
                            }                            
                        }                                                                                                                                            
                    }                      
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                              
            }
            return domains;
        }  

        @Override
        public Collection<Domain> getDomains()
        {
            return Collections.unmodifiableCollection(getDomainsById().values());
        }
        
        @Override
        public void addDomain(Domain domain)
        {
            getDomainsById().put(domain.getDomainID(), domain);
            changeSupport.fireChange();            
        }
        
        @Override
        public void removeDomain(String domainID)
        {
            Domain domain = getDomainsById().remove(domainID);
            if(domain != null)
            {
                changeSupport.fireChange();                            
            }
        }        
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Domain"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RssChannelProject.this;
        }                        

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }   

        @Override
        public String getName() 
        {
            return "domain";
        }

        @Override
        public String getDisplayName() 
        {
            return "Domains";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.DOMAINS);
        }               
    }     
    
// TODO DataGroup     

    private final class WatchLaterDataGroupProviderImpl implements DataGroupProvider, BulletIconProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public WatchLaterDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {        
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/WatchLater"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RssChannelProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_WATCH_LATER;
        }                  

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }   

        @Override
        public FileObject getRootFolder() throws IOException
        {
            return getDataDirectory();
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "watch_later";
        }

        @Override
        public String getDisplayName() 
        {
            return "Watch Later";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.WATCH_LATER);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                WatchLater watchLater = data.getLookup().lookup(WatchLater.class);
                if(watchLater != null)
                {
                    return watchLater.isWatchLater();
                } 
            }                                  
            return false;
        }
        
        @Override
        public Image getBullet()
        {
            try
            {
                for(FileObject file : getRootFolder().getChildren())
                {
                    DataObject data = DataObject.find(file);
                    if(contains(data))
                    {
                        IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);            
                        return provider.getImage(IconsProvider.ICON.BULLET_BLUE);
                    }
                }
            }
            catch(IOException e)                
            {
                LOG.warning(e.getMessage());
            }
            return null;
        }        

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof WatchLater)
            {
                changeSupport.fireChange();
            }
        }
    } 
    
    private final class BookDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public BookDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Book"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RssChannelProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_BOOKS;
        }                  

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }   

        @Override
        public FileObject getRootFolder() throws IOException
        {
            return getDataDirectory();
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "book";
        }

        @Override
        public String getDisplayName() 
        {
            return "Books";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.BOOKS);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Book book = data.getLookup().lookup(Book.class);
                if(book != null)
                {
                    return true;
                } 
            }                                  
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Book)
            {
                changeSupport.fireChange();
            }
        }
    }   
    
    private final class ArticleDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public ArticleDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Article"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RssChannelProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_ARTICLES;
        }                  

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }   

        @Override
        public FileObject getRootFolder() throws IOException
        {
            return getDataDirectory();
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "article";
        }

        @Override
        public String getDisplayName() 
        {
            return "Articles";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.ARTICLES);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Article article = data.getLookup().lookup(Article.class);
                if(article != null)
                {
                    return true;
                }                 
            }                                    
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Article)
            {
                changeSupport.fireChange();
            }
        }
    } 
    
    private final class DocumentDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public DocumentDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Document"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RssChannelProject.this;
        }         
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_DOCUMENTS;
        }                 

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }   

        @Override
        public FileObject getRootFolder() throws IOException 
        {
            return getDataDirectory();
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "document";
        }

        @Override
        public String getDisplayName() 
        {
            return "Documents";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.DOCUMENTS);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Document document = data.getLookup().lookup(Document.class);
                if(document != null)
                {
                    return true;
                }                 
            }                                    
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Document)
            {
                changeSupport.fireChange();
            }
        }
    }  

    private final class LinkDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 

        public LinkDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Link"));         
            return actions;
        }        
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RssChannelProject.this;
        }        
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_LINKS;
        }                  

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }   

        @Override
        public FileObject getRootFolder() throws IOException
        {
            return getDataDirectory();
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "link";
        }

        @Override
        public String getDisplayName() 
        {
            return "Links";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.LINKS);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Link link = data.getLookup().lookup(Link.class);
                if(link != null)
                {
                    return true;
                }                 
            }                                    
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Link)
            {
                changeSupport.fireChange();
            }
        }
    }  

    private final class PictureDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 
                
        public PictureDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 

        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Picture"));         
            return actions;
        }
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RssChannelProject.this;
        }        
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_PICTURES;
        }                  

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }   

        @Override
        public FileObject getRootFolder() throws IOException 
        {
            return getDataDirectory();
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "picture";
        }

        @Override
        public String getDisplayName() 
        {
            return "Pictures";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.PICTURES);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Picture picture = data.getLookup().lookup(Picture.class);
                if(picture != null)
                {
                    return true;
                }                 
            }                                   
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Picture)
            {
                changeSupport.fireChange();
            }
        }
    } 
    
    private final class VideoDataGroupProviderImpl implements DataGroupProvider, PropertyChangeListener
    {        
        private final ChangeSupport changeSupport; 
                
        public VideoDataGroupProviderImpl()
        {
            changeSupport = new ChangeSupport(this); 
            propertyChangeSupport.addPropertyChangeListener(PROP_LAST_SOURCE, this);
        } 

        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/OpenPKM/Video"));         
            return actions;
        }
        
        @Override
        public Lookup.Provider getProvider()
        {
            return RssChannelProject.this;
        }        
        
        @Override
        public Integer getPosition() 
        {
            return POSITION_VIDEOS;
        }                  

        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }   

        @Override
        public FileObject getRootFolder() throws IOException 
        {
            return getDataDirectory();
        }
        
        @Override
        public Comparator<DataObject> getComparator() 
        {
            return DataGroupProvider.titleComparator();
        } 
        
        @Override
        public boolean isReversed()
        {
            return false;
        }          

        @Override
        public String getName() 
        {
            return "video";
        }

        @Override
        public String getDisplayName() 
        {
            return "Videos";
        }

        @Override
        public Image getIcon(boolean hasChildren) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(IconsProvider.ICON.VIDEOS);
        }

        @Override
        public boolean contains(DataObject data) 
        {
            if(data != null)
            {
                Video video = data.getLookup().lookup(Video.class);
                if(video != null)
                {
                    return true;
                }                 
            }                                   
            return false;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(getLastSource() instanceof Video)
            {
                changeSupport.fireChange();
            }
        }
    }    
    
// TODO SourceGroup
   
    private final class WebSourceProviderImpl extends WebSourceProvider implements FileChangeListener, PropertyChangeListener, Runnable
    {  
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/www_page.png";         
        
        public WebSourceProviderImpl(WebPageProvider provider) 
        {
            super(provider);
        }               
        
        @Override
        public Lookup.Provider getLookupProvider()
        {
            return RssChannelProject.this;
        } 
        
        @Override
        public Icon getIcon(boolean bln) 
        {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }        
        
        @Override
        public synchronized Map<String, WebPage> getLinks()
        {
            if(links == null)
            {
                links = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            WebPage webPage = provider.getWebPage(Utils.getProperties(file)); 
                            webPage.addPropertyChangeListener(Source.PROP_MODIFIED, this);
                            links.put(webPage.getSourceID(), webPage);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return links;
        }                

        @Override
        public FileObject getRootFolder() 
        {
            if(rootDir == null)
            {
                try
                {                
                    rootDir = getProjectDirectory().getFileObject(ROOT_FOLDER);
                    if(rootDir == null)
                    {
                        rootDir = getProjectDirectory().createFolder(ROOT_FOLDER);
                        LOG.info("Content root folder created: " + rootDir.getPath());                        
                    } 
                    rootDir.addFileChangeListener(this);                                        
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                
            }
            return rootDir;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }
        
        @Override
        public FileObject createData(WebPage webPage, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, webPage.getSourceID());  

            if(webPage instanceof Article)
            {
                Article article = (Article)webPage;
                if(fileTypeProvider instanceof ArticleProvider)
                {
                    ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(articleProvider.getArticle(article.getTitle(), article.getPublisher()).getBytes());
                    output.close();
                }                         
            }  
            
            return primaryFile;            
        } 
        
        private XmlReader getReader() throws IOException
        {
            if(isRssFile())
            {
                FileObject rssFile = getProjectDirectory().getFileObject(RssChannelProjectFactory.PROJECT_FOLDER).getFileObject(RSS_FILE);  
                if(rssFile != null)
                {
                    return new XmlReader(FileUtil.toFile(rssFile));                    
                }
            }
            URL url = new URL(getRssUrl());  
            return new XmlReader(url);           
        }

        @Override
        public void run()
        {
            AsciiDocSupport fileTypeProvider = Lookup.getDefault().lookup(AsciiDocSupport.class);
            if(fileTypeProvider != null)            
            {
                try
                {                    
                    SyndFeedInput input = new SyndFeedInput();
                    SyndFeed feed = input.build(getReader());
                    Iterator itr = feed.getEntries().iterator();
                    while (itr.hasNext()) 
                    {
                        SyndEntry syndEntry = (SyndEntry) itr.next();
                        LocalDateTime entryPublishedDate = DateTimeUtils.convertToLocalDateTime(syndEntry.getPublishedDate());
                        if (!getLinks().containsKey(entryPublishedDate.getNano() + ""))
                        {
                            LocalDateTime now = LocalDateTime.now();
                            Properties props = new Properties();
                            props.setProperty(WebPageProvider.PROP_TYPE, WebPageProvider.Type.RSS.getName());                            
                            props.setProperty(WebPage.PROP_TIME_CREATED, now.format(DateTimeFormatter.ISO_DATE_TIME));
                            props.setProperty(Rss.PROP_URI, syndEntry.getUri());                            
                            props.setProperty(Rss.PROP_LINK, syndEntry.getLink());                                     
                            props.setProperty(Rss.PROP_TITLE, syndEntry.getTitle());     
                            props.setProperty(Rss.PROP_DESCRIPTION, syndEntry.getDescription().getValue());                                                      
                            props.setProperty(Rss.PROP_PUBLISHED_DATE, DateTimeUtils.convertToLocalDateTime(syndEntry.getPublishedDate()).format(DateTimeFormatter.ISO_DATE_TIME)); 

                            MessageDigest digest = MessageDigest.getInstance("SHA-256");
                            byte[] hash = digest.digest(syndEntry.getUri().getBytes(StandardCharsets.UTF_8));                                  
                            StringBuilder hex = new StringBuilder();
                            for (byte b : hash) {
                                hex.append(String.format("%02x", b));
                            }
                            String rssID = hex.toString();
                            props.setProperty(Rss.PROP_RSS_ID, rssID);  
                            props.setProperty(Rss.PROP_WATCH_LATER, Boolean.TRUE.toString()); 
                            
                            
                            WebPage webPage = provider.getWebPage(props);
                            FileObject file = createData(webPage, fileTypeProvider);
                            
                            FileObject folder = getRootFolder();
                            if(folder != null)
                            {  
                                OutputStream os = folder.createAndOpen(webPage.getSourceID() + "." + PropertiesProvider.EXTENSION);  
                                webPage.save(os, "New Content Created");
                                os.close();  
                            }                             
                            
                            if(file != null)
                            {                               
                                String text = getTitle() + ": " + syndEntry.getTitle();

                                BufferedImage image = null;
                                if(!syndEntry.getEnclosures().isEmpty())
                                {
                                    SyndEnclosure syndEnclosure = syndEntry.getEnclosures().get(0);                                  
                                    if(syndEnclosure.getType().startsWith("image"))
                                    {
                                        URL url2 = new URL(syndEnclosure.getUrl());
                                        //Image image = Utils.resizeImage(ImageIO.read(url2), 320, 180); 
                                        image = ImageIO.read(url2);                                         
                                    }
                                }

                                IconProvider provider = getLookup().lookup(IconProvider.class);
                                Icon icon = ImageUtilities.image2Icon(provider.getIcon(BeanInfo.ICON_COLOR_16x16)); 
                                
                                JLabel baloonDetails = new JLabel();
                                if(image == null)
                                {
                                    baloonDetails.addMouseListener(FileUtils.clicked2open(file));
                                    baloonDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                    JComponent details = createDetails(syndEntry.getDescription().getValue(), FileUtils.action2open(file), null);                                                                
                                    NotificationDisplayer.getDefault().notify(text, icon, baloonDetails, details, NotificationDisplayer.Priority.NORMAL, "Web-Category-Name");                                 
                                }
                                else
                                {
                                    baloonDetails.setIcon(ImageUtilities.image2Icon(Utils.resizeImage(image, text, baloonDetails.getFont().deriveFont(Font.BOLD), icon.getIconWidth())));
                                    baloonDetails.addMouseListener(FileUtils.clicked2open(file));
                                    baloonDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                    JComponent details = createDetails(syndEntry.getDescription().getValue(), FileUtils.action2open(file), ImageUtilities.image2Icon(Utils.resizeImage(image, 320)));                                                                
                                    NotificationDisplayer.getDefault().notify(text, icon, baloonDetails, details, NotificationDisplayer.Priority.NORMAL, "Web-Category-Name");                                  
                                }
                            }                                
                        }
                    }                                                      
                }
                catch (MalformedURLException e)
                {
                    LOG.warning(e.getMessage());
                }
                catch (IOException e)
                {
                    LOG.warning(e.getMessage());
                }    
                catch (FeedException e)
                {
                    LOG.warning(e.getMessage());
                }  
                catch (NoSuchAlgorithmException e)
                {
                    LOG.warning(e.getMessage());
                }                 
            }                                  
        }          

        private static JComponent createDetails(String text, ActionListener action, Icon icon) 
        {
            if (null == action) {
                return new JLabel(text);
            }
            JButton btn = new JButton(Utils.convertStringToHtml(text, 50));
            btn.setFocusable(false);
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFont(btn.getFont().deriveFont(btn.getFont().getSize() + 2));
            btn.addActionListener(action);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            Color c = UIManager.getColor("nb.html.link.foreground"); //NOI18N
            if (c != null) {
                btn.setForeground(c);
            }
            if(icon != null)
            {
                btn.setIcon(icon);
                btn.setIconTextGap(10);            
            }
            btn.setVerticalTextPosition(SwingConstants.TOP);
            btn.setHorizontalTextPosition(SwingConstants.LEFT);
            btn.setVerticalAlignment(SwingConstants.CENTER);
            btn.setHorizontalAlignment(SwingConstants.LEFT);        
            return btn;
        }          
        
        @Override
        public void fileFolderCreated(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            DataFolder data = DataFolder.findFolder(file);
            setLastData(data);
            */
        }

        @Override
        public void fileDataCreated(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            try
            {
                WebPage webPage = provider.getWebPage(Utils.getProperties(file)); 
                webPage.addPropertyChangeListener(Source.PROP_MODIFIED, this);
                getLinks().put(webPage.getSourceID(), webPage);               
                setLastSource(webPage);                
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }               
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            WebPage webPage = getLinks().get(file.getName());  
            if(webPage != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            WebPage webPage = getLinks().remove(file.getName());  
            if(webPage != null)
            {
                webPage.removePropertyChangeListener(Source.PROP_MODIFIED, this);
                setLastSource(webPage);
            }
        }

        @Override
        public void fileRenamed(FileRenameEvent fre) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fae) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }          

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            if(evt.getPropertyName().equals(PROP_PUBLISHED_DATE))
            {
                LocalDateTime oldValue = (LocalDateTime)evt.getOldValue();
                LocalDateTime newValue = (LocalDateTime)evt.getNewValue();                                
                if(oldValue == null || newValue.isAfter(oldValue))
                {
                    RP.post(this);  
                }                  
            }
            else
            {
                new SavableImpl(this, evt);                
            }            
        }
    }     
    
    private final class ReferenceSourceProviderImpl extends ReferenceSourceProvider implements FileChangeListener, PropertyChangeListener
    {               
        public ReferenceSourceProviderImpl(ReferenceProvider provider) 
        {
            super(provider);
        }               
        
        @Override
        public Lookup.Provider getLookupProvider()
        {
            return RssChannelProject.this;
        }  
        
        @Override
        public synchronized Map<String, Reference> getReferences()
        {
            if(references == null)
            {
                references = new HashMap<>();
                FileObject folder = getRootFolder();
                if(folder !=  null)
                {
                    for (FileObject file : folder.getChildren()) 
                    {
                        try
                        {
                            Reference reference = provider.getReference(Utils.getProperties(file)); 
                            reference.addPropertyChangeListener(Source.PROP_MODIFIED, this);
                            references.put(reference.getSourceID(), reference);
                        }
                        catch(IOException e)
                        {
                            LOG.warning(e.getMessage());
                        }                                                                                                                                             
                    }                     
                }                
            }
            return references;
        }                

        @Override
        public FileObject getRootFolder() 
        {
            if(rootDir == null)
            {
                try
                {                
                    rootDir = getProjectDirectory().getFileObject(ROOT_FOLDER);
                    if(rootDir == null)
                    {
                        rootDir = getProjectDirectory().createFolder(ROOT_FOLDER);
                        LOG.info("Reference root folder created: " + rootDir.getPath());                        
                    } 
                    rootDir.addFileChangeListener(this);                                        
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                
            }
            return rootDir;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.addPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) 
        {
            propertyChangeSupport.removePropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP, listener);
        }
        
        @Override
        public FileObject createData(Reference reference, FileTypeProvider fileTypeProvider) throws IOException    
        {
            String fileName = FileUtils.getFileName(getDataDirectory(), fileTypeProvider.getExtension());
            FileObject primaryFile = getDataDirectory().createData(fileName, fileTypeProvider.getExtension());
            FileObject file = getFileWithAttrs(primaryFile, true);
            file.setAttribute(ATTR_SOURCE_PROVIDER, getName());
            file.setAttribute(ATTR_SOURCE_ID, reference.getSourceID());  

            if(reference instanceof Article)
            {
                Article article = (Article)reference;
                if(fileTypeProvider instanceof ArticleProvider)
                {
                    ArticleProvider articleProvider = (ArticleProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(articleProvider.getArticle(article.getTitle(), article.getPublisher()).getBytes());
                    output.close();
                }                         
            }
            else if(reference instanceof Book)
            {
                Book book = (Book)reference;
                if(fileTypeProvider instanceof BookProvider)
                {
                    BookProvider bookProvider = (BookProvider)fileTypeProvider;
                    OutputStream output = primaryFile.getOutputStream();
                    output.write(bookProvider.getBook(book.getTitle(), book.getAuthors()).getBytes());
                    output.close();
                }                         
            }            

            return primaryFile;
        }          
        
        @Override
        public void fileFolderCreated(FileEvent evt) 
        {
            /*
            FileObject file = evt.getFile();
            DataFolder data = DataFolder.findFolder(file);
            setLastData(data);
            */
        }

        @Override
        public void fileDataCreated(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            try
            {
                Reference reference = provider.getReference(Utils.getProperties(file)); 
                reference.addPropertyChangeListener(Source.PROP_MODIFIED, this);
                getReferences().put(reference.getSourceID(), reference);               
                setLastSource(reference);                
            }           
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }               
        }

        @Override
        public void fileChanged(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Reference reference = getReferences().get(file.getName());  
            if(reference != null)
            {
                
            }
        }

        @Override
        public void fileDeleted(FileEvent evt) 
        {
            FileObject file = evt.getFile();
            Reference reference = getReferences().remove(file.getName());  
            if(reference != null)
            {
                reference.removePropertyChangeListener(Source.PROP_MODIFIED, this);
                setLastSource(reference);
            }
        }

        @Override
        public void fileRenamed(FileRenameEvent fre) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fae) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }          

        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            new SavableImpl(this, evt);
        }
    }           

// TODO HtmlFilesProvider        
    
    private final class HtmlFilesProviderImpl implements HtmlFilesProvider, FileChangeListener
    {
        private static final String DATA_FOLDER = "html";       
        
        private FileObject dataDir;   
        private Map<String, FileObject> dataFiles;        
        
        private final ChangeSupport changeSupport = new ChangeSupport(this);             

        @Override
        public String getLastDataID() 
        {
            return props.getProperty(PROP_LAST_DATA_ID);
        }

        @Override
        public void setLastDataID(String dataID) 
        {
            if(dataID == null)
            {
                Object oldValue = props.remove(PROP_LAST_DATA_ID);
                propertyChangeSupport.firePropertyChange(PROP_LAST_DATA_ID, oldValue, dataID);
            }
            else        
            {
                Object oldValue = props.setProperty(PROP_LAST_DATA_ID, dataID);  
                propertyChangeSupport.firePropertyChange(PROP_LAST_DATA_ID, oldValue, dataID);
            }   
        }                   
        
        @Override
        public synchronized FileObject getDataDirectory() throws IOException 
        {
            if(dataDir == null)
            {
                dataDir = getProjectDirectory().getFileObject(DATA_FOLDER);
                if(dataDir == null)
                {
                    dataDir = getProjectDirectory().createFolder(DATA_FOLDER);
                    LOG.info("Html data dir created: " + dataDir.getPath());
                } 
                dataDir.addFileChangeListener(this);                  
            }
            return dataDir;
        } 
        
        private synchronized Map<String, FileObject> getDataMap()
        {
            if(dataFiles == null)
            {
                dataFiles = new HashMap<>();
                try
                {
                    for (FileObject file : getDataDirectory().getChildren()) 
                    {
                        if(file.getExt().equalsIgnoreCase(FILE_EXT))
                        {
                            dataFiles.put(file.getName(), file);                             
                        }                                                   
                    }                     
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }
            } 
            return dataFiles;
        }  
        
        private synchronized void clear()
        {
            dataFiles = null;           
        }         
        
        @Override
        public Collection<FileObject> getDataFiles()
        {
            return Collections.unmodifiableCollection(getDataMap().values());
        }
        
        @Override
        public FileObject getDataFile(String dataID)
        {
            return getDataMap().get(dataID);
        }
        
        @Override
        public void fileFolderCreated(FileEvent fe) 
        {
            /*
            clear();
            fileEvent = fe;
            cs.fireChange();
            */
        }

        @Override
        public void fileDataCreated(FileEvent fe) 
        {
            FileObject file = fe.getFile();
            if(file.getExt().equalsIgnoreCase(FILE_EXT))
            {
                getDataMap().put(file.getName(), file);                             
            }               
            changeSupport.fireChange();
        }

        @Override
        public void fileChanged(FileEvent fe) 
        {
            changeSupport.fireChange();
        }

        @Override
        public void fileDeleted(FileEvent fe) 
        {
            FileObject file = fe.getFile();
            if(file.getExt().equalsIgnoreCase(FILE_EXT))
            {
                getDataMap().remove(file.getName());                             
            }               
            changeSupport.fireChange();
        }

        @Override
        public void fileRenamed(FileRenameEvent fre) 
        {
            clear();
            changeSupport.fireChange();
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fae) 
        {
            changeSupport.fireChange();
        }       
        
        @Override
        public void addChangeListener(ChangeListener listener) 
        {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) 
        {
            changeSupport.removeChangeListener(listener);
        }        
        
        @Override
        public Lookup.Provider getProvider() 
        {
            return RssChannelProject.this;
        }                 
    }    
}
