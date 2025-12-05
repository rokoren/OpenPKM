/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.Icon;
import javax.swing.UIManager;
import openpkm.base.Source;
import openpkm.base.SourceProvider;
import openpkm.base.SourceProviders;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ParentProjectProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Rok Koren
 */
public class Utils 
{
    private static final String ICON_KEY_UIMANAGER = "Tree.closedIcon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER = "Tree.openIcon"; // NOI18N
    private static final String ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.icon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.openedIcon"; // NOI18N
    private static final String ICON_PATH = "second/brain/core/resources/defaultFolder.gif"; // NOI18N
    private static final String OPENED_ICON_PATH = "second/brain/core/resources/defaultFolderOpen.gif"; // NOI18N    
    
    private static final Logger LOG = Logger.getLogger(Utils.class.getName());     
    
    public static Properties getProperties(FileObject file) throws IOException
    {
        InputStream input = file.getInputStream();
        Properties props = new Properties();
        props.load(input);
        input.close();   
        return props;
    }       
    
    public static List<FileObject> getDataFiles(FileObject folder)
    {
        FileObject[] files = folder.getChildren();
        List<FileObject> dataFiles = new ArrayList<>(files.length);
        for (FileObject file : files) 
        {
            if(file.isData())
            {
                dataFiles.add(file);                             
            }                           
        }             
        return dataFiles;
    }       
    
    public static FileObject getDirectory(String dir)
    {
        if(dir != null)
        {
            File file = new File(dir);
            if(file.exists() && file.isDirectory())
            { 
                return FileUtil.toFileObject(file); 
            }           
        }
        return null;
    }     
    
    public static Image getTreeFolderIcon(boolean opened) 
    {
        Image base = (Image) UIManager.get(opened ? OPENED_ICON_KEY_UIMANAGER_NB : ICON_KEY_UIMANAGER_NB); // #70263;
        if (base == null) {
            Icon baseIcon = UIManager.getIcon(opened ? OPENED_ICON_KEY_UIMANAGER : ICON_KEY_UIMANAGER); // #70263
            if (baseIcon != null) {
                base = ImageUtilities.icon2Image(baseIcon);
            } else { // fallback to our owns
                base = ImageUtilities.loadImage(opened ? OPENED_ICON_PATH : ICON_PATH, true);
            }
        }
        assert base != null;
        return base;
    }   
    
    public static Comparator objectNameComparator() 
    {
        return new Comparator() 
        {
            private final Collator LOC_COLLATOR = Collator.getInstance();
            public int compare(Object o1, Object o2)
            {
                return LOC_COLLATOR.compare(o1.toString(), o2.toString());
            }
        };
    }     
    
    public static Comparator<Project> projectDisplayNameComparator() {
        return new Comparator<Project>() {
            private final Collator LOC_COLLATOR = Collator.getInstance();
            public int compare(Project o1, Project o2) {
                ProjectInformation i1 = ProjectUtils.getInformation(o1);
                ProjectInformation i2 = ProjectUtils.getInformation(o2);
                int result = LOC_COLLATOR.compare(i1.getDisplayName(), i2.getDisplayName());
                if (result != 0) {
                    return result;
                } else {
                    result = i1.getName().compareTo(i2.getName());
                    if (result != 0) {
                        return result;
                    } else {
                        return System.identityHashCode(o1) - System.identityHashCode(o2);
                    }
                }
            }
        };
    } 
    
    public static Comparator<DataObject> dataLastModifiedComparator() 
    {
        return new Comparator<DataObject>() 
        {
            @Override
            public int compare(DataObject do1, DataObject do2) 
            {
                File file1 = FileUtil.toFile(do1.getPrimaryFile());
                File file2 = FileUtil.toFile(do2.getPrimaryFile());
                Date date1 = new Date(file1.lastModified());
                Date date2 = new Date(file2.lastModified());
                int result = date1.compareTo(date2);
                if (result != 0) 
                {
                    return result;
                } 
                else 
                {
                    result = do1.getName().compareTo(do2.getName());
                    if (result != 0) {
                        return result;
                    } else {
                        return System.identityHashCode(do1) - System.identityHashCode(do2);
                    }
                }
            }
        };
    }     
    
