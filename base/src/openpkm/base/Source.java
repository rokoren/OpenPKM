/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface Source extends Lookup.Provider
{        
    String PROP_APP_ID       = "app.id";        
    String PROP_TIME_CREATED = "time.created";  
    String PROP_STATE        = "source.state";         
    
    String getSourceID();
    String getAppID();
    LocalDateTime getTimeCreated(); 
    void save(OutputStream os, String comments) throws IOException; 
    void markModified();      
    void notifyDeleted();
    SourceState getState();
    
    public enum SourceState 
    {
        MODIFIED,
        DELETED;    
    }      
}
