/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
    
    protected FileObject rootDir; 
    protected Blogs blogs;

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
    
    protected abstract Blogs getBlogs();    

    @Override
    public Blog getSource(String sourceID) 
    {
        return getBlogs().getBlogsByFile().get(sourceID);
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
        return getBlogs().getBlogsByFile().containsKey(file.getName());        
    } 

    public static final class Blogs
    {
        private final Map<String, Blog> blogsByUrl = new HashMap<>();         
        private Map<String, Blog> blogsByFile = new HashMap<>();  
        
        public Collection<Blog> getBlogs()
        {
            return Collections.unmodifiableCollection(blogsByUrl.values());
        }   
        
        public Map<String, Blog> getBlogsByUrl()
        {
            return blogsByUrl;
        }
        
        public Map<String, Blog> getBlogsByFile()
        {
            return blogsByFile;
        }        
        
        public void addBlog(Blog blog)
        {
            blogsByUrl.put(blog.getUrl(), blog);
            blogsByFile.put(blog.getFileName(), blog);
        }
        
        public Blog removeBlog(String fileName)
        {
            Blog blog = blogsByFile.remove(fileName);
            if(blog != null)
            {
                blogsByUrl.remove(blog.getUrl());
            }
            return blog;
        }
    } 
}
