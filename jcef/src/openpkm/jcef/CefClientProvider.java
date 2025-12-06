/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.jcef;

import org.cef.CefClient;

/**
 *
 * @author Rok Koren
 */
public interface CefClientProvider 
{
    CefClient getCefClient() throws Exception;  
    void dispose(); 
}
