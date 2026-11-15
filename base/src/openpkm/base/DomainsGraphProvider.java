/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author rok
 */
public interface DomainsGraphProvider extends GraphProvider
{
    List<Domain> getDomains(); 
    List<Domain> getRootDomains();
    String getTreeID(Domain domain);
    Collection<Domain> getSelectedDomains(); 
    void clearSelectedDomains();
    void selectDomain(Domain domain);
    boolean isDomain(DomainsProvider provider);
    List<ChildrenDomain> getChildrenDomains(String parentID);
    void addRootDomain(String name, String tag);
    void addRootDomain(String domainID, String name, String tag);    
    void removeRootDomain(Domain domain);
    void addChildrenDomain(String parentID, String name, String tag);
    void addChildrenDomain(String parentID, String domainID, String name, String tag);
    void removeChildrenDomain(ChildrenDomain domain);          
}
