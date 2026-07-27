/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.domain;

import openpkm.base.DescriptionProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.StateSupport;
import openpkm.base.TitleProvider;

/**
 *
 * @author Rok Koren
 */
public interface Blog extends Source, PropertiesProvider, StateSupport, TitleProvider, DescriptionProvider
{
    String PROP_URL     = "url"; 
    String PROP_FAVICON = "favicon";     

    String getUrl();
    String getFavicon();    
}
