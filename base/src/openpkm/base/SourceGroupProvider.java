/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import java.util.SortedSet;

/**
 *
 * @author Rok Koren
 */
public interface SourceGroupProvider extends GroupProvider
{  
    Image getIcon(boolean isEmpty, boolean isOpen);
    SortedSet<? extends NodeProvider> getNodes();   
}
