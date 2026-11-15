/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Comparator;

/**
 *
 * @author Rok Koren
 */
public interface Domain
{ 
    String getDomainID();
    String getName();
    void setName(String name);   
    String getTag();
    void setTag(String tag); 
    
    public static Comparator<Domain> nameComparator() 
    {
        return new Comparator<Domain>() 
        {
            @Override
            public int compare(Domain domain1, Domain domain2) 
            {
                return domain1.getName().compareTo(domain2.getName());
            }
        };
    }     
}
