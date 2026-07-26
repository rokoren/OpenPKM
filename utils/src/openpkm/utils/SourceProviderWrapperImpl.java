/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import openpkm.base.ActionProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.TagsProvider;
import org.openide.util.ChangeSupport;

/**
 *
 * @author rok
 */
public class SourceProviderWrapperImpl implements SourceProviderWrapper
{
    private final String sourceID;
    private final SourceProvider provider;
    private final ChangeSupport changeSupport;

    public SourceProviderWrapperImpl(String sourceID, SourceProvider provider) {
        this.sourceID = sourceID;
        this.provider = provider;
        changeSupport = new ChangeSupport(this);    
    }

    @Override
    public Source getSource() 
    {
        return provider.getSource(sourceID);
    }
    
    @Override
    public Source deleteSource()
    {
        Source source = getSource();
        if(source != null)
        {
            provider.deleteSource(source);
        }
        return source;
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
    public List<Action> getActions() 
    {
        Source source = getSource();
        if(source != null)
        {
            Collection<? extends ActionProvider> providers = source.getLookup().lookupAll(ActionProvider.class);
            if(!providers.isEmpty())
            {
                List<Action> actions = new ArrayList();
                for(ActionProvider actionProvider : providers)
                {
                    actions.add(actionProvider.getAction(provider));
                }
                return actions;                  
            }          
        }
        return Collections.EMPTY_LIST;
    }
}
