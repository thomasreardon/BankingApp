
import java.sql.Connection;
import java.util.Scanner;
import java.util.regex.Pattern;

public class BankingApp {
	static PostgreSQL db = new PostgreSQL();
	static Connection conn;

	public static void main(String[] args) {
		
		db.dbConnection();
		 
		showMenu();
	}
	public static void showMenu() {
		
		char userInput = 'x';
		do {
		System.out.println("------------------------------");
		System.out.println("Welcome to the Banking App!" + "\n");
		System.out.println("A: Press 'A' to Sign In"); 
		System.out.println("B: Press 'B' to Create an Account" + "\n");
		System.out.println("Press anything else to exit...");
		System.out.println("------------------------------");
		//looking for a char so we get char from string
		Scanner scanner = new Scanner(System.in);
		userInput = scanner.next().charAt(0);
		
		//print new line
	//	System.out.println("\n");
		
		switch(userInput) {
		case 'A': SignIn(); break;
		case 'B': CreateAccount(); break;
		default: System.out.println("Exiting the app."); break;
		}
	}  while(userInput == 'A' || userInput == 'B');
	}
	public static void CreateAccount() {
		createUser();
	}
	
	public static void SignIn() {
		Scanner scanner = new Scanner(System.in);
		
		StringBuilder accountType = new StringBuilder(setAccount());
		StringBuilder email = new StringBuilder();
		StringBuilder password = new StringBuilder();
		System.out.println("Please enter your email to sign in.");
		email = new StringBuilder(scanner.nextLine());
		  do {   
		    	
		    	if (!isValid(email))  {
		            System.out.print("Not a valid email try again." + '\n');
		            System.out.println("Please enter your email.");
		    		email = new StringBuilder(scanner.nextLine());
		    }} while(!isValid(email));
		  
		  System.out.println("Please enter your password.");
			password = new StringBuilder(scanner.nextLine());
			
			boolean result = false;
			do {
				
			if(passwordValid(password, password)) {
			//	System.out.println("valid password!" + '\n');
				
				if(accountType.toString().equals("Checking")) {
					db.logIn(email,password);
				}
				else {
					db.savingsLogIn(email,password);
				}
				result = true;
			} else {
				System.out.println("Invalid input. Password must contain atleast 8 characters, one uppercase, one number, and one special character.");
				 System.out.println("Please enter your password.");
					password = new StringBuilder(scanner.nextLine());
			} }while(result == false);
		}
		

	public static boolean isValid(StringBuilder email) 
    { 
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                            "[a-zA-Z0-9_+&*-]+)*@" + 
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                            "A-Z]{2,7}$"; 
                              
        Pattern pat = Pattern.compile(emailRegex); 
        if (email == null) 
            return false; 
        return pat.matcher(email).matches(); 
    }
	 public static boolean validateName(StringBuilder name )
	   {
		 String nameRegex = "[A-Z][a-zA-Z]*";
		 Pattern pat = Pattern.compile(nameRegex); 
	        if (name == null) 
	            return false; 
	        return pat.matcher(name).matches(); 
	   } // end method validateFirstName

  
	 public static boolean passwordValid(StringBuilder passwordhere, StringBuilder confirmhere) {

		    Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		    Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
		    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
		    Pattern digitCasePatten = Pattern.compile("[0-9 ]");
		    
		    boolean flag=true;

	    if (!passwordhere.toString().equals(confirmhere.toString())) {
	        System.out.println("Password and confirm password does not match");
		       flag=false;
		   }
		    if (passwordhere.length() < 8) {
		        System.out.println("Password length must have atleast 8 characters!");
		        flag=false;
		    }
		    if (!specailCharPatten.matcher(passwordhere.toString()).find()) {
		        System.out.println("Password must have atleast one special character!");
		        flag=false;
		    }
		    if (!UpperCasePatten.matcher(passwordhere.toString()).find()) {
		    	System.out.println("Password must have atleast one uppercase character!");
		        flag=false;
		    }
		    if (!lowerCasePatten.matcher(passwordhere).find()) {
		        System.out.println("Password must have atleast one lowercase character!");
		        flag=false;
		    }
		    if (!digitCasePatten.matcher(passwordhere).find()) {
		        System.out.println("Password must have atleast one digit!");
		        flag=false;
		    }

		    return flag;

		}
	public static void createUser() {
		
		StringBuilder accountType = new StringBuilder(setAccount());
		
		
		System.out.println("Please enter an email.");
		Scanner scanner = new Scanner(System.in);
		StringBuilder email = new StringBuilder(scanner.nextLine());
		
	    do {   
	    	
	    	if (!isValid(email)) {
	        	
	            System.out.print("Invalid email try again." + '\n');
	            System.out.println("Please enter your email.");
	    		email = new StringBuilder(scanner.nextLine());
	    }} while(!isValid(email));
	  //  System.out.println(email);
		System.out.println("Please enter your first name.");
		StringBuilder firstName = new StringBuilder(scanner.nextLine());
		do { 
			if(!validateName(firstName)) {
				System.out.println("Invalid input try again!");
				System.out.println("Please enter your first name.");
				firstName = new StringBuilder(scanner.nextLine());
			}
		} while (!validateName(firstName));
		System.out.println("Please enter your last name.");
		StringBuilder lastName = new StringBuilder(scanner.nextLine());
		do { 
			if(!validateName(lastName)) {
				System.out.println("Invalid input try again!");
				System.out.println("Please enter your last name.");
				lastName = new StringBuilder(scanner.nextLine());
				
			}
		} while (!validateName(lastName));
		StringBuilder password = new StringBuilder();
		StringBuilder confirmPassword = new StringBuilder();

		boolean result = false;
		System.out.println("Please enter a password with atleast 8 characters, one uppercase, one number, and one special character.");
		password = new StringBuilder(scanner.nextLine());
		System.out.println("Please enter password again to confirm.");
		confirmPassword = new StringBuilder(scanner.nextLine());
	while(!passwordValid(password, confirmPassword)) {

		System.out.println("Please enter a password with atleast 8 characters, one uppercase, one number, and one special character.");
		password = new StringBuilder(scanner.nextLine());
		System.out.println("Please enter password again to confirm.");
		confirmPassword = new StringBuilder(scanner.nextLine());
		
	}
	int accountNumber = (int) (Math.random() * 10000) + 10000;
		while(db.checkIfAccountNumExists(accountNumber, accountType)) {
			accountNumber = (int) (Math.random() * 10000) + 10000;
		}
		
	//	System.out.println(accountNumber);
		// db.dbConnection();
		if(!db.checkIfAccountExists(email, accountType)) { 
		if(accountType.toString().equals("Checking")) {
		db.insertChecking(email, firstName, lastName, password, accountNumber);
		}
		else {
			db.insertSavings(email, firstName, lastName, password, accountNumber);

		}
		}
	}
	
	 public static StringBuilder setAccount() {
		 char userInput = 'x';
	 while(userInput != 'A' && userInput != 'B') {
			
			System.out.println("A: Press 'A' to access a Checking's Account");
			System.out.println("B: Press 'B' to access a Saving's Account");
			
			Scanner scanner = new Scanner(System.in);
			userInput = scanner.next().charAt(0);
				if(userInput != 'A' && userInput != 'B') {
					System.out.println("Please enter 'A' or 'B' to choose an option.");
				}
			} 
	 	StringBuilder accountType = new StringBuilder();
			if(userInput == 'A') {
				accountType.append("Checking");
		
			}
			else {
				accountType.append("Saving");
			}
			return accountType;
	 }
}