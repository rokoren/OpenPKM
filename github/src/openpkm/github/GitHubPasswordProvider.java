/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.github;

import openpkm.base.PasswordProvider;

/**
 *
 * @author Rok Koren
 */
public interface GitHubPasswordProvider extends PasswordProvider
{
    String PROP_PERSONAL_ACCESS_TOKEN = "personal.access.token";    
    
    String getPersonalAccessToken();
}
