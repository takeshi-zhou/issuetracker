package cn.edu.fudan.issueservice;

import java.sql.*;

public class Test1 {
    private static String driver = "org.postgresql.Driver";//驱动
    private static String url ="jdbc:postgresql://10.141.221.85:5432/sonar"; //JDBC连接URL
    private static String user = "sonar"; //用户名
    private static String password = "sonar"; //密码

    static {
        try {
            //加载驱动
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("驱动加载出错！");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        };

        return con;
    }

    public static void main(String[] args){
        try(Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from issues where severity=?");
            ps.setString(1,"BLOCKER");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                byte[] locations= resultSet.getBytes("locations");
                String location = new String(locations,"iso-8859-1");
                System.out.println(location);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
