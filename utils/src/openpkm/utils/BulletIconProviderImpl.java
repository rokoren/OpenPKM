/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.event.ChangeListener;
import openpkm.base.BulletIconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.Source;
import openpkm.base.Source.SourceState;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public class BulletIconProviderImpl implements BulletIconProvider, PropertyChangeListener
{
    private final ChangeSupport changeSupport; 
    private final Source source;

    public BulletIconProviderImpl(Source source, PropertyChangeSupport propertyChangeSupport)
    {
        this.source = source;
        changeSupport = new ChangeSupport(this); 
        propertyChangeSupport.addPropertyChangeListener(Source.PROP_STATE, this);
    }          

    @Override
    public Image getBullet() 
    {
        if(source.getState() == SourceState.DELETED)
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);            
            return provider.getImage(IconsProvider.ICON.BULLET_DELETE);                 
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) 
    {
        changeSupport.fireChange(); 
    }     
}
