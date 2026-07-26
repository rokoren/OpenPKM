/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.Icon;
import openpkm.base.IconsProvider;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public abstract class BlogProvider implements SourceProvider<Blog>
{
    protected static final String ROOT_FOLDER = "blog";       

    protected Map<String, Blog> blogs; 
    protected FileObject rootDir; 

    protected final BlogFactory factory;

    public BlogProvider(BlogFactory factory) 
    {
        this.factory = factory;
    } 
    
    @Override
    public BlogFactory getFactory()
    {
        return factory;
    }
    
    protected abstract Map<String, Blog> getBlogsById();
    
    public Collection<Blog> getBlogs()
    {
        return Collections.unmodifiableCollection(getBlogsById().values());
    }

    @Override
    public Blog getSource(String sourceID) 
    {
        return getBlogsById().get(sourceID);
    }     

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Blog";
    }

    @Override
    public Icon getIcon(boolean bln) 
    {
        IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
        return provider.getIcon(IconsProvider.ICON.BLOG);
    }

    @Override
    public boolean contains(FileObject file) 
{
        if(file.isData())
        {
            return getBlogsById().containsKey(file.getName());                
        }
        return false;        
    }     
}
