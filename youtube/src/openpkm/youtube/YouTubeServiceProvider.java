/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.youtube;

import com.google.api.services.youtube.YouTube;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 *
 * @author Rok Koren
 */
public interface YouTubeServiceProvider 
{
    String PROP_GOOGLE_KEY = "google.key"; 
    String PROP_DATA_DIR   = "data.dir";    
    
    String ACTIVITY_TYPE_UPLOAD = "upload";    
    
    YouTube getService() throws GeneralSecurityException, IOException;        
}