    public static Comparator<Project> projectCreatedTimeComparator() 
    {
        return new Comparator<Project>() 
        {
            @Override
            public int compare(Project project1, Project project2) 
            {
                Long time1 = Long.parseLong(project1.getProjectDirectory().getName());
                Long time2 = Long.parseLong(project2.getProjectDirectory().getName());
                return time1.compareTo(time2);
            }
        };
    }      
    
    public static Comparator<Project> projectNameComparator() {
        return new Comparator<Project>() {
            private final Collator LOC_COLLATOR = Collator.getInstance();
            public int compare(Project o1, Project o2) {
                ProjectInformation i1 = ProjectUtils.getInformation(o1);
                ProjectInformation i2 = ProjectUtils.getInformation(o2);
                int result = LOC_COLLATOR.compare(i1.getName(), i2.getName());
                if (result != 0) {
                    return result;
                } else {
                    result = i1.getName().compareTo(i2.getName());
                    if (result != 0) {
                        return result;
                    } else {
                        return System.identityHashCode(o1) - System.identityHashCode(o2);
                    }
                }
            }
        };
    }     
    
    public static Comparator<DataObject> dataTimeComparator() {
        return new Comparator<DataObject>() {
            public int compare(DataObject do1, DataObject do2) {
                FileObject fo1 = do1.getPrimaryFile();
                FileObject fo2 = do2.getPrimaryFile();
                int result = fo1.lastModified().compareTo(fo2.lastModified());
                if (result != 0) {
                    return result;
                } else {
                    result = do1.getName().compareTo(do2.getName());
                    if (result != 0) {
                        return result;
                    } else {
                        return System.identityHashCode(do1) - System.identityHashCode(do2);
                    }
                }
            }
        };
    } 
    
