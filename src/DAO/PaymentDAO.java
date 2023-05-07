package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import DTO.PaymentDTO;
import GUI.LoginGUI;

public class PaymentDAO {
	private Connection con;
	LoginGUI loginGUI = new LoginGUI();
	
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
	
	public ArrayList<PaymentDTO> getAllPayments(){
		ArrayList<PaymentDTO> arr = new ArrayList<PaymentDTO>();
		
		if (openConnection()) {
			try {
				String sql = "Select * from Payment";
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while(rs.next()){
					PaymentDTO em = new PaymentDTO();
					em.setPaymentId(rs.getInt("paymentId"));
					em.setCustomerId(rs.getInt("customerId"));
					em.setStaffId(rs.getInt("staffId"));
					calTotal(rs.getInt("paymentId"));
					em.setCreateDate(rs.getString("createDate"));
					em.setPaymentDate(rs.getString("paymentDate")); 
					em.setTotal(rs.getInt("total"));
					em.setStatus(rs.getBoolean("status")); 

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
	
	public boolean addPayment(PaymentDTO payment) {
		boolean result = false;
		if (openConnection()) {
			try {
				String sql = "Insert into Payment(paymentId, customerId, staffId, status) values(?, ?, ?, ?)"; 
//				String sql = "SELECT Customer.customerId FROM Customer INNER JOIN Payment ON Customer.customerId = Payment;"
				PreparedStatement stmt = con.prepareStatement(sql);
				PaymentDTO em = new PaymentDTO();
				stmt.setInt(1, payment.getPaymentId());
				stmt.setInt(2, payment.getCustomerId());
				stmt.setInt(3, loginGUI.loginUser.getId());
				stmt.setInt(4, 0);
				
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
	
	public boolean editPayment(PaymentDTO payment) {
		boolean result = false;
		
		if (openConnection()) {
			try { 
				String sql = "update Payment set ";
				Statement stmt = con.createStatement();
				
				sql = sql + "paymentId = " + payment.getPaymentId() + ", ";
				sql = sql + "customerId = " + payment.getCustomerId() + ", ";
				sql = sql + "staffId = " + payment.getStaffId() + ", ";
				
				sql = sql + "where paymentId = " + payment.getPaymentId() + ";";
				
				if (stmt.executeUpdate(sql) >= 1)
					result = true;

			} catch (SQLException ex) {
				System.out.println(ex);
			} finally{
				closeConnection();
			} 
		}
		
		return result;
	}
	
	public boolean deletePayment(PaymentDTO payment) {
		boolean result = false;
		if (openConnection()) {
			try {
				String sql = "delete from Payment where paymentId = " + payment.getPaymentId();
				Statement stmt = con.createStatement();
				if (stmt.executeUpdate(sql) >= 1)
					result = true;

			} catch (SQLException ex) {
				System.out.println(ex);
			} finally{
				closeConnection();
			} 
		}
		
		return result;
	}
	

	public boolean hasPaymentId(int id){
		boolean result = false;
		
		if (openConnection()) {
			try {
				String sql = "Select * from Payment where payemntId = " + id;
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				result = rs.next();
			} catch (SQLException ex) {
				System.out.println(ex);
			} finally { closeConnection(); } }
		return result;
	}

	public boolean pay(PaymentDTO payment) {
		boolean result = false;
		
		if (openConnection()) {
			try {
	            String sql = "UPDATE Payment SET paymentDate = ?, status = 1 WHERE paymentId = ?";
	            PreparedStatement stmt = con.prepareStatement(sql);
	            stmt.setDate(1, new java.sql.Date(new Date().getTime())); 
	            stmt.setInt(2, payment.getPaymentId()); 
	            
				if (stmt.executeUpdate() >= 1)
					result = true;
				
			} catch (SQLException ex) {
				System.out.println(ex);
			} finally { closeConnection(); } }
		
		return result;

	}

	public void calTotal(int id) {
		
		if(openConnection()) {
			try {
				int total = 0;
				
				String sql_calTotal_1 = "Select * from Reservations where paymentId = " + id + ";";		
				Statement stmt_1 = con.createStatement();
				
				String sql_calTotal_2 = "SELECT s.* FROM Service s INNER JOIN Orders o ON s.serviceId = o.serviceId INNER JOIN Reservations res ON o.reservationId = res.reservationId WHERE res.paymentId = " + id + ";";
				Statement stmt_2 = con.createStatement();
				
				ResultSet rs_1 = stmt_1.executeQuery(sql_calTotal_1);
				while(rs_1.next())
					total = total + rs_1.getInt("amount");
				
				ResultSet rs_2 = stmt_2.executeQuery(sql_calTotal_2);
				while(rs_2.next())
					total = total + rs_2.getInt("amount");
				
				String sql_updateTotal =  "Update Payment set total = ? where paymentId = " + id +";"; 
				PreparedStatement stmt_3 = con.prepareStatement(sql_updateTotal);
				stmt_3.setInt(1, total);
				
				if (stmt_3.executeUpdate()>=1)
					return;
				
			} catch (SQLException ex) {
				System.out.println(ex);
			} finally { closeConnection(); } 
		}
	}
	
}
