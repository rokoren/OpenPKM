/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.reference;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.reference.Reference;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;

/**
 *
 * @author rokor
 */
public class IconProviderImpl implements IconProvider, PropertyChangeListener
{
    private final ChangeSupport changeSupport;         
    private final AbstractReference reference;

    public IconProviderImpl(AbstractReference reference) 
    {
        this.reference = reference;
        changeSupport = new ChangeSupport(this);
        reference.addPropertyChangeListener(Reference.PROP_FILE_EXT, this);
    }  
    
    @Override
    public Image getIcon(int type) 
    {
        String nameExt = reference.getProperties().getProperty(Reference.PROP_FILE_EXT);
        if(nameExt != null)
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            if(nameExt.equalsIgnoreCase(AbstractReference.EXT_GIF))
            {
                return provider.getImage(IconsProvider.ICON.FILE_GIF);                
            }
            else if(nameExt.equalsIgnoreCase(AbstractReference.EXT_JPG))
            {
                return provider.getImage(IconsProvider.ICON.FILE_JPG); 
            } 
            else if(nameExt.equalsIgnoreCase(AbstractReference.EXT_PNG))
            {
                return provider.getImage(IconsProvider.ICON.FILE_PNG);                 
            }             
            else if(nameExt.equalsIgnoreCase(AbstractReference.EXT_MP4))
            {
                return provider.getImage(IconsProvider.ICON.FILE_MP4);                 
            }  
            else if(nameExt.equalsIgnoreCase(AbstractReference.EXT_PDF))
            {
                return provider.getImage(IconsProvider.ICON.FILE_PDF);                 
            }             
        }    
        return null;
    }    

    @Override
    public void propertyChange(PropertyChangeEvent evt) 
    {
        changeSupport.fireChange();
    }        

    @Override
    public void addChangeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) 
    {
        changeSupport.removeChangeListener(listener);
    }    
}
