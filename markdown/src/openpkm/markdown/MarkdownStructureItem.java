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
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
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
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public class MarkdownStructureItem implements StructureItem, ElementHandle
{ 
    @StaticResource()
    public static final String ICON_HEADING_1 = "openpkm/markdown/resources/text_heading_1.png";   
    
    @StaticResource()
    public static final String ICON_HEADING_2 = "openpkm/markdown/resources/text_heading_2.png"; 
    
    @StaticResource()
    public static final String ICON_HEADING_3 = "openpkm/markdown/resources/text_heading_3.png";      
    
    @StaticResource()
    public static final String ICON_HEADING_4 = "openpkm/markdown/resources/text_heading_4.png";   
    
    @StaticResource()
    public static final String ICON_HEADING_5= "openpkm/markdown/resources/text_heading_5.png"; 
    
    @StaticResource()
    public static final String ICON_HEADING_6 = "openpkm/markdown/resources/text_heading_6.png";       
    
    private static final Logger LOG = Logger.getLogger(MarkdownStructureItem.class.getName());
    
    private OffsetRange range;    
    private List<MarkdownStructureItem> nestedItems;      
    
    private final Heading heading;
    private final Source source;  

    public MarkdownStructureItem(Heading heading, Source source) 
    {
        this.heading = heading;  
        this.source = source;                                       
    }
    
    public Source getSource()
    {
        return source;
    }
    
    public Node getNode()
    {
        return heading;
    }

    @Override
    public String getName() 
    {
        return heading.getAnchorRefText();
    }

    @Override
    public String getSortText() 
    {      
        return heading.getAnchorRefText();
    }

    @Override
    public String getHtml(HtmlFormatter formatter) 
    {  
        if(heading.getLevel() == 1)
        {
            formatter.appendText("Title ");            
        }
        formatter.appendHtml("<font color='!controlShadow'>");
        formatter.appendText(heading.getAnchorRefText());     
        formatter.appendHtml("</font>");  
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
    public List<MarkdownStructureItem> getNestedItems() 
    { 
        if(nestedItems == null)
        {
            nestedItems = new ArrayList<>();
            Node node = heading.getNextAny(Heading.class);
            while(node instanceof Heading next && next.getLevel() > heading.getLevel())
            {
                MarkdownStructureItem item = new MarkdownStructureItemChild(next, source, this);
                nestedItems.add(item);
                node = next.getNextAny(Heading.class);
            }            
        }
        return nestedItems;
    }

    @Override
    public long getPosition() 
    {
        return getOffsetRange(null).getStart();
    }

    @Override
    public long getEndPosition() 
    {             
        return getOffsetRange(null).getEnd();
    }

    @Override
    public ImageIcon getCustomIcon() 
    {
        ImageIcon icon = null;
        switch(heading.getLevel())
        {
            case 1:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_1, true);
            break; 
            case 2:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_2, true);
            break; 
            case 3:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_3, true);
            break; 
            case 4:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_4, true);
            break; 
            case 5:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_5, true);
            break; 
            case 6:
            icon = ImageUtilities.loadImageIcon(ICON_HEADING_6, true);
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
        if(range == null)
        {
            Node node = heading.getNextAny(Heading.class);
            while(node instanceof Heading next && next.getLevel() > heading.getLevel())
            {
                node = next.getNextAny(Heading.class);
            }  

            int end = heading.getEndOffset();
            if(node == null)
            {
                end = heading.getDocument().getEndOffset();     
            }
            else
            {
                end = node.getPrevious().getEndOffset();
            }
            
            range = new OffsetRange(heading.getStartOffset(), end);              
        }
        return range;
    }     
}
