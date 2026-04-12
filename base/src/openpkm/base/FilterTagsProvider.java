/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.List;
import javax.swing.event.ChangeListener;

/**
 *
 * @author rokor
 */
public interface FilterTagsProvider 
{
    boolean isTag(TagsProvider provider);    
    boolean removeTagFromFilter(String tag);    
    void addTagToFilter(String tag);    
    void setFilterTags(List<String> tags);    
    void clearFilterTags();    
    List<String> getFilterTags();    
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener); 
    
    public static boolean isTag(FilterTagsProvider filterProvider, TagsProvider tagsProvider)
    {
        if(filterProvider == null)
        {
            return true;
        }
        return filterProvider.isTag(tagsProvider);
    }     
}
