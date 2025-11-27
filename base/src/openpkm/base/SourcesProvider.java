/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface SourcesProvider 
{
    SourceProvider getSourceProvider(String folder);
    Lookup.Provider getProvider();
}
