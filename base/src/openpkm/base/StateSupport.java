/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author rok
 */
public interface StateSupport 
{
    String PROP_STATE = "state";     
    
    void markModified(); 
    boolean isModified();
    boolean isDeleted();
    
    public enum State 
    {
        MODIFIED,
        DELETED;    
    }      
}
