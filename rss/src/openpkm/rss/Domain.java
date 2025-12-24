/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.rss;

/**
 *
 * @author Rok Koren
 */
public interface Domain 
{
    String PROP_DOMAIN_ID       = "domain.id";
    String PROP_HOME_PAGE       = "home.page";
    String PROP_YOUTUBE_CHANNEL = "youtube.channel";
    String PROP_GITHUB          = "github";
    String PROP_FACEBOOK        = "facebook";
    String PROP_INSTAGRAM       = "instagram";
    String PROP_LINKEDIN        = "linkedin";
    String PROP_TWITTER         = "twitter";
    String PROP_TELEGRAM        = "telegram";
    String PROP_RSS             = "rss";
    
    String PROJECT_FOLDER = "openpkm-domain";     
    String PROJECT_FILE   = "project.properties";    
    
    String getDomainID();
    String getHomePage();
    void setHomePage(String homePage);
    String getYouTubeChannel();
    void setYouTubeChannel(String channelID);
    String getGitHub();
    void setGitHub(String github);
    String getFacebook();
    String getInstagram();
    String getLinkedIn();
    String getTwitter();
    String getTelegram();
    String getRSS();
}
