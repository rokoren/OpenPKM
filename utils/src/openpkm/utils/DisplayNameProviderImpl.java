/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import openpkm.base.ChangeSupportProvider;
import openpkm.base.DisplayNameProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.TitleProvider;
import org.openide.util.ChangeSupport;

/**
 *
 * @author rokor
 */
public class DisplayNameProviderImpl implements DisplayNameProvider, ChangeSupportProvider, PropertyChangeListener 
{
    private final ChangeSupport changeSupport;         
    private final PropertiesProvider provider;

    public DisplayNameProviderImpl(PropertiesProvider provider) 
    {
        this.provider = provider;
        changeSupport = new ChangeSupport(this);
        provider.addPropertyChangeListener(TitleProvider.PROP_TITLE, this);
    }    

    @Override
    public void propertyChange(PropertyChangeEvent evt) 
    {
        changeSupport.fireChange();
    }        

    @Override
    public String getDisplayName(boolean isHtml) 
    {
        if(isHtml)
        {
            return null;
        }
        return provider.getProperties().getProperty(TitleProvider.PROP_TITLE);
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
