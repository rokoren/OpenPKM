/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Rok Koren
 */
public interface DataGroupProvider extends GroupProvider
{
    List<FileObject> getFiles() throws IOException;    
    boolean contains(DataObject data);   
    Comparator<DataObject> getComparator();
    boolean isReversed();
    
    public static Comparator<DataObject> timeCreatedComparator() 
    {
        return new Comparator<DataObject>() 
        {
            @Override
            public int compare(DataObject data1, DataObject data2) 
            {
                SourceProviderWrapper provider1 = data1.getLookup().lookup(SourceProviderWrapper.class);
                SourceProviderWrapper provider2 = data2.getLookup().lookup(SourceProviderWrapper.class);
                if(provider1 != null && provider2 != null)
                {
                    Source source1 = provider1.getSource();
                    Source source2 = provider2.getSource();
                    if(source1 != null && source2 != null)
                    {
                        return source1.getTimeCreated().compareTo(source2.getTimeCreated());                         
                    }                   
                }
                return -1;
            }
        };
    }      
    
    public static Comparator<DataObject> titleComparator() 
    {
        return new Comparator<DataObject>() 
        {
            @Override
            public int compare(DataObject data1, DataObject data2) 
            {
                SourceProviderWrapper sourceProvider1 = data1.getLookup().lookup(SourceProviderWrapper.class);
                SourceProviderWrapper sourceProvider2 = data2.getLookup().lookup(SourceProviderWrapper.class);
                if(sourceProvider1 != null && sourceProvider2 != null)
                {
                    Source source1 = sourceProvider1.getSource();
                    Source source2 = sourceProvider2.getSource();
                    if(source1 instanceof TitleProvider provider1 && source2 instanceof TitleProvider provider2)
                    {
                        return provider1.getTitle().compareTo(provider2.getTitle());                         
                    }                   
                }
                return -1;                
            }
        };
    } 
}
