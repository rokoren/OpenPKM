/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author Rok Koren
 */
public interface HomePage extends TitleProvider, DescriptionProvider
{
    public static final String PROP_HOME_PAGE_ID = "home.page.id";
    public static final String PROP_URL          = "url"; 
    public static final String PROP_FAVICON      = "favicon"; 

    String getHomePageID();
    String getUrl();
    String getFavicon();
}
