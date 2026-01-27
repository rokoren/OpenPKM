/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.facebook;

import openpkm.base.PasswordProvider;

/**
 *
 * @author Rok Koren
 */
public interface FacebookPasswordProvider extends PasswordProvider
{
    String PROP_APP_ID     = "app.id";    
    String PROP_APP_SECRET = "app.secret";    

    String PROP_ACCESS_TOKEN = "access.token";
    
    String getAccessToken();   
}
