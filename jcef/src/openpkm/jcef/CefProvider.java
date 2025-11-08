/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.jcef;

import org.cef.CefApp;

/**
 *
 * @author Rok Koren
 */
public interface CefProvider 
{
    CefApp getCef() throws Exception;
    void dispose();
}
