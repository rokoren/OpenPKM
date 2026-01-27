/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core;

import java.awt.Component;
import javax.swing.JPasswordField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import openpkm.base.PasswordProvider;
import openpkm.facebook.FacebookPasswordProvider;
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
@ServiceProvider(service = FacebookPasswordProvider.class)    
})
public class FacebookPasswordProviderImpl implements FacebookPasswordProvider, DocumentListener
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
    public String getAccessToken() 
    {
        return NbPreferences.forModule(FacebookPasswordProvider.class).get(PROP_ACCESS_TOKEN, "");
    }

    @Override
    public void load()
    {
        field.setText(getAccessToken()); 
        changed = false;
    }
    
    @Override
    public void store() 
    {
        if(changed)
        {
            String password = new String(field.getPassword());
            NbPreferences.forModule(FacebookPasswordProvider.class).put(PROP_ACCESS_TOKEN, password);
            changed = false;
        }                
    }

    @Override
    public String getName() 
    {
        return "facebook";
    }

    @Override
    public String getDisplayName() 
    {
        return "Facebook";
    }     
    
    @Override
    public Component getField()
    {
        if(field == null)
        {
            field = new JPasswordField(getAccessToken());
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
