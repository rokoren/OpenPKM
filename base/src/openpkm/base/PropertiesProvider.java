/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Properties;

/**
 *
 * @author Rok Koren
 */
public interface PropertiesProvider 
{
    String EXTENSION = "properties";  
    
    String PROP_PROPS_ALL = "props.all";
    
    Properties getProperties();  
    void merge(PropertiesProvider provider);
}
