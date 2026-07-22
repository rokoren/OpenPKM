/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.domain;

import openpkm.base.DescriptionProvider;
import openpkm.base.Source;
import openpkm.base.TitleProvider;

/**
 *
 * @author rok
 */
public interface HomePage extends Source, TitleProvider, DescriptionProvider
{
    String PROP_URL     = "url"; 
    String PROP_FAVICON = "favicon";     

    String getHomePageID();
    String getUrl();
    String getFavicon();      
}
