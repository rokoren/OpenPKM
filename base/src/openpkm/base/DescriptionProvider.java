/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.beans.PropertyChangeListener;

/**
 *
 * @author rokor
 */
public interface DescriptionProvider 
{
    String PROP_DESCRIPTION = "description";
    
    String getDescription();
    void setDescription(String description);   
    void addDescriptionListener(PropertyChangeListener listener);
    void removeDescriptionListener(PropertyChangeListener listener);      
}
