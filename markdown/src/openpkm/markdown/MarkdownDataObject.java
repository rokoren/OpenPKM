/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/templateDataObjectAnno.java to edit this template
 */
package openpkm.markdown;

import java.io.IOException;
import java.util.logging.Logger;
import openpkm.base.DisplayNameProvider;
import openpkm.base.LinkFactory;
import openpkm.base.LiteratureNoteFactory;
import openpkm.base.Source;
import openpkm.base.SourceProviderWrapper;
import openpkm.utils.OpenPkmMultiViewEditorElement;
import openpkm.utils.Utils;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

@Messages({
    "LBL_Markdown_LOADER=Files of Markdown"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Markdown_LOADER",
        mimeType = MarkdownLanguageConfig.MIME_TYPE,
        extension = {"md"}
)
@DataObject.Registration(
        mimeType = MarkdownLanguageConfig.MIME_TYPE,
        iconBase = "openpkm/markdown/resources/markdown.png",
        displayName = "#LBL_Markdown_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class MarkdownDataObject extends MultiDataObject
{
    private static final Logger LOG = Logger.getLogger(MarkdownDataObject.class.getName());
    
    private Lookup lookup;     
    
    public MarkdownDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException 
    {
        super(pf, loader);    
        registerEditor(MarkdownLanguageConfig.MIME_TYPE, true);
    }     

    @Override
    protected int associateLookup() {
        return 1;
    }
    
    @Override
    public Lookup getLookup()
    { 
        if(lookup == null)
        { 
            SourceProviderWrapper provider = Utils.getSourceProvider(getPrimaryFile());
            if(provider == null)
            {
                lookup = super.getLookup();
            }  
            else
            {   
                LiteratureNoteFactory literatureNoteFactory = provider.getLiteratureNoteFactory(this);
                if(literatureNoteFactory == null)
                {
                    lookup = new ProxyLookup(super.getLookup(), Lookups.singleton(provider), Lookups.singleton(new MarkdownLinkFactory()));                     
                }
                else
                {
                    lookup = new ProxyLookup(super.getLookup(), Lookups.singleton(provider), Lookups.singleton(new MarkdownLinkFactory()), Lookups.singleton(literatureNoteFactory)); 
                }                               
            }
        }
        return lookup;   
    } 
    
    @Override
    protected void handleDelete() throws IOException 
    {        
        SourceProviderWrapper provider = getLookup().lookup(SourceProviderWrapper.class);
        if(provider != null)
        {
            provider.deleteSource();
        }

        // pokličeš privzeto brisanje datoteke
        super.handleDelete();
    }      

    @Override
    protected Node createNodeDelegate() 
    {
        return new MarkdownDataNode(this);
    }    

    @MultiViewElement.Registration(
            displayName = "#LBL_Markdown_EDITOR",
            iconBase = "openpkm/markdown/resources/markdown.png",
            mimeType = MarkdownLanguageConfig.MIME_TYPE,
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID = "Markdown",
            position = 1000
    )
    @Messages("LBL_Markdown_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new OpenPkmMultiViewEditorElement(lkp);
    } 
    
    private static class MarkdownLinkFactory implements LinkFactory 
    {
        @Override
        public String createLink(DataObject data) 
        {
            SourceProviderWrapper provider = data.getLookup().lookup(SourceProviderWrapper.class);
            if(provider != null)
            {
                Source source = provider.getSource();
                if(source != null)
                {
                    DisplayNameProvider displayNameProvider = provider.getSource().getLookup().lookup(DisplayNameProvider.class);   
                    if(displayNameProvider != null)
                    {
                        return "[" + displayNameProvider.getDisplayName(DisplayNameProvider.TextFormat.PLAIN) + "](openpkm:" + data.getPrimaryFile().getNameExt() + ")";                     
                    }
                }
            }
            return null;
        }
    }      
}
