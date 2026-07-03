/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import java.awt.Component;
import javax.swing.JPasswordField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import openpkm.base.PasswordProvider;
import openpkm.github.GitHubPasswordProvider;
import org.openide.util.ChangeSupport;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author Rok Koren
 */
@ServiceProviders({
@ServiceProvider(service = PasswordProvider.class),    
@ServiceProvider(service = GitHubPasswordProvider.class)    
})
public class GitHubPasswordProviderImpl implements GitHubPasswordProvider, DocumentListener
{
    private JPasswordField field;
    private ChangeSupport changeSupport;
    
    private boolean changed;    
    
    private ChangeSupport getChangeSupport()
    {
        if(changeSupport == null)
        {
            changeSupport = new ChangeSupport(this);
        }
        return changeSupport;
    }
    
    private void changed()
    {
        changed = true;
        getChangeSupport().fireChange();
    }    
    
    @Override
    public String getPersonalAccessToken() 
    {
        return NbPreferences.forModule(GitHubPasswordProvider.class).get(PROP_PERSONAL_ACCESS_TOKEN, "");
    }

    @Override
    public void load()
    {
        field.setText(getPersonalAccessToken()); 
        changed = false;
    }
    
    @Override
    public void store() 
    {
        if(changed)
        {
            String password = new String(field.getPassword());
            NbPreferences.forModule(GitHubPasswordProvider.class).put(PROP_PERSONAL_ACCESS_TOKEN, password);
            changed = false;
        }                
    }

    @Override
    public String getName() 
    {
        return "github";
    }

    @Override
    public String getDisplayName() 
    {
        return "GitHub";
    }     
    
    @Override
    public Component getField()
    {
        if(field == null)
        {
            field = new JPasswordField(getPersonalAccessToken());
            field.getDocument().addDocumentListener(this);
            field.setColumns(20);            
        }
        return field;
    }  
    
    @Override
    public void addListener(ChangeListener listener) 
    {
        getChangeSupport().addChangeListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) 
    {
        getChangeSupport().removeChangeListener(listener);
    }

    @Override
    public void insertUpdate(DocumentEvent e) 
    {
        changed();
    }

    @Override
    public void removeUpdate(DocumentEvent e) 
    {
        changed();
    }

    @Override
    public void changedUpdate(DocumentEvent e) 
    {
        changed();
    }    
}
