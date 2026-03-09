/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author rokor
 */
public interface DescriptionProvider 
{
    String PROP_DESCRIPTION = "description";
    
    String getDescription();
    void setDescription(String description);    
}
