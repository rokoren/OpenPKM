/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

/**
 *
 * @author Rok Koren
 */
public interface ContentFactory extends SourceFactory<Content>
{
    String PROP_TYPE = "content.type";    
    
    Content getContent(Properties props);    
    
    public enum Type 
    {
        BOOK("book"),
        ARTICLE("article"),        
        DOCUMENT("document"),       
        IDEA("idea"),        
        DAILY_JOT("daily-jot"), 
        THOUGHT("thought"),         
        NOTE("note");

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
