/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.asciidoctor.ast.StructuralNode;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.parsing.api.Source;
import org.openide.text.NbDocument;

/**
 *
 * @author Rok Koren
 */
public class AsciidocStructureItemChild extends AsciidocStructureItem
{
    private final AsciidocStructureItem parent;
    
    public AsciidocStructureItemChild(StructuralNode node, Source source, AsciidocStructureItem parent) 
    {
        super(node, source);
        this.parent = parent;
    } 
    
    @Override
    public String getSortText() 
    {
        return String.format("%08d", parent.getNestedItems().indexOf(this));
    }    
    
    @Override
    protected Integer getEndPosition1() 
    {              
        if(endPosition == null)
        {
            Document document = source.getDocument(false); 
            List<? extends StructureItem> items = parent.getNestedItems();
            if(items.isEmpty())
            {
                List<StructuralNode> blocks = node.getBlocks();
                if(blocks.isEmpty())
                {
                    endPosition = getStartPosition().intValue() + node.getContent().toString().length();                    
                }
                else
                {
                    int position = getStartPosition().intValue();
                    for (StructuralNode block : blocks) 
                    {
                        int line = block.getSourceLocation().getLineNumber() - 1;                    
                        int end = NbDocument.findLineOffset ((StyledDocument) document, line) + block.getContent().toString().length();
                        if(end > position)
                        {
                            position = end;
                        }                                                        
                    } 
                    endPosition = position;                    
                }
            }
            else
            {
                int index = items.indexOf(this);
                try
                {
                    StructureItem next = parent.getNestedItems().get(index + 1);         
                    endPosition = (int)next.getPosition() - 1;
                }
                catch(Exception e)
                {
                    List<StructuralNode> blocks = node.getBlocks();
                    if(blocks.isEmpty())
                    {
                        endPosition = getStartPosition().intValue() + + node.getContent().toString().length();                    
                    }
                    else
                    {
                        int position = getStartPosition().intValue();
                        for (StructuralNode block : blocks) 
                        {
                            int line = block.getSourceLocation().getLineNumber() - 1;                    
                            int end = NbDocument.findLineOffset ((StyledDocument) document, line) + block.getContent().toString().length();
                            if(end > position)
                            {
                                position = end;
                            }                                                        
                        } 
                        endPosition = position;                    
                    }
                }                 
            }                                    
        }
        return endPosition;
    }     
}
