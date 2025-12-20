/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Comparator;
import java.util.Optional;

/**
 *
 * @author Rok Koren
 */
public interface ThemeProvider 
{
    Optional<? extends Theme> getTheme(String name);
    
    public static Comparator<ThemeProvider> titleComparator() 
    {
        return new Comparator<ThemeProvider>() 
        {
            @Override
            public int compare(ThemeProvider provider1, ThemeProvider provider2) 
            {
                return provider1.toString().compareTo(provider2.toString());
            }
        };
    }    
}
