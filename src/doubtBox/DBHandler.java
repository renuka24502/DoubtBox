package doubtBox;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.jdbc.pool.OracleDataSource;

/**
* The DBHandler class implements all the methods that are required to access the database for performing various 
* operations 
* @author  Gulshan Singh
* @since   2017-07-30
*/
public class DBHandler
{
	/**
	   * This method establishes database connection with Oracle Database
	   * @return Connection This returns a connection with Oracle Database.
	   */
	public Connection getDBConWithOracle()
	{
		Connection con =null;
		OracleDataSource ods;
		
		try {
			ods = new OracleDataSource();
			ods.setURL("jdbc:oracle:thin:@//localhost:1521/xe");
			con = ods.getConnection("gulshan","icsd");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return con;
		
	}
	
	/**
	   * This method is used to getall the Teacher's details from teacher table
	   * @param strTname This is the first paramter to getTdetais method.
	   * @return Teacher This returns an object of Teacher class
	   */
	public Teacher getTdetais(String strTname)
	{
		Teacher t1 =null;
		Connection con = getDBConWithOracle();
		
		try {
			PreparedStatement stmt = con.prepareStatement("select * from teacher where tname = ?");
			stmt.setString(1, strTname);
			ResultSet rset = stmt.executeQuery();
			if(rset.next())
			{
				String strIP,strTime;
				strIP=rset.getString("ip");
				strTime=rset.getString("timeslot");
				//System.out.println(strIP);
				
				t1 = new Teacher(strTname, strIP, strTime);		
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t1;
	}
	
	/**
	   * This method is used to get Teacher's name from Teacher table
	   * @return Vector<String> This returns a Vector containing names of all the teacher in Teacher table
	   */
	public Vector<String> getTnameTblteacher()
	{
		Vector<String> vctrTdetails = new Vector<String>();
		Connection con = getDBConWithOracle();
		
		try {
		
			PreparedStatement stmt = con.prepareStatement("select * from teacher");
			ResultSet rset = stmt.executeQuery();
			
			
			while(rset.next())
			{
				vctrTdetails.add(rset.getString("tname"));
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return vctrTdetails;
		
	}
	
	
	/**
	   * This method is used to insert values into Teacher table.
	   * @param strTname This is the first paramter to insertIntoTblteacher method
	   * @param strIP  This is the second parameter to insertIntoTblteacher method
	   * @param strTime  This is the second parameter to insertIntoTblteacher method
	   * @return void This returns nothing.
	   */
	public void insertIntoTblteacher(String strTname, String strIP, String strTime)
	{
		//1. Establish the connection with DB
		Connection con = getDBConWithOracle();
		
		//2. Specify Operation
		try {
			PreparedStatement stmt = con.prepareStatement("insert into teacher values(?,?,?)");
			//3. Pass parameters if any
			stmt.setString(1,strTname);
			stmt.setString(2,strIP);
			stmt.setString(3,strTime);
			
			//4. Execute query
			stmt.executeUpdate();
			System.out.println("Data inserted");
			//5. Close your connection
			con.createStatement();stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	   * This method is used to delete values from Teacher table.
	   * @param strIP  This is the first parameter to deleteFromTblteacher method
	   * @return void This returns nothing.
	   */
	public void deleteFromTblteacher(String strIP)
	{
		//1- establish the connection with database
		Connection con=getDBConWithOracle();
		//2- specify your objective
		try {
			PreparedStatement stmt=con.prepareStatement("delete from teacher where ip=?");
			//3- pass the paramter if any
			stmt.setString(1, strIP);
			//4- execute your query
			stmt.executeUpdate();
			//5- close your connection
			con.close();
			System.out.println("Data deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	/**
	   * This method is used to update values of Teacher table.
	   * @param strTname This is the first paramter to updateIntoTblteacher method
	   * @param strIP  This is the second parameter to updateIntoTblteacher method
	   * @param strTime  This is the second parameter to updateIntoTblteacher method
	   * @return void This returns nothing.
	   */
	public void updateIntoTblteacher(String strTname, String strIP, String strTime)
	{
		//1- estalishe the connection
		Connection con=getDBConWithOracle();
		//2- specify your objective
		try {
			PreparedStatement stmt=con.prepareStatement("update teacher set tname=?,timeslot=? where ip=?");
			//3- pass the paramter if any
			stmt.setString(1, strTname);
			stmt.setString(2, strTime);
			stmt.setString(3, strIP);
			//4- execute your query
			stmt.executeUpdate();
			//5- close the connection
			con.close();
			System.out.println("data updated ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	/**
	   * This method is used to validate login details of teacher from USERTEACHER table.
	   * @param strUid This is the first paramter to isValidTeacher method
	   * @param strPwd  This is the second parameter to isValidTeacher method
	   * @return boolean This returns true if the login details are found in Database.
	   */
	public boolean isValidTeacher(String strUid, String strPwd) {
		boolean res = false;
		Connection con = getDBConWithOracle();
		try {
			PreparedStatement stmt = con.prepareStatement("Select * from USERTEACHER where userid=? and upwd=?");
			stmt.setString(1, strUid);
			stmt.setString(2, strPwd);
			ResultSet rset = stmt.executeQuery();
			if(rset.next())
			{
				res = true;
			}
			else
			{
				res = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	   * This method is used to validate login details of student from USERSTUDENT table.
	   * @param strUid This is the first paramter to isValidStudent method
	   * @param strPwd  This is the second parameter to isValidStudent method
	   * @return boolean This returns true if the login details are found in Database.
	   */
	public boolean isValidStudent(String strUid, String strPwd) {
		boolean res = false;
		Connection con = getDBConWithOracle();
		try {
			PreparedStatement stmt = con.prepareStatement("Select * from USERSTUDENT where userid=? and upwd=?");
			stmt.setString(1, strUid);
			stmt.setString(2, strPwd);
			ResultSet rset = stmt.executeQuery();
			if(rset.next())
			{
				res = true;
			}
			else
			{
				res = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}