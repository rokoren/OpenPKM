/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.github;

import java.time.LocalDateTime;
import openpkm.base.DescriptionProvider;
import openpkm.base.TitleProvider;

/**
 *
 * @author Rok Koren
 */
public interface GitHubUser extends TitleProvider, DescriptionProvider
{
    String GITHUB_URL = "https://github.com/";
    
    String PROP_USER_ID            = "user.id";
    String PROP_USER_NAME          = "user.name";
    String PROP_AVATAR_URL         = "avatar.url";
    String PROP_HTML_URL           = "html.url";
    String PROP_FOLLOWERS_COUNT    = "followers.count";
    String PROP_PUBLIC_REPOS_COUNT = "public.repos.count";
    String PROP_CREATED_AT         = "created.at";
    String PROP_LOCATION           = "location";
    String PROP_COMPANY            = "company";
    
    String getUserID();
    String getUserName();
    String getAvatarUrl();
    String getHtmlUrl();
    Integer getFollowersCount();
    void setFollowersCount(Integer count);
    Integer getPublicReposCount();
    void setPublicReposCount(Integer count);
    LocalDateTime getCreatedAt();
    String getLocation();
    void setLocation(String location);
    String getCompany();
    void setCompany(String company);
}
