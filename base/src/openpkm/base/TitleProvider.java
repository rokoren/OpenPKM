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
public interface TitleProvider 
{
    String PROP_TITLE = "title";

    String getTitle();
    void setTitle(String title);   

    public static Comparator<TitleProvider> titleComparator() 
    {
        return new Comparator<TitleProvider>() 
        {
            @Override
            public int compare(TitleProvider provider1, TitleProvider provider2) 
            {
                return provider1.getTitle().compareTo(provider2.getTitle());
            }
        };
    }     
}
