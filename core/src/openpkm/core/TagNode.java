/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import openpkm.base.FilterTagsProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rokor
 */
public class TagNode extends AbstractNode
{
    @StaticResource()
    public static final String ICON = "openpkm/core/resources/tag_hash.png";     
    
    private final String tag;
    
    public TagNode(String tag)
    {
        super(Children.LEAF);
        this.tag = tag;
        setName(tag);
        setDisplayName(tag);
        //setDisplayName("#" + tag);
    }     
    
    public TagNode(Project project, String tag)
    {
        super(Children.LEAF, Lookups.fixed(project));
        this.tag = tag;
        setName(tag); // NOI18N
        setDisplayName("#" + tag);
    } 
    
    public String getTag()
    {
        return tag;
    }

    private Image getIcon(boolean opened) 
    {
        return ImageUtilities.loadImage(ICON);
    }

    @Override
    public Image getIcon(int type) 
    {
        return getIcon(false);
    } 
    
    @Override
    public Action[] getActions(boolean context) 
    {
        return new Action[]
        {
           new AddTagToFilter(tag),
           new RemoveTagFromFilter(tag)
        };
    }  
    
    @Override
    public Action getPreferredAction()
    {
        return new PreferredAction(tag);
    }
    
    private static final class AddTagToFilter extends AbstractAction
    {
        private final String tag;            

        public AddTagToFilter(String tag) 
        {
            super("Add Tag to Filter");
            this.tag = tag;
            setEnabled(!Lookup.getDefault().lookup(FilterTagsProvider.class).getFilterTags().contains(tag));
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            Lookup.getDefault().lookup(FilterTagsProvider.class).addTagToFilter(tag);
        }
    }  
    
    private static final class RemoveTagFromFilter extends AbstractAction
    {
        private final String tag;            

        public RemoveTagFromFilter(String tag) 
        {
            super("Remove Tag from Filter");
            this.tag = tag;
            setEnabled(Lookup.getDefault().lookup(FilterTagsProvider.class).getFilterTags().contains(tag));
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            Lookup.getDefault().lookup(FilterTagsProvider.class).removeTagFromFilter(tag);
        }
    }  
    
    private static final class PreferredAction extends AbstractAction
    {
        private final String tag;            

        public PreferredAction(String tag) 
        {
            super();
            this.tag = tag;
        }

        @Override
        public void actionPerformed(ActionEvent evt) 
        {
            FilterTagsProvider provider = Lookup.getDefault().lookup(FilterTagsProvider.class);
            if(provider.getFilterTags().contains(tag))
            {
                provider.removeTagFromFilter(tag);
            }
            else
            {
                provider.addTagToFilter(tag);
            }
        }        
    }    
}
