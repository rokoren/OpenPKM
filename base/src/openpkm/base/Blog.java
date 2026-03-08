/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author Rok Koren
 */
public interface Blog
{
    public static final String PROP_BLOG_ID = "blog.id";
    public static final String PROP_URL     = "url"; 
    public static final String PROP_FAVICON = "favicon";     

    String getBlogID(); 
    String getUrl();
    String getFavicon();    
}
