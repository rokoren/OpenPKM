/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Rok Koren
 */
public class AsciiDocLexer implements Lexer<AsciiDocTokenId>
{
    private LexerRestartInfo<AsciiDocTokenId> info;
    private AsciiDocParserTokenManager javaParserTokenManager;

    public AsciiDocLexer(LexerRestartInfo<AsciiDocTokenId> info) {
        this.info = info;
        AsciiDocCharStream stream = new AsciiDocCharStream(info.input());
        javaParserTokenManager = new AsciiDocParserTokenManager(stream);
    }

    @Override
    public org.netbeans.api.lexer.Token<AsciiDocTokenId> nextToken() 
    {
        try
        {
            Token token = javaParserTokenManager.getNextToken();
            if (token == null || info.input().readLength() < 1) {
                return null;
            }  
            return info.tokenFactory().createToken(AsciiDocLanguageHierarchy.getToken(token.kind));              
        }
        catch(Exception e)
        {
            
        }
        return null;
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }   
}
