/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author Rok Koren
 */
public class AsciiDocTokenId implements TokenId
{
    private final String name;
    private final String primaryCategory;
    private final int id;

    public AsciiDocTokenId(String name, String primaryCategory, int id) 
    {
        this.name = name;
        this.primaryCategory = primaryCategory;
        this.id = id;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }

    @Override
    public int ordinal() {
        return id;
    }

    @Override
    public String name() {
        return name;
    } 
    
    public static Language<AsciiDocTokenId> getLanguage() 
    {
        return new AsciiDocLanguageHierarchy().language();
    }    
}
