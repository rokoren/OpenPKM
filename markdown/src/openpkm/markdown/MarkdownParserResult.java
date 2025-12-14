/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.ParseException;

/**
 *
 * @author Rok Koren
 */
public class MarkdownParserResult extends ParserResult
{
    private final Document document;

    private boolean valid;
    private final int extensions;

    public MarkdownParserResult(Snapshot snapshot, Document document, int extensions) 
    {
        super(snapshot);
        valid = true;
        this.document = document;
        this.extensions = extensions;
    }

    public Node getDocument() throws ParseException 
    {
        if (!valid) 
        {
            throw new ParseException();
        }
        return document;
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
