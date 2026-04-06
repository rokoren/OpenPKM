/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/moduleInstall.java to edit this template
 */
package openpkm.jcef;

import java.io.File;
import java.util.logging.Logger;
import me.friwi.jcefmaven.CefAppBuilder;
import org.cef.CefApp;
import org.cef.CefSettings;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;
import org.openide.modules.Places;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

public class Installer extends ModuleInstall implements Runnable
{ 
    private static final Logger LOG = Logger.getLogger(Installer.class.getName());        
    
    private static final RequestProcessor RP = new RequestProcessor(Installer.class);     
    
    @Override
    public void restored() 
    {
        RP.post(this);
    }    
    
    @Override
    public void close() 
    {        
        CefAppProvider provider = Lookup.getDefault().lookup(CefAppProvider.class);
        if(provider != null)
        {
            provider.dispose();
        }
    }   
    
    public static CefApp getApp() throws Exception
    {
        CefAppBuilder builder = new CefAppBuilder();

        //Configure the builder instance
        builder.setInstallDir(new File(Places.getUserDirectory(), "jcef-bundle"));
        builder.setProgressHandler(new ProgressHandlerImpl());

        builder.getCefSettings().windowless_rendering_enabled = false;           
        builder.getCefSettings().log_severity = CefSettings.LogSeverity.LOGSEVERITY_DISABLE;
        builder.getCefSettings().locale = "en-US";
        builder.getCefSettings().persist_session_cookies = true;
        FileObject userDir = FileUtil.toFileObject(Places.getUserDirectory());
        FileObject cacheFolder = userDir.getFileObject("cef_cache");
        if(cacheFolder == null)
        {
            cacheFolder = userDir.createFolder("cef_cache");
        }
        builder.getCefSettings().cache_path = cacheFolder.getPath();  // OBVEZNO!                                    
        // settings.cache_path = "path/to/cache"; // če želiš trajne podatke
        // settings.remote_debugging_port = 9222; // za DevTools                 

        return builder.build();          
    }

    @Override
    public void run() 
    {
        try
        {
            getApp();
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
    }
}
