/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import openpkm.base.FileTypeProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=FileTypeProvider.class)
public class AsciiDocFileTypeProvider implements FileTypeProvider
{
    public static final String EXTENSION = "adoc";
    
    @Override
    public String getExtension() 
    {
        return EXTENSION;
    }

    @Override
    public String getDisplayName() 
    {
        return "AsciiDoc";
    }   
    
    @Override
    public String toString()
    {
        return getDisplayName();
    }    
}
