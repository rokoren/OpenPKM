/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.text.Document;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.spi.ParseException;
import org.openide.util.Exceptions;

/**
 *
 * @author Rok Koren
 */
public class MarkdownStructureScanner implements StructureScanner
{
    private static final Logger LOG = Logger.getLogger(MarkdownStructureScanner.class.getName());    
    
    @Override
    public List<? extends StructureItem> scan(ParserResult pr) 
    {
        List<StructureItem> items = new ArrayList<>();
        if (pr instanceof MarkdownParserResult result) 
        {
            try 
            {
                MarkdownStructureItem heading1 = null;
                MarkdownStructureItem heading2 = null;
                MarkdownStructureItem heading3 = null;
                MarkdownStructureItem heading4 = null;
                MarkdownStructureItem heading5 = null;              
                for (Node child : result.getDocument().getChildren()) 
                {
                    if(child instanceof Heading)
                    {
                        Heading heading = (Heading)child;
                        switch(heading.getLevel())
                        {
                            case 1:
                            heading1 = new MarkdownStructureItem(heading, pr.getSnapshot().getSource()); 
                            items.add(heading1); 
                            break; 
                            case 2:
                            heading2 = new MarkdownStructureItem(heading, pr.getSnapshot().getSource()); 
                            if(heading1 == null)
                            {
                                items.add(heading2);                
                            } 
                            break; 
                            case 3:
                            heading3 = new MarkdownStructureItem(heading, pr.getSnapshot().getSource()); 
                            if(heading1 == null && heading2 == null)
                            {
                                items.add(heading3);                
                            } 
                            break; 
                            case 4:
                            heading4 = new MarkdownStructureItem(heading, pr.getSnapshot().getSource()); 
                            if(heading1 == null && heading2 == null && heading3 == null)
                            {
                                items.add(heading4);                
                            } 
                            break; 
                            case 5:
                            heading5 = new MarkdownStructureItem(heading, pr.getSnapshot().getSource()); 
                            if(heading1 == null && heading2 == null && heading3 == null && heading4 == null)
                            {
                                items.add(heading5);                
                            } 
                            break; 
                            case 6:
                            MarkdownStructureItem heading6 = new MarkdownStructureItem(heading, pr.getSnapshot().getSource()); 
                            if(heading1 == null && heading2 == null && heading3 == null && heading5 == null)
                            {
                                items.add(heading6);                
                            } 
                            break;                  
                        }                                          
                    }                                                  
                }              
            }
            catch (ParseException ex) 
            {
                LOG.warning(ex.getMessage());
            }
        }
        return items;
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult pr) 
    {
        Map<String, List<OffsetRange>> foldsByType = null;
        if (pr instanceof MarkdownParserResult result) 
        {
            try 
            {
                //Document document = result.getSnapshot().getSource().getDocument(false);                 
                foldsByType = Collections.singletonMap("comments", getSectionFolds(result.getDocument().getFirstChild()));
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
    
    private List<OffsetRange> getSectionFolds(Node node)
    {
        List<OffsetRange> sectionFolds = new ArrayList<OffsetRange>();
        if(node != null)
        {
            Node next = node.getNextAnyNot(Heading.class);
            while(next != null)
            {              
                sectionFolds.add(new OffsetRange(next.getStartOffset(), next.getEndOffset()));             
                next = next.getNextAnyNot(Heading.class);
            }              
        }       
        return sectionFolds;
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(false, false);
    }     
}
