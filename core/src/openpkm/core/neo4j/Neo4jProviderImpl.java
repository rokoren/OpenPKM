/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import openpkm.neo4j.Neo4jInstance;
import openpkm.neo4j.Neo4jInstance.Type;
import openpkm.neo4j.Neo4jProvider;
import org.openide.util.ChangeSupport;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service = Neo4jProvider.class)
public class Neo4jProviderImpl implements Neo4jProvider
{
    private static final Logger LOG = Logger.getLogger(Neo4jProviderImpl.class.getName());
    
    private final ChangeSupport cs = new ChangeSupport(this); 
    private final Instances instances = new Instances();   
    
    @Override
    public void addInstance(Neo4jInstance instance)
    {
        instances.getInstances().put(instance.getInstanceID(), instance);
        cs.fireChange();
    }
    
    @Override
    public void removeInstance(Neo4jInstance instance)
    {
        instances.getInstances().remove(instance.getInstanceID());
        cs.fireChange();
    }
    
    @Override
    public Neo4jInstance getInstance(String instanceID) 
    {
        return instances.getInstances().get(instanceID);
    }  
    
    @Override
    public Collection<Neo4jInstance> getInstances()
    {       
        return instances.getInstances().values();
    }
    
    @Override
    public void store(Neo4jInstance instance) 
    {
        // Store the instance
        Preferences preferences =  instance.getPreferences();   
        preferences.put(Neo4jInstance.PROP_INSTANCE_ID, instance.getInstanceID());
        preferences.put(Neo4jInstance.PROP_NEO4J_URI, instance.getNeo4jUri());
        preferences.put(Neo4jInstance.PROP_INSTANCE_NAME, instance.getInstanceName());
        preferences.put(Neo4jInstance.PROP_NEO4J_USERNAME, instance.getNeo4jUsername());
        preferences.put(Neo4jInstance.PROP_NEO4J_PASSWORD, instance.getNeo4jPassword());
        preferences.put(Neo4jInstance.PROP_NEO4J_DATABASE, instance.getNeo4jDatabase());
        preferences.put(Neo4jInstance.PROP_NEO4J_TYPE, instance.getNeo4jType().toString());
    }

    @Override
    public ChangeSupport getChangeSupport() 
    {
        return cs;
    }      
    
    private static final class Instances
    {
        private Map<String, Neo4jInstance> instances;  
        
        public synchronized Map<String, Neo4jInstance> getInstances()
        {
            if(instances == null)
            {
                instances = new HashMap<>();  
                try
                {
                    load();                    
                }
                catch(BackingStoreException e)
                {
                    LOG.warning(e.getMessage());
                }
            }  
            return instances;
        }
        
        private void load() throws BackingStoreException
        {
            // Load the instances list        
            String[] ids = Neo4jInstanceImpl.PREFERENCES.childrenNames();
            for (String instanceID : ids)
            {
                Preferences preferences = Neo4jInstanceImpl.PREFERENCES.node(instanceID);
                Neo4jInstance instance = getInstance(preferences);
                if(instance != null)
                {
                    instances.put(instance.getInstanceID(), instance);                
                }
            }
        } 

        private Neo4jInstance getInstance(Preferences preferences)
        {
            String instanceID = preferences.get(Neo4jInstance.PROP_INSTANCE_ID, null);
            String neo4jUri = preferences.get(Neo4jInstance.PROP_NEO4J_URI, "");
            String instanceName = preferences.get(Neo4jInstance.PROP_INSTANCE_NAME, "");
            String neo4jUsername = preferences.get(Neo4jInstance.PROP_NEO4J_USERNAME, "");
            String neo4jPassword = preferences.get(Neo4jInstance.PROP_NEO4J_PASSWORD, "");
            String neo4jDatabase = preferences.get(Neo4jInstance.PROP_NEO4J_DATABASE, "");
            Optional<Type> neo4jType = Type.get(preferences.get(Neo4jInstance.PROP_NEO4J_TYPE, Neo4jInstance.Type.NEO4J_DESKTOP.name()));
            if(instanceID != null && neo4jType.isPresent())
            {
                Neo4jInstance instance = new Neo4jInstanceImpl(instanceID);
                instance.setNeo4jUri(neo4jUri);
                instance.setInstanceName(instanceName);
                instance.setNeo4jUsername(neo4jUsername);
                instance.setNeo4jPassword(neo4jPassword);
                instance.setNeo4jDatabase(neo4jDatabase);
                instance.setNeo4jType(neo4jType.get());
                return instance;            
            }
            return null;
        }        
    }    
}
