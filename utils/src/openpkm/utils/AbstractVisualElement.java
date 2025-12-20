/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.Action;
import javax.swing.JComponent;
import openpkm.base.HtmlFilesProvider;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 *
 * @author Rok Koren
 */
public abstract class AbstractVisualElement extends JFXPanel implements MultiViewElement
{
    private final Lookup lkp;     
    
    protected WebView browser;
    protected transient MultiViewElementCallback callback;

    public AbstractVisualElement(Lookup lkp) 
    {
        this.lkp = lkp;
    } 
    
    public abstract String getThemeUrl(FileObject file, Project project);

    // Variables declaration - do not modify                     
    // End of variables declaration                   
    @Override
    public JComponent getVisualRepresentation() 
    {
        return this;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }

    @Override
    public Lookup getLookup() 
    {
        return lkp;
    }

    @Override
    public void componentOpened() 
    {
        if(browser ==  null)
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() 
                {
                    browser = new WebView();
                    Scene scene = new Scene(browser);
                    /*
                    if (isDarkLaF)
                    {
                        scene.getStylesheets().add(getClass().getResource("/openpkm/asciidoc/resources/javafx-nb-dark.css").toString());
                        // Loading default content to force apply a content with css for dark background
                        //browser.getEngine().loadContent(readLoadingPage());
                    }              
                    */
                    setScene(scene);                    
                }
            });            
        }
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() 
    {  
        DataObject data = lkp.lookup(DataObject.class);        
        Project project = FileOwnerQuery.getOwner(data.getPrimaryFile());        
        HtmlFilesProvider provider = project.getLookup().lookup(HtmlFilesProvider.class);             
        if(provider != null)
        {
            FileObject file = provider.getDataFile(data.getPrimaryFile().getName());
            if(file != null)
            {
                final String urlFile = file.toURI().toString();
                final String urlTheme = getThemeUrl(data.getPrimaryFile(), project);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() 
                    {
                        WebEngine webEngine = browser.getEngine();
                        if(urlTheme != null)
                        {
                            webEngine.setUserStyleSheetLocation(urlTheme);                            
                        }
                        webEngine.load(urlFile);  
                    }
                });
            }             
        }              
    }

    @Override
    public void componentDeactivated()
    {
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
}
