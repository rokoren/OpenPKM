/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public interface FileTypeProvider
{
    String getExtension();
    String getDisplayName();

    public static Collection<FileTypeProvider> getAll()
    {
        Collection<? extends FileTypeProvider> coll = Lookup.getDefault().lookupAll(FileTypeProvider.class);
        return Collections.unmodifiableCollection(coll);
    } 
    
    public static SortedSet<FileTypeProvider> getAll(Comparator<FileTypeProvider> comparator)
    {
        Collection<FileTypeProvider> coll = getAll();
        SortedSet<FileTypeProvider> providers = new TreeSet<FileTypeProvider>(comparator);
        providers.addAll(coll);
        return providers;
    }    
    
    public static Comparator<FileTypeProvider> displayNameComparator() 
    {
        return new Comparator<FileTypeProvider>() 
        {
            @Override
            public int compare(FileTypeProvider provider1, FileTypeProvider provider2) 
            {
                return provider1.getDisplayName().compareTo(provider2.getDisplayName());
            }
        };
    }     
}