    public static List<Project> getAllSubprojects(Project project)
    {
        SubprojectProvider provider = project.getLookup().lookup(SubprojectProvider.class);
        if (provider != null)
        {
            List<Project> list = new ArrayList<>();
            for (Project prj : provider.getSubprojects())
            {
                list.add(prj);
                list.addAll(getAllSubprojects(prj));
            } 
            return list;
        }
        return Collections.EMPTY_LIST;
    }        
    
    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
            "%02d:%02d:%02d",
            absSeconds / 3600,
            (absSeconds % 3600) / 60,
            absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }  
    
    public static String getCacheDirectoryPath() {
        try {
            // Obtain the cache directory from the SystemFileSystem
            FileObject cacheDirFileObject = FileUtil.getConfigRoot().getFileObject("Cache");
            
            // If the cache directory is found, return its path
            if (cacheDirFileObject != null) {
                return FileUtil.toFile(cacheDirFileObject).getAbsolutePath();
            }
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }

        return null;
    }  
    
    /*
    public static Topic getTopic(FileObject dir)
    {
        FileObject parent = dir.getParent();
        if(parent != null)
        {
            if(ProjectManager.getDefault().isProject(parent))
            {
                try
                {
                    Project project = ProjectManager.getDefault().findProject(parent);
                    if(project instanceof Topic)
                    {
                        Topic topic = (Topic)project;
                        return topic;
                    }
                    else
                    {
                        return getTopic(parent);
                    }                    
                }
                catch(IOException e)
                {
                    Exceptions.printStackTrace(e);
                }
            }
            else
            {
                return getTopic(parent);
            }
        }
        return null;         
    } 
    */
    
    public static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) 
    {
        // Create a new BufferedImage with the desired width and height
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Get the graphics context of the new image
        Graphics2D g2d = resizedImage.createGraphics();

        // Draw the original image onto the new image, scaling it to the desired size
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        g2d.drawImage(scaledImage, 0, 0, null);

        // Dispose of the graphics context
        g2d.dispose();

        return resizedImage;
    }  
    
    public static BufferedImage resizeImage(BufferedImage originalImage, int width) 
    {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calculate the scaling factor
        double scaleFactor = (double) width / originalWidth;

        // Calculate the new height to maintain the aspect ratio
        int newHeight = (int) (originalHeight * scaleFactor); 

        return resizeImage(originalImage, width, newHeight);
    }     
    
    public static BufferedImage resizeImage(BufferedImage originalImage, String text, Font font) 
    {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();

        // Calculate text width
        int textWidth = fontMetrics.stringWidth(text);        
        
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calculate the new width based on the text length
        int newWidth = Math.min(originalWidth, textWidth);

        // Calculate the scaling factor
        double scaleFactor = (double) newWidth / originalWidth;

        // Calculate the new height to maintain the aspect ratio
        int newHeight = (int) (originalHeight * scaleFactor);        

        // Dispose of the graphics context
        g2d.dispose();

        return resizeImage(originalImage, newWidth, newHeight);
    }   
    
    public static BufferedImage resizeImage(BufferedImage originalImage, String text, Font font, int addedWidth) 
    {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();

        // Calculate text width
        int textWidth = fontMetrics.stringWidth(text) + addedWidth;        
        
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calculate the new width based on the text length
        int newWidth = Math.min(originalWidth, textWidth);

        // Calculate the scaling factor
        double scaleFactor = (double) newWidth / originalWidth;

        // Calculate the new height to maintain the aspect ratio
        int newHeight = (int) (originalHeight * scaleFactor);        

        // Dispose of the graphics context
        g2d.dispose();

        return resizeImage(originalImage, newWidth, newHeight);
    }         
    
    public static String getText(String text, int maxLength)
    {
        if(text.length() > maxLength)
        {
            return text.substring(0, maxLength) + "...";
        }  
        return text;
    }    
    
    public static String convertString(String input, int maxLength) {
        if (input == null || input.length() <= maxLength) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s+"); // Split the input string into words

        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (currentLine.length() + word.length() <= maxLength) {
                currentLine.append(word).append(" ");
            } else {
                result.append(currentLine.toString().trim()).append("\n");
                currentLine = new StringBuilder(word + " ");
            }
        }

        // Append the last line
        result.append(currentLine.toString().trim());

        return result.toString();
    }

    public static String convertStringToHtml(String input, int maxLength) {
        if (input == null || input.length() <= maxLength) {
            return input;
        }

        StringBuilder result = new StringBuilder("<html>");
        String[] words = input.split("\\s+"); // Split the input string into words

        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (currentLine.length() + word.length() <= maxLength) {
                currentLine.append(word).append(" ");
            } else {
                result.append(currentLine.toString().trim()).append("<br>");
                currentLine = new StringBuilder(word + " ");
            }
        }

        // Append the last line
        result.append(currentLine.toString().trim());

        result.append("</html>");
        return result.toString();
    }  

    public static Project getRootProject(Project project)
    {
        ParentProjectProvider provider = project.getLookup().lookup(ParentProjectProvider.class);
        if(provider == null)
        {
            return project;
        }        
        Project parent = provider.getPartentProject();
        if(parent != null)
        {
            return getRootProject(parent);            
        }
        return null;        
    }
    
    public static Source getSource(FileObject file)
    {        
        Project project = FileOwnerQuery.getOwner(file);
        if(project != null)
        {
            SourceProviders providers = project.getLookup().lookup(SourceProviders.class);
            if(providers != null)
            {     
                FileObject fileWithAttrs = providers.getFileWithAttrs(file);
                if(fileWithAttrs != null)
                {
                    String sourceID = (String)fileWithAttrs.getAttribute(SourceProviders.ATTR_SOURCE_ID);
                    String name = (String)fileWithAttrs.getAttribute(SourceProviders.ATTR_SOURCE_PROVIDER); 
                    SourceProvider provider = providers.getSourceProvider(name);
                    if(provider != null)
                    {
                        return provider.getSource(sourceID);
                    }                      
                }            
            }
        } 
        return null;
    }
    
    public static final String getAppID()
    {
        Preferences prefs = Preferences.userRoot().node("openpkm");
        String appId = prefs.get("app.id", null);
        if (appId == null) {
            appId = UUID.randomUUID().toString();
            prefs.put("app.id", appId);
        }  
        return appId;
    }
}
