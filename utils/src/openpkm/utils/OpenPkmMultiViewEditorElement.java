/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import openpkm.base.LinkFactory;
import openpkm.base.Source;
import openpkm.base.SourceProviderWrapper;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.loaders.DataObject;
import org.openide.loaders.LoaderTransfer;
import org.openide.nodes.NodeTransfer;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public class OpenPkmMultiViewEditorElement extends MultiViewEditorElement
{
    private static final Logger LOG = Logger.getLogger(OpenPkmMultiViewEditorElement.class.getName());
    
    private transient MultiViewElementCallback callback;    
    private transient DropTarget dropTarget;

    public OpenPkmMultiViewEditorElement(Lookup lookup) 
    {
        super(lookup);
    }
    
    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) 
    {
        this.callback = callback;
        super.setMultiViewCallback(callback);
    }    

    @Override
    public void componentOpened() 
    {
        super.componentOpened(); 
        
        DataObject data = getLookup().lookup(DataObject.class);
        if(data != null) 
        {
            SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
            if(sourceProvider != null)
            {
                Source source = sourceProvider.getSource();
                if(source != null)
                {
                    MultiViewDescription mvd = source.getLookup().lookup(MultiViewDescription.class);
                    if(mvd != null)
                    {
                        MultiViewHandler handler = MultiViews.findMultiViewHandler(callback.getTopComponent());
                        if(handler != null)
                        {
                            MultiViewPerspective perspective = handler.getSelectedPerspective();
                            SwingUtilities.invokeLater(() -> 
                            {
                                handler.requestVisible(perspective);
                            });                              
                            handler.addMultiViewDescription(mvd, -1);                                                                                   
                        }                           
                    } 
                }
            }
        }
                      
        installDropTarget();                
    }

    @Override
    public void componentClosed() 
    {
        uninstallDropTarget();
        super.componentClosed();
    }

    private void installDropTarget() {
        JEditorPane editorPane = getEditorPane();

        if (editorPane == null) {
            return;
        }

        dropTarget = new DropTarget(editorPane, new OpenPkmDropTargetListener(this));
    }

    private void uninstallDropTarget() 
    {
        if (dropTarget != null) 
        {
            dropTarget.setComponent(null);
            dropTarget = null;
        }
    }  
    
    private static class OpenPkmDropTargetListener extends DropTargetAdapter 
    {
        private final OpenPkmMultiViewEditorElement editor;

        public OpenPkmDropTargetListener(OpenPkmMultiViewEditorElement editor) 
        {
            this.editor = editor;
        }

        @Override
        public void drop(DropTargetDropEvent event) {

            Transferable transferable = event.getTransferable();
            DataObject targetdata = LoaderTransfer.getDataObject(transferable, NodeTransfer.DND_COPY);

            if (targetdata != null) 
            {
                DataObject sourceData = editor.getLookup().lookup(DataObject.class);
                LinkFactory linkFactory = sourceData.getLookup().lookup(LinkFactory.class);
                
                if (linkFactory != null) 
                {
                    String link = linkFactory.createLink(targetdata);
                    int caret = editor.getEditorPane().getCaretPosition();

                    try
                    {
                        editor.getEditorPane().getDocument().insertString(caret, link, null);   
                        event.acceptDrop(DnDConstants.ACTION_COPY);
                        event.dropComplete(true);
                    }
                    catch(BadLocationException e)
                    {
                        event.dropComplete(false);
                        LOG.warning(e.getMessage());
                    }
                }              
                return;
            }

            event.rejectDrop();
        }            
    }
}
