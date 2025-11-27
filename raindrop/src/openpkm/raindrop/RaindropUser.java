/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.util.Date;

/**
 *
 * @author Rok Koren
 */
public class RaindropUser 
{
    private final int userID;        
    
    private int filesSize, filesUsed;
    private boolean pro;    
    private String name, fullName, email, avatar;
    private Date lastAction, lastVisit, registered, lastUpdate;

    public RaindropUser(int userID) 
    {
        this.userID = userID;
    }    
     
    public int getUserID() 
    {
        return userID;
    }    
    
    public int getFilesSize() 
    {
        return filesSize;
    }
    
    public void setFilesSize(int size) 
    {
        filesSize = size;
    }    

    public int getFilesUsed() 
    {
        return filesUsed;
    }
    
    public void setFilesUsed(int size) 
    {
        filesUsed = size;
    }    

    public String getName() 
    {
        return name;
    }
    
    public void setName(String name) 
    {
        this.name = name;
    }    

    public String getFullName() 
    {
        return fullName;
    }
    
    public void setFulName(String name) 
    {
        fullName = name;
    }    

    public String getEmail() 
    {
        return email;
    }
    
    public void setEmail(String email) 
    {
        this.email = email;
    }    

    /*
    @Override
    public List<Integer> getRootCollections()
    {
        return rootCollections;
    }
    */

    public Date getLastAction() 
    {
        return lastAction;
    }

    public Date getLastVisit() 
    {
        return lastVisit;
    }

    public Date getRegistered() 
    {
        return registered;
    }

    public Date getLastUpdate() 
    {
        return lastUpdate;
    }  

    public String getAvatar() 
    {
        return avatar;
    }
    
    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }    

    public boolean isPro() 
    {
        return pro;
    }
    
    public void setPro(boolean pro) 
    {
        this.pro = pro;
    }     
}
