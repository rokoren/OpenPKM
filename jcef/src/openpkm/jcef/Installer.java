/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/moduleInstall.java to edit this template
 */
package openpkm.jcef;

import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

public class Installer extends ModuleInstall 
{
    @Override
    public void restored() 
    {
        CefProvider provider = Lookup.getDefault().lookup(CefProvider.class);
        if(provider != null)
        {
            provider.dispose();
        }
    }
}
