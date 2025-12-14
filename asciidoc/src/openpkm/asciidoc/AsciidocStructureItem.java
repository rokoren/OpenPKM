/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.asciidoctor.ast.Section;
import org.asciidoctor.ast.StructuralNode;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Source;
import org.openide.filesystems.FileObject;
import org.openide.text.NbDocument;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public class AsciidocStructureItem implements StructureItem, ElementHandle
{      
    @StaticResource()
    public static final String ICON_HEADING_1 = "openpkm/asciidoc/resources/text_heading_1.png";   
    
    @StaticResource()
    public static final String ICON_HEADING_2 = "openpkm/asciidoc/resources/text_heading_2.png"; 
    
    @StaticResource()
    public static final String ICON_HEADING_3 = "openpkm/asciidoc/resources/text_heading_3.png";  

    @StaticResource()
    public static final String ICON_HEADING_4 = "openpkm/asciidoc/resources/text_heading_4.png"; 
    
    @StaticResource()
    public static final String ICON_HEADING_5 = "openpkm/asciidoc/resources/text_heading_5.png";       
    
    private static final Logger LOG = Logger.getLogger(AsciidocStructureItem.class.getName());
    
    protected final StructuralNode node;
    protected final Source source;
    
    private List<StructureItem> nestedItems;
    protected Integer startPosition, endPosition;

    public AsciidocStructureItem(StructuralNode node, Source source) 
    {
        this.node = node;  
        this.source = source;              
    }
    
    public Source getSource()
    {
        return source;
    }
    
    public StructuralNode getNode()
    {
        return node;
    }
    
    private Section findSectionNode(Document document, int level) {
        return (Section) node.findBy(Collections.singletonMap("context", ":section"))
                .stream()
                .filter(n -> n.getLevel() == level)
                .findFirst()
                .get();
    } 
    
    protected Integer getStartPosition() 
    {
        if(startPosition == null)
        {
            Document document = source.getDocument(false);        
            startPosition = NbDocument.findLineOffset ((StyledDocument) document, node.getSourceLocation().getLineNumber() - 1);             
        }
        return startPosition;
    }  
    
    protected Integer getEndPosition1() 
    {              
        if(endPosition == null)
        {
            Document document = source.getDocument(false); 
            endPosition = document.getEndPosition().getOffset();                         
        }
        return endPosition;
    }     

    @Override
    public String getName() 
    {
        return node.getTitle();
    }

    @Override
    public String getSortText() 
    {
        return getName();
    }

    @Override
    public String getHtml(HtmlFormatter formatter) 
    {
        if(node.getLevel() == 0)
        {
            formatter.appendText("Title ");            
        }
        if(node.getTitle() != null)
        {         
            formatter.appendHtml("<font color='!controlShadow'>");
            formatter.appendText(node.getTitle());     
            formatter.appendHtml("</font>");            
        }
        return formatter.getText();
    }

    @Override
    public ElementHandle getElementHandle() 
    {
        return this;
    }

    @Override
    public ElementKind getKind() 
    {
        return ElementKind.OTHER;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return Collections.emptySet();
    }

    @Override
    public boolean isLeaf() 
    {
        return getNestedItems().isEmpty();
    }

    @Override
    public List<? extends StructureItem> getNestedItems() 
    { 
        if(nestedItems == null)
        {
            nestedItems = new ArrayList<>();
            List<StructuralNode> blocks = node.getBlocks();
            for (StructuralNode block : blocks) 
            {
                if(block instanceof Section)
                {
                    StructureItem item = new AsciidocStructureItemChild(block, source, this); 
                    nestedItems.add(item);                      
                }                                 
            }             
        }
        return nestedItems;
    }

    @Override
    public long getPosition() 
    {
        return getStartPosition().longValue();
    }

    @Override
    public long getEndPosition() 
    { 
        return getEndPosition1().longValue();
    }

    @Override
    public ImageIcon getCustomIcon() 
    {
        ImageIcon icon = null;
        switch(node.getLevel())
        {
            case 0:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_1, false);
            break;
            case 1:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_2, false);
            break; 
            case 2:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_3, false);
            break; 
            case 3:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_4, false);
            break;  
            case 4:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_5, false);
            break;             
        }
        return icon;
    }

    /*
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof AsciidocStructureItem item)
        {
            if(item.getSource().equals(getSource()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.source);
        return hash;
    }
    */
    
    @Override
    public FileObject getFileObject() 
    {
        return source.getFileObject();
    }

    @Override
    public String getMimeType() 
    {
        return source.getMimeType();
    }

    @Override
    public String getIn() 
    {
        return source.getFileObject().getName();
    }

    @Override
    public boolean signatureEquals(ElementHandle handle) 
    {
        return handle.getFileObject().equals(getFileObject());
    }

    @Override
    public OffsetRange getOffsetRange(ParserResult pr) 
    {        
        return new OffsetRange(getStartPosition().intValue(), getEndPosition1().intValue());
    }    
}
