/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import org.openide.nodes.Children;
import org.openide.util.*;

/**
 *
 * @author Rok Koren
 */
public interface NodeProvider extends DisplayNameProvider, IconProvider, ActionsProvider
{
    String getName();
    String getShortDescription();
    String getHtmlDisplayName();    
    Image getIcon(int type);
    Image getOpenedIcon(int type);
    Children getChildren();
    HelpCtx getHelpCtx();        
}
