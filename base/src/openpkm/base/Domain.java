/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import org.netbeans.api.project.Project;

/**
 *
 * @author Rok Koren
 */
public interface Domain extends Project 
{
    String PROP_APP_ID = "app.id";   
    String PROP_TIME_CREATED = "time.created"; 
    
    String getDomainID();
    String getAppID(); 
    LocalDateTime getTimeCreated();     
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);    
}
