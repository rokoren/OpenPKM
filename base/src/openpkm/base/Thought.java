/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author rok
 */
public interface Thought 
{
    String getThoughtID();
    String getText();
    void setText(String text);   
    Type getType();
    void setType(Type type);  
    
    public enum Type 
    {
        QUESTION("Question"),
        STATEMENT("Statement");

        private final String name;

        Type(String name) 
        {
            this.name = name;
        }

        @Override
        public String toString() 
        {
            return name;
        }
        
        public static Optional<Type> get(String name) {
            return Arrays.stream(Type.values())
                    .filter(modifier -> modifier.name.equalsIgnoreCase(name))
                    .findFirst();
        }     
    }         
}
