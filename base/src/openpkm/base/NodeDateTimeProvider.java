/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 *
 * @author Rok Koren
 */
public interface NodeDateTimeProvider extends NodeProvider
{
    LocalDateTime getDateTime();
    
    public static Comparator<NodeDateTimeProvider> dateTimeComparator() 
    {
        return new Comparator<NodeDateTimeProvider>() 
        {
            @Override
            public int compare(NodeDateTimeProvider provider1, NodeDateTimeProvider provider2) 
            {
                return provider1.getDateTime().compareTo(provider2.getDateTime());      
            }
        };
    }      
}
