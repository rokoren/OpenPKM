/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.netbeans.modules.spellchecker.spi.language.TokenListProvider;

/**
 *
 * @author Rok Koren
 */
@MimeRegistration(mimeType = "text/x-asciidoc", service = TokenListProvider.class, position = 1000)
public class AsciidocTokenListProvider implements TokenListProvider
{
    @Override
    public TokenList findTokenList(Document doc) 
    {
        if (doc instanceof BaseDocument) 
        {
            BaseDocument baseDoc = (BaseDocument) doc;
            final Object mimeType = baseDoc.getProperty("mimeType");
            if (mimeType.equals("text/x-asciidoc")) {
                return new AsciidocTokenList(doc);
            }
        }
        return null;
    }    
}
