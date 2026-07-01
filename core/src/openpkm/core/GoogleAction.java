/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/actionListener.java to edit this template
 */
package openpkm.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@ActionID(
        category = "OpenPKM",
        id = "openpkm.core.GoogleAction"
)
@ActionRegistration(
        iconBase = "openpkm/core/resources/google.png",
        displayName = "#CTL_GoogleAction"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/OpenPKM", position = 0),
    @ActionReference(path = "Shortcuts", name = "D-G")
})
@Messages("CTL_GoogleAction=Google")
public final class GoogleAction implements ActionListener
{
    private static final Logger LOG = Logger.getLogger(GoogleAction.class.getName());     
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        // TODO implement action body      
        TopComponent tc = new GoogleTopComponent();
        tc.open();
        tc.requestActive();
    }     
}
