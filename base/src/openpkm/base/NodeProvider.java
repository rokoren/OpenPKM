/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import org.openide.nodes.Children;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface NodeProvider extends Lookup.Provider
{
    String getName();
    Children getChildren();
    HelpCtx getHelpCtx();        
}
