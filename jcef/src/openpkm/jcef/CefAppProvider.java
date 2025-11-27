/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.jcef;

import org.cef.CefApp;
import org.cef.CefClient;

/**
 *
 * @author Rok Koren
 */
public interface CefAppProvider
{
    CefApp getApp() throws Exception;  
    CefClient getDefaultClient() throws Exception;    
    void dispose();    
}
