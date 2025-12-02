/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbPreferences;

/**
 *
 * @author Rok Koren
 */
public abstract class AbstractFilesProvider 
{
    public static final String PROP_BOOK     = "files.book";   
    public static final String PROP_ARTICLE  = "files.article"; 
    public static final String PROP_DOCUMENT = "files.document"; 
    public static final String PROP_PICTURE  = "files.picture";
    public static final String PROP_VIDEO    = "files.video";

    public static final AbstractFilesProvider BOOKS = new Books();
    public static final AbstractFilesProvider ARTICLES = new Articles();
    public static final AbstractFilesProvider DOCUMENTS = new Documents();
    public static final AbstractFilesProvider PICTURES = new Pictures();
    public static final AbstractFilesProvider VIDEOS = new Videos();
    
    private static final Logger LOG = Logger.getLogger(AbstractFilesProvider.class.getName());          
    
    public abstract FileObject getDirectory() throws IOException;
    
    public FileObject getFile(String filePath) throws IOException 
    {
        return getDirectory().getFileObject(filePath);
    }

    public List<FileObject> getFiles() throws IOException
    {
        List<FileObject> files = new ArrayList<>();
        for (FileObject file : getDirectory().getChildren()) 
        {
            files.add(file);                                                  
        }  
        return files;
    }     

    public String getRelativePath(FileObject file) throws IOException 
    {
        return FileUtil.getRelativePath(getDirectory(), file);
    }  
    
    public static final class Documents extends AbstractFilesProvider
    {
        @Override
        public FileObject getDirectory() throws IOException 
        {
            String pathname = NbPreferences.forModule(AbstractFilesProvider.class).get(PROP_DOCUMENT, null);  
            if(pathname == null)
            {
                throw new IOException("Documents directory not set");
            }            
            File dir = new File(pathname);
            return FileUtil.createFolder(dir);
        }        
    }

    public static final class Articles extends AbstractFilesProvider
    {
        @Override
        public FileObject getDirectory() throws IOException 
        {
            String pathname = NbPreferences.forModule(AbstractFilesProvider.class).get(PROP_ARTICLE, null);  
            if(pathname == null)
            {
                throw new IOException("Articles directory not set");
            }            
            File dir = new File(pathname);
            return FileUtil.createFolder(dir);
        }        
    } 
    
    public static final class Books extends AbstractFilesProvider
    {
        @Override
        public FileObject getDirectory() throws IOException 
        {
            String pathname = NbPreferences.forModule(AbstractFilesProvider.class).get(PROP_BOOK, null);  
            if(pathname == null)
            {
                throw new IOException("Books directory not set");
            }            
            File dir = new File(pathname);
            return FileUtil.createFolder(dir);
        }        
    }    
    
    public static final class Pictures extends AbstractFilesProvider
    {
        @Override
        public FileObject getDirectory() throws IOException 
        {
            String pathname = NbPreferences.forModule(AbstractFilesProvider.class).get(PROP_PICTURE, null);  
            if(pathname == null)
            {
                throw new IOException("Pictures directory not set");
            }            
            File dir = new File(pathname);
            return FileUtil.createFolder(dir);
        }        
    }   
    
    public static final class Videos extends AbstractFilesProvider
    {
        @Override
        public FileObject getDirectory() throws IOException 
        {
            String pathname = NbPreferences.forModule(AbstractFilesProvider.class).get(PROP_VIDEO, null);  
            if(pathname == null)
            {
                throw new IOException("Videos directory not set");
            }            
            File dir = new File(pathname);
            return FileUtil.createFolder(dir);
        }        
    }     
}
