/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.reference;

import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public abstract class ReferenceSourceProvider implements SourceProvider<Reference>
{
    protected static final String ROOT_FOLDER = "reference";       

    protected Map<String, Reference> references; 
    protected FileObject rootDir; 

    protected final ReferenceProvider provider;

    public ReferenceSourceProvider(ReferenceProvider provider) 
    {
        this.provider = provider;
    } 
    
    public ReferenceProvider getReferenceProvider()
    {
        return provider;
    }
    
    public abstract Map<String, Reference> getReferences();

    @Override
    public Source getSource(String sourceID) 
    {
        return getReferences().get(sourceID);
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
        return getReferences().containsKey(file.getName());
    }      
}
