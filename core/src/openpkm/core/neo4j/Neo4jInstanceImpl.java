/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.neo4j;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import openpkm.base.ChildrenDomain;
import openpkm.base.ChildrenGoal;
import openpkm.base.ChildrenThought;
import openpkm.base.ChildrenTopic;
import openpkm.base.Domain;
import openpkm.base.Goal;
import openpkm.base.TagsProvider;
import openpkm.base.Thought;
import openpkm.base.Topic;
import openpkm.base.VisibilityProvider;
import openpkm.neo4j.Neo4jInstance;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.TransactionContext;
import org.neo4j.driver.Value;
import org.openide.util.ChangeSupport;
import org.openide.util.NbPreferences;
import openpkm.base.DomainChannel;
import openpkm.core.domain.AbstractDomainChannel;

/**
 *
 * @author Rok Koren
 */
public class Neo4jInstanceImpl implements Neo4jInstance
{    
    public static final Preferences PREFERENCES = NbPreferences.forModule(Neo4jInstance.class);  
    
    private static final Logger LOG = Logger.getLogger(Neo4jInstanceImpl.class.getName());    
    
    private final ChangeSupport cs = new ChangeSupport(this); 

    private final String instanceID;
    
    private String neo4jUri, instanceName, neo4jUsername, neo4jPassword, neo4jDatabase;  
    private Type neo4jType;
    
    private Driver driver;

    public Neo4jInstanceImpl(String instanceID) 
    {
        this.instanceID = instanceID;
    }
    
    private Driver getDriver()
    {
        if(driver == null)
        {
            try
            {
                driver = GraphDatabase.driver(getNeo4jUri(), AuthTokens.basic(getNeo4jUsername(), getNeo4jPassword()));
                driver.verifyConnectivity();                
            }
            catch(Exception e)
            {
                LOG.warning(e.getMessage());
            }
        }
        return driver;        
    }
    
    @Override
    public String getInstanceID()
    {
        return instanceID;
    }

    @Override
    public String getNeo4jUri() 
    {
        return neo4jUri;
    }

    @Override
    public void setNeo4jUri(String uri) 
    {
        neo4jUri = uri;
    }

    @Override
    public String getNeo4jUsername() 
    {
        return neo4jUsername;
    }

    @Override
    public void setNeo4jUsername(String username) 
    {
        neo4jUsername = username;
    }

    @Override
    public String getNeo4jPassword() 
    {
        return neo4jPassword;
    }

    @Override
    public void setNeo4jPassword(String password) 
    {
        neo4jPassword = password;
    }
    
    @Override
    public String getNeo4jDatabase() 
    {
        return neo4jDatabase;
    }

    @Override
    public void setNeo4jDatabase(String database) 
    {
        neo4jDatabase = database;
    }    

    @Override
    public String getInstanceName() 
    {
        return instanceName;
    }

    @Override
    public void setInstanceName(String name) 
    {
        instanceName = name;
    } 
    
    @Override
    public Type getNeo4jType()
    {
        return neo4jType;
    }
    
    @Override
    public void setNeo4jType(Type type)
    {
        neo4jType = type;
    }
    
    @Override
    public Preferences getPreferences() 
    {
        return PREFERENCES.node(instanceID);
    }   

