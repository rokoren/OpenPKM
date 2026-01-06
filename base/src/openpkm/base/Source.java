/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Rok Koren
 */
public interface Source
{        
    String PROP_APP_ID       = "app.id";        
    String PROP_TIME_CREATED = "time.created";      
    
    String getSourceID();
    String getAppID();
    LocalDateTime getTimeCreated(); 
    void save(OutputStream os, String comments) throws IOException;    
    void setDeleted();
    boolean isDeleted();
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
    void addChangeListener(ChangeListener listener);  
    void removeChangeListener(ChangeListener listener);     
}
