/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/visualPanel.java to edit this template
 */
package openpkm.raindrop;

import java.awt.Component;
import java.beans.BeanInfo;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import openpkm.base.IconProvider;
import openpkm.base.TitleProvider;
import org.controlsfx.control.CheckComboBox;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.util.ImageUtilities;

public final class CreateRaindropVisualPanel1 extends JPanel 
{
    private final DefaultComboBoxModel<RaindropProject> projects = new DefaultComboBoxModel<>();     
    private final ListCellRenderer renderer = new ListCellRendererImpl();
    private final ObservableList<String> tags = FXCollections.observableArrayList();  
    
    private CheckComboBox<String> comboBox;
    private JFXPanel panel;       
    
    /**
     * Creates new form CreateRaindropVisualPanel1
     */
    public CreateRaindropVisualPanel1() 
    {
        setProjects();
        initComponents();
        
        panel = new JFXPanel();
        jPanel1.add(panel);
        
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {                
                comboBox = new CheckComboBox(tags);
                comboBox.setMaxWidth(Double.MAX_VALUE);  
                comboBox.setMaxHeight(Double.MAX_VALUE);   
                Scene scene = new Scene(comboBox);                             
                panel.setScene(scene);                
            }
        });          
    }

    @Override
    public String getName() 
    {
        return "General";
    }
    
    public RaindropProject getRaindropProject()
    {
        return (RaindropProject)projects.getSelectedItem();
    }
    
    private void setProjects()
    {
        Project mainProject = OpenProjects.getDefault().getMainProject();
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
        SortedSet<RaindropProject> sorted = new TreeSet<RaindropProject>(titleComparator());
        for(Project project : openProjects)
        {
            if(project instanceof RaindropProject raindropProject)
            {
                sorted.add(raindropProject);
            }
        }
        projects.addAll(sorted);
        if(mainProject instanceof RaindropProject raindropProject)
        {
            projects.setSelectedItem(raindropProject);                     
        }
    }
    
    public void setTags(Set<String> projectTags)
    {
        tags.clear();
        tags.addAll(projectTags);
        Collections.sort(tags);        
    }      
    
    private static class ListCellRendererImpl extends JLabel implements ListCellRenderer<RaindropProject>
    {        
        public ListCellRendererImpl() 
        {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEADING);
            setVerticalAlignment(CENTER);
            setIconTextGap(10);
        }        

        @Override
        public Component getListCellRendererComponent(JList list, RaindropProject raindropProject, int index, boolean isSelected, boolean cellHasFocus) 
        {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if(raindropProject == null)
            {
                setText("");
                setIcon(null);
            }
            else
            {  
                TitleProvider titleProvider = raindropProject.getLookup().lookup(TitleProvider.class);
                if(titleProvider != null)
                {
                    setText(titleProvider.getTitle());                     
                }
                
                IconProvider iconProvider = raindropProject.getLookup().lookup(IconProvider.class);
                if(iconProvider != null)
                {
                    setIcon(ImageUtilities.image2Icon(iconProvider.getIcon(BeanInfo.ICON_COLOR_16x16)));                      
                }
            }
            
            return this;
        }
    } 

    private static Comparator<RaindropProject> titleComparator() 
    {
        return new Comparator<RaindropProject>() 
        {
            @Override
            public int compare(RaindropProject project1, RaindropProject project2) 
            {
                TitleProvider titleProvider1 = project1.getLookup().lookup(TitleProvider.class);
                TitleProvider titleProvider2 = project2.getLookup().lookup(TitleProvider.class);
                if(titleProvider1 != null && titleProvider2 != null)
                {
                    return titleProvider1.getTitle().compareTo(titleProvider2.getTitle());                     
                }
                return -1;
            }
        };
    }     

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CreateRaindropVisualPanel1.class, "CreateRaindropVisualPanel1.jLabel1.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel1, gridBagConstraints);

        jComboBox1.setMaximumRowCount(30);
        jComboBox1.setModel(projects);
        jComboBox1.setPreferredSize(new java.awt.Dimension(200, 22));
        jComboBox1.setRenderer(renderer);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(jComboBox1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(filler2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CreateRaindropVisualPanel1.class, "CreateRaindropVisualPanel1.jLabel2.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel2, gridBagConstraints);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler2;
    private javax.swing.JComboBox<RaindropProject> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
