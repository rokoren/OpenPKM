/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/templateDataObjectMultiForm.java to edit this template
 */
package openpkm.asciidoc;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import openpkm.utils.AbstractVisualElement;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@MultiViewElement.Registration(
        displayName = "#LBL_AsciiDoc_VISUAL",
        iconBase = "openpkm/asciidoc/resources/asciidoc.png",
        mimeType = "text/x-asciidoc",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "AsciiDocVisual",
        position = 2000
)
@Messages("LBL_AsciiDoc_VISUAL=Visual")
public final class AsciiDocVisualElement extends AbstractVisualElement
{
    private JToolBar toolbar = new JToolBar();

    public AsciiDocVisualElement(Lookup lkp)
    {
        super(lkp);
    }

    @Override
    public String getName() {
        return "AsciiDocVisualElement";
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }
}
