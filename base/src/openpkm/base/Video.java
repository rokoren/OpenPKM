/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author Rok Koren
 */
public interface Video 
{
    public enum Resolution 
    {
        LOWEST("Lowest"),
        LOW("Low"),
        HIGH("High"),
        HIGHEST("Highest");

        private String name;       

        Resolution(String name) 
        {
            this.name = name;
        } 
        
        @Override
        public String toString()
        {
            return name;
        }
        
        public static Optional<Resolution> get(String name) {
            return Arrays.stream(Resolution.values())
                    .filter(resolution -> resolution.name.equalsIgnoreCase(name))
                    .findFirst();
        }     
    }     
}
