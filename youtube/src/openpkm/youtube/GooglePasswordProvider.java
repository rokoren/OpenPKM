/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.youtube;

import openpkm.base.PasswordProvider;

/**
 *
 * @author Rok Koren
 */
public interface GooglePasswordProvider extends PasswordProvider
{
    String PROP_GOOGLE_KEY = "google.key";
    
    String getKey();    
}
