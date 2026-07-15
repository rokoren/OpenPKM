/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DescriptionProvider;
import openpkm.base.ShortDescriptionProvider;
import org.openide.util.ChangeSupport;

/**
 *
 * @author rok
 */
public class ShortDescriptionProviderImpl implements ShortDescriptionProvider, ChangeSupportProvider, PropertyChangeListener 
{
    private final ChangeSupport changeSupport;         
    private final DescriptionProvider provider;

    public ShortDescriptionProviderImpl(DescriptionProvider provider) 
    {
        this.provider = provider;
        changeSupport = new ChangeSupport(this);
        provider.addDescriptionListener(this);
    }    

    @Override
    public String getShortDescription() 
    {
        return provider.getDescription();
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
