/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Rok Koren
 */
public class AsciiDocLanguageHierarchy extends LanguageHierarchy<AsciiDocTokenId>
{
    private static List<AsciiDocTokenId> tokens;
       
    private static Map<Integer, AsciiDocTokenId> idToToken;

    private static void init() {
        tokens = Arrays.asList(new AsciiDocTokenId[]{
        new AsciiDocTokenId("EOF", "whitespace", 0),
        new AsciiDocTokenId("HEADER1", "identifier", 1),
        new AsciiDocTokenId("HEADER2", "identifier", 2),
        new AsciiDocTokenId("HEADER3", "identifier", 3),
        new AsciiDocTokenId("BOLD", "bold", 4),
        new AsciiDocTokenId("ITALIC", "italic", 5),
        new AsciiDocTokenId("LIST_ITEM", "operator", 6),
        new AsciiDocTokenId("NEWLINE", "whitespace", 7),
        new AsciiDocTokenId("WHITESPACE", "whitespace", 8),  
        new AsciiDocTokenId("INTEGER_LITERAL", "literal", 9),
        new AsciiDocTokenId("HEX_LITERAL", "literal", 10),
        new AsciiDocTokenId("HEX_DIGIT", "number", 11),
        new AsciiDocTokenId("OCTAL_LITERAL", "literal", 12),
        new AsciiDocTokenId("REAL_LITERAL", "literal", 13),
        new AsciiDocTokenId("EXPONENT", "number", 14),
        new AsciiDocTokenId("SIMPLE_STRING_LITERAL", "literal", 15),   
        new AsciiDocTokenId("ESCAPED_CHAR", "literal", 16),
        new AsciiDocTokenId("PRINT_CHAR", "literal", 17),
        new AsciiDocTokenId("ENCODED_STRING_LITERAL", "literal", 18),
        new AsciiDocTokenId("BINARY_LITERAL", "literal", 19),
        new AsciiDocTokenId("OCTET", "number", 20),
        new AsciiDocTokenId("ENCODED_CHARACTER", "literal", 21),
        new AsciiDocTokenId("SIMPLE_ID", "literal", 22), 
        new AsciiDocTokenId("LETTER", "literal", 23),
        new AsciiDocTokenId("DIGIT", "literal", 24),
        new AsciiDocTokenId("LPAREN", "operator", 25),
        new AsciiDocTokenId("RPAREN", "operator", 26),
        new AsciiDocTokenId("LBRACE", "operator", 27),
        new AsciiDocTokenId("RBRACE", "operator", 28),
        new AsciiDocTokenId("LBRACKET", "operator", 29),       
        new AsciiDocTokenId("RBRACKET", "operator", 30),
        new AsciiDocTokenId("SEMICOLON", "literal", 31),
        new AsciiDocTokenId("COLON", "literal", 32),
        new AsciiDocTokenId("COMMA", "literal", 33),
        new AsciiDocTokenId("DOT", "literal", 34)});
        idToToken = new HashMap<Integer, AsciiDocTokenId>();
        for (AsciiDocTokenId token : tokens) {
            idToToken.put(token.ordinal(), token);
        }
    }

    static synchronized AsciiDocTokenId getToken(int id) {
        if (idToToken == null) {
            init();
        }
        return idToToken.get(id);
    }

    @Override
    protected synchronized Collection<AsciiDocTokenId> createTokenIds() {
        if (tokens == null) {
            init();
        }
        return tokens;
    }

    @Override
    protected synchronized Lexer<AsciiDocTokenId> createLexer(LexerRestartInfo<AsciiDocTokenId> info)
    {
        return new AsciiDocLexer(info);
    }

    @Override
    protected String mimeType() 
    {
        return AsciidocLanguageConfig.MIME_TYPE;
    }  
}