    @Override
    public Session getSession()
    {
        return driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());
    }
    
    @Override
    public List<Domain> getRootDomains(String projectID) 
    {
        List<Domain> domains = new ArrayList<>();
        EagerResult result = getDriver().executableQuery("MATCH (d:Domain {project: $project}) RETURN d.id AS ID, d.name AS name, d.tag AS tag")
                .withParameters(Map.of("project", projectID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var records = result.records();
        records.forEach(r -> {
            Domain domain = new DomainImpl(r.get("ID").asString());  
            domain.setName(r.get("name").asString());
            domain.setTag(r.get("tag").asString());
            domains.add(domain);
        });
        return domains;        
    } 
    
    @Override
    public List<ChildrenDomain> getChildrenDomains(String parentID) 
    {
        List<ChildrenDomain> domains = new ArrayList<>();
        EagerResult result = getDriver().executableQuery("MATCH (d:Domain)-[:SUBCLASS]->(:Domain {id: $id}) RETURN d.id AS ID, d.name AS name, d.tag AS tag")
                .withParameters(Map.of("id", parentID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var records = result.records();
        records.forEach(r -> {
            ChildrenDomain domain = new ChildrenDomainImpl(r.get("ID").asString());
            domain.setParentID(parentID);
            domain.setName(r.get("name").asString());
            domain.setTag(r.get("tag").asString());
            domains.add(domain);
        });
        return domains;        
    }      
    
    @Override
    public Domain addRootDomain(String projectID, String name, String tag) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            String domainID = session.executeWrite(tx -> createRootDomain(tx, projectID, name, tag));
            Domain domain = new DomainImpl(domainID);
            domain.setName(name);
            domain.setTag(tag);
            return domain;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }
        return null;        
    }  
    
    @Override
    public Domain addRootDomain(String projectID, String domainID, String name, String tag) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            session.executeWriteWithoutResult(tx -> createRootDomain(tx, projectID, domainID, name, tag));
            Domain domain = new DomainImpl(domainID);
            domain.setName(name);
            domain.setTag(tag);
            return domain;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }
        return null;        
    }   
    
    @Override
    public void removeRootDomain(String domainID) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ChildrenDomain addChildrenDomain(String parentID, String name, String tag, VisibilityProvider.Modifier modifier) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            String domainID = session.executeWrite(tx -> createChildrenDomain(tx, parentID, name, tag, modifier));
            ChildrenDomain domain = new ChildrenDomainImpl(domainID);
            domain.setParentID(parentID);
            domain.setName(name);
            domain.setTag(tag);
            return domain;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }        
        return null;        
    }
    
    @Override
    public ChildrenDomain addChildrenDomain(String parentID, String domainID, String name, String tag, VisibilityProvider.Modifier modifier) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            session.executeWriteWithoutResult(tx -> createChildrenDomain(tx, parentID, domainID, name, tag, modifier));
            ChildrenDomain domain = new ChildrenDomainImpl(domainID);
            domain.setParentID(parentID);
            domain.setName(name);
            domain.setTag(tag);
            domain.setModifier(modifier);
            return domain;            
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }        
        return null;        
    }    

    @Override
    public void removeChildrenDomain(ChildrenDomain domain) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }  
    
    @Override
    public List<DomainChannel> getDomainChannels(String domainID) 
    {
        List<DomainChannel> channels = new ArrayList<>();
        EagerResult result = getDriver().executableQuery("MATCH (dc:DomainChannel)-[d:DOMAIN]->(:Domain {id: $id}) RETURN dc.id AS ID, dc.name AS name, dc.props AS props, d.type AS type")
                .withParameters(Map.of("id", domainID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var records = result.records();
        records.forEach(r -> {            
            Optional<DomainChannel.Type> type = DomainChannel.Type.get(r.get("type").asString());
            if(type.isPresent())
            {
                Properties props = new Properties();
                try
                {  
                    props.load(new StringReader(r.get("props").asString()));
                    String channelID = r.get("ID").asString();
                    String name = r.get("name").asString();                    
                    channels.add(AbstractDomainChannel.getDomainChannel(domainID, channelID, name, props, type.get()));                      
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }                                                                                                 
            }              
        });
        return channels;
    }   
    
   @Override
    public DomainChannel addDomainChannel(String domainID, String name, Properties props, DomainChannel.Type type)
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());  
            StringWriter writer = new StringWriter();
            props.store(writer, "Created by OpenPKM");
            String channelID = session.executeWrite(tx -> createDomainChannel(tx, domainID, name, writer.toString(), type));
            ChildrenDomain domain = new ChildrenDomainImpl(domainID);
            domain.setParentID(parentID);
            domain.setName(name);
            domain.setTag(tag);
            return domain;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }        
        return null;        
    }    

    @Override
    public List<Topic> getRootTopics(String projectID) 
    {
        List<Topic> topics = new ArrayList<>();
        EagerResult result = getDriver().executableQuery("MATCH (t:Topic {project: $project}) RETURN t.id AS ID, t.name AS name, t.tag AS tag")
                .withParameters(Map.of("project", projectID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var records = result.records();
        records.forEach(r -> {
            Topic t = new TopicImpl(r.get("ID").asString());
            t.setName(r.get("name").asString());
            t.setTag(r.get("tag").asString());
            topics.add(t);
        });
        return topics;
    }
    
    @Override
    public List<ChildrenTopic> getChildrenTopics(String parentID) 
    {
        List<ChildrenTopic> topics = new ArrayList<>();
        EagerResult result = getDriver().executableQuery("MATCH (t:Topic)-[:SUBCLASS]->(:Topic {id: $id}) RETURN t.id AS ID, t.name AS name, t.tag AS tag")
                .withParameters(Map.of("id", parentID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var records = result.records();
        records.forEach(r -> {
            ChildrenTopicImpl t = new ChildrenTopicImpl(r.get("ID").asString());
            t.setParentID(parentID);
            t.setName(r.get("name").asString());
            t.setTag(r.get("tag").asString());
            Optional<VisibilityProvider.Modifier> visibility = VisibilityProvider.Modifier.get(r.get("visibility").asString());
            if(visibility.isPresent())
            {
                t.setModifier(visibility.get());                
            }
            topics.add(t);
        });
        return topics;
    }    

    @Override
    public Topic addRootTopic(String projectID, String name, String tag) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            String topicID = session.executeWrite(tx -> createRootTopic(tx, projectID, name, tag));
            Topic t = new TopicImpl(topicID);
            t.setName(name);
            t.setTag(tag);
            return t;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }
        return null;
    }
    
    @Override
    public Topic addRootTopic(String projectID, String topicID, String name, String tag) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            session.executeWriteWithoutResult(tx -> createRootTopic(tx, projectID, topicID, name, tag));
            Topic t = new TopicImpl(topicID);
            t.setName(name);
            t.setTag(tag);
            return t;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }
        return null;
    }    

    @Override
    public void removeRootTopic(String topicID) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ChildrenTopic addChildrenTopic(String parentID, String name, String tag, VisibilityProvider.Modifier modifier) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            String topicID = session.executeWrite(tx -> createChildrenTopic(tx, parentID, name, tag, modifier));
            ChildrenTopicImpl t = new ChildrenTopicImpl(topicID);
            t.setParentID(parentID);
            t.setName(name);
            t.setTag(tag);
            t.setModifier(modifier);
            return t;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }        
        return null;
    }
    
    @Override
    public ChildrenTopic addChildrenTopic(String parentID, String topicID, String name, String tag, VisibilityProvider.Modifier modifier) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            session.executeWriteWithoutResult(tx -> createChildrenTopic(tx, parentID, topicID, name, tag, modifier));
            ChildrenTopicImpl t = new ChildrenTopicImpl(topicID);
            t.setParentID(parentID);
            t.setName(name);
            t.setTag(tag);
            t.setModifier(modifier);
            return t;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }        
        return null;
    }    

    @Override
    public void removeChildrenTopic(ChildrenTopic topic) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public List<Goal> getRootGoals(String projectID) 
    {
        List<Goal> goals = new ArrayList<>();
        EagerResult result = getDriver().executableQuery("MATCH (goal:Goal {project: $project}) RETURN goal.id AS ID, goal.name AS name, goal.tag AS tag, goal.level AS level, goal.startDate AS startDate, goal.endDate AS endDate, goal.vision AS vision, goal.accountability AS accountability, goal.rewards AS rewards, goal.obstacles AS obstacles, goal.support AS support, goal.brainstorming AS brainstorming")
                .withParameters(Map.of("project", projectID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var records = result.records();
        records.forEach(r -> {
            Goal goal = new GoalImpl(r.get("ID").asString());
            goal.setName(r.get("name").asString());
            goal.setTag(r.get("tag").asString());
            Optional<Goal.Level> level = Goal.Level.get(r.get("level").asString());
            Value startDate = r.get("startDate");
            Value endDate = r.get("endDate");
            if(level.isPresent())
            {
                goal.setLevel(level.get());
            }            
            if(!startDate.isNull())
            {
                goal.setStartDate(LocalDate.parse(startDate.asString(), DateTimeFormatter.ISO_DATE));
            }
            if(!endDate.isNull())
            {
                goal.setEndDate(LocalDate.parse(endDate.asString(), DateTimeFormatter.ISO_DATE));
            }   
            goal.setVision(r.get("vision").asString());
            goal.setAccountability(r.get("accountability").asString());
            goal.setRewards(r.get("rewards").asString());
            goal.setObstacles(r.get("obstacles").asString());
            goal.setSupport(r.get("support").asString());
            goal.setBrainstorming(r.get("brainstorming").asString());            
            goals.add(goal);
        });
        return goals;
    }
    
    @Override
    public List<ChildrenGoal> getChildrenGoals(String parentID) 
    {
        List<ChildrenGoal> goals = new ArrayList<>();
        EagerResult result = getDriver().executableQuery("MATCH (goal:Goal)-[:SUBCLASS]->(:Goal {id: $id}) RETURN goal.id AS ID, goal.name AS name, goal.tag AS tag, goal.level AS level, goal.startDate AS startDate, goal.endDate AS endDate, goal.vision AS vision, goal.accountability AS accountability, goal.rewards AS rewards, goal.obstacles AS obstacles, goal.support AS support, goal.brainstorming AS brainstorming")
                .withParameters(Map.of("id", parentID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var records = result.records();
        records.forEach(r -> {
            ChildrenGoalImpl goal = new ChildrenGoalImpl(r.get("ID").asString());
            goal.setParentID(parentID);
            goal.setName(r.get("name").asString());
            goal.setTag(r.get("tag").asString());
            Optional<VisibilityProvider.Modifier> visibility = VisibilityProvider.Modifier.get(r.get("visibility").asString());
            if(visibility.isPresent())
            {
                goal.setModifier(visibility.get());                
            }
            Optional<Goal.Level> level = Goal.Level.get(r.get("level").asString());
            Value startDate = r.get("startDate");
            Value endDate = r.get("endDate");            
            if(level.isPresent())
            {
                goal.setLevel(level.get());
            }            
            if(!startDate.isNull())
            {
                goal.setStartDate(LocalDate.parse(startDate.asString(), DateTimeFormatter.ISO_DATE));
            }
            if(!endDate.isNull())
            {
                goal.setEndDate(LocalDate.parse(endDate.asString(), DateTimeFormatter.ISO_DATE));
            }   
            goal.setVision(r.get("vision").asString());
            goal.setAccountability(r.get("accountability").asString());
            goal.setRewards(r.get("rewards").asString());
            goal.setObstacles(r.get("obstacles").asString());
            goal.setSupport(r.get("support").asString());
            goal.setBrainstorming(r.get("brainstorming").asString());
            goals.add(goal);
        });
        return goals;
    }    

    @Override
    public Goal addRootGoal(String projectID, String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            String goalID = session.executeWrite(tx -> createRootGoal(tx, projectID, name, tag, level, startDate, endDate, vision, accountability, rewards, obstacles, support, brainstorming));
            Goal goal = new GoalImpl(goalID);
            goal.setName(name);
            goal.setTag(tag);
            goal.setLevel(level);
            if(startDate != null)
            {
                goal.setStartDate(startDate);
            }
            if(endDate != null)
            {
                goal.setEndDate(endDate);
            }
            goal.setVision(vision);
            goal.setAccountability(accountability);
            goal.setRewards(rewards);
            goal.setObstacles(obstacles);
            goal.setSupport(support);
            goal.setBrainstorming(brainstorming);
            return goal;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }
        return null;
    }

    @Override
    public void removeRootGoal(String goalID) 
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ChildrenGoal addChildrenGoal(String parentID, String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming, VisibilityProvider.Modifier modifier) 
    {
        Session session = null;
        try
        {
            session = driver.session(SessionConfig.builder().withDatabase(getNeo4jDatabase()).build());    
            String goalID = session.executeWrite(tx -> createChildrenGoal(tx, parentID, name, tag, level, startDate, endDate, vision, accountability, rewards, obstacles, support, brainstorming, modifier));
            ChildrenGoalImpl goal = new ChildrenGoalImpl(goalID);
            goal.setParentID(parentID);
            goal.setName(name);
            goal.setTag(tag);
            goal.setLevel(level);
            if(startDate != null)
            {
                goal.setStartDate(startDate);
            }
            if(endDate != null)
            {
                goal.setEndDate(endDate);
            }
            goal.setVision(vision);
            goal.setAccountability(accountability);
            goal.setRewards(rewards);
            goal.setObstacles(obstacles);
            goal.setSupport(support);
            goal.setBrainstorming(brainstorming);
            goal.setModifier(modifier);
            return goal;
        }
        catch(Exception e)
        {
            LOG.warning(e.getMessage());
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }        
        return null;
    }

    @Override
    public void removeChildrenGoal(ChildrenGoal goal) 
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }  
    
    @Override
    public Thought getThought(String thoughtID) throws NoSuchElementException
    {        
        EagerResult result = getDriver().executableQuery("MATCH (t:Thought {id: $id}) OPTIONAL MATCH (t)-[:HAS_PARENT]->(parent:Thought) RETURN t.id AS ID, t.text AS text, t.type AS type, t.tags AS tags, parent.id AS parentID")
                .withParameters(Map.of("id", thoughtID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var record = result.records().getFirst();
        String parentID = record.get("parentID").asString();
        Set<String> tags = new HashSet<>(record.get("tags").asList(Value::asString)); 
        
        Thought thought = null;
        if(parentID == null)
        {
            thought = new ThoughtImpl(record.get("ID").asString(), tags);
        }
        else
        {
            thought = new ChildrenThoughtImpl(parentID, record.get("ID").asString(), tags);
        }

        thought.setText(record.get("text").asString());            
        Optional<Thought.Type> type = Thought.Type.get(record.get("type").asString());
        if(type.isPresent())
        {
            thought.setType(type.get());                
        }         
        
        return thought;
    }    
    
    @Override
    public List<Thought> getRootThoughts(String projectID)
    {        
        List<Thought> thoughts = new ArrayList<>();
        EagerResult result = getDriver().executableQuery("MATCH (t:Thought {project: $project}) WHERE NOT (t)-[:HAS_PARENT]->() RETURN t.id AS ID, t.text AS text, t.type AS type, t.tags AS tags")
                .withParameters(Map.of("project", projectID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var records = result.records();
        records.forEach(r -> {
            Set<String> tags = new HashSet<>(r.get("tags").asList(Value::asString));            
            Thought thought = new ThoughtImpl(r.get("ID").asString(), tags);
            thought.setText(r.get("text").asString());            
            Optional<Thought.Type> type = Thought.Type.get(r.get("type").asString());
            if(type.isPresent())
            {
                thought.setType(type.get());                
            }                        
            thoughts.add(thought);
        });
        return thoughts;
    }
    
    @Override
    public List<ChildrenThought> getChildrenThoughts(String parentID)
    {
        List<ChildrenThought> thoughts = new ArrayList<>();
        EagerResult result = getDriver().executableQuery("MATCH (thought:Thought)-[:HAS_PARENT]->(:Thought {id: $id}) RETURN thought.id AS ID, thought.text AS text, thought.type AS type, thought.tags AS tags")
                .withParameters(Map.of("id", parentID))
                .withConfig(QueryConfig.builder().withDatabase(getNeo4jDatabase()).build())
                .execute();    
        
        var records = result.records();
        records.forEach(r -> {
            Set<String> tags = new HashSet<>(r.get("tags").asList(Value::asString));  
            ChildrenThought thought = new ChildrenThoughtImpl(parentID, r.get("ID").asString(), tags);
            thought.setText(r.get("text").asString());            
            Optional<Thought.Type> type = Thought.Type.get(r.get("type").asString());
            if(type.isPresent())
            {
                thought.setType(type.get());                
            }                          
            thoughts.add(thought);
        });
        return thoughts;
    }
    
    @Override
    public Thought addRootThought(Session session, String projectID, String text, Thought.Type type, Set<String> tags) throws Exception
    {
        String thoughtID = session.executeWrite(tx -> createRootThought(tx, projectID, text, type, tags));
        Thought thought = new ThoughtImpl(thoughtID, tags);
        thought.setText(text);
        thought.setType(type);
        return thought;
    }  
    
    @Override
    public ChildrenThought addChildrenThought(Session session, String projectID, Thought parent, String text, Thought.Type type, Set<String> tags) throws Exception
    {
        String thoughtID = session.executeWrite(tx -> createChildrenThought(tx, parent.getThoughtID(), projectID, text, type, tags));
        ChildrenThought thought = new ChildrenThoughtImpl(parent.getThoughtID(), thoughtID, tags);
        thought.setText(text);
        thought.setType(type);
        return thought;
    }      
    
    @Override
    public void thoughtHasTopic(Session session, Thought thought, Topic topic, VisibilityProvider.Modifier visibility) throws Exception
    {
        session.executeWriteWithoutResult(tx -> thoughtHasTopic(tx, thought.getThoughtID(), topic.getTopicID(), visibility));       
    }
    
    @Override
    public void thoughtHasGoal(Session session, Thought thought, Goal goal, VisibilityProvider.Modifier visibility) throws Exception
    {
        session.executeWriteWithoutResult(tx -> thoughtHasGoal(tx, thought.getThoughtID(), goal.getGoalID(), visibility));       
    }    
    
    @Override
    public void thoughtHasParent(Session session, Thought thought, Thought parent, VisibilityProvider.Modifier visibility) throws Exception
    {
        session.executeWriteWithoutResult(tx -> thoughtHasParent(tx, thought.getThoughtID(), parent.getThoughtID(), visibility)); 
    }
    
    @Override
    public void removeThought(Session session, String thoughtID) throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }    
    
    @Override
    public String toString()
    {
        return getInstanceName();
    }

    private static class DomainImpl implements Domain
    {
        private final String domainID;
        
        private String name, tag;          

        public DomainImpl(String domainID) 
        {
            this.domainID = domainID;           
        }
                
        @Override
        public String getDomainID() 
        {
            return domainID;
        }
        
        @Override
        public String getName()
        {
            return name;
        }
        
        @Override
        public void setName(String name)
        {
            this.name = name;
        }
        
        @Override
        public String getTag() 
        {
            return tag;
        }

        @Override
        public void setTag(String tag) 
        {
            this.tag = tag;
        }  
        
        @Override
        public String toString()
        {
            return getName();
        }       
    }
    
    private static final class ChildrenDomainImpl extends DomainImpl implements ChildrenDomain
    {
        private String parentID;
        private VisibilityProvider.Modifier modifier;

        public ChildrenDomainImpl(String domainID) 
        {
            super(domainID);
        }
                
        @Override
        public String getParentID() 
        {
            return parentID;
        }

        @Override
        public void setParentID(String parentID)
        {
            this.parentID = parentID;
        }  

        @Override
        public VisibilityProvider.Modifier getModifier() 
        {
            return modifier;
        }

        @Override
        public void setModifier(VisibilityProvider.Modifier modifier) 
        {
            this.modifier = modifier;
        }                
    }         
    
    private static class TopicImpl implements Topic
    {
        private final String topicID;
        
        private String name, tag;

        public TopicImpl(String topicID) 
        {
            this.topicID = topicID;
        }
                
        @Override
        public String getTopicID() 
        {
            return topicID;
        }

        @Override
        public String getName() 
        {
            return name;
        }

        @Override
        public void setName(String name) 
        {
            this.name = name;
        }
        
        @Override
        public String getTag() 
        {
            return tag;
        }

        @Override
        public void setTag(String tag) 
        {
            this.tag = tag;
        }  
        
        @Override
        public String toString()
        {
            return getName();
        }
    }  
    
    private static final class ChildrenTopicImpl extends TopicImpl implements ChildrenTopic, VisibilityProvider
    {
        private String parentID;
        private VisibilityProvider.Modifier modifier;

        public ChildrenTopicImpl(String topicID) 
        {
            super(topicID);
        }
                
        @Override
        public String getParentID() 
        {
            return parentID;
        }

        @Override
        public void setParentID(String parentID)
        {
            this.parentID = parentID;
        }
        
        @Override
        public VisibilityProvider.Modifier getModifier() 
        {
            return modifier;
        }

        @Override
        public void setModifier(VisibilityProvider.Modifier modifier) 
        {
            this.modifier = modifier;
        }        
    }  
    
    private static class ThoughtImpl implements Thought, TagsProvider
    {
        private final String thoughtID;
        
        private String text, tag;
        private Thought.Type type;
        
        private final Set<String> tags;        

        public ThoughtImpl(String thoughtID, Set<String> tags) 
        {
            this.thoughtID = thoughtID;
            this.tags = tags;
        }                
                
        @Override
        public String getThoughtID() 
        {
            return thoughtID;
        }

        @Override
        public String getText() 
        {
            return text;
        }

        @Override
        public void setText(String text) 
        {
            this.text = text;
        }
        
        @Override
        public Thought.Type getType()
        {
            return type;
        }
        
        @Override
        public void setType(Thought.Type type)
        {
            this.type = type;
        }
        
        @Override
        public Set<String> getTags() 
        {
            return Collections.unmodifiableSet(tags);
        }      
        
        @Override
        public String toString()
        {
            return getText();
        }
    } 
    
    private static class ChildrenThoughtImpl extends ThoughtImpl implements ChildrenThought
    {
        private final String parentID;
        
        public ChildrenThoughtImpl(String parentID, String thoughtID, Set<String> tags) 
        {
            super(thoughtID, tags);
            this.parentID = parentID;
        }          

        @Override
        public String getParentID() 
        {
            return parentID;
        }
    }

    private static String createRootDomain(TransactionContext tx, String projectID, String name, String tag)
    {
        var result = tx.run("""
            CREATE (d:Domain {id: randomuuid(), createdDate: datetime(), project: $project, name: $name, tag: $tag})
            RETURN t.id AS ID
        """, Map.of("project", projectID, "name", name, "tag", tag));
        var d = result.single();
        var domainID = d.get("ID").asString();
        return domainID;
    } 
    
    private static void createRootDomain(TransactionContext tx, String projectID, String domainID, String name, String tag) 
    {
        tx.run("""
            CREATE (d:Domain {id: $domainID, createdDate: datetime(), project: $project, name: $name, tag: $tag})
        """, Map.of("project", projectID, "domainID", domainID, "name", name, "tag", tag));
    }  
    
    private static String createChildrenDomain(TransactionContext tx, String parentID, String name, String tag, VisibilityProvider.Modifier modifier) 
    {
        var result = tx.run("""
            CREATE (d:Domain {id: randomuuid(), createdDate: datetime(), name: $name, tag: $tag})
            RETURN d.id AS ID
        """, Map.of("name", name, "tag", tag));
        var d = result.single();
        var domainID = d.get("ID").asString();
        addDomainToParent(tx, parentID, domainID, modifier);
        return domainID;
    }  
    
    private static void createChildrenDomain(TransactionContext tx, String parentID, String domainID, String name, String tag, VisibilityProvider.Modifier modifier) 
    {
        var result = tx.run("""
            CREATE (d:Domain {id: $domainID, createdDate: datetime(), name: $name, tag: $tag})
        """, Map.of("domainID", domainID, "name", name, "tag", tag));
        addDomainToParent(tx, parentID, domainID, modifier);
    }      
    
    private static void addDomainToParent(TransactionContext tx, String parentID, String domainID, VisibilityProvider.Modifier modifier) 
    {
        tx.run("""
            MATCH (d:Domain {id: domainID})
            MATCH (p:Domain {id: $parentID})
            MERGE (t)-[:SUBCLASS {accessibility: $accessibility}]->(p)
            """, Map.of("domainID", domainID, "parentID", parentID, "accessibility", modifier.toString())
        );
    }  
    
    private static String createDomainChannel(TransactionContext tx, String domainID, String name, String props, DomainChannel.Type type) 
    {
        var result = tx.run("""
            CREATE (dc:DomainChannel {id: randomuuid(), createdDate: datetime(), name: $name, props: $props})
            RETURN d.id AS ID
        """, Map.of("name", name, "props", props));
        var d = result.single();
        var channelID = d.get("ID").asString();
        addChannelToDomain(tx, domainID, channelID, type);
        return domainID;
    }      
    
    private static void createDomainChannel(TransactionContext tx, String domainID, String channelID, String name, String props, DomainChannel.Type type) 
    {
        var result = tx.run("""
            CREATE (dc:DomainChannel {id: $channelID, createdDate: datetime(), name: $name, props: $props})
        """, Map.of("channelID", channelID, "name", name, "props", props));
        addChannelToDomain(tx, domainID, channelID, type);
    }  
    
    private static void addChannelToDomain(TransactionContext tx, String domainID, String channelID, DomainChannel.Type type) 
    {
        tx.run("""
            MATCH (dc:DomainChannel {id: channelID})
            MATCH (p:Domain {id: $domainID})
            MERGE (t)-[:DOMAIN {type: $type}]->(p)
            """, Map.of("domainID", domainID, "channelID", channelID, "type", type.toString())
        );
    }      
    
    private static String createRootTopic(TransactionContext tx, String projectID, String name, String tag) 
    {
        var result = tx.run("""
            CREATE (t:Topic {id: randomuuid(), createdDate: datetime(), project: $project, name: $name, tag: $tag})
            RETURN t.id AS ID
        """, Map.of("project", projectID, "name", name, "tag", tag));
        var t = result.single();
        var topicID = t.get("ID").asString();
        return topicID;
    }  
    
    private static void createRootTopic(TransactionContext tx, String projectID, String topicID, String name, String tag) 
    {
        tx.run("""
            CREATE (t:Topic {id: $topicID, createdDate: datetime(), project: $project, name: $name, tag: $tag})
        """, Map.of("project", projectID, "topicID", topicID, "name", name, "tag", tag));
    }     
    
    private static String createChildrenTopic(TransactionContext tx, String parentID, String name, String tag, VisibilityProvider.Modifier modifier) 
    {
        var result = tx.run("""
            CREATE (t:Topic {id: randomuuid(), createdDate: datetime(), name: $name, tag: $tag})
            RETURN t.id AS ID
        """, Map.of("name", name, "tag", tag));
        var t = result.single();
        var topicID = t.get("ID").asString();
        addTopicToParent(tx, parentID, topicID, modifier);
        return topicID;
    }  
    
    private static void createChildrenTopic(TransactionContext tx, String parentID, String topicID, String name, String tag, VisibilityProvider.Modifier modifier) 
    {
        var result = tx.run("""
            CREATE (t:Topic {id: $topicID, createdDate: datetime(), name: $name, tag: $tag})
        """, Map.of("topicID", topicID, "name", name, "tag", tag));
        addTopicToParent(tx, parentID, topicID, modifier);
    }      
    
    private static void addTopicToParent(TransactionContext tx, String parentID, String topicID, VisibilityProvider.Modifier modifier) 
    {
        tx.run("""
            MATCH (t:Topic {id: $topicID})
            MATCH (p:Topic {id: $parentID})
            MERGE (t)-[:SUBCLASS {accessibility: $accessibility}]->(p)
            """, Map.of("topicID", topicID, "parentID", parentID, "accessibility", modifier.toString())
        );
    }   
    
    private static String createRootThought(TransactionContext tx, String projectID, String text, Thought.Type type, Set<String> tags) 
    {
        var result = tx.run("""
            CREATE (t:Thought {id: randomuuid(), createdDate: datetime(), project: $project, text: $text, type: $type, tags: $tags})
            RETURN t.id AS ID
        """, Map.of("project", projectID, "text", text, "type", type.toString(), "tags", tags));
        var t = result.single();
        var thoughtID = t.get("ID").asString();
        return thoughtID;
    }  
    
    private static String createChildrenThought(TransactionContext tx, String parentID, String projectID, String text, Thought.Type type, Set<String> tags) 
    {
        var result = tx.run("""
            CREATE (t:Thought {id: randomuuid(), createdDate: datetime(), project: $project, text: $text, type: $type, tags: $tags})
            RETURN t.id AS ID
        """, Map.of("project", projectID, "text", text, "type", type.toString(), "tags", tags));
        var t = result.single();
        var thoughtID = t.get("ID").asString();
        thoughtHasParent(tx, thoughtID, parentID, VisibilityProvider.Modifier.PUBLIC);
        return thoughtID;
    }      

    private static void thoughtHasTopic(TransactionContext tx, String thoughtID, String topicID, VisibilityProvider.Modifier visibility) 
    {
        tx.run("""
            MATCH (thought:Thought {id: $thoughtID})               
            MATCH (topic:Topic {id: $topicID})
            MERGE (thought)-[:HAS_TOPIC {visibility: $visibility}]->(topic)
            """, Map.of("thoughtID", thoughtID, "topicID", topicID, "visibility", visibility.toString())
        );
    } 
    
    private static void thoughtHasGoal(TransactionContext tx, String thoughtID, String goalID, VisibilityProvider.Modifier visibility) 
    {
        tx.run("""
            MATCH (thought:Thought {id: $thoughtID})               
            MATCH (goal:Goal {id: goalID})
            MERGE (thought)-[:HAS_GOAL {visibility: $visibility}]->(goal)
            """, Map.of("thoughtID", thoughtID, "goalID", goalID, "visibility", visibility.toString())
        );
    }     
    
    private static void thoughtHasParent(TransactionContext tx, String thoughtID, String parentID, VisibilityProvider.Modifier visibility) 
    {
        tx.run("""              
            MATCH (thought:Thought {id: $thoughtID})
            MATCH (parent:Thought {id: $parentID})                
            MERGE (thought)-[:HAS_PARENT {visibility: $visibility}]->(parent)
            """, Map.of("thoughtID", thoughtID, "parentID", parentID, "visibility", visibility.toString())
        );
    }      
    
    private static class GoalImpl implements Goal
    {
        private final String goalID;
        
        private String name, tag, vision, accountability, rewards, obstacles, support, brainstorming;
        private LocalDate startDate, endDate, achievedDate;
        private Level level;
        private boolean achieved;

        public GoalImpl(String goalID) 
        {
            this.goalID = goalID;
        }
                
        @Override
        public String getGoalID() 
        {
            return goalID;
        }

        @Override
        public String getName() 
        {
            return name;
        }

        @Override
        public void setName(String name) 
        {
            this.name = name;
        }
        
        @Override
        public String getTag() 
        {
            return tag;
        }

        @Override
        public void setTag(String tag) 
        {
            this.tag = tag;
        } 
        
        @Override
        public Level getLevel() 
        {
            return level;
        }

        @Override
        public void setLevel(Level level) 
        {
            this.level = level;
        }

        @Override
        public boolean isAchieved() 
        {
            return achieved;
        }

        @Override
        public void setAchieved(boolean achieved) 
        {
            this.achieved = achieved;
        }

        @Override
        public LocalDate getAchievedDate() 
        {
            return achievedDate;
        }

        @Override
        public void setAchievedDate(LocalDate date) 
        {
            achievedDate = date;
        }

        @Override
        public LocalDate getStartDate() 
        {
            return startDate;
        }

        @Override
        public void setStartDate(LocalDate date) 
        {
            startDate = date;
        }

        @Override
        public LocalDate getEndDate() 
        {
            return endDate;
        }

        @Override
        public void setEndDate(LocalDate date) 
        {
            endDate = date;
        }        
        
        @Override
        public String getVision() 
        {
            return vision;
        }

        @Override
        public void setVision(String vision) 
        {
            this.vision = vision;
        } 
        
        @Override
        public String getAccountability() 
        {
            return accountability;
        }

        @Override
        public void setAccountability(String accountability) 
        {
            this.accountability = accountability;
        }   
        
        @Override
        public String getRewards() 
        {
            return rewards;
        }

        @Override
        public void setRewards(String rewards) 
        {
            this.rewards = rewards;
        } 
        
        @Override
        public String getObstacles() 
        {
            return obstacles;
        }

        @Override
        public void setObstacles(String obstacles) 
        {
            this.obstacles = obstacles;
        }  
        
        @Override
        public String getSupport() 
        {
            return support;
        }

        @Override
        public void setSupport(String support) 
        {
            this.support = support;
        } 
        
        @Override
        public String getBrainstorming() 
        {
            return brainstorming;
        }

        @Override
        public void setBrainstorming(String brainstorming) 
        {
            this.brainstorming = brainstorming;
        }          
        
        @Override
        public String toString()
        {
            return getName();
        }
    }  
    
    private static final class ChildrenGoalImpl extends GoalImpl implements ChildrenGoal, VisibilityProvider
    {
        private String parentID;
        private VisibilityProvider.Modifier modifier;

        public ChildrenGoalImpl(String goalID) 
        {
            super(goalID);
        }
                
        @Override
        public String getParentID() 
        {
            return parentID;
        }

        @Override
        public void setParentID(String parentID)
        {
            this.parentID = parentID;
        }
        
        @Override
        public VisibilityProvider.Modifier getModifier() 
        {
            return modifier;
        }

        @Override
        public void setModifier(VisibilityProvider.Modifier modifier) 
        {
            this.modifier = modifier;
        }        
    }  

    private static String createRootGoal(TransactionContext tx, String projectID, String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming) 
    {
        Map<String, Object> map = new HashMap<>();
        map.put("project", projectID);
        map.put("name", name);
        map.put("tag", tag);
        map.put("level", level.toString());           
        map.put("vision", vision);
        map.put("accountability", accountability);
        map.put("rewards", rewards);
        map.put("obstacles", obstacles); 
        map.put("support", support);
        map.put("brainstorming", brainstorming);            
        
        if(startDate == null)
        {
            map.put("startDate", null);
        }
        else
        {
            map.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE));            
        }
        
        if(endDate == null)
        {
            map.put("endDate", null);
        } 
        else
        {
            map.put("endDate", endDate.format(DateTimeFormatter.ISO_DATE));
        }
        
        var result = tx.run("""
            CREATE (goal:Goal {id: randomuuid(), createdDate: datetime(), project: $project, name: $name, tag: $tag, level: $level, startDate: $startDate, endDate: $endDate, vision: $vision, accountability: $accountability, rewards: $rewards, obstacles: $obstacles, support: $support, brainstorming: $brainstorming})
            RETURN goal.id AS ID
        """, map);
        var goal = result.single();
        var goalID = goal.get("ID").asString();
        return goalID;
    }    
    
    private static String createChildrenGoal(TransactionContext tx, String parentID, String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming, VisibilityProvider.Modifier modifier) 
    {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("tag", tag);
        map.put("level", level.toString());           
        map.put("vision", vision);
        map.put("accountability", accountability);
        map.put("rewards", rewards);
        map.put("obstacles", obstacles); 
        map.put("support", support);
        map.put("brainstorming", brainstorming);            
        
        if(startDate == null)
        {
            map.put("startDate", null);
        }
        else
        {
            map.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE));            
        }
        
        if(endDate == null)
        {
            map.put("endDate", null);
        } 
        else
        {
            map.put("endDate", endDate.format(DateTimeFormatter.ISO_DATE));
        }       
        
        var result = tx.run("""
            CREATE (goal:Goal {id: randomuuid(), createdDate: datetime(), name: $name, tag: $tag, level: $level, startDate: $startDate, endDate: $endDate, vision: $vision, accountability: $accountability, rewards: $rewards, obstacles: $obstacles, support: $support, brainstorming: $brainstorming})
            RETURN goal.id AS ID
        """, map);
        var goal = result.single();
        var goalID = goal.get("ID").asString();
        addGoalToParent(tx, parentID, goalID, modifier);
        return goalID;
    }  
    
    private static void addGoalToParent(TransactionContext tx, String parentID, String goalID, VisibilityProvider.Modifier modifier) 
    {
        tx.run("""
            MATCH (goal:Goal {id: $goalID})
            MATCH (parent:Goal {id: $parentID})
            MERGE (goal)-[:SUBCLASS {accessibility: $accessibility}]->(parent)
            """, Map.of("goalID", goalID, "parentID", parentID, "accessibility", modifier.toString())
        );
    }  
}
