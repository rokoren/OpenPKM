/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import openpkm.base.Source;
import openpkm.base.TagsProvider;
import openpkm.base.TitleProvider;
import openpkm.raindrop.Raindrop.Type;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 *
 * @author Rok Koren
 */
public class RaindropUtils 
{
    public static RaindropUser getUser(String token, String title)
    {
        // Construct the URL for the Raindrop.io API endpoint
        String apiUrl = "https://api.raindrop.io/rest/v1/user";

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the API key in the request header
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                return getUser(response.toString());
            } 
            else 
            {
                NotifyDescriptor d = new NotifyDescriptor(
                        "Get User Raindrop.io API Request failed. Response Code: " + responseCode, // message
                        title, // title
                        NotifyDescriptor.DEFAULT_OPTION, // option type
                        NotifyDescriptor.WARNING_MESSAGE, // message type
                        null, // custom buttons (as Object[])
                        null); // default value
                DialogDisplayer.getDefault().notify(d);
            }                
        }
        catch(IOException e)
        {
            Exceptions.printStackTrace(e);
        }
        finally
        {
            if(connection != null)
            {
                // Close the connection
                connection.disconnect();                  
            }
        }                  
        return null;
    }      
    
    public static RaindropUser getUser(String jsonResponse)
    {
        // Create a JSON object from the response string
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject user = jsonObject.getJSONObject("user");
        JSONObject files = user.getJSONObject("files");
        int userID = user.getInt("_id");
        int filesUsed = files.getInt("used");
        int filesSize = files.getInt("size");
        String name = user.getString("name");
        String fullName = user.getString("fullName");
        String email = user.getString("email");
        String avatar = user.getString("avatar");
        boolean pro = user.getBoolean("pro");
        
        RaindropUser raindropUser = new RaindropUser(userID); 
        raindropUser.setName(name);
        raindropUser.setFulName(fullName);
        raindropUser.setEmail(email);
        raindropUser.setFilesSize(filesSize);
        raindropUser.setFilesUsed(filesUsed);
        raindropUser.setPro(pro);
        raindropUser.setAvatar(avatar);

        /*
        // Example: Extracting an array from the JSON response
        JSONObject collections = (JSONObject) user.getJSONArray("groups").get(0);
        JSONArray collectionsArray = collections.getJSONArray("collections");

        // Iterate through the array and print some information
        for (int i = 0; i < collectionsArray.length(); i++) 
        {
            raindropUser.getRootCollections().add(collectionsArray.getInt(i));
        }
        */

        return raindropUser;
    }  
    
    public static List<RaindropTag> getTags(RaindropAccount account, RaindropCollection collection)
    {
        // Construct the URL for the Raindrop.io API endpoint
        String apiUrl = "https://api.raindrop.io/rest/v1/tags/";

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(apiUrl + collection.getCollectionID());

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the API key in the request header
            connection.setRequestProperty("Authorization", "Bearer " + account.getToken());

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                return getTags(account, collection, response.toString());
            } 
            else 
            {
                NotifyDescriptor d = new NotifyDescriptor(
                        "Get Tags Raindrop.io API Request failed. Response Code: " + responseCode, // message
                        collection.getTitle(), // title
                        NotifyDescriptor.DEFAULT_OPTION, // option type
                        NotifyDescriptor.WARNING_MESSAGE, // message type
                        null, // custom buttons (as Object[])
                        null); // default value
                DialogDisplayer.getDefault().notify(d);
            }                
        }
        catch(IOException e)
        {
            Exceptions.printStackTrace(e);
        }
        finally
        {
            if(connection != null)
            {
                // Close the connection
                connection.disconnect();                  
            }
        }

        return Collections.EMPTY_LIST;
    }  

    private static List<RaindropTag> getTags(RaindropAccount account, RaindropCollection collection, String jsonResponse)
    {
        List<RaindropTag> tags = new ArrayList<>();
        // Create a JSON object from the response string
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Example: Extracting an array from the JSON response
        JSONArray raindropsArray = jsonObject.getJSONArray("items");

        // Iterate through the array and print some information
        for (int i = 0; i < raindropsArray.length(); i++) 
        {
            JSONObject json = raindropsArray.getJSONObject(i);
            String tag = json.getString("_id");
            int count = json.getInt("count");        

            RaindropTag raindropTag = new RaindropTag(account, collection, tag);
            raindropTag.setCount(count);
            tags.add(raindropTag);
        }
        return tags;
    } 
    
    public static List<RaindropCollection> getRootCollections(RaindropAccount account)
    {
        // Construct the URL for the Raindrop.io API endpoint
        String apiUrl = "https://api.raindrop.io/rest/v1/collections";

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the API key in the request header
            connection.setRequestProperty("Authorization", "Bearer " + account.getToken());

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                return getRootCollections(account, response.toString());
            } 
            else 
            {
                NotifyDescriptor d = new NotifyDescriptor(
                        "Get Root Collections Raindrop.io API Request failed. Response Code: " + responseCode, // message
                        account.getTitle(), // title
                        NotifyDescriptor.DEFAULT_OPTION, // option type
                        NotifyDescriptor.WARNING_MESSAGE, // message type
                        null, // custom buttons (as Object[])
                        null); // default value
                DialogDisplayer.getDefault().notify(d);
            }                
        }
        catch(IOException e)
        {
            Exceptions.printStackTrace(e);
        }
        finally
        {
            if(connection != null)
            {
                // Close the connection
                connection.disconnect();                  
            }
        }                  
        return Collections.EMPTY_LIST;
    }  
    
    private static List<RaindropCollection> getRootCollections(RaindropAccount account, String jsonResponse)
    {
        List<RaindropCollection> collections = new ArrayList<>();
        // Create a JSON object from the response string
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Example: Extracting an array from the JSON response
        JSONArray raindropsArray = jsonObject.getJSONArray("items");

        // Iterate through the array and print some information
        for (int i = 0; i < raindropsArray.length(); i++) 
        {
            JSONObject collection = raindropsArray.getJSONObject(i);
            int collectionID = collection.getInt("_id");
            String title = collection.getString("title");
            String description = collection.getString("description");
            String lastAction = collection.getString("lastAction");
            String lastUpdate = collection.getString("lastUpdate");  
            int count = collection.getInt("count");
            boolean share = collection.getBoolean("public");
            String cover = (String) collection.getJSONArray("cover").get(0);            

            RaindropCollection raindropCollection = new RaindropRootCollection(account, collectionID, share);
            raindropCollection.setTitle(title);
            raindropCollection.setDescription(description);
            raindropCollection.setCover(cover);            
            raindropCollection.setCount(count);
            collections.add(raindropCollection);
        }
        return collections;
    }     
    
    public static List<RaindropChildrenCollection> getChildrenCollections(RaindropAccount account)
    {
        // Construct the URL for the Raindrop.io API endpoint
        String apiUrl = "https://api.raindrop.io/rest/v1/collections/childrens";

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the API key in the request header
            connection.setRequestProperty("Authorization", "Bearer " + account.getToken());

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                return  getChildrenCollections(account, response.toString());
            } 
            else 
            {
                NotifyDescriptor d = new NotifyDescriptor(
                        "Get Children Collections Raindrop.io API Request failed. Response Code: " + responseCode, // message
                        account.getTitle(), // title
                        NotifyDescriptor.DEFAULT_OPTION, // option type
                        NotifyDescriptor.WARNING_MESSAGE, // message type
                        null, // custom buttons (as Object[])
                        null); // default value
                DialogDisplayer.getDefault().notify(d);
            }                
        }
        catch(IOException e)
        {
            Exceptions.printStackTrace(e);
        }
        finally
        {
            if(connection != null)
            {
                // Close the connection
                connection.disconnect();                  
            }
        }

        return Collections.EMPTY_LIST;
    }  

    private static List<RaindropChildrenCollection> getChildrenCollections(RaindropAccount account, String jsonResponse)
    {
        List<RaindropChildrenCollection> collections = new ArrayList<>();
        // Create a JSON object from the response string
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Example: Extracting an array from the JSON response
        JSONArray raindropsArray = jsonObject.getJSONArray("items");

        // Iterate through the array and print some information
        for (int i = 0; i < raindropsArray.length(); i++) 
        {
            JSONObject collection = raindropsArray.getJSONObject(i);
            if(collection.has("parent") && !collection.isNull("parent"))
            {
                JSONObject parent = collection.getJSONObject("parent");
                int parentID = parent.getInt("$id");
                
                int collectionID = collection.getInt("_id");
                String title = collection.getString("title");
                String description = collection.getString("description");
                String lastAction = collection.getString("lastAction");
                String lastUpdate = collection.getString("lastUpdate");  
                int count = collection.getInt("count");
                boolean share = collection.getBoolean("public");                

                RaindropChildrenCollection raindropCollection = new RaindropChildrenCollection(account, collectionID, parentID, share);
                raindropCollection.setTitle(title);
                raindropCollection.setDescription(description);
                raindropCollection.setCount(count);
                collections.add(raindropCollection);

                if(!collection.getJSONArray("cover").isEmpty())
                {
                    String cover = (String) collection.getJSONArray("cover").get(0); 
                    raindropCollection.setCover(cover);
                }                 
            }           
        }
        return collections;
    }   
    
    public static List<Raindrop> getRaindrops(RaindropAccount account, RaindropCollection collection)
    {
        // Construct the URL for the Raindrop.io API endpoint
        String apiUrl = "https://api.raindrop.io/rest/v1/raindrops/";

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(apiUrl + collection.getCollectionID());

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the API key in the request header
            connection.setRequestProperty("Authorization", "Bearer " + account.getToken());

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                return getRaindrops(account, collection, response.toString());
            } 
            else 
            {
                NotifyDescriptor d = new NotifyDescriptor(
                        "Get Raindrops Raindrop.io API Request failed. Response Code: " + responseCode, // message
                        collection.getTitle(), // title
                        NotifyDescriptor.DEFAULT_OPTION, // option type
                        NotifyDescriptor.WARNING_MESSAGE, // message type
                        null, // custom buttons (as Object[])
                        null); // default value
                DialogDisplayer.getDefault().notify(d);
            }                
        }
        catch(IOException e)
        {
            Exceptions.printStackTrace(e);
        }
        finally
        {
            if(connection != null)
            {
                // Close the connection
                connection.disconnect();                  
            }
        }

        return Collections.EMPTY_LIST;
    }   
    
    private static List<Raindrop> getRaindrops(RaindropAccount account, RaindropCollection collection, String jsonResponse)
    {
        List<Raindrop> raindrops = new ArrayList<>();
        // Create a JSON object from the response string
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Example: Extracting an array from the JSON response
        JSONArray raindropsArray = jsonObject.getJSONArray("items");

        // Iterate through the array and print some information
        for (int i = 0; i < raindropsArray.length(); i++) 
        {
            JSONObject json = raindropsArray.getJSONObject(i);                             
            Raindrop raindrop = getRaindrop(account, collection, json);
            if(raindrop != null)
            {
                raindrops.add(raindrop);                      
            }              
        }
        return raindrops;
    }  

    public static Raindrop createRaindrop(RaindropAccount account, RaindropCollection collection, String link)
    {
        // Construct the URL for the Raindrop.io API endpoint
        String apiUrl = "https://api.raindrop.io/rest/v1/raindrop";

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set the API key in the request header
            connection.setRequestProperty("Authorization", "Bearer " + account.getToken());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            
            JSONObject json = new JSONObject();
            json.put("collection", new JSONObject().put("$id", collection.getCollectionID()));
            json.put("link", link);
            json.put("pleaseParse", new JSONObject());
            
            try (OutputStream os = connection.getOutputStream()) {
                /*
                System.out.println("JSON:" + json.toString());
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);		
                */

                System.out.println("Link:" + link);
                Writer writer = new OutputStreamWriter(os);
                json.write(writer);
                writer.close();
            }         

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                return getRaindrop(account, collection, response.toString());
            } 
            else 
            {
                NotifyDescriptor d = new NotifyDescriptor(
                        "Create Raindrop Raindrop.io API Request failed. Response Code: " + responseCode, // message
                        collection.getTitle(), // title
                        NotifyDescriptor.DEFAULT_OPTION, // option type
                        NotifyDescriptor.WARNING_MESSAGE, // message type
                        null, // custom buttons (as Object[])
                        null); // default value
                DialogDisplayer.getDefault().notify(d);
            }                
        }
        catch(IOException e)
        {
            Exceptions.printStackTrace(e);
        }
        finally
        {
            if(connection != null)
            {
                // Close the connection
                connection.disconnect();                  
            }
        }

        return null;
    }  
    
    private static Raindrop getRaindrop(RaindropAccount account, RaindropCollection collection, String jsonResponse)
    {
        // Create a JSON object from the response string
        JSONObject jsonObject = new JSONObject(jsonResponse);
        
        boolean result = jsonObject.getBoolean("result");
        
        if(result)
        {
            JSONObject item = jsonObject.getJSONObject("item");
            return getRaindrop(account, collection, item);
        }
        return null;
    }   
    
    private static Raindrop getRaindrop(RaindropAccount account, RaindropCollection collection, JSONObject json)
    {
        int raindropID = json.getInt("_id");
        //System.out.println("Type: " + json1.getString("type"));
        Optional<AbstractRaindrop.Type> type = AbstractRaindrop.Type.get(json.getString("type"));
        if(type.isPresent())
        {
            RaindropUser creator = null; 
            JSONObject json2 = json.getJSONObject("creatorRef");
            int creatorID = json2.getInt("_id");
            RaindropAccount creatorAccount = RaindropService.getDefault().getAccount(creatorID);
            if(creatorAccount != null)
            {
                creator = creatorAccount.getUser();
            }
            else
            {
                creator = new RaindropUser(creatorID);
                String name = json2.getString("name");
                String email = json2.getString("email");
                creator.setName(name);
                creator.setEmail(email);
            }

            String link = json.getString("link");
            String title = json.getString("title");
            String excerpt = json.getString("excerpt");
            String note = json.getString("note");
            String cover = json.getString("cover");  

            Boolean important = null;
            if(json.has("important"))
            {
                important = json.getBoolean("important");                    
            }

            boolean removed = json.getBoolean("removed");
            String domain = json.getString("domain");
            String created = json.getString("created");
            String lastUpdate = json.getString("lastUpdate");   

            JSONArray tagsArray = json.getJSONArray("tags");
            List<String> tags = new ArrayList<>();
            for (int k = 0; k < tagsArray.length(); k++) 
            {  
                tags.add(tagsArray.getString(k));
            } 

            String reminder = null;
            if(json.has("reminder"))
            {
                JSONObject reminderJson = json.getJSONObject("reminder");
                if(!reminderJson.isNull("date"))
                {
                    reminder = reminderJson.getString("date");
                }                          
            } 

            List<String> highlights = new ArrayList<>();
            if(json.has("highlights"))
            {
                JSONArray highlightsArray = json.getJSONArray("highlights");
                for (int k = 0; k < highlightsArray.length(); k++) 
                {  
                    //highlights.add(highlightsArray.getString(k));
                }                  
            }             

            Properties props = new Properties();
            props.setProperty(Raindrop.PROPS_TYPE, type.get().toString()); 
            props.setProperty(Raindrop.PROPS_RAINDROP_ID, raindropID + "");
            props.setProperty(Raindrop.PROPS_RAINDROP_USER_ID, account.getUser().getUserID() + "");
            props.setProperty(Raindrop.PROPS_RAINDROP_CREATOR_ID, creator.getUserID() + "");
            props.setProperty(Raindrop.PROPS_RAINDROP_COLLECTION_ID, collection.getCollectionID() + "");
            props.setProperty(Raindrop.PROPS_LINK, link);
            props.setProperty(TitleProvider.PROP_TITLE, title);
            props.setProperty(Raindrop.PROPS_REMOVED, Boolean.toString(removed));
            props.setProperty(Raindrop.PROPS_EXCERPT, excerpt);
            props.setProperty(Raindrop.PROPS_COVER, cover); 
            props.setProperty(Raindrop.PROPS_NOTE, note); 
            if(!tags.isEmpty())
            {
                props.setProperty(TagsProvider.PROP_TAGS, String.join(",", tags));                        
            }
            if(important != null)
            {
                props.setProperty(Raindrop.PROPS_IMPORTANT, important.toString());                    
            }
            if(reminder != null)
            {
                props.setProperty(Raindrop.PROPS_REMINDER, reminder);                        
            }
            props.setProperty(Source.PROP_TIME_CREATED, created);                  
            props.setProperty(Raindrop.PROPS_LAST_UPDATE, lastUpdate);
            if(!highlights.isEmpty())
            {
                props.setProperty(Raindrop.PROPS_HIGHLIGHTS, String.join(",", highlights));                    
            }
            props.setProperty(Raindrop.PROPS_DOMAIN, domain); 

            if(type.get() == Type.DOCUMENT)
            {
                JSONObject file = json.getJSONObject("file");
                String fileName = file.getString("name");
                long fileSize = file.getLong("size");
                String fileType = file.getString("type");                   

                props.setProperty(Raindrop.PROPS_FILE_NAME, fileName);
                props.setProperty(Raindrop.PROPS_FILE_SIZE, fileSize + "");
                props.setProperty(Raindrop.PROPS_FILE_TYPE, fileType); 
            }

            return AbstractRaindrop.getRaindrop(props);
        }
        return null;
    }      
}
