package pers.stor;

import java.sql.*;

public class H2TagManager implements TagManager {

    private String url;
    static{
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public H2TagManager(String url){
        this.url = url;
    }
    @Override
    public void insertTag(String tagName, String tagType) throws SQLException {
        String sql = "MERGE INTO tags VALUES(?,?)";
        try(Connection c = DriverManager.getConnection(url);
            PreparedStatement ps = c.prepareStatement(sql)){
                ps.setString(1, tagName);
                ps.setString(2, tagType);

                ps.executeUpdate();
        }
    }

    @Override
    public String getType(String tagName) throws SQLException{
        String sql = "SELECT type FROM tags WHERE name=?";
        String type = "";
        try(Connection c = DriverManager.getConnection(url);
            PreparedStatement ps = c.prepareStatement(sql)){

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    type = rs.getString(1);
            }
        }

        return type;
    }

    @Override
    public void createTable() throws SQLException{
        String sql = "CREATE TABLE IF NOT EXISTS tags (name VARCHAR(64), type VARCHAR(32) )";
        try(Connection c = DriverManager.getConnection(url);
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.executeUpdate();
        }
    }
}
