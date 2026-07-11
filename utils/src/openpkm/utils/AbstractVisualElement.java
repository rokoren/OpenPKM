/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import openpkm.base.HomeProvider;
import openpkm.base.HtmlFilesProvider;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Rok Koren
 */
public abstract class AbstractVisualElement extends JFXPanel implements HomeProvider, MultiViewElement, UndoRedo
{
    private final Lookup lkp;     
    
    protected WebView browser;
    protected transient MultiViewElementCallback callback;
    
    private final ChangeSupport changeSupport = new ChangeSupport(this);

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
        return new ProxyLookup(lkp, Lookups.singleton(this));
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
                    
                    browser.getEngine().getHistory().currentIndexProperty().addListener((obs, oldValue, newValue) -> {
                        changeSupport.fireChange();
                    });
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
    public void reloadHome() 
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() 
                    {
                        WebEngine webEngine = browser.getEngine();
                        webEngine.load(urlFile);  
                    }
                });
            }             
        } 
    }     

    @Override
    public UndoRedo getUndoRedo() 
    {
        return this;
    }
    
    @Override
    public boolean canUndo() 
    {
        if(browser == null)
        {
            return false;
        }
        return browser.getEngine().getHistory().getCurrentIndex() > 0;
    }

    @Override
    public void undo() throws CannotUndoException
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() 
            {
                browser.getEngine().getHistory().go(-1);
            }
        });                 
    }

    @Override
    public boolean canRedo() 
    {
        if(browser == null)
        {
            return false;
        }        
        WebHistory history = browser.getEngine().getHistory();
        return history.getCurrentIndex() < history.getEntries().size() - 1;
    }

    @Override
    public void redo() throws CannotRedoException 
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() 
            {
                browser.getEngine().getHistory().go(1);
            }
        });                 
    }

    @Override
    public void addChangeListener(ChangeListener cl) 
    {
        changeSupport.addChangeListener(cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) 
    {
        changeSupport.removeChangeListener(cl);
    }

    @Override
    public String getUndoPresentationName() 
    {
        return "Back";
    }

    @Override
    public String getRedoPresentationName() 
    {
        return "Forward";
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
