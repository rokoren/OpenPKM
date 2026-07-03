/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.swing.event.ChangeListener;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.TagsProvider;
import org.openide.util.ChangeSupport;

/**
 *
 * @author rok
 */
public class SourceProviderWrapperImpl implements SourceProviderWrapper, PropertyChangeListener
{
    private final String sourceID;
    private final SourceProvider provider;
    private final ChangeSupport changeSupport;

    public SourceProviderWrapperImpl(String sourceID, SourceProvider provider) {
        this.sourceID = sourceID;
        this.provider = provider;
        changeSupport = new ChangeSupport(this);    
        provider.addSourceListener(this);
    }

    @Override
    public Source getSource() 
    {
        return provider.getSource(sourceID);
    }
    
    @Override
    public void deleteSource() throws IOException
    {
        provider.deleteSource(sourceID);
    }

    @Override
    public SourceProvider getProvider() 
    {
        return provider;
    } 
    
    @Override
    public Set<String> getTags() 
    {
        Source source = provider.getSource(sourceID);
        if(source != null)
        {
            TagsProvider tagsProvider = source.getLookup().lookup(TagsProvider.class);
            if(tagsProvider != null)
            {
                return tagsProvider.getTags();
            }
        }
        return Collections.EMPTY_SET;
    }    

    @Override
    public void addListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) 
    {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) 
    {
        Source source = (Source)evt.getNewValue();
        if(source != null)
        {
            if(source.getSourceID().equals(sourceID))
            {
                changeSupport.fireChange();
            }
        }
    }
}
