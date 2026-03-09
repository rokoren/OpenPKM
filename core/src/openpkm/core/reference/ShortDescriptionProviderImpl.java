/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.reference;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import openpkm.base.ShortDescriptionProvider;
import openpkm.reference.Reference;
import org.openide.util.ChangeSupport;

/**
 *
 * @author rokor
 */
public class ShortDescriptionProviderImpl implements ShortDescriptionProvider, PropertyChangeListener
{
    private static final Logger LOG = Logger.getLogger(ShortDescriptionProviderImpl.class.getName());     
    
    private final ChangeSupport changeSupport;         
    private final AbstractReference reference;

    public ShortDescriptionProviderImpl(AbstractReference reference) 
    {
        this.reference = reference;
        changeSupport = new ChangeSupport(this);
        reference.addPropertyChangeListener(Reference.PROP_FILE_PATH, this);
    }  
    
    @Override
    public String getShortDescription() 
    {
        try
        {
            return reference.getFile().getPath();            
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
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
