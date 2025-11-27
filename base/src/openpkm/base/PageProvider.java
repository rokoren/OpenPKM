/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Rok Koren
 */
public interface PageProvider 
{
    String PROP_PAGE_NUMBER   = "page.number";
    String PROP_PAGE_MODIFIED = "page.modified";    
    
    Integer getPageNumber();
    void setPageNumber(Integer page);
    boolean isPageModified();
    void setPageModified(boolean newValue);
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);    
}
