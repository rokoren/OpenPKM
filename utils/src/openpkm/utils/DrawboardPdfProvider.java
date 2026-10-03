/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import openpkm.base.DrawboardPdf;
import openpkm.base.DrawboardPdfFactory;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author rok
 */
public abstract class DrawboardPdfProvider implements SourceProvider<DrawboardPdf>
{
    protected static final String ROOT_FOLDER = "drawboard-pdf";       

    protected Map<String, DrawboardPdf> pdfs; 
    protected FileObject rootDir; 

    protected final DrawboardPdfFactory factory;

    public DrawboardPdfProvider(DrawboardPdfFactory factory) 
    {
        this.factory = factory;
    }
    
    @Override
    public boolean isLiteratureNoteProvider()
    {
        return false;
    }
    
    @Override
    public DrawboardPdfFactory getFactory()
    {
        return factory;
    }
    
    public abstract Map<String, DrawboardPdf> getPdfsById();
    
    public Collection<DrawboardPdf> getPdfs()
    {
        return Collections.unmodifiableCollection(getPdfsById().values());
    }

    @Override
    public DrawboardPdf getSource(String sourceID) 
    {
        return getPdfsById().get(sourceID);
    }     

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Drawboard PDF";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getPdfsById().containsKey(file.getName());
    }     
}
