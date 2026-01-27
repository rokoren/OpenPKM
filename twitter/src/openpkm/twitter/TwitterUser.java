/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.twitter;

import java.net.URL;
import openpkm.base.DescriptionProvider;
import openpkm.base.TitleProvider;

/**
 *
 * @author Rok Koren
 */
public interface TwitterUser extends TitleProvider, DescriptionProvider
{
    String X_URL = "https://x.com/";  
    
    String PROP_USER_ID           = "user.id";
    String PROP_USER_NAME         = "user.name";    
    String PROP_PROFILE_IMAGE_URL = "profile.image.url"; 
    
    String getUserID();    
    String getUserName();
    URL getProfileImageUrl();
}
