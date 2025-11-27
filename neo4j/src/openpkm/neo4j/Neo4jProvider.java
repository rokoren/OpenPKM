/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.neo4j;

import java.util.Collection;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Rok Koren
 */
public interface Neo4jProvider 
{
    void addInstance(Neo4jInstance instance);
    void removeInstance(Neo4jInstance instance);
    Neo4jInstance getInstance(String instanceID);
    Collection<Neo4jInstance> getInstances();
    ChangeSupport getChangeSupport();
    void store(Neo4jInstance instance);    
}
