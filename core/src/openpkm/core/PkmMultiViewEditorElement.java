/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.util.Collection;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Rok Koren
 */
public class PkmMultiViewEditorElement extends MultiViewEditorElement implements LookupListener
{
    private Lookup.Result<MultiViewDescription> result; 
    
    public PkmMultiViewEditorElement(Lookup lkp)
    {
        super(lkp);
    }
    
    @Override
    public void componentOpened() 
    {
        super.componentOpened();
        result = getLookup().lookupResult(MultiViewDescription.class);  
        result.addLookupListener(this);        
    } 
    
    @Override
    public void componentClosed() 
    {
        result.removeLookupListener(this);         
        super.componentClosed();        
    }     
    
    @Override
    public void resultChanged(LookupEvent evt) 
    {
        Collection<? extends MultiViewDescription> results = result.allInstances();
        MultiViewHandler handler = MultiViews.findMultiViewHandler(getComponent());
    }      
}
