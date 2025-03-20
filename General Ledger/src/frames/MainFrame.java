/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frames;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;


import javax.swing.JTextField;
import utils.SQLThread;

/**
 *
 * @author netha
 */
public class MainFrame extends javax.swing.JFrame {
    public static int cores = Runtime.getRuntime().availableProcessors();
    private int limit;

    public int getLimit() {
        return limit;
    }
    
    /**
     * @return the accounts
     */
    public List<String> getAccounts() {
        return accounts;
    }

    /**
     * @return the headers
     */
    public List<String> getFields() {
        return fields;
    }
    
    public List<SQLThread> getQueryThreads() {
        return queryThreads;
    }
    
    public int getNoThreads() {
        return noThreads;
    }
    
    /**
     * Creates new form MainFrame
     */
    private List<String> accounts = new ArrayList<>();
    private List<String> fields = new ArrayList<>();
    private List<SQLThread> queryThreads = new ArrayList<>();
    
    public LogInFrame logInFrame;
    public SplashScreen splashFrame;
    public TransactionFrame transactionFrame;
    public LedgerFrame ledgerFrame;
    
    private final int noThreads = MainFrame.cores;


    private int nRows;
    
     public void switchFrames(javax.swing.JPanel firstFrame, javax.swing.JPanel secondFrame) {
        if (secondFrame != null) {
            firstFrame.setVisible(false);        
            secondFrame.setVisible(true);

            for (java.awt.Component comp : firstFrame.getComponents()) {
                if (comp instanceof javax.swing.JTextField) {
                    ((javax.swing.JTextField) comp).setText("");
                }
            }
        }
    }
    
    public MainFrame() {
        this.initComponents();
        this.initFrames();
    }
    
    private void startThreads() {
        long startTime = System.nanoTime();
        Thread splashThread = new Thread(this.splashFrame);
        splashThread.start();
        try {
            SQLThread countThread = new SQLThread("SELECT COUNT(*) FROM tblentry;");
            SQLThread descThread = new SQLThread("DESCRIBE tblentry;");
            countThread.start();
        
            countThread.join();
            ResultSet countRes = countThread.getResultSet();
            if (countRes.next()) {
                this.nRows = countRes.getInt(1);
            }
            
            descThread.start();
            descThread.join();
            ResultSet descRes = descThread.getResultSet();
            System.out.println(descRes.next());
            while (descRes.next()) {
                String fields = descRes.getString(1);
                
                if (!fields.equals("AccountCode") && !fields.equals("Posted")) {
                    this.fields.add(fields);
                }
            }
            
            this.limit = this.nRows/this.noThreads;
            int offset = 0;
            
            for (int i=1; i <= this.noThreads; i++) {
                String statement = String.format("SELECT DISTINCT AccountCode FROM tblentry LIMIT %d OFFSET %d;", limit, offset);
                SQLThread accountThread = new SQLThread(i, statement);
                
                accountThread.start();
                accountThread.join();
                ResultSet accountsRes = accountThread.getResultSet();
                
                while (accountsRes.next()) {
                    this.accounts.add(accountsRes.getString(1));
                }

                offset += limit;
            }
            
            splashThread.join();
        } catch (InterruptedException | SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        long endTime = System.nanoTime();
        System.out.println("Time elapsed: " + (endTime - startTime) / 1_000_000_000.0 + " milliseconds");
    }
        
//    public List<Object> retrieveData(int index) {
//        List<Object> results = new ArrayList<>();
//        for (SQLThread thread : this.getQueryThreads()) {
//            try {
//                thread.join();
//                ResultSet res = thread.getResultSet();
//                ResultSetMetaData metaData = res.getMetaData();
//                int columnType = metaData.getColumnType(index);
//                while (res.next()) {
//                    Object value = getTypedValue(res, index, columnType);
//                    results.add(value);
//                }
//            } catch (InterruptedException | SQLException ex) {
//                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        this.load();
//        return results;
//    }
    
    private Object getTypedValue(ResultSet res, int index, int columnType) throws SQLException {
        return switch (columnType) {
            case Types.INTEGER -> res.getInt(index);
            case Types.DOUBLE, Types.FLOAT -> res.getDouble(index);
            case Types.BOOLEAN -> res.getBoolean(index);
            case Types.DATE -> res.getDate(index);
            case Types.TIMESTAMP -> res.getTimestamp(index);
            default -> res.getString(index);
        };
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1200, 700));
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(null);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void initFrames() {
        splashFrame = new frames.SplashScreen(this);
        logInFrame = new frames.LogInFrame(this);
        transactionFrame = new frames.TransactionFrame(this);
        ledgerFrame = new frames.LedgerFrame(this);
        
        javax.swing.JPanel[] frames = 
        {
            splashFrame, logInFrame, transactionFrame, ledgerFrame
        };
        
        /*Set and Add Panels to Frame*/
        for (javax.swing.JPanel frame : frames) {
            this.add(frame);
            System.out.println(frame);
            frame.setBounds(0, 0, frame.getPreferredSize().width,
                    frame.getPreferredSize().height);
            frame.setBackground(this.getBackground());
        }
        
        for (java.awt.Component comp : getContentPane().getComponents()) {
            if (comp != splashFrame && comp instanceof javax.swing.JPanel) {
                comp.setVisible(false);
            }
        }

        pack();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Flatlaf".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        MainFrame main = new MainFrame();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                main.setVisible(true);
            }
        });
        
        main.startThreads();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
