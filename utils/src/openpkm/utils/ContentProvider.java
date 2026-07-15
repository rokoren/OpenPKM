/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import openpkm.base.Content;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import openpkm.base.PropertiesProvider;
import openpkm.base.ContentFactory;

/**
 *
 * @author Rok Koren
 */
public abstract class ContentProvider implements SourceProvider<Content>
{
    protected static final String ROOT_FOLDER = "content";       

    protected Map<String, Content> contents; 
    protected FileObject rootDir; 

    protected final ContentFactory factory;

    public ContentProvider(ContentFactory factory) 
    {
        this.factory = factory;
    }
    
    @Override
    public ContentFactory getFactory()
    {
        return factory;
    }
    
    public abstract Map<String, Content> getContentsById();
    
    public Collection<Content> getContents()
    {
        return Collections.unmodifiableCollection(getContentsById().values());
    }

    @Override
    public Source getSource(String sourceID) 
    {
        return getContentsById().get(sourceID);
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
        return "Content";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getContentsById().containsKey(file.getName());
    }      
}
