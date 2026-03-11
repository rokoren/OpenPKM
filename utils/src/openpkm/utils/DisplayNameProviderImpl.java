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
import openpkm.base.TitleProvider;
import org.openide.util.ChangeSupport;

/**
 *
 * @author rokor
 */
public class DisplayNameProviderImpl implements DisplayNameProvider, ChangeSupportProvider, PropertyChangeListener 
{
    private final ChangeSupport changeSupport;         
    private final TitleProvider provider;

    public DisplayNameProviderImpl(TitleProvider provider) 
    {
        this.provider = provider;
        changeSupport = new ChangeSupport(this);
        provider.addTitleListener(this);
    }    

    @Override
    public void propertyChange(PropertyChangeEvent evt) 
    {
        changeSupport.fireChange();
    }        

    @Override
    public String getDisplayName(TextFormat format) 
    {
        if(format == TextFormat.PLAIN)
        {
            return provider.getTitle();
        }
        return null;        
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
