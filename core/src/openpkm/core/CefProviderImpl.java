/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.io.File;
import openpkm.jcef.CefProvider;
import org.cef.CefApp;
import org.cef.CefSettings;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.Places;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=CefProvider.class)
public class CefProviderImpl implements CefProvider
{
    private CefApp cefApp;
    
    @Override
    public CefApp getCef() throws Exception
    {
        if(cefApp == null)
        {
            CefSettings settings = new CefSettings();
            settings.windowless_rendering_enabled = false;
            settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_DISABLE;
            settings.locale = "en-US";
            settings.persist_session_cookies = true;
            FileObject userDir = FileUtil.toFileObject(Places.getUserDirectory());
            FileObject cacheFolder = userDir.getFileObject("cef_cache");
            if(cacheFolder == null)
            {
                cacheFolder = userDir.createFolder("cef_cache");
            }
            settings.cache_path = cacheFolder.getPath();  // OBVEZNO!                                    
            // settings.cache_path = "path/to/cache"; // če želiš trajne podatke
            // settings.remote_debugging_port = 9222; // za DevTools


            File jcef_helper = InstalledFileLocator.getDefault().locate(
                    "modules/lib/amd64/jcef_helper.exe",
                    "openpkm.jcef",
                    false);
            settings.browser_subprocess_path = jcef_helper.getAbsolutePath();
            //modules_path = jcef_helper.getAbsolutePath().split("modules")[0] + "modules";        
            cefApp = CefApp.getInstance(settings);             
        }
        return cefApp;
    }

    @Override
    public void dispose() 
    {
        if(cefApp != null)
        {
            cefApp.dispose();
        }
    }    
}
