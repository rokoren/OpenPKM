/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.domain;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.*;

/**
 *
 * @author rok
 */
public abstract class DomainSourceProvider implements SourceProvider<Domain>
{
    protected static final String ROOT_FOLDER = "domain";       

    protected Map<String, Domain> domains; 
    protected FileObject rootDir; 

    protected final DomainProvider provider;

    public DomainSourceProvider(DomainProvider provider) 
    {
        this.provider = provider;
    } 
    
    public DomainProvider getDomainProvider()
    {
        return provider;
    }
    
    public abstract Map<String, Domain> getDomainsById();
    
    public Collection<Domain> getDomains()
    {
        return Collections.unmodifiableCollection(getDomainsById().values());
    }

    @Override
    public Source getSource(String sourceID) 
    {
        return getDomainsById().get(sourceID);
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
        return "References";
    }

    @Override
    public Icon getIcon(boolean bln) 
    {
        //return new ImageIcon(ImageUtilities.loadImage(Reference.ICON));
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getDomainsById().containsKey(file.getName());
    }       
}
