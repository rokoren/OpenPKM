/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.util.Collections;
import java.util.List;
import org.asciidoctor.ast.Document;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.ParseException;

/**
 *
 * @author Rok Koren
 */
public class AsciidocParserResult extends ParserResult
{
    private final Document asciiDoc;

    private boolean valid;
    private final int extensions;

    public AsciidocParserResult(Snapshot snapshot, Document asciiDoc, int extensions) {
        super(snapshot);
        valid = true;
        this.asciiDoc = asciiDoc;
        this.extensions = extensions;        
    }

    public Document getDocument() throws ParseException {
        if (!valid) {
            throw new ParseException();
        }
        return asciiDoc;
    }

    public int getExtensions() throws ParseException {
        if (!valid) {
            throw new ParseException();
        }
        return extensions;
    }

    public boolean isValid()
    {
        return valid;
    }
    
    @Override
    protected void invalidate() {
        valid = false;
    }

    @Override
    public List<? extends org.netbeans.modules.csl.api.Error> getDiagnostics() {
        return Collections.emptyList();
    }    
}
