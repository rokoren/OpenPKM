/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.twitter;

import openpkm.base.PasswordProvider;

/**
 *
 * @author Rok Koren
 */
public interface TwitterPasswordProvider extends PasswordProvider
{
    String PROP_BEARER_TOKEN = "bearer.token";    
    
    String getBearerToken();    
}
