/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.youtube;

import openpkm.jcef.CefClientProvider;
import org.cef.browser.CefBrowser;

/**
 *
 * @author Rok Koren
 */
public interface YouTubeCefClientProvider extends CefClientProvider
{
    CefBrowser getBrowser(YouTubeVideo video) throws Exception;
    CefBrowser getBrowser(YouTubeChannel channel) throws Exception;
}
