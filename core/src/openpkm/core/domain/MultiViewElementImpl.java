/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import java.awt.BorderLayout;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import openpkm.domain.Blog;
import openpkm.jcef.CefClientProvider;
import org.cef.browser.CefBrowser;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public class MultiViewElementImpl extends JPanel implements MultiViewElement
{
    private static final Logger LOG = Logger.getLogger(MultiViewElementImpl.class.getName());           

    private final Blog blog;
    private final boolean isOffscreenRendered;
    
    private CefBrowser browser; 
    private JToolBar toolbar;  
    
    private transient MultiViewElementCallback callback;     

    public MultiViewElementImpl(Blog blog, boolean isOffscreenRendered) 
    {
        this.blog = blog;
        this.isOffscreenRendered = isOffscreenRendered;
        setLayout(new BorderLayout());
    }                

    @Override
    public UndoRedo getUndoRedo() 
    {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) 
    {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() 
    {
        return CloseOperationState.STATE_OK;
    } 

    @Override
    public JComponent getVisualRepresentation() 
    {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() 
    {
        if(toolbar == null)
        {
            toolbar = new JToolBar();
            /*
            JCheckBox watchLater = new JCheckBox("Watch Later");
            watchLater.setFocusable(false);
            watchLater.addItemListener(this);
            toolbar.add(watchLater);
            */
        }
        return toolbar;
    }

    @Override
    public Action[] getActions() 
    {
        return new Action[0];
    }

    @Override
    public Lookup getLookup() 
    {
        return blog.getLookup();
    }        

    @Override
    public void componentOpened() 
    {             
        if(browser == null)
        {
            CefClientProvider provider = Lookup.getDefault().lookup(CefClientProvider.class);
            if(provider != null)
            {
                try
                {
                    browser = provider.getCefClient().createBrowser(blog.getUrl(), isOffscreenRendered, false);
                    add(browser.getUIComponent(), BorderLayout.CENTER);
                }
                catch(Exception e)
                {
                    LOG.warning(e.getMessage());
                }
            }                
        }            
    }

    @Override
    public void componentClosed() 
    {
        if(browser != null)
        {
            browser.close(true);
        }            
    }

    @Override
    public void componentShowing() 
    {              
    }

    @Override
    public void componentHidden() 
    {            
    }

    @Override
    public void componentActivated() 
    { 
        browser.setFocus(true);            
    }

    @Override
    public void componentDeactivated() 
    { 
        browser.setFocus(false);            
    }    
}
