package com.example.lmh.util;

import android.util.Log;

import java.sql.*;


class sql_context{
    boolean valid;
    String message;
    String locationx;
    String locationy;
    int dateh;
    int datem;
    String weather;
    sql_context(boolean valid,String message,String locationx,String locationy,int dateh,int datem,String weather){
        this.valid = valid;
        this.message = message;
        this.locationx = locationx;
        this.locationy = locationy;
        this.datem = datem;
        this.dateh = dateh;
        this.weather = weather;
    }
    sql_context(boolean valid){
        this.valid = valid;
    }
}


public class SQLLink{

    // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://129.211.46.169:3306/test?useUnicode=true&characterEncoding=gbk";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "lmh";
    static boolean error = false;
    //message,location,date,weather
    public static boolean insert(String message,String locationx,String locationy,int dateh,int datem,String weather) {
        Connection conn = null;
        Statement stmt = null;
        try{
            error = false;
            Log.i("sql","开始驱动");
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            Log.i("sql","连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            Log.i("sql"," 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            //String temp = new String(message.getBytes("UTF-8"),"UTF-8");
            sql = "insert into lmh_database(message,locationx,locationy,dateh,datem,weather)values(\""+
                    message + "\",\""+ locationx +"\",\"" +locationy+"\","+ dateh + "," + datem+",\"" + weather + "\");";

            Log.i("sql",sql);

            stmt.execute(sql);
            // 完成后关闭
            stmt.close();
            conn.close();
            error = true;
        }catch (SQLException se) {
            Log.e("ERRO1", se.getMessage());
            se.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e("ERRO2", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO3", e.getMessage());}
        finally {
            //关闭资源
            try{
                if(stmt!=null) stmt.close();
            }
            catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }
            catch(SQLException se){
                se.printStackTrace();
            }
        }
        return error;
    }


    //message,location,date,weather
    public static String select(String locationx,String locationy,String weather,int hour,int minute){
        Connection conn = null;
        Statement stmt = null;
        int count = 0;
        String message = null;
        int dateh = 0,datem = 0;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            Log.i("sql","连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            Log.i("sql"," 实例化Statement对象...");
            stmt = conn.createStatement();

            String sql;
            String limit;


            double locx_d = Double.parseDouble(locationx);
            double locy_d = Double.parseDouble(locationy);
            double bound = 1;

            String min_limit = "";
            //00 - 59
            if(minute >= 5 && minute <= 54) {
                min_limit = "datem between " + (minute -5) + " and " + (minute +5);
            }
            else if(minute < 5) {
                min_limit = "datem > " + (minute - 5 + 60) + " and datem < " + (minute + 5);
            }
            else if(minute > 54) {
                min_limit = "datem > " + (minute -5) + " and datem < " + (minute + 5 - 60);
                }

            Log.e("sql",min_limit);


            limit = "locationx between \"" + (locx_d - bound) + "\" and \""  + (locx_d + bound)
                    + "\" and " + "locationy between \"" + (locy_d - bound) + "\" and \"" + (locy_d + bound)
                    + "\"" + " and weather = \"" + weather + "\"" +  " and dateh = " + hour + " and " + min_limit + ";";


            sql = "select message from "+ "lmh_database where " + limit;
            Log.e("sql",sql);

            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                count++;
                String one = rs.getString("message");
                if(count!=1)message = message + "\n\n" + one;
                else message = one;
//                locx = rs.getString("locationx");
//                locy = rs.getString("locationy");
//                dateh = rs.getInt("dateh");
//                datem = rs.getInt("datem");
//                weather = rs.getString("weather");
            }
            Log.i("sql","" + count);
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        if(count == 0) {
            //return new sql_context(false);
            return null;
        }
        else {
            //return new sql_context(true,message,locx,locy,dateh,datem,weather);
            //Log.i("sql","en?" + message);
            return message;
        }
    }
}