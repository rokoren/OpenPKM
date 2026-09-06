/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public abstract class WebPageProvider implements SourceProvider<WebPage>
{
    protected static final String ROOT_FOLDER = "web";       

    protected FileObject rootDir; 
    protected Pages pages;    

    protected final WebPageFactory factory;

    public WebPageProvider(WebPageFactory factory) 
    {
        this.factory = factory;
    } 
    
    @Override
    public boolean isLiteratureNoteProvider()
    {
        return false;
    }
    
    @Override
    public WebPageFactory getFactory()
    {
        return factory;
    }
    
    public abstract Pages getPages();    

    @Override
    public WebPage getSource(String sourceID) 
    {
        return getPages().getPagesByFile().get(sourceID);
    }     

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Web";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        if(file.isData())
        {
            return getPages().getPagesByFile().containsKey(file.getName());                
        }
        return false;   
    }  
    
    public static final class Pages
    {
        private final Map<String, WebPage> pagesByUrl = new HashMap<>();         
        private Map<String, WebPage> pagesByFile = new HashMap<>();  
        
        public Collection<WebPage> getPages()
        {
            return Collections.unmodifiableCollection(pagesByUrl.values());
        }   
        
        public Map<String, WebPage> getPagesByUrl()
        {
            return pagesByUrl;
        }
        
        public Map<String, WebPage> getPagesByFile()
        {
            return pagesByFile;
        }        
        
        public void addPage(WebPage page)
        {
            pagesByUrl.put(page.getLinkUrl(), page);
            pagesByFile.put(page.getFileName(), page);
        }
        
        public WebPage removePage(String fileName)
        {
            WebPage page = pagesByFile.remove(fileName);
            if(page != null)
            {
                pagesByUrl.remove(page.getLinkUrl());
            }
            return page;
        }
    }    
}
