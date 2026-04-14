/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

/**
 *
 * @author rokor
 */
public class RaindropTag 
{
    private final RaindropAccount account;
    private final RaindropCollection collection;
    private final String tag;
    
    private int count;

    public RaindropTag(RaindropAccount account, RaindropCollection collection, String tag) 
    {
        this.account = account;
        this.collection = collection;
        this.tag = tag;
    }

    public RaindropAccount getAccount()
    {
        return account;
    }
    
    public String getTag() 
    {
        return tag;
    }

    public int getCount() 
    {
        return count;
    }  
    
    public void setCount(int count)
    {
        this.count = count;
    }
}
