package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import DTO.CustomerDTO;

public class CustomerDAO {
	
	private Connection con;
	
	public boolean openConnection() {
		try {
//			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			String dbUrl = "jdbc:sqlserver://localhost:1433; DatabaseName=QuanLyKhachSan; encrypt=true;trustServerCertificate=true;";
			String username = "sa"; String password= "sqlmnwk11112003";
			con = DriverManager.getConnection(dbUrl, username, password);
			return true;
			
		} catch (Exception ex) {
			System.out.println(ex);
			return false; 
		}
	}
	
	public void closeConnection() {
		try {
			if (con!=null)
				con.close();
		} catch (SQLException ex) {
			System.out.println(ex); 
		}
	}
	
	public ArrayList<CustomerDTO> getAllCustomers(){
		ArrayList<CustomerDTO> arr = new ArrayList<CustomerDTO>();
		
		if (openConnection()) {
			try {
				String sql = "Select * from Customer";
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while(rs.next()){
					CustomerDTO em = new CustomerDTO();
					em.setId(rs.getInt("customerId"));
					em.setFullname(rs.getString("fullname"));
					em.setPhone(rs.getInt("phone"));
					em.setGender(rs.getInt("gender"));
					em.setBirthday(rs.getString("birthday"));
					em.setAddress(rs.getString("address")); 

					arr.add(em); 
				}
			} catch (SQLException ex) {
				System.out.println(ex);
			} finally {
				closeConnection();
			} 
		}
		
		return arr;
	}
	
	public boolean addCustomer(CustomerDTO cus) {
		boolean result = false;
		if (openConnection()) {
			try {
				String sql = "Insert into Customer(customerId, fullname, phone, gender, birthday, address) values(?, ?, ?, ?, ?, ?)"; 
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setInt(1, cus.getId());
				stmt.setString(2, cus.getFullname());
				stmt.setInt(3, cus.getPhone());
				stmt.setInt(4, cus.getGender());
				stmt.setString(5, cus.getBirthday());
				stmt.setString(6, cus.getAddress());
				
				if (stmt.executeUpdate()>=1)
					result = true;

			} catch (SQLException ex) {
				System.out.println(ex);
			} finally{
				closeConnection();
			} 
		}
		
		return result;
	}
	
	public boolean editCustomer(CustomerDTO cus) {
		boolean result = false;
		if (openConnection()) {
			try { 
				String sql = "update Customer set ";
				Statement stmt = con.createStatement();
				
				sql = sql + "customerId = " + cus.getId() + ", ";
				sql = sql + "fullname = " + "'" + cus.getFullname() + "'" + ", ";
				sql = sql + "phone = " + cus.getPhone() + ", ";
				sql = sql + "gender = " + cus.getGender() + ", ";
				sql = sql + "birthday = " + "'" + cus.getBirthday() + "'" + ", ";
				sql = sql + "address = " + "'" + cus.getAddress() + "'" + " ";
				
				sql = sql + "where customerId = " + cus.getId() + ";";
				
				if (stmt.executeUpdate(sql)>=1)
					result = true;

			} catch (SQLException ex) {
				System.out.println(ex);
			} finally{
				closeConnection();
			} 
		}
		
		return result;
	}
	
	public boolean deleteCustomer(CustomerDTO cus) {
		boolean result = false;
		if (openConnection()) {
			try {
				String sql = "delete from Customer where customerId = " + cus.getId();
				Statement stmt = con.createStatement();
				if (stmt.executeUpdate(sql)>=1)
					result = true;

			} catch (SQLException ex) {
				System.out.println(ex);
			} finally{
				closeConnection();
			} 
		}
		
		return result;
	}
	

	public boolean hasCustomerID(int id){
		boolean result = false;
		
		if (openConnection()) {
			try {
				String sql = "Select * from Customer where customerId = " + id;
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				result = rs.next();
			} catch (SQLException ex) {
				System.out.println(ex);
			} finally { closeConnection(); } }
		return result;
	}
	
	public boolean hasPhone(int phone, int id) {
		boolean result = false;
		
		if (openConnection()) {
			try {
				String sql = "Select * from Customer where phone = " + phone + " and " + "customerId != " + id;
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				result = rs.next();
			} catch (SQLException ex) {
				System.out.println(ex);
			} finally { closeConnection(); } }
		
		return result;
	}
	
	public boolean checkValidPhone(int phone) {
		boolean result = false;
		if(Integer.toString(phone).chars().count() == 10 && Integer.toString(phone).charAt(0) == '0')
			result = true;
			
		return result;
	}
	
	 public boolean checkIfDateIsValid(String date) {
	        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	        format.setLenient(false);
	        try {
	            format.parse(date);
	        } catch (ParseException e) {
	            return false;
	        }
	        return true;
	 }
}	