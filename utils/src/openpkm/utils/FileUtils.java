/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author Rok Koren
 */
public class FileUtils
{
    private static final Logger LOG = Logger.getLogger(FileUtils.class.getName());    
    
    public static ActionListener action2open(FileObject file)
    {
        return new ActionListenerImpl(file);
    }
    
    public static MouseListener clicked2open(FileObject file)
    {
        return new MouseListenerImpl(file);
    } 
    
    public static String getFileName(LocalDateTime time, FileObject folder, String extension)
    {
        String name = time.format(DateTimeFormatter.BASIC_ISO_DATE);
        return FileUtil.findFreeFileName(folder, name, extension);
    }   
    
    public static String getFileName(FileObject folder, String extension)
    {
        return getFileName(LocalDateTime.now(), folder, extension);
    }      
    
    private static final class ActionListenerImpl implements ActionListener
    {
        private final FileObject file;

        public ActionListenerImpl(FileObject file) 
        {
            this.file = file;
        }                

        @Override
        public void actionPerformed(ActionEvent event) 
        {
            try
            {
                DataObject data = DataObject.find(file);                    
                OpenCookie open = data.getCookie(OpenCookie.class);
                open.open(); 
            }
            catch(DataObjectNotFoundException e)
            {
                LOG.info(e.getMessage());
            }                     
        }   
    }  
    
    private static final class MouseListenerImpl implements MouseListener
    {
        private final FileObject file;

        public MouseListenerImpl(FileObject file) 
        {
            this.file = file;
        }                  

        @Override
        public void mouseClicked(MouseEvent event) 
        {
            try
            {
                DataObject data = DataObject.find(file);                    
                OpenCookie open = data.getCookie(OpenCookie.class);
                open.open(); 
            }
            catch(DataObjectNotFoundException e)
            {
                LOG.info(e.getMessage());
            }                                     
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }    
}
