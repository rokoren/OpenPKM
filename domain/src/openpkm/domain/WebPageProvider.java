/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.domain;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public abstract class WebPageProvider implements SourceProvider<WebPage>
{
    protected static final String ROOT_FOLDER = "web";       

    protected Map<String, WebPage> links; 
    protected FileObject rootDir; 

    protected final WebPageFactory factory;

    public WebPageProvider(WebPageFactory factory) 
    {
        this.factory = factory;
    } 
    
    @Override
    public WebPageFactory getFactory()
    {
        return factory;
    }
    
    public abstract Map<String, WebPage> getLinksById();
    
    public Collection<WebPage> getLinks()
    {
        return Collections.unmodifiableCollection(getLinksById().values());
    }

    @Override
    public Source getSource(String sourceID) 
    {
        return getLinksById().get(sourceID);
    } 
    
    @Override
    public void deleteSource(String sourceID) throws IOException
    {
        FileObject root = getRootFolder();
        if(root != null)
        {
            FileObject file = root.getFileObject(sourceID, PropertiesProvider.EXTENSION);
            if(file != null)
            {  
                file.delete();
            }              
        }  
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
        return getLinksById().containsKey(file.getName());
    }     
}
