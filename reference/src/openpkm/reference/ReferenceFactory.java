/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.reference;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

/**
 *
 * @author Rok Koren
 */
public interface ReferenceFactory 
{    
    String PROP_TYPE  = "reference.type";    
    
    Reference getReference(Properties props);  
    
    public enum Type 
    {
        BOOK("book"),
        ARTICLE("article"),        
        DOCUMENT("document"),
        VIDEO("video"),        
        PICTURE("picture");

        private String name;       

        Type(String name) 
        {
            this.name = name;
        } 
        
        public String getName()
        {
            return name;
        }
        
        public static Optional<Type> get(String name) {
            return Arrays.stream(Type.values())
                    .filter(type -> type.name.equalsIgnoreCase(name))
                    .findFirst();
        }     
    }     
}
