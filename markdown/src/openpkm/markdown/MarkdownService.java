/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.logging.Logger;

/**
 *
 * @author Rok Koren
 */
public class MarkdownService 
{
    private static final Logger LOG = Logger.getLogger(MarkdownService.class.getName()); 

    private static MarkdownService service;    
    
    private final Parser parser;   
    private final HtmlRenderer renderer;

    public MarkdownService() 
    {
        //MutableDataSet options = new MutableDataSet();
        
        MutableDataHolder options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);        
        
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build(); 
    }
    
    public Parser getParser()
    {
        return parser;
    }   
    
    public HtmlRenderer getRenderer()
    {
        return renderer;
    }
    
    public static synchronized MarkdownService getDeafult()
    {
        if(service == null)
        {
            service = new MarkdownService();
        }
        return service;
    }    
}
