/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.domain;

import openpkm.base.DescriptionProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.TitleProvider;

/**
 *
 * @author Rok Koren
 */
public interface Blog extends Source, PropertiesProvider, TitleProvider, DescriptionProvider
{
    String PROP_URL       = "url"; 
    String PROP_FAVICON   = "favicon";     
    String PROP_FILE_NAME = "file.name"; 
    
    String getFileName();         
    String getUrl();
    String getFavicon();    
}
