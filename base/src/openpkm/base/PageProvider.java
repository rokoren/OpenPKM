/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author Rok Koren
 */
public interface PageProvider 
{
    String PROP_PAGE_NUMBER = "page.number"; 
    
    Integer getPageNumber();
    void setPageNumber(Integer page);
}
