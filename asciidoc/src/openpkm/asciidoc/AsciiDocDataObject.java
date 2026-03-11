/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/templateDataObjectAnno.java to edit this template
 */

package openpkm.asciidoc;

import java.io.IOException;
import java.util.logging.Logger;
import openpkm.base.RemoteDataProvider;
import openpkm.base.Source;
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
    "LBL_AsciiDoc_LOADER=Files of AsciiDoc"
})
@MIMEResolver.ExtensionRegistration(
    displayName="#LBL_AsciiDoc_LOADER",
    mimeType=AsciidocLanguageConfig.MIME_TYPE,
    extension={ "adoc" }
)
@DataObject.Registration(
    mimeType = AsciidocLanguageConfig.MIME_TYPE, 
    iconBase = "openpkm/asciidoc/resources/asciidoc.png",
    displayName="#LBL_AsciiDoc_LOADER",
    position=300
)
@ActionReferences({
    @ActionReference(
        path="Loaders/text/x-asciidoc/Actions", 
        id=@ActionID(category="System", id="org.openide.actions.OpenAction"),
        position=100, 
        separatorAfter=200
    ),
    @ActionReference(
        path="Loaders/text/x-asciidoc/Actions", 
        id=@ActionID(category="Edit", id="org.openide.actions.CutAction"),
        position=300
    ),
    @ActionReference(
        path="Loaders/text/x-asciidoc/Actions", 
        id=@ActionID(category="Edit", id="org.openide.actions.CopyAction"),
        position=400,
        separatorAfter=500
    ),
    @ActionReference(
        path="Loaders/text/x-asciidoc/Actions", 
        id=@ActionID(category="Edit", id="org.openide.actions.DeleteAction"),
        position=600
    ),
    @ActionReference(
        path="Loaders/text/x-asciidoc/Actions", 
        id=@ActionID(category="System", id="org.openide.actions.RenameAction"),
        position=700,
        separatorAfter=800
    ),
    @ActionReference(
        path="Loaders/text/x-asciidoc/Actions", 
        id=@ActionID(category="System", id="org.openide.actions.SaveAsTemplateAction"),
        position=900,
        separatorAfter=1000
    ),
    @ActionReference(
        path="Loaders/text/x-asciidoc/Actions", 
        id=@ActionID(category="System", id="org.openide.actions.FileSystemAction"),
        position=1100,
        separatorAfter=1200
    ),
    @ActionReference(
        path="Loaders/text/x-asciidoc/Actions", 
        id=@ActionID(category="System", id="org.openide.actions.ToolsAction"),
        position=1300
    ),
    @ActionReference(
        path="Loaders/text/x-asciidoc/Actions", 
        id=@ActionID(category="System", id="org.openide.actions.PropertiesAction"),
        position=1400
    )
})
public class AsciiDocDataObject extends MultiDataObject
{    
    private static final Logger LOG = Logger.getLogger(AsciiDocDataObject.class.getName());
    
    private Lookup lookup;    
    
    public AsciiDocDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException 
    {
        super(pf, loader); 
        registerEditor(AsciidocLanguageConfig.MIME_TYPE, true);        
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
            Source source = Utils.getSource(getPrimaryFile());
            if(source == null)
            {
                lookup = super.getLookup();
            }  
            else
            {                   
                lookup = new ProxyLookup(super.getLookup(), Lookups.proxy(source));                
            }
        }
        return lookup; 
    } 
    
    @Override
    protected void handleDelete() throws IOException 
    {
        RemoteDataProvider provider = getLookup().lookup(RemoteDataProvider.class);
        if(provider != null)
        {
            provider.delete();
        }
        
        Source source = getLookup().lookup(Source.class);
        if(source != null)
        {
            source.notifyDeleted();
        }

        // pokličeš privzeto brisanje datoteke
        super.handleDelete();
    }     
    
    @Override
    protected Node createNodeDelegate() 
    {
        return new AsciiDocDataNode(this);
    }        

    @MultiViewElement.Registration(
        displayName = "#LBL_AsciiDoc_EDITOR",
        iconBase = "openpkm/asciidoc/resources/asciidoc.png",
        mimeType = AsciidocLanguageConfig.MIME_TYPE,
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
        preferredID = "AsciiDoc",
        position = 1000
    )
    @Messages("LBL_AsciiDoc_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new MultiViewEditorElement(lkp);
    }  
}
