/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.github;

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
public abstract class GitHubProvider implements SourceProvider<GitHubUser>
{
    protected static final String ROOT_FOLDER = "github";       

    protected Map<String, GitHubUser> users; 
    protected FileObject rootDir; 

    protected final GitHubFactory factory;

    public GitHubProvider(GitHubFactory factory) 
    {
        this.factory = factory;
    } 
    
    @Override
    public boolean isLiteratureNoteProvider()
    {
        return false;
    }      
    
    @Override
    public GitHubFactory getFactory()
    {
        return factory;
    }
    
    protected abstract Map<String, GitHubUser> getUsersById();
    
    public Collection<GitHubUser> getUsers()
    {
        return Collections.unmodifiableCollection(getUsersById().values());
    }

    @Override
    public GitHubUser getSource(String sourceID) 
    {
        return getUsersById().get(sourceID);
    }    

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "GitHub";
    }

    @Override
    public Icon getIcon(boolean bln) 
    {
        IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
        return provider.getIcon(IconsProvider.ICON.GITHUB);
    }

    @Override
    public boolean contains(FileObject file) 
{
        if(file.isData())
        {
            return getUsersById().containsKey(file.getName());                
        }
        return false;        
    }       
}
