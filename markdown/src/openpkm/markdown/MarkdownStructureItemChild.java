/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import com.vladsch.flexmark.ast.Heading;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author rok
 */
public class MarkdownStructureItemChild extends MarkdownStructureItem
{
    private final MarkdownStructureItem parent;
    
    public MarkdownStructureItemChild(Heading heading, Source source, MarkdownStructureItem parent) 
    {
        super(heading, source);
        this.parent = parent;
    } 
    
    @Override
    public String getSortText() 
    {
        return String.format("%08d", parent.getNestedItems().indexOf(this));
    }       
}
