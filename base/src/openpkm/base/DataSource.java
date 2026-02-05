/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.time.LocalDateTime;

/**
 *
 * @author Rok Koren
 */
public interface DataSource 
{       
    String getSourceID();
    String getAppID();
    LocalDateTime getTimeCreated();     
}
