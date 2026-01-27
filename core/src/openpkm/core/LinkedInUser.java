/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.core;

import openpkm.base.DescriptionProvider;
import openpkm.base.TitleProvider;

/**
 *
 * @author Rok Koren
 */
public interface LinkedInUser extends TitleProvider, DescriptionProvider
{
    String LINKEDIN_URL = "https://www.linkedin.com/in/";
    
    String PROP_USER_NAME = "user.name";  
    
    String getUserName();
}
