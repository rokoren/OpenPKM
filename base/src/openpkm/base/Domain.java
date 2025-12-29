/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.beans.PropertyChangeListener;
import org.netbeans.api.project.Project;

/**
 *
 * @author Rok Koren
 */
public interface Domain extends Project 
{
    String PROP_APP_ID = "app.id";    
    
    String getDomainID();
    String getAppID();    
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);    
}
