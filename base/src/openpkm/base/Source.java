/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.time.LocalDateTime;
import java.util.Comparator;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface Source extends Lookup.Provider
{        
    String PROP_APP_ID       = "app.id";        
    String PROP_TIME_CREATED = "time.created";         
    
    String getSourceID();
    String getAppID();
    LocalDateTime getTimeCreated();    
    
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
}
