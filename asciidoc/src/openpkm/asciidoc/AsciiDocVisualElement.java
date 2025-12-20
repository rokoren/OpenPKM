/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/templateDataObjectMultiForm.java to edit this template
 */
package openpkm.asciidoc;

import java.io.IOException;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import openpkm.base.SourceProviders;
import openpkm.utils.AbstractVisualElement;
import org.netbeans.api.project.Project;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@MultiViewElement.Registration(
        displayName = "#LBL_AsciiDoc_VISUAL",
        iconBase = "openpkm/asciidoc/resources/asciidoc.png",
        mimeType = AsciidocLanguageConfig.MIME_TYPE,
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "AsciiDocVisual",
        position = 2000
)
@Messages("LBL_AsciiDoc_VISUAL=Visual")
public final class AsciiDocVisualElement extends AbstractVisualElement
{
    public static final String ATTR_ASCIIDOC_THEME = "asciidoc.theme";     
    
    private JToolBar toolbar;

    public AsciiDocVisualElement(Lookup lkp)
    {
        super(lkp);
    }

    @Override
    public String getName() {
        return "AsciiDocVisualElement";
    }

    @Override
    public JComponent getToolbarRepresentation() 
    {
        if(toolbar == null)
        {
            toolbar = new JToolBar();
            JComboBox themes = new JComboBox();
            
        }
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }
    
    @Override
    public String getThemeUrl(FileObject file, Project project)
    {
        SourceProviders providers = project.getLookup().lookup(SourceProviders.class);
        if(providers != null)
        {     
            FileObject fileWithAttrs = providers.getFileWithAttrs(file, false);
            if(fileWithAttrs != null)
            {
                String name = (String)fileWithAttrs.getAttribute(ATTR_ASCIIDOC_THEME);
                AsciiDocTheme theme = AsciiDocTheme.getTheme(name); 
                if(theme != null)
                {
                    return theme.getUrl();
                }
            }            
        }
        return AsciiDocStandardThemeProviderImpl.getDefaultTheme().getUrl();        
    }
    
    private void setTheme(AsciiDocTheme theme) throws IOException
    {
        /*
        FileObject file = getPrimaryFile();
        Project project = FileOwnerQuery.getOwner(file);
        if(project != null)
        {
            SourceProviders providers = project.getLookup().lookup(SourceProviders.class);
            if(providers != null)
            {     
                FileObject fileWithAttrs = providers.getFileWithAttrs(file, false);
                if(fileWithAttrs != null)
                {
                    fileWithAttrs.setAttribute(ATTR_ASCIIDOC_THEME, theme.getName());
                }            
            }
        } 
        */
    }     
}
