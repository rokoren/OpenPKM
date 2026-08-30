/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package openpkm.core.trello;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javax.swing.DefaultComboBoxModel;
import openpkm.base.Topic;
import openpkm.trello.TrelloAccount;
import openpkm.trello.TrelloAccountsProvider;
import openpkm.trello.TrelloBoard;
import openpkm.utils.TopicNode;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import openpkm.base.TopicsGraphProvider;

/**
 *
 * @author Rok Koren
 */
public class TrelloBoardVisualPanel1 extends javax.swing.JPanel implements ExplorerManager.Provider
{
    private final DefaultComboBoxModel<TrelloBoard> model = new DefaultComboBoxModel<>();
    private final ExplorerManager explorerManager = new ExplorerManager(); 
    
    private JFXPanel panel; 
    private ColorPicker colorPicker;
    
    /**
     * Creates new form TrelloBoardVisualPanel1
     */
    public TrelloBoardVisualPanel1() 
    {
        panel = new JFXPanel();
        setTrelloBoards();
        initComponents();
        jPanel1.add(panel);
        
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {                
                colorPicker = new ColorPicker(Color.BLUE);
                colorPicker.setMaxWidth(Double.MAX_VALUE);  
                colorPicker.setMaxHeight(Double.MAX_VALUE);   
                Scene scene = new Scene(colorPicker);                             
                panel.setScene(scene);                
            }
        });         
    }
    
    @Override
    public String getName() 
    {
        return "Board";
    } 
    
    @Override
    public ExplorerManager getExplorerManager() 
    {
        return explorerManager;
    } 
    
    public TrelloBoard getTrelloBoard()
    {
        return (TrelloBoard)model.getSelectedItem();
    }
    
    public Color getBoardBackground()
    {
        return colorPicker.getValue();
    }
    
    public List<Topic> getBoardTopics()
    {
        Node[] nodes = explorerManager.getSelectedNodes();
        if(nodes.length > 0)
        {
            List<Topic> topics = new ArrayList<>();
            for(Node node : nodes)
            {
                Topic topic = node.getLookup().lookup(Topic.class);
                if(topic != null)
                {
                    topics.add(topic);
                }
            }
            return topics;
        }
        return null;
    }    
    
    public void setTrelloBoards()
    {
        Set<TrelloBoard> boards = new TreeSet<TrelloBoard>(TrelloBoard.nameComparator());
        model.removeAllElements();
        TrelloAccountsProvider provider = Lookup.getDefault().lookup(TrelloAccountsProvider.class);
        if(provider != null)
        {
            for(TrelloAccount account : provider.getAccounts())
            {
                boards.addAll(account.getBoards());
            }            
        }
        model.addAll(boards);       
    }     
    
    public void setTopics(Lookup.Provider provider)
    {
        assert provider != null;        
        TopicsGraphProvider knowledgeGraph = provider.getLookup().lookup(TopicsGraphProvider.class);
        if(knowledgeGraph != null)
        {
            TopicsChildren topics = new TopicsChildren(knowledgeGraph);
            explorerManager.setRootContext(new AbstractNode(topics));
        }        
    }     
    
    static final class TopicsChildren extends Children.Keys<Topic> 
    {
        private final TopicsGraphProvider provider;

        public TopicsChildren(TopicsGraphProvider provider)
        {
            this.provider = provider;
        }  

        @Override
        protected void addNotify() 
        {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            EventQueue.invokeLater(new Runnable() 
            {
                @Override
                public void run()
                {                                                
                    SortedSet<Topic> topics = new TreeSet<Topic>(Topic.nameComparator());
                    topics.addAll(provider.getRootTopics());           
                    setKeys(topics);                   
                }
            });
        }        

        @Override
        protected void removeNotify() 
        {                            
            setKeys(Collections.<Topic>emptySet());
        }

        @Override
        protected Node[] createNodes(Topic topic) 
        {
            return new Node[] {new TopicNode(provider, topic)};
        }           
    }     

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel2 = new javax.swing.JLabel();
        iconView1 = new org.openide.explorer.view.IconView();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TrelloBoardVisualPanel1.class, "TrelloBoardVisualPanel1.jLabel1.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel1, gridBagConstraints);

        jComboBox1.setModel(model);
        jComboBox1.setFocusable(false);
        jComboBox1.setPreferredSize(new java.awt.Dimension(200, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jComboBox1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(filler1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(TrelloBoardVisualPanel1.class, "TrelloBoardVisualPanel1.jLabel2.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(iconView1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(filler2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(TrelloBoardVisualPanel1.class, "TrelloBoardVisualPanel1.jLabel3.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel3, gridBagConstraints);

        jPanel1.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private org.openide.explorer.view.IconView iconView1;
    private javax.swing.JComboBox<TrelloBoard> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
