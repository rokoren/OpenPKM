/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author rok
 */
public interface WorkflowProvider 
{
    String PROP_WORKFLOW = "workflow";     
    
    Workflow getWorkflow(); 
    void setWorkflow(Workflow workflow); 
    
    public enum Workflow 
    {
        WATCH_LATER("Watch Later"),
        READ_LATER("Read Later"),
        RECYCLE_BIN("Recycle Bin"),
        DEFAULT("Default"),
        IN_PROGRESS("In Progress");

        private String name;       

        Workflow(String name) 
        {
            this.name = name;
        } 
        
        @Override
        public String toString()
        {
            return name;
        }
        
        public static Optional<Workflow> get(String name) {
            return Arrays.stream(Workflow.values())
                    .filter(workflow -> workflow.name.equalsIgnoreCase(name))
                    .findFirst();
        }     
    }        
}
