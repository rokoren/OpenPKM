/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.reference;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public abstract class ReferenceProvider implements SourceProvider<Reference>
{
    protected static final String ROOT_FOLDER = "reference";       

    protected Map<String, Reference> references; 
    protected FileObject rootDir; 

    protected final ReferenceFactory factory;

    public ReferenceProvider(ReferenceFactory factory) 
    {
        this.factory = factory;
    } 
    
    @Override
    public ReferenceFactory getFactory()
    {
        return factory;
    }
    
    public abstract Map<String, Reference> getReferencesById();
    
    public Collection<Reference> getReferences()
    {
        return Collections.unmodifiableCollection(getReferencesById().values());
    }

    @Override
    public Reference getSource(String sourceID) 
    {
        return getReferencesById().get(sourceID);
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
        return new ImageIcon(ImageUtilities.loadImage(Reference.ICON));
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getReferencesById().containsKey(file.getName());
    }      
}
