/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.github;

import java.util.Properties;
import openpkm.base.SourceFactory;

/**
 *
 * @author rok
 */
public interface GitHubFactory extends SourceFactory<GitHubUser>
{
    GitHubUser getGitHubUser(Properties props);     
}
