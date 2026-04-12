/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import openpkm.base.FilterTagsProvider;
import openpkm.base.TagsProvider;
import org.openide.util.ChangeSupport;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author rok
 */
@ServiceProvider(service=FilterTagsProvider.class)
public class FilterTagsProviderImpl implements FilterTagsProvider
{
    private final List<String> filterTags = new ArrayList<>();
    private final ChangeSupport changeSupport = new ChangeSupport(this);   
    
    @Override
    public boolean isTag(TagsProvider provider) 
    {
        if(filterTags.isEmpty())
        {
            return true;
        }
        List<String> tags = provider.getTags();
        if(tags.isEmpty())
        {
            return false;
        }
        for(String tag : tags)
        {
            if(filterTags.contains(tag))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeTagFromFilter(String tag) 
    {
        if(filterTags.remove(tag))
        {
            changeSupport.fireChange();
            return true;
        }
        return false;
    }

    @Override
    public void addTagToFilter(String tag) 
    {
        filterTags.add(tag);
        changeSupport.fireChange();  
    }

    @Override
    public void setFilterTags(List<String> tags) 
    {
        filterTags.clear();
        filterTags.addAll(tags);
        changeSupport.fireChange();
    }

    @Override
    public void clearFilterTags() 
    {
        filterTags.clear();
        changeSupport.fireChange();  
    }

    @Override
    public List<String> getFilterTags() 
    {
        return Collections.unmodifiableList(filterTags);
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
