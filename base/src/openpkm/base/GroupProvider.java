/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface GroupProvider 
{
    String getName();
    String getDisplayName();
    List<Action> getActions();
    Integer getPosition();
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);
    Lookup.Provider getProvider(); 
    
    public static Comparator<GroupProvider> positionComparator() 
    {
        return new Comparator<GroupProvider>() 
        {
            @Override
            public int compare(GroupProvider group1, GroupProvider group2) 
            {
                return group1.getPosition().compareTo(group2.getPosition());
            }
        };
    }     
}
