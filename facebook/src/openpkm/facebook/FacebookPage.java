/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.facebook;

import openpkm.base.DescriptionProvider;
import openpkm.base.TitleProvider;

/**
 *
 * @author Rok Koren
 */
public interface FacebookPage extends TitleProvider, DescriptionProvider
{
    String FACEBOOK_URL = "https://facebook.com/";
    
    String PROP_PAGE_ID   = "page.id";
    String PROP_LINK      = "link";
    String PROP_FAN_COUNT = "fan.count";
    String PROP_PICTURE   = "picture";
    
    String getPageID();
    /*
    String getLink();
    String getFanCount();
    */
    String getPicture();
}
