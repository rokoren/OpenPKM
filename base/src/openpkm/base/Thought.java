/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Arrays;
import java.util.Comparator;
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
        QUESTION("Question", IconsProvider.ICON.QUESTION),
        STATEMENT("Statement", IconsProvider.ICON.STATEMENT);

        private final String name;
        private final IconsProvider.ICON icon;

        Type(String name, IconsProvider.ICON icon) 
        {
            this.name = name;
            this.icon = icon;
        }
        
        public IconsProvider.ICON getIcon()
        {
            return icon;
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
    
    public static Comparator<Thought> textComparator() 
    {
        return new Comparator<Thought>() 
        {
            @Override
            public int compare(Thought thought1, Thought thought2) 
            {
                return thought1.getText().compareTo(thought2.getText());
            }
        };
    }     
}
