/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.beans.PropertyChangeListener;

/**
 *
 * @author rokor
 */
public interface TitleProvider 
{
    String PROP_TITLE = "title";
    
    String getTitle();
    void setTitle(String title);
    void addTitleListener(PropertyChangeListener listener);
    void removeTitleListener(PropertyChangeListener listener);    
}
