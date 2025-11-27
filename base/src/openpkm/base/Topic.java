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
public interface Topic 
{    
    String getTopicID();
    String getName();
    void setName(String name); 
    String getTag();
    void setTag(String tag);
    
    public static Comparator<Topic> nameComparator() 
    {
        return new Comparator<Topic>() 
        {
            @Override
            public int compare(Topic topic1, Topic topic2) 
            {
                String name1 = topic1.getName();
                String name2 = topic2.getName();
                return name1.compareTo(name2);
            }
        };
    }     
}
