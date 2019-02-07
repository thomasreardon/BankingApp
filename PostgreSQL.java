
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
public class PostgreSQL {
	Connection connection;
	String url = "jdbc:postgresql://localhost:5432/BankingApp";
	String user = "postgres";
	String password = "password";
	
	public Connection dbConnection() {
		try {
		Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.getMessage();
		}
		try {
			connection = DriverManager.getConnection(url, user, password);
			JOptionPane.showMessageDialog(null, "Connected");
		} catch(SQLException ex) {
			
		Logger.getLogger(PostgreSQL.class.getName()).log(Level.SEVERE, null, ex);
		JOptionPane.showMessageDialog(null, "Failed to connect.");

		}
		return connection;
		}

	public void logIn(StringBuilder email, StringBuilder userpassword) {
	
		double balance = 0;
	//	String sql ="SELECT email, userpassword FROM customers WHERE email = email AND password = userpassword";
		boolean found = false;
		try {
	        	
	    	//	Connection conn = this.dbConnection();
	    		PreparedStatement pstmt = connection.prepareStatement("SELECT email, userpassword, balance, accountnumber FROM accounts WHERE email = email AND userpassword = userpassword");
	    		//System.out.println(pstmt);
	    		//System.out.println(email);
	    		//System.out.println(userpassword);
	    		
	    		ResultSet rs = pstmt.executeQuery();
	    		
	    		char input = 's';
	    		Scanner scanner = new Scanner(System.in);
	    	
	    		while(rs.next()){
	    			
	    			if(rs.getString("email").equals(email.toString()) && !rs.getString("userpassword").equals(userpassword.toString())) {
	    				System.out.println("We found the account registered to your email but the password you entered does not match what we have in our database!" + "\n" + "Please try signing in again.");
	    				found = true;
	    			}

	    			if(rs.getString("email").equals(email.toString()) && rs.getString("userpassword").equals(userpassword.toString())) {
	    				
	    			 found = true;
	    			 balance = rs.getDouble("balance");
	    			 int accountNumber = rs.getInt("accountnumber");
	    			//System.out.println(balance);
	    			
	    			System.out.println("You are signed in!");
	    			
	    			do {
	    			System.out.println("--------------------------------------------------------------------");
	    			System.out.println("A: Press 'A' to make a withdrawl.");
	    			System.out.println("B: Press 'B' to make a deposit.");
	    			System.out.println("C: Press 'C' to transfer money to an account.");
	    			System.out.println("D: Press 'D' to check balance.");
	    			System.out.println("E: Press 'E' to create a new user with joint access to your account.");
	    			System.out.println("F: Press 'F' to transfer money to your saving's account.");
	    			System.out.println(" ");
	    			System.out.println("Press anything else to sign out.");
	    			System.out.println("--------------------------------------------------------------------");
	    			
	    			input = scanner.next().charAt(0);
	    			switch(input) {
		    		case 'A': balance = withdrawal(email, userpassword, balance, accountNumber); break;
		    		case 'B': balance = deposit(email, userpassword, balance, accountNumber); break;
		    		case 'C': balance = transfer(email, userpassword, balance, accountNumber); break;
		    		case 'D': System.out.println("Your balance is $" + balance); break;
		    		case 'E': createJointAccount(balance, accountNumber, "Checking"); break;
		    		case 'F': balance = transferToSavings(email, userpassword, balance, accountNumber); break;
		    		default: System.out.println("Signing out."); break;
		    		
	    			}
	    			
	    		} while(input == 'A' || input == 'B' || input == 'C' || input == 'D' || input == 'E' || input == 'F');
	    			}  
	    			
	    			
	    		}
	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		  if(found == false) {
				System.out.println("We do not have a record registered to that email! Please ensure you entered your email correctly.");
			}
		
	}
	public double transferToSavings(StringBuilder email, StringBuilder password, double balance, int checkingNumber) {
		Scanner scanner = new Scanner(System.in);
		
		
			boolean found = false;	
			
			double recievingBalance = 0;
		 try {
			 	String SQL = "SELECT email, balance, accountnumber, firstname, lastname FROM savingsaccount WHERE savingsaccount.email = ?";
		    		PreparedStatement pstmt = connection.prepareStatement(SQL);		    		
		    		pstmt.setString(1, email.toString());
		    		
		    		ResultSet rs = pstmt.executeQuery();
		    	//	System.out.println(pstmt);
		    		if ( rs.next() ) {
		    			//if(rs.getInt("accountnumber") == accountNumber) {
		    				
		    			 found = true;
		    			 int accountNumber = rs.getInt("accountnumber");
		    			 recievingBalance = rs.getDouble("balance");
		    			// String savingsEmail = rs.getString("email");
		    		//	System.out.println(recievingBalance);
		    			System.out.println("Account found registered to " + rs.getString("firstname") + " " + rs.getString("lastname") + "!");
		    			
		    				System.out.println("How much would you like to transfer?");	
		    				double transferAmount = isNumValid();
		    				
		    				recievingBalance = recievingBalance + transferAmount;
		    				  String SQL2 = "UPDATE savingsaccount "
		    			                + "SET balance = ? "
		    			                + "WHERE accountnumber = ?";
		    			 
		    			      
		    			           PreparedStatement pstmt2 = connection.prepareStatement(SQL2);
		    			        	
		    			            pstmt2.setDouble(1, recievingBalance);
		    			            pstmt2.setInt(2, accountNumber);
		    			 
		    			            pstmt2.executeUpdate();
		    			            
		    			            balance = balance - transferAmount;
				    				  String SQL3 = "UPDATE accounts "
				    			                + "SET balance = ? "
				    			                + "WHERE accountnumber = ?";
				    			 
				    			      
				    			           PreparedStatement pstmt3 = connection.prepareStatement(SQL3);
				    			        	
				    			            pstmt3.setDouble(1, balance);
				    			            pstmt3.setInt(2, checkingNumber);
				    			 
				    			            pstmt3.executeUpdate();
				    			            System.out.println("Transfer successful!");
		    			    //    } 
		    		}
		 }
		         catch (SQLException e) {
		            System.out.println(e.getMessage());
		        }
			  if(found == false) {
					System.out.println("Record not found; please ensure you entered the correct account number.");
					
				}
		 return balance;
	}
	public void savingsLogIn(StringBuilder email, StringBuilder userpassword) {
		
		double balance = 0;
	//	String sql ="SELECT email, userpassword FROM customers WHERE email = email AND password = userpassword";
		boolean found = false;
		try {
	        	
	    	//	Connection conn = this.dbConnection();
	    		PreparedStatement pstmt = connection.prepareStatement("SELECT email, userpassword, balance, accountnumber FROM savingsaccount WHERE email = email AND userpassword = userpassword");
	    	//	System.out.println(pstmt);
	    		//System.out.println(email);
	    		//System.out.println(userpassword);
	    		
	    		ResultSet rs = pstmt.executeQuery();
	    		
	    		char input = 's';
	    		Scanner scanner = new Scanner(System.in);
	    	
	    		while(rs.next()){
	    			
	    			
	    			if(rs.getString("email").equals(email.toString()) && rs.getString("userpassword").equals(userpassword.toString())) {
	    				
	    			 found = true;
	    			 balance = rs.getDouble("balance");
	    			 int accountNumber = rs.getInt("accountnumber");
	    		//	System.out.println(balance);
	     			System.out.println("You are signed in!");
	    			
	    			do {
	    			System.out.println("--------------------------------------------------------------");
	    			System.out.println("A: Press 'A' to make a withdrawl.");
	    			System.out.println("B: Press 'B' to make a deposit.");
	    			System.out.println("C: Press 'C' to make a transfer to someone's Saving's account.");
	    			System.out.println("D: Press 'D' to check balance.");
	    			System.out.println("E: Press 'E' to create a new user for your account.");
	    			System.out.println("F: Press 'F' to transfer funds to your Checking account.");
	    			System.out.println(" ");
	    			System.out.println("Press anything else to sign out.");
	    			System.out.println("--------------------------------------------------------------");
	    			
	    			input = scanner.next().charAt(0);
	    			switch(input) {
		    		case 'A': balance = savingsWithdrawal(email, userpassword, balance); break;
		    		case 'B': balance = savingsDeposit(email, userpassword, balance); break;
		    		case 'C': balance = savingsTransfer(email, userpassword, balance, accountNumber); break;
		    		case 'D': System.out.println("Your balance is $" + balance); break;
		    		case 'E': createJointAccount(balance, accountNumber, "savings"); break;
		    		case 'F': balance = transferToChecking(email, userpassword, balance, accountNumber); break;

		    		default: System.out.println("Signing out."); break;
		    		
	    			}
	    			
	    		} while(input == 'A' || input == 'B' || input == 'C' || input == 'D' || input == 'E' || input == 'F');
	    			}  
	    			
	    			
	    		}
	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		  if(found == false) {
				System.out.println("Record not found; please ensure you entered correct email and password.");
			}
		
	}
	
	public double transferToChecking(StringBuilder email, StringBuilder password, double balance, int checkingNumber) {
		Scanner scanner = new Scanner(System.in);
		
			boolean found = false;	
			
			double recievingBalance = 0;
		 try {
			 	String SQL = "SELECT email, balance, firstname, lastname, accountnumber FROM accounts WHERE accounts.email = ?";
		    		PreparedStatement pstmt = connection.prepareStatement(SQL);		    		
		    		pstmt.setString(1, email.toString());
		    		
		    		ResultSet rs = pstmt.executeQuery();
		    		//System.out.println(pstmt);
		    		if ( rs.next() ) {
		    			//if(rs.getInt("accountnumber") == accountNumber) {
		    				
		    			 found = true;
		    			 int accountNumber = rs.getInt("accountnumber");
		    			 recievingBalance = rs.getDouble("balance");
		    			// String savingsEmail = rs.getString("email");
		    			//System.out.println(recievingBalance);
		    			System.out.println("Found the account owned by " + rs.getString("firstname") + " " + rs.getString("lastname") + "!");
		    					    				
		    				double transferAmount = isNumValid();
		    				
		    				recievingBalance = recievingBalance + transferAmount;
		    				  String SQL2 = "UPDATE accounts "
		    			                + "SET balance = ? "
		    			                + "WHERE accountnumber = ?";
		    			 
		    			      
		    			           PreparedStatement pstmt2 = connection.prepareStatement(SQL2);
		    			        	
		    			            pstmt2.setDouble(1, recievingBalance);
		    			            pstmt2.setInt(2, accountNumber);
		    			 
		    			            pstmt2.executeUpdate();
		    			            
		    			            balance = balance - transferAmount;
				    				  String SQL3 = "UPDATE savingsaccount "
				    			                + "SET balance = ? "
				    			                + "WHERE accountnumber = ?";
				    			 
				    			      
				    			           PreparedStatement pstmt3 = connection.prepareStatement(SQL3);
				    			        	
				    			            pstmt3.setDouble(1, balance);
				    			            pstmt3.setInt(2, checkingNumber);
				    			 
				    			            pstmt3.executeUpdate();
				    			            System.out.println("Transfer successful!");
		    			    //    } 
		    		}
		 }
		         catch (SQLException e) {
		            System.out.println(e.getMessage());
		        }
			  if(found == false) {
					System.out.println("Record not found; please ensure you entered the correct account number.");
					
				}
		 return balance;
	}

	public void createJointAccount(double balance, int accountNumber, String accountType) {
		
		BankingApp user = new BankingApp();
		System.out.println("Please enter an email.");
		Scanner scanner = new Scanner(System.in);
		StringBuilder email = new StringBuilder(scanner.nextLine());
		
	    do {   
	        if(!user.isValid(email)) {
	        	
	            System.out.print("Not a valid email try again." + '\n');
	            System.out.println("Please enter your email.");
	    		email = new StringBuilder(scanner.nextLine());
	    }} while(!user.isValid(email));
	  //  System.out.println(email);
		System.out.println("Please enter your first name.");
		StringBuilder firstName = new StringBuilder(scanner.nextLine());
		do { 
			if(!user.validateName(firstName)) {
				System.out.println("Invalid input try again!");
				System.out.println("Please enter your first name.");
				firstName = new StringBuilder(scanner.nextLine());
				
			}
		} while (!user.validateName(firstName));
		System.out.println("Please enter your last name.");
		StringBuilder lastName = new StringBuilder(scanner.nextLine());
		do { 
			if(!user.validateName(lastName)) {
				System.out.println("Invalid input try again!");
				System.out.println("Please enter your last name.");
				lastName = new StringBuilder(scanner.nextLine());
				
			}
		} while (!user.validateName(lastName));
		StringBuilder password = new StringBuilder();
		StringBuilder confirmPassword = new StringBuilder();

		boolean result = false;
	while(result == false) {

		System.out.println("Please enter a password with atleast 8 characters, one uppercase, one number, and one special character.");
		password = new StringBuilder(scanner.nextLine());
		System.out.println("Please enter password again to confirm.");
		confirmPassword = new StringBuilder(scanner.nextLine());
		if(user.passwordValid(password, confirmPassword)) {
			
			result = true;
		} else {
			System.out.println("Invalid input try again!");
		}
	}
	
		
		if(accountType.equals("Checking")) {
			 try {
		        	Connection conn = dbConnection();
		    		PreparedStatement pstmt = conn.prepareStatement("INSERT INTO accounts(email,firstname,lastname,userpassword,accountnumber, balance) VALUES ('"+email+"','"+firstName+"', '"+lastName+"', '"+password+"', '"+accountNumber+"','"+balance+"')");
		    		pstmt.executeUpdate();
		        } catch (SQLException e) {
		            System.out.println(e.getMessage());
		        }
		        System.out.println("Please sign in to make a deposit.");

			}
		
		else {

	        try {
	        	Connection conn = dbConnection();
	    		PreparedStatement pstmt = conn.prepareStatement("INSERT INTO savingsaccount(email,firstname,lastname,userpassword,accountnumber, balance) VALUES ('"+email+"','"+firstName+"', '"+lastName+"', '"+password+"', '"+accountNumber+"','"+balance+"')");
	    		pstmt.executeUpdate();
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }

		}
	}
	
	public double isNumValid() {
		Scanner scanner = new Scanner(System.in);
		double withdrawAmount = 0;
		
		do {
			try {
				scanner = new Scanner(System.in);
				System.out.println("Please enter an amount greater than 0.");
				withdrawAmount = scanner.nextDouble();
				
				if(withdrawAmount <= 0) {
					System.out.println("Failed to follow simple directions! Enter an amount greater than 0!");

				}

		}
		 catch(InputMismatchException e) {
			
		}
		} while(withdrawAmount <= 0);
		return withdrawAmount;
	}
	public double withdrawal(StringBuilder userEmail, StringBuilder userPassword, double balance, int accountNumber) {
			
			double withdrawAmount = isNumValid();
			balance = balance - withdrawAmount;
			  String SQL = "UPDATE accounts "
		                + "SET balance = ? "
		                + "WHERE accountnumber = ?";
		 
		      
		 
		        try (//Connection conn = connection;
		                PreparedStatement pstmt = connection.prepareStatement(SQL)) {
		        	
		            pstmt.setDouble(1, balance);
		            pstmt.setInt(2, accountNumber);
		 
		            pstmt.executeUpdate();
		            System.out.println("Withdraw successful! Your new balance is $" + balance);
		        } catch (SQLException ex) {
		            System.out.println(ex.getMessage());
		        }
		return balance;

	}
	public double savingsWithdrawal(StringBuilder userEmail, StringBuilder userPassword, double balance) {
		double withdrawAmount = isNumValid();
			balance = balance - withdrawAmount;
			  String SQL = "UPDATE savingsaccount "
		                + "SET balance = ? "
		                + "WHERE email = ?";
		 
		      
		 
		        try (//Connection conn = connection;
		                PreparedStatement pstmt = connection.prepareStatement(SQL)) {
		        	
		            pstmt.setDouble(1, balance);
		            pstmt.setString(2, userEmail.toString());
		 
		            pstmt.executeUpdate();
		            System.out.println("Withdraw successful! Your new balance is $" + balance);
		        } catch (SQLException ex) {
		            System.out.println(ex.getMessage());
		        }
		return balance;

	}
	
	public double savingsAccountValid(int accountNumber, double balance, StringBuilder userEmail, int userAccNumber) {
		 
			boolean found = false;	
			double recievingBalance = 0;
	 try {
		 	String SQL = "SELECT email, balance, firstname, lastname FROM savingsaccount WHERE savingsaccount.accountnumber = ?";
	    		PreparedStatement pstmt = connection.prepareStatement(SQL);		    		
	    		pstmt.setInt(1, accountNumber);
	    		ResultSet rs = pstmt.executeQuery();
	    		
	    		if ( rs.next() ) {
	    			 found = true;
	    			 recievingBalance = rs.getDouble("balance");
	    			 String email = rs.getString("email");
	    			 
	    			System.out.println("Account found registered to " + rs.getString("firstname") + " " + rs.getString("lastname") + " is valid!");   				
	    				double transferAmount = isNumValid();
	    			
	    				recievingBalance = recievingBalance + transferAmount;
	    				  String SQL2 = "UPDATE savingsaccount "
	    			                + "SET balance = ? "
	    			                + "WHERE accountnumber = ?";
	    			    PreparedStatement pstmt2 = connection.prepareStatement(SQL2);
	    			        	
	    			            pstmt2.setDouble(1, recievingBalance);
	    			            pstmt2.setInt(2, accountNumber);
	    			 
	    			            pstmt2.executeUpdate();
	    			            balance = balance - transferAmount;
			    				  String SQL3 = "UPDATE savingsaccount "
			    			                + "SET balance = ? "
			    			                + "WHERE accountnumber = ?";
			    			 
			    			      
			    			           PreparedStatement pstmt3 = connection.prepareStatement(SQL3);
			    			        	
			    			            pstmt3.setDouble(1, balance);
			    			            pstmt3.setInt(2, userAccNumber);
			    			 
			    			            pstmt3.executeUpdate();
			    			            System.out.println("Transfer successful!");
	    		}
	 }
	         catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		  if(found == false) {
				System.out.println("Record not found; please ensure you entered the correct account number.");
				
			}
	 return balance;
 }
	
	 public double accountValid(int accountNumber, double balance, StringBuilder userEmail, int userAccNumber) {	 
			boolean found = false;	
			double recievingBalance = 0;
		 try {
			 	String SQL = "SELECT email, balance, firstname, lastname FROM accounts WHERE accounts.accountnumber = ?";
		    		PreparedStatement pstmt = connection.prepareStatement(SQL);		    		
		    		pstmt.setInt(1, accountNumber);
		    		ResultSet rs = pstmt.executeQuery();
		    		
		    		if ( rs.next() ) {
		    			 found = true;
		    			 recievingBalance = rs.getDouble("balance");
		    			 String email = rs.getString("email");
		     			System.out.println("Account found registered to " + rs.getString("firstname") + " " + rs.getString("lastname") + " is valid!");
		    				double transferAmount = isNumValid();
		    				recievingBalance = recievingBalance + transferAmount;
		    				  String SQL2 = "UPDATE accounts "
		    			                + "SET balance = ? "
		    			                + "WHERE accountnumber = ?";
		    			 
		    			      
		    			           PreparedStatement pstmt2 = connection.prepareStatement(SQL2);
		    			        	
		    			            pstmt2.setDouble(1, recievingBalance);
		    			            pstmt2.setInt(2, accountNumber);
		    			 
		    			            pstmt2.executeUpdate();
		    			          
		    			            
		    			            balance = balance - transferAmount;
				    				  String SQL3 = "UPDATE accounts "
				    			                + "SET balance = ? "
				    			                + "WHERE accountnumber = ?";
				    			 
				    			      
				    			           PreparedStatement pstmt3 = connection.prepareStatement(SQL3);
				    			        	
				    			            pstmt3.setDouble(1, balance);
				    			            pstmt3.setInt(2, userAccNumber);
				    			 
				    			            pstmt3.executeUpdate();
				    			            System.out.println("Transfer successful!");
		    			    //    } 
		    		}
		 }
		         catch (SQLException e) {
		            System.out.println(e.getMessage());
		        }
			  if(found == false) {
					System.out.println("Record not found; please ensure you entered the correct account number.");
					
				}
		 return balance;
	 }
	public double transfer(StringBuilder userEmail, StringBuilder userPassword, double balance, int userAccNumber) {
		Scanner scanner = new Scanner(System.in);
		
		int accountNumber = 0;
		try {
		do {
			System.out.println("Please enter a valid account number you would like to transfer to.");
			accountNumber = scanner.nextInt();
			} while (accountNumber <= 10000 || accountNumber >= 20000);
	}
	 catch(InputMismatchException e) {
		
	}		
			
			balance = accountValid(accountNumber, balance, userEmail, userAccNumber);
		
		
		return balance;
	}
	public double savingsTransfer(StringBuilder userEmail, StringBuilder userPassword, double balance, int userAccNumber) {
		Scanner scanner = new Scanner(System.in);
		
		int accountNumber = 0;
		try {
		do {
			System.out.println("Please enter a valid account number you would like to transfer to.");
			accountNumber = scanner.nextInt();
			} while (accountNumber <= 10000 || accountNumber >= 20000);
		
		}
		 catch(InputMismatchException e) {
			
		}
		balance = savingsAccountValid(accountNumber, balance, userEmail, userAccNumber);
		return balance;
	}
	
	
	 
	public double deposit(StringBuilder email, StringBuilder userPassword, double balance, int accountNumber) {
		double depositAmount = isNumValid();
			balance = depositAmount + balance;
			  String SQL = "UPDATE accounts "
		                + "SET balance = ? "
		                + "WHERE accountnumber = ?";
	 
		        try (
		                PreparedStatement pstmt = connection.prepareStatement(SQL)) {
	//	        	System.out.println(connection);
		            pstmt.setDouble(1, balance);
		            pstmt.setInt(2, accountNumber);
		 
		            pstmt.executeUpdate();
		            System.out.println("Deposit successful! Your new balance is $" + balance);
		        } catch (SQLException ex) {
		            System.out.println(ex.getMessage());
		        }
		return balance;
	    		
}
	public double savingsDeposit(StringBuilder email, StringBuilder userPassword, double balance) {
		
			double depositAmount = isNumValid();
			balance = depositAmount + balance;
			  String SQL = "UPDATE savingsaccount "
		                + "SET balance = ? "
		                + "WHERE email = ?";
	 
		        try (
		                PreparedStatement pstmt = connection.prepareStatement(SQL)) {
		        //	System.out.println(connection);
		            pstmt.setDouble(1, balance);
		            pstmt.setString(2, email.toString());
		 
		            pstmt.executeUpdate();
		            System.out.println("Deposit successful! Your new balance is " + balance);
		        } catch (SQLException ex) {
		            System.out.println(ex.getMessage());
		        }
		return balance;
	    		
}
	public void insertSavings(StringBuilder userEmail, StringBuilder firstname, StringBuilder lastname, StringBuilder userPassword, int accountNumber) {
     
        try {
    		PreparedStatement pstmt = connection.prepareStatement("INSERT INTO savingsaccount(email,firstname,lastname,userpassword,accountnumber, balance) VALUES ('"+userEmail+"','"+firstname+"', '"+lastname+"', '"+userPassword+"', '"+accountNumber+"','"+0+"')");
    		pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Please sign in to make a deposit.");
    }
	
	public void insertChecking(StringBuilder userEmail, StringBuilder firstname, StringBuilder lastname, StringBuilder userPassword, int accountNumber) {
     
        try {
    		PreparedStatement pstmt = connection.prepareStatement("INSERT INTO accounts(email,firstname,lastname,userpassword,accountnumber, balance) VALUES ('"+userEmail+"','"+firstname+"', '"+lastname+"', '"+userPassword+"', '"+accountNumber+"','"+0+"')");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Please sign in to make a deposit.");
    }
	
public boolean checkIfAccountNumExists(int accountNumber, StringBuilder accountType) {
		
		boolean found = false;
		try {
			 	if(accountType.toString().equals("Checking")) {
			 		String SQL = "SELECT accountnumber FROM accounts WHERE accounts.accountnumber = ?";
		    		PreparedStatement pstmt = connection.prepareStatement(SQL);		    		
		    		pstmt.setInt(1, accountNumber);
		    		
		    		ResultSet rs = pstmt.executeQuery();
		    		//System.out.println(pstmt);
		    		if ( rs.next() ) {
		    			//if(rs.getInt("accountnumber") == accountNumber) {
		    			//	System.out.println("An account with your email has already been registered.");
		    			found = true;
		    			}
			 	}
			 	else {
			 	 	String SQL = "SELECT accountnumber FROM savingsaccount WHERE savingsaccount.accountnumber = ?";
			 		PreparedStatement pstmt = connection.prepareStatement(SQL);		    		
			 		pstmt.setInt(1, accountNumber);
			 		
			 		ResultSet rs = pstmt.executeQuery();
			 		//System.out.println(pstmt);
			 		if ( rs.next() ) {
			 			//if(rs.getInt("accountnumber") == accountNumber) {
			 			//System.out.println("An account with your email has already been registered.");

			 			found = true;
			 			}
			 		}
			 			}
			 				 	catch (SQLException e) {
			 			            System.out.println(e.getMessage());
			 			        }		 	
			 			return found;
			 		}
	public boolean checkIfAccountExists(StringBuilder email, StringBuilder accountType) {
		
		boolean found = false;
		try {
			 	if(accountType.toString().equals("Checking")) {
			 		String SQL = "SELECT email FROM accounts WHERE accounts.email = ?";
		    		PreparedStatement pstmt = connection.prepareStatement(SQL);		    		
		    		pstmt.setString(1, email.toString());
		    		
		    		ResultSet rs = pstmt.executeQuery();
		    	//	System.out.println(pstmt);
		    		if ( rs.next() ) {
		    			//if(rs.getInt("accountnumber") == accountNumber) {
		    				System.out.println("An account with your email has already been registered.");
		    			found = true;
		    			}
		    		
			 	
}	 	else {
	
 	String SQL = "SELECT email FROM savingsaccount WHERE savingsaccount.email = ?";
	PreparedStatement pstmt = connection.prepareStatement(SQL);		    		
	pstmt.setString(1, email.toString());
	
	ResultSet rs = pstmt.executeQuery();
	//System.out.println(pstmt);
	if ( rs.next() ) {
		//if(rs.getInt("accountnumber") == accountNumber) {
		System.out.println("An account with your email has already been registered.");

		found = true;
		}
	}
		}
			 	catch (SQLException e) {
		            System.out.println(e.getMessage());
		        }		 	
		return found;
	}
	}
