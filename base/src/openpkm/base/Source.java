/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

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
    
    String getSourceID();
    String getAppID();
    LocalDateTime getTimeCreated();   
    void notifyDeleted();   
}
