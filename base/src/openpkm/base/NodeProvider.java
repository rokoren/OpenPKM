/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Comparator;
import openpkm.base.DisplayNameProvider.TextFormat;
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
    HelpCtx getHelp();  
    
    public static Comparator<NodeProvider> displayNameComparator() 
    {
        return new Comparator<NodeProvider>() 
        {
            @Override
            public int compare(NodeProvider provider1, NodeProvider provider2) 
            {
                DisplayNameProvider dnp1 = provider1.getLookup().lookup(DisplayNameProvider.class);
                DisplayNameProvider dnp2 = provider2.getLookup().lookup(DisplayNameProvider.class);
                if(dnp1 != null && dnp2 != null)
                {
                    return dnp1.getDisplayName(TextFormat.PLAIN).compareTo(dnp2.getDisplayName(TextFormat.PLAIN));                    
                }
                return -1;
            }
        };
    }     
}
