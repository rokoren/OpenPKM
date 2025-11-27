/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

/**
 *
 * @author Rok Koren
 */
public interface Source
{     
    String PROP_DELETED             = "deleted";    
    String PROP_TIME_CREATED        = "time.created";    
    
    String getSourceID();
    LocalDateTime getTimeCreated(); 
    void save(OutputStream os, String comments) throws IOException;    
    boolean isDeleted();
    void setDeleted(boolean deleted);  
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}
