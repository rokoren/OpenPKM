/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.rss;

import java.awt.Image;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface DomainsProvider 
{
    Collection<Domain> getDomains(); 
    boolean createDomain(Properties props, boolean open);    
    Lookup.Provider getProvider();
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener); 
    List<Action> getActions();
    String getName();
    String getDisplayName();
    Image getIcon(boolean hasChildren); 
    boolean contains(Lookup.Provider provider);   
}
