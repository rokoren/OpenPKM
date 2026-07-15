/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.facebook;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.Icon;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public abstract class FacebookProvider implements SourceProvider<FacebookPage>
{
    protected static final String ROOT_FOLDER = "facebook";       

    protected Map<String, FacebookPage> pages; 
    protected FileObject rootDir; 

    protected final FacebookFactory factory;

    public FacebookProvider(FacebookFactory factory) 
    {
        this.factory = factory;
    } 
    
    @Override
    public FacebookFactory getFactory()
    {
        return factory;
    }
    
    protected abstract Map<String, FacebookPage> getPagesById();
    
    public Collection<FacebookPage> getPages()
    {
        return Collections.unmodifiableCollection(getPagesById().values());
    }

    @Override
    public Source getSource(String sourceID) 
    {
        return getPagesById().get(sourceID);
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
        return "Facebook";
    }

    @Override
    public Icon getIcon(boolean bln) 
    {
        IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
        return provider.getIcon(IconsProvider.ICON.FACEBOOK);
    }

    @Override
    public boolean contains(FileObject file) 
{
        if(file.isData())
        {
            return getPagesById().containsKey(file.getName());                
        }
        return false;        
    }     
}
