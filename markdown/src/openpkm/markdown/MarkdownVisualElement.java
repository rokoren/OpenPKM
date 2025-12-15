/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/templateDataObjectMultiForm.java to edit this template
 */
package openpkm.markdown;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import openpkm.utils.AbstractVisualElement;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@MultiViewElement.Registration(
        displayName = "#LBL_Markdown_VISUAL",
        iconBase = "openpkm/markdown/resources/markdown.png",
        mimeType = MarkdownLanguageConfig.MIME_TYPE,
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "MarkdownVisual",
        position = 2000
)
@Messages("LBL_Markdown_VISUAL=Visual")
public final class MarkdownVisualElement extends AbstractVisualElement
{
    private JToolBar toolbar = new JToolBar();

    public MarkdownVisualElement(Lookup lkp) 
    {
        super(lkp);
    }

    @Override
    public String getName() 
    {
        return "MarkdownVisualElement";
    }

    @Override
    public JComponent getToolbarRepresentation() 
    {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }
}
