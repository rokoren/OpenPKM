/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface GraphProvider 
{
    boolean isTag(TagsProvider provider);
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);     
    Lookup.Provider getProvider();    
}
