/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.util.Map;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.WebPage;
import openpkm.base.WebPageProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public abstract class WebSourceProvider implements SourceProvider<WebPage>
{
    protected static final String ROOT_FOLDER = "web";       

    protected Map<String, WebPage> links; 
    protected FileObject rootDir; 

    protected final WebPageProvider provider;

    public WebSourceProvider(WebPageProvider provider) 
    {
        this.provider = provider;
    } 
    
    public WebPageProvider getWebPageProvider()
    {
        return provider;
    }
    
    public abstract Map<String, WebPage> getLinks();

    @Override
    public Source getSource(String sourceID) 
    {
        return getLinks().get(sourceID);
    }                                  

    @Override
    public String getName() 
    {
        return ROOT_FOLDER;
    }

    @Override
    public String getDisplayName() 
    {
        return "Web";
    }

    @Override
    public boolean contains(FileObject file) 
    {
        return getLinks().containsKey(file.getName());
    }     
}
