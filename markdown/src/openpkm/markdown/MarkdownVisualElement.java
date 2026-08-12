/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/templateDataObjectMultiForm.java to edit this template
 */
package openpkm.markdown;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javafx.application.Platform;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import openpkm.base.SourceProviders;
import openpkm.utils.AbstractVisualElement;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
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
public final class MarkdownVisualElement extends AbstractVisualElement implements ActionListener
{
    public static final String ATTR_MARKDOWN_THEME = "markdown.theme";     
    
    private static final String ACTION_COMMAND_PROVIDER = "provider";
    private static final String ACTION_COMMAND_THEME    = "theme";      
    
    private final DefaultComboBoxModel<MarkdownThemeProvider> themes1 = new DefaultComboBoxModel<>(); 
    private final DefaultComboBoxModel<MarkdownTheme> themes2 = new DefaultComboBoxModel<>(); 
    
    private JToolBar toolbar;

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
        if(toolbar == null)
        {
            themes1.addAll(MarkdownThemeProvider.getAll());
            toolbar = new JToolBar();
            JComboBox comboBox1 = new JComboBox();
            JComboBox comboBox2 = new JComboBox();
            comboBox1.setActionCommand(ACTION_COMMAND_PROVIDER);
            comboBox1.addActionListener(this);
            comboBox1.setModel(themes1);
            comboBox2.setActionCommand(ACTION_COMMAND_THEME);
            comboBox2.addActionListener(this);
            comboBox2.setModel(themes2);  
            //toolbar.add(new JSeparator(JSeparator.VERTICAL));
            toolbar.add(comboBox1);
            toolbar.add(comboBox2);
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
                String name = (String)fileWithAttrs.getAttribute(ATTR_MARKDOWN_THEME);
                MarkdownTheme theme = MarkdownTheme.getTheme(name); 
                if(theme != null)
                {
                    return theme.getUrl();
                }
            }            
        }
        return StandardThemeProviderImpl.getDefaultTheme().getUrl();  
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) 
    {
        if(evt.getActionCommand().equals(ACTION_COMMAND_PROVIDER))
        {
            MarkdownThemeProvider provider = (MarkdownThemeProvider)themes1.getSelectedItem();
            themes2.removeAllElements();
            themes2.addAll(provider.getThemes());
        }
        else if(evt.getActionCommand().equals(ACTION_COMMAND_THEME))
        {
            MarkdownTheme theme = (MarkdownTheme)themes2.getSelectedItem();
            if(theme != null)
            {
                DataObject data = getLookup().lookup(DataObject.class);  
                Project project = FileOwnerQuery.getOwner(data.getPrimaryFile());
                if(project != null)
                {
                    SourceProviders providers = project.getLookup().lookup(SourceProviders.class);
                    if(providers != null)
                    {     
                        FileObject fileWithAttrs = providers.getFileWithAttrs(data.getPrimaryFile(), false);
                        if(fileWithAttrs != null)
                        {
                            try
                            {
                                fileWithAttrs.setAttribute(ATTR_MARKDOWN_THEME, theme.getName());    
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() 
                                    {
                                        browser.getEngine().setUserStyleSheetLocation(theme.getUrl()); 
                                    }
                                });                              
                            }
                            catch(IOException e)
                            {
                                LOG.warning(e.getMessage());
                            }
                        }            
                    }
                }                 
            }                                                              
        }
    }    
}
