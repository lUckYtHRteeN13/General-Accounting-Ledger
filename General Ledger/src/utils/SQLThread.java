package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author netha
 */
public class SQLThread extends Thread {
    
    public static PreparedStatement stmnt = null;
    public static ResultSet resultSet = null;
    
    private Connection conn = null;
    private String statement = "";
    private String type = "";
    
    private static String database = "jdbc:mysql://localhost:3306/transactions_119";
    private static String username = "root";
    private static String password = "";
    private int threadId;

    public SQLThread(int threadID, String queryStatement) {
       this.threadId = threadID;
       this.statement = queryStatement; 
       this.type = queryStatement.split(" ")[0];
    }
    
    public SQLThread(String queryStatement) {
       this.threadId = 0;
       this.statement = queryStatement; 
       this.type = queryStatement.split(" ")[0];
    }

    public ResultSet getResultSet() throws InterruptedException {
        return this.resultSet;
    }
    
    @Override
    public void run() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(database, username, password);
            this.stmnt = connection.prepareStatement(this.statement);
            
            switch (this.type.toUpperCase()) {
                case "DESCRIBE", "SELECT", "SHOW" -> this.resultSet = this.stmnt.executeQuery();
                case "UPDATE", "DROP", "ALTER", "DELETE", "INSERT" -> this.stmnt.executeUpdate();
            }

        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * @return the threadId
     */
    public int getThreadId() {
        return threadId;
    }
    
    public static void main(String args[]) {
        try {
            String database = "jdbc:mysql://localhost:3306/transactions_119";
            String username = "root";
            String password = "";
            PreparedStatement stmnt = null;
            
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(database, username, password);
            
            stmnt = connection.prepareStatement("SELECT COUNT(*) FROM tblentry WHERE AccountCode='asdadwdasw';");
            stmnt.executeQuery();
     
            ResultSet res = stmnt.getResultSet();
            System.out.println(res.next());
            while (res.next()) {
                System.out.println(res.getInt(1));
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SQLThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}