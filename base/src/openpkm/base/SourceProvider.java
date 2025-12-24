/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Properties;
import org.netbeans.api.project.SourceGroup;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface SourceProvider extends SourceGroup 
{    
    Source getSource(String sourceID);
    boolean createSource(Properties props, FileTypeProvider fileTypeProvider);
    Lookup.Provider getProvider();
}
