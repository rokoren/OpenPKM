/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.time.LocalDate;

/**
 *
 * @author Rok Koren
 */
public interface SomedayMaybeProvider 
{
    String PROP_TICKLE_DATE = "tickle.date";     
    
    boolean isActive();
    LocalDate getTickleDate();
    void setTickleDate(LocalDate date);
}
