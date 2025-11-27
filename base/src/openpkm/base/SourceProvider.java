/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import org.netbeans.api.project.SourceGroup;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface SourceProvider extends SourceGroup 
{
    String ATTR_SOURCE_ID     = "source.id";
    String ATTR_SOURCE_FOLDER = "source.folder";    
    
    Source getSource(String sourceID);
    Lookup.Provider getProvider();
}
