/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Rok Koren
 */
public interface DataGroupProvider extends GroupProvider
{
    FileObject getRootFolder() throws IOException;    
    Image getIcon(boolean hasChildren);
    boolean contains(DataObject data);    
}
