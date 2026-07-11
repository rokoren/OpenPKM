/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/contextAction.java to edit this template
 */
package openpkm.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import openpkm.base.HomeProvider;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "OpenPKM",
        id = "openpkm.core.HomeAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/home.png",
        displayName = "#CTL_HomeAction"
)
@ActionReference(path = "Toolbars/OpenPKM", position = 200)
@Messages("CTL_HomeAction=Home Page")
public final class HomeAction implements ActionListener {

    private final HomeProvider provider;

    public HomeAction(HomeProvider provider) 
    {
        this.provider = provider;
    }

    @Override
    public void actionPerformed(ActionEvent ev) 
    {
        provider.reloadHome();
    }
}
