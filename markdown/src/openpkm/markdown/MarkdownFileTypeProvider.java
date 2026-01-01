/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import openpkm.base.FileTypeProvider;
import openpkm.base.MarkdownSupport;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author Rok Koren
 */
@ServiceProviders({
@ServiceProvider(service = MarkdownSupport.class),    
@ServiceProvider(service = FileTypeProvider.class)    
})
public class MarkdownFileTypeProvider implements MarkdownSupport
{    
    @Override
    public String getExtension() 
    {
        return EXTENSION;
    }

    @Override
    public String getDisplayName() 
    {
        return "Markdown";
    }   
    
    @Override
    public String toString()
    {
        return getDisplayName();
    }     
}
