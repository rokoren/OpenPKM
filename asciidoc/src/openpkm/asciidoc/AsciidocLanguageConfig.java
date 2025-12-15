/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.util.logging.Logger;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.textmate.lexer.api.GrammarRegistration;

/**
 *
 * @author Rok Koren
 */
@LanguageRegistration(mimeType = AsciidocLanguageConfig.MIME_TYPE)
@GrammarRegistration(mimeType = AsciidocLanguageConfig.MIME_TYPE, grammar = "resources/asciidoc.tmLanguage.json")
public class AsciidocLanguageConfig extends DefaultLanguageConfig
{  
    public static final String MIME_TYPE = "text/x-asciidoc";     
    
    private static final Logger LOG = Logger.getLogger(AsciidocLanguageConfig.class.getName());         
    
    /*
    @Override
    public Language<AsciiDocTokenId> getLexerLanguage() 
    {      
        return AsciiDocTokenId.getLanguage();
    }
    */
    
    @Override
    public Language getLexerLanguage() 
    { 
        //return Language.find(MIME_TYPE);  
        return null;
    } 

    @Override
    public String getDisplayName() {
        return "AsciiDoc"; //NOI18N
    }

    @Override
    public Parser getParser() 
    {
        return new AsciidocParser();
    }

    @Override
    public boolean hasStructureScanner() 
    {
        return true;
    }

    @Override
    public StructureScanner getStructureScanner()
    {
        return new AsciidocStructureScanner();
    }         
}
