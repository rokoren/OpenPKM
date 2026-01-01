/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author Rok Koren
 */
public interface WatchLater 
{
    String PROP_WATCH_LATER = "watch.later";
    
    boolean isWatchLater();
    void setWatchLater(boolean watchLater);
}
