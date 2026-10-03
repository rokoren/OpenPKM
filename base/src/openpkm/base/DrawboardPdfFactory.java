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
 * @author rok
 */
public interface DrawboardPdfFactory extends SourceFactory<DrawboardPdf>
{
    String PROP_TYPE = "drawboard.pdf.type";    
    
    DrawboardPdf getDrawboardPdf(Properties props);    
    
    public enum Type 
    {
        BOOK("book"),
        ARTICLE("article"),        
        DOCUMENT("document");

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
