/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import openpkm.jcef.CefAppProvider;
import openpkm.jcef.CefClientProvider;
import org.cef.CefClient;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=CefClientProvider.class)
public class CefClientProviderImpl implements CefClientProvider
{
    private CefClient client;

    @Override
    public CefClient getCefClient() throws Exception 
    {
        if(client == null)
        {    
            CefAppProvider provider = Lookup.getDefault().lookup(CefAppProvider.class);
            if(provider != null)
            {
                client = provider.getApp().createClient();   
            }           
        }
        return client;
    }

    @Override
    public void dispose() 
    {
        if(client != null)
        {
            client.dispose();
        }
    }    
}
