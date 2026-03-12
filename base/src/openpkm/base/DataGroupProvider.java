/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import java.util.Comparator;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Rok Koren
 */
public interface DataGroupProvider extends GroupProvider
{
    FileObject getRootFolder() throws IOException;    
    boolean contains(DataObject data);   
    Comparator<DataObject> getComparator();
    boolean isReversed();
    
    public static Comparator<DataObject> titleComparator() 
    {
        return new Comparator<DataObject>() 
        {
            @Override
            public int compare(DataObject data1, DataObject data2) 
            {
                TitleProvider provider1 = data1.getLookup().lookup(TitleProvider.class);
                TitleProvider provider2 = data2.getLookup().lookup(TitleProvider.class);
                if(provider1 != null && provider2 != null)
                {
                    return provider1.getTitle().compareTo(provider2.getTitle());                    
                }
                return -1;
            }
        };
    } 
}
