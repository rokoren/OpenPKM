/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.jcef;

import java.util.Collection;
import openpkm.jcef.CefAppProvider;
import openpkm.jcef.CefClientProvider;
import openpkm.jcef.Installer;
import org.cef.CefApp;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=CefAppProvider.class)
public class CefAppProviderImpl implements CefAppProvider
{
    private CefApp app;  

    @Override
    public CefApp getApp() throws Exception
    {
        if(app == null)
        { 
            app = Installer.getApp();             
        }
        return app;
    }  

    @Override
    public void dispose() 
    {
        Collection<? extends CefClientProvider> providers = Lookup.getDefault().lookupAll(CefClientProvider.class);
        for(CefClientProvider provider : providers)
        {
            provider.dispose();
        }  
        
        if(app != null)
        {
            app.dispose();
        }
    }     
}
