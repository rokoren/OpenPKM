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
public interface VisibilityProvider
{
    String PROP_VISIBILITY_MODIFIER = "visibility.modifier";  

    Modifier getModifier();
    void setModifier(Modifier modifier);     
    
    public enum Modifier 
    {
        PUBLIC("public"),
        PROTECTED("protected"),
        NONE("none"),
        PRIVATE("private");

        private final String name;

        Modifier(String name) 
        {
            this.name = name;
        }

        @Override
        public String toString() 
        {
            return name;
        }
        
        public static Optional<Modifier> get(String name) {
            return Arrays.stream(Modifier.values())
                    .filter(modifier -> modifier.name.equalsIgnoreCase(name))
                    .findFirst();
        }     
    }      
}
