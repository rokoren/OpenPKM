/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import openpkm.jcef.CefAppProvider;
import openpkm.youtube.YouTubeCefClientProvider;
import openpkm.youtube.YouTubeVideo;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefRequestHandler;
import org.cef.handler.CefRequestHandlerAdapter;
import org.cef.handler.CefResourceRequestHandler;
import org.cef.handler.CefResourceRequestHandlerAdapter;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=YouTubeCefClientProvider.class)
public class YouTubeCefClientProviderImpl implements YouTubeCefClientProvider
{
    private CefClient client;

    @Override
    public CefBrowser getBrowser(YouTubeVideo video) throws Exception
    {
        String url = YouTubeVideo.YOUTUBE_URL + "embed/" + video.getVideoID() + "?rel=0&modestbranding=1&playsinline=1&autoplay=1&origin=https://www.youtube.com";
        CefClient client = getCefClient();
        if(client != null)
        {
            return client.createBrowser(url, false, false);            
        }
        return null;
    }

    @Override
    public CefClient getCefClient() throws Exception 
    {
        if(client == null)
        {    
            CefAppProvider provider = Lookup.getDefault().lookup(CefAppProvider.class);
            if(provider != null)
            {
                CefRequestHandler requestHandler = new CefRequestHandlerAdapter() 
                {
                    @Override
                    public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser,
                                                                               CefFrame frame,
                                                                               CefRequest request,
                                                                               boolean isNavigation,
                                                                               boolean isDownload,
                                                                               String requestInitiator,
                                                                               BoolRef disableDefaultHandling) {
                        return new CefResourceRequestHandlerAdapter() {
                            @Override
                            public boolean onBeforeResourceLoad(CefBrowser browser,
                                                                CefFrame frame,
                                                                CefRequest request) {
                                String url = request.getURL();
                                //if (url.contains("youtube.com")) {
                                    /*
                                    request.setHeaderByName("Referer", "https://www.youtube.com", true);
                                    request.setHeaderByName("Origin", "https://www.youtube.com", true);
                                    request.setHeaderByName("Referrer-Policy", "strict-origin-when-cross-origin", true); 
                                    request.setHeaderByName("refererpolicy", "cross-origin-with-strict-origin", true); 
                                    */   
                                    request.setHeaderByName("Origin", "https://www.youtube.com", true);
                                    request.setReferrer(YouTubeVideo.YOUTUBE_URL + "embed/", CefRequest.ReferrerPolicy.REFERRER_POLICY_DEFAULT);
                                    //request.setHeaderByName("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36", true);                            
                                //}
                                return false; // nadaljuj z nalaganjem
                            }
                        };
                    }
                };

                client = provider.getApp().createClient();  
                client.addRequestHandler(requestHandler);   
            }           
        }
        return client;
    }

    @Override
    public void dispose() 
    {
        if(client != null)
        {
            client.dispose();
        }
    }
}
