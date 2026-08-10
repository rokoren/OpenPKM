/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

/**
 *
 * @author Rok Koren
 */
public class RaindropChildrenCollection extends RaindropRootCollection
{
    private final int parentID;

    public RaindropChildrenCollection(int collectionID, int parentID, boolean isPublic)
    {
        super(collectionID, isPublic);
        this.parentID = parentID;
    }
    
    public int getParentID() 
    {
        return parentID;
    }     
}
