package pers.db;

import pers.stor.Configuration;

import java.sql.*;

public class H2TagManager implements TagManager {

    private static String url = Configuration.getDbUrl();
    private static String user = Configuration.getDbUser();
    private static String pass = Configuration.getDbPass();
    private static boolean authNeeded = (user != null) && (pass != null);

    public void insertTag(String tagName, String tagType) throws SQLException {
        String sql = "MERGE INTO tags KEY (NAME) VALUES(?,?)";
        try(Connection c = (authNeeded)
                ? DriverManager.getConnection(url, user, pass)
                : DriverManager.getConnection(url);
            PreparedStatement ps = c.prepareStatement(sql)){
                ps.setString(1, tagName);
                ps.setString(2, tagType);

                ps.executeUpdate();
        }
    }

    public String getType(String tagName) throws SQLException{
        String sql = "SELECT type FROM tags WHERE name=?";
        String type = "";
        try(Connection c = (authNeeded)
                ? DriverManager.getConnection(url, user, pass)
                : DriverManager.getConnection(url);
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setString(1, tagName);

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    type = rs.getString(1);
            }
        }

        return type;
    }

    public void createTable() throws SQLException{
        String sql = "CREATE TABLE IF NOT EXISTS tags (name VARCHAR(64), type VARCHAR(32) )";
        try(Connection c = (authNeeded)
                ? DriverManager.getConnection(url, user, pass)
                : DriverManager.getConnection(url);
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.executeUpdate();
        }
    }
}
