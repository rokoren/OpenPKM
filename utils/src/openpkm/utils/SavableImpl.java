/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.beans.PropertyChangeEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.TitleProvider;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public class SavableImpl extends AbstractSavable
{    
    private final SourceProvider provider;
    private final PropertyChangeEvent evt;

    public SavableImpl(SourceProvider provider, PropertyChangeEvent evt) 
    {
        this.provider = provider;
        this.evt = evt;
        register();
    }

    @Override
    protected String findDisplayName() 
    {
        StringBuilder sb = new StringBuilder();
        TitleProvider title1 = provider.getLookupProvider().getLookup().lookup(TitleProvider.class);
        if(title1 != null)
        {
            sb.append(title1.getTitle());
        }
        if(evt.getSource() instanceof TitleProvider)
        {            
            TitleProvider title2 = (TitleProvider)evt.getSource();
            if(!sb.isEmpty())
            {
                sb.append(": ");
            }
            sb.append(title2.getTitle());
        }
        sb.append(" (");
        sb.append(evt.getPropertyName());
        sb.append(")");
        return sb.toString();
    }

    @Override
    protected void handleSave() throws IOException 
    {
        Source source = (Source)evt.getSource();
        FileObject file = provider.getRootFolder().getFileObject(source.getSourceID(), PropertiesProvider.EXTENSION);
        if(file == null)
        {
            throw new FileNotFoundException("File Not Found");
        }
        OutputStream os = file.getOutputStream();
        source.save(os, evt.getPropertyName());
        os.close(); 
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SavableImpl) {
            return ((SavableImpl) other).evt.getSource().equals(evt.getSource());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return evt.getSource().hashCode();
    }    
}
