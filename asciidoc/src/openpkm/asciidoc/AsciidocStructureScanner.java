/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.StructuralNode;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.spi.ParseException;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author Rok Koren
 */
public class AsciidocStructureScanner implements StructureScanner
{
    private static final Logger LOG = Logger.getLogger(AsciidocStructureScanner.class.getName());    
    
    @Override
    public List<? extends StructureItem> scan(ParserResult pr) 
    {
        List<StructureItem> items = new ArrayList<>();
        if (pr instanceof AsciidocParserResult result) 
        {
            try 
            {
                StructureItem item = new AsciidocStructureItem(result.getDocument(), pr.getSnapshot().getSource()); 
                items.add(item);
                /*
                int extensions = result.getExtensions();
                for (StructuralNode node : asciiDoc.getBlocks())
                {
                    if (node instanceof org.asciidoctor.ast.Section section) 
                    {
                        StructureItem item = new AsciidocStructureItem(section); 
                        items.add(item);                        
                    }   
                }
                */
            }
            catch (ParseException ex) {
                //Exceptions.printStackTrace(ex);
            }
        }
        return items;
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult pr) 
    {
        Map<String, List<OffsetRange>> foldsByType = null;
        if (pr instanceof AsciidocParserResult result) 
        {
            try 
            {
                Document document = result.getSnapshot().getSource().getDocument(false);                 
                foldsByType = Collections.singletonMap("comments", getSectionFolds(document, result.getDocument()));
            }
            catch (ParseException ex) 
            {
                Exceptions.printStackTrace(ex);
            }
        }
        if (foldsByType == null) {
            foldsByType = Collections.emptyMap();
        }
        return foldsByType;
    }
    
    private List<OffsetRange> getSectionFolds(Document document, StructuralNode node)
    {
        List<OffsetRange> sectionFolds = new ArrayList<OffsetRange>();
        if(node instanceof Block block)
        {
            int line = node.getSourceLocation().getLineNumber() - 1;
            int start = NbDocument.findLineOffset ((StyledDocument) document, line);  
            int end = start + block.getSource().length();  
            sectionFolds.add(new OffsetRange(start, end));            
        }
        else if(node instanceof org.asciidoctor.ast.List list)
        {
            int line = node.getSourceLocation().getLineNumber() - 1;
            int start = NbDocument.findLineOffset ((StyledDocument) document, line);  
            int end = start; 
            for(StructuralNode listItem : list.getItems())
            {
                int listItemLine = listItem.getSourceLocation().getLineNumber();
                int listItemStart = NbDocument.findLineOffset ((StyledDocument) document, listItemLine);   
                if(listItemStart > end)
                {
                    end = listItemStart;
                }
            }
            sectionFolds.add(new OffsetRange(start, end));                
        }
        for(StructuralNode block : node.getBlocks())
        {
            sectionFolds.addAll(getSectionFolds(document, block));
        }
        return sectionFolds;
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(false, false);
    }    
}
