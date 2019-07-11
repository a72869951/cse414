import java.sql.*;

public class Query extends QuerySearchOnly {

	// Logged In User
	private String username; // customer username is unique

	// transactions
	private static final String BEGIN_TRANSACTION_SQL = "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;";
	protected PreparedStatement beginTransactionStatement;

	private static final String COMMIT_SQL = "COMMIT TRANSACTION";
	protected PreparedStatement commitTransactionStatement;

	private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";
	protected PreparedStatement rollbackTransactionStatement;

	private static final String LOGIN_SQL =
			"SELECT * " +
					"FROM Users " +
					"WHERE username = ? and password = ?;";
	protected PreparedStatement loginStatement;

	private static final String CHECKUSEREXIST_SQL =
			"SELECT * " +
					"FROM Users " +
					"WHERE username = ?;";
	protected PreparedStatement userExistStatement;

	private static final String CREATECUSTOMER_SQL =
			"INSERT INTO Users " +
					"VALUES(?, ?, ?);";
	protected PreparedStatement NewCustomerStatement;

	private static final String CHECKRESERVETION_SQL =
			"SELECT * FROM Reservations " +
					"WHERE username = ?;";
	protected PreparedStatement checkReservationsStatement;

	private static final String NEW_CAPACITY =
			"INSERT INTO Capacities " +
					"VALUES (?,?);";

	protected PreparedStatement NewCapacityStatement;

	private static final String GET_CAPACITIES =
			"SELECT capacity " +
					"FROM Capacities " +
					"WHERE fid = ?;";
	protected PreparedStatement capacitiesStatement;

	private static final String UPDATE_CAPACITIES =
			"UPDATE Capacities SET capacity = ? - 1 " +
					"WHERE fid = ?;";
	protected PreparedStatement updateCapacitiesStatement;

	private static final String RESERVATION_COUNT =
			"SELECT COUNT(*) " +
					"FROM Reservations;";
	protected PreparedStatement reservationCountStatement;

	private static final String GET_PAID =
			"SELECT * " +
					"FROM Reservations " +
					"WHERE rid = ? and username = ?;";
	protected PreparedStatement PaidStatement;

	private static final String GET_BALANCE =
			"SELECT balance " +
					"FROM Users " +
					"WHERE username = ?;";
	protected PreparedStatement getBalanceStatement;

	private static final String SET_BALANCE =
			"UPDATE USERS SET " +
					"balance = ? WHERE username = ?;";
	private PreparedStatement setBalanceStatement;

	private static final String SET_PAID_STATUS =
			"UPDATE Reservations " +
					"SET paid = ? WHERE rid = ?;";
	protected PreparedStatement setPaidStatusStatement;

	private static final String GET_RESERVATIONS =
			"SELECT *" +
					" FROM Reservations WHERE username = ?;";
	protected PreparedStatement getReservationsStatement;

	private static final String GET_FLIGHT =
			"SELECT * " +
					"FROM Flights " +
					"WHERE fid = ?;";
	protected PreparedStatement getFlightStatement;

	private static final String INSERT_RESERVATION =
			"INSERT INTO Reservations " +
					"VALUES (?,?,?,?,?,?);";
	
	protected PreparedStatement insertReservationStatement;

	public Query(String configFilename) {
		super(configFilename);
	}


	/**
	 * Clear the data in any custom tables created. Do not drop any tables and do not
	 * clear the flights table. You should clear any tables you use to store reservations
	 * and reset the next reservation ID to be 1.
	 */
	public void clearTables () throws SQLException
	{
		beginTransaction();
		Statement clear = conn.createStatement();
		clear.executeUpdate("DELETE FROM Reservations");
		clear.executeUpdate("DELETE FROM Users");
		clear.executeUpdate("DELETE FROM Capacities");
		commitTransaction();
	}


	/**
	 * prepare all the SQL statements in this method.
	 * "preparing" a statement is almost like compiling it.
	 * Note that the parameters (with ?) are still not filled in
	 */
	@Override
	public void prepareStatements() throws Exception
	{
		super.prepareStatements();
		beginTransactionStatement = conn.prepareStatement(BEGIN_TRANSACTION_SQL);
		commitTransactionStatement = conn.prepareStatement(COMMIT_SQL);
		rollbackTransactionStatement = conn.prepareStatement(ROLLBACK_SQL);
		loginStatement = conn.prepareStatement(LOGIN_SQL);
		NewCapacityStatement = conn.prepareStatement(NEW_CAPACITY);
		NewCustomerStatement = conn.prepareStatement(CREATECUSTOMER_SQL);
		checkReservationsStatement = conn.prepareStatement(CHECKRESERVETION_SQL);
		capacitiesStatement = conn.prepareStatement(GET_CAPACITIES);
		updateCapacitiesStatement = conn.prepareStatement(UPDATE_CAPACITIES);
		PaidStatement = conn.prepareStatement(GET_PAID);
		getBalanceStatement = conn.prepareStatement(GET_BALANCE);
		setPaidStatusStatement = conn.prepareStatement(SET_PAID_STATUS);
		setBalanceStatement = conn.prepareStatement(SET_BALANCE);
		getReservationsStatement = conn.prepareStatement(GET_RESERVATIONS);
		getFlightStatement = conn.prepareStatement(GET_FLIGHT);
		insertReservationStatement = conn.prepareStatement(INSERT_RESERVATION);
		reservationCountStatement = conn.prepareStatement(RESERVATION_COUNT);
		userExistStatement = conn.prepareStatement(CHECKUSEREXIST_SQL);


		/* add here more prepare statements for all the other queries you need */
		/* . . . . . . */
	}


	/**
	 * Takes a user's username and password and attempts to log the user in.
	 *
	 * @return If someone has already logged in, then return "User already logged in\n"
	 * For all other errors, return "Login failed\n".
	 *
	 * Otherwise, return "Logged in as [username]\n".
	 */
	public String transaction_login(String username, String password)
	{
		try {
			// If someone has already logged in, then return "User already logged in\n"
			if (this.username != null) {
				return "User already logged in\n";
			}
			beginTransaction();
			loginStatement.clearParameters();
			loginStatement.setString(1, username);
			loginStatement.setString(2, password);
			ResultSet Results = loginStatement.executeQuery();
			if (Results.next()) {
				this.username = username;
				commitTransaction();
				Results.close();
				return "Logged in as " + username + "\n";
			} else {
				rollbackTransaction();
				Results.close();
				return "Login failed\n";
			}
		} catch (SQLException e) { e.printStackTrace();}
		return "Login failed\n";
	}

	/**
	 * Implement the create user function.
	 *
	 * @param username new user's username. User names are unique the system.
	 * @param password new user's password.
	 * @param initAmount initial amount to deposit into the user's account, should be >= 0 (failure otherwise).
	 *
	 * @return either "Created user {@code username}\n" or "Failed to create user\n" if failed.
	 */
	public String transaction_createCustomer (String username, String password, int initAmount)
	{
		try {
			//check if the deposit less than 0
			if (initAmount < 0) {
				return "Failed to create user\n";
			}
			beginTransaction();
			
			// check if the username already exists
			userExistStatement.clearParameters();
			userExistStatement.setString(1, username);
			
			// checkUserExistStatement.setString(2, password);
			ResultSet Results = userExistStatement.executeQuery();
			if (Results.next()) {
				rollbackTransaction();
				return "Failed to create user\n";
			}
			Results.close();
			NewCustomerStatement.clearParameters();
			NewCustomerStatement.setString(1, username);
			NewCustomerStatement.setString(2, password);
			NewCustomerStatement.setInt(3, initAmount);
			NewCustomerStatement.execute();
			commitTransaction();
			return "Created user " + username + "\n";
		} catch (SQLException e) { e.printStackTrace();}
		return "Failed to create user\n";
	}

	/**
	 * Implements the book itinerary function.
	 *
	 * @param itineraryId ID of the itinerary to book. This must be one that is returned by search in the current session.
	 *
	 * @return If the user is not logged in, then return "Cannot book reservations, not logged in\n".
	 * If try to book an itinerary with invalid ID, then return "No such itinerary {@code itineraryId}\n".
	 * If the user already has a reservation on the same day as the one that they are trying to book now, then return
	 * "You cannot book two flights in the same day\n".
	 * For all other errors, return "Booking failed\n".
	 *
	 * And if booking succeeded, return "Booked flight(s), reservation ID: [reservationId]\n" where
	 * reservationId is a unique number in the reservation system that starts from 1 and increments by 1 each time a
	 * successful reservation is made by any user in the system.
	 */
	public String transaction_book(int itineraryId)
	{

		// if the user has not searched yet
		if(this.sortFlights == null){
			return "Booking failed\n";
		}

		// if the user did not log in
		if(this.username == null){
			return "Cannot book reservations, not logged in\n";
		}

		// if user try to book an itineary with invalid ID
		if (itineraryId < 0 || itineraryId >= this.sortFlights.size()){
			return "No such itinerary " + itineraryId + "\n";
		}

		// if the user hasn't searched yet
		if (this.sortFlights.size() <= 0){
			return "Booking failed\n";
		}
		try {

			// check if the user are trying to do a reservation which
			// they already have one at the same day
			Flight userChoose = sortFlights.get(itineraryId);
			beginTransaction();
			checkReservationsStatement.clearParameters();
			checkReservationsStatement.setString(1,this.username);
			ResultSet ReservationResult = checkReservationsStatement.executeQuery();
			while(ReservationResult.next()){
				if (userChoose.fid1 == ReservationResult.getInt("fid1")){
					ReservationResult.close();
					rollbackTransaction();
					return "You cannot book two flights in the same day\n";
				}
			}
			ReservationResult.close();

			// before a user make a reservation, we need to check
			// if there is space for that flight

			// check whether the first flight is in capbility table
			// if not insert the it into capbility
			NewCapacityStatement.clearParameters();
			NewCapacityStatement.setInt(1, userChoose.fid1);
			NewCapacityStatement.setInt(2, userChoose.capacity1);
			NewCapacityStatement.execute();

			// get the flight capacities from the capcitity table
			capacitiesStatement.clearParameters();
			capacitiesStatement.setInt(1,userChoose.fid1);
			ResultSet result1 = capacitiesStatement.executeQuery();
			result1.next();
			int capacity1 = result1.getInt("capacity");
			result1.close();

			// if there is not enough space for this flight, then the book
			// is failed
			if (capacity1 == 0){
				rollbackTransaction();
				return "Booking failed\n";
			}

			// check if there is enough space on the second flight
			int capacity2 = 0;
			if (userChoose.fid2 != 0){

				// check whether the second flight is in capbility table
				// if not insert the it into capbility
				NewCapacityStatement.clearParameters();
				NewCapacityStatement.setInt(1, userChoose.fid2);
				NewCapacityStatement.setInt(2, userChoose.capacity2);
				NewCapacityStatement.execute();

				// get the flight capacities from the capcitity table
				capacitiesStatement.clearParameters();
				capacitiesStatement.setInt(1, userChoose.fid2);
				ResultSet result2 = capacitiesStatement.executeQuery();
				result2.next();
				capacity2 = result2.getInt("capacity");
				result2.close();
				if (capacity2 == 0){
					rollbackTransaction();
					return "Booking failed\n";
				}
			}

			// after the user book a iterary, update the capbilities of flights
			// in capbility
			// first flight
			updateCapacitiesStatement.clearParameters();
			updateCapacitiesStatement.setInt(1,capacity1);
			updateCapacitiesStatement.setInt(2,userChoose.fid1);
			updateCapacitiesStatement.execute();

			// second flight
			if (userChoose.fid2 != 0){
				updateCapacitiesStatement.clearParameters();
				updateCapacitiesStatement.setInt(1,capacity2);
				updateCapacitiesStatement.setInt(2,userChoose.fid2);
				updateCapacitiesStatement.execute();
			}

			// update reservation table and give each reservation
			// a unique ID
			reservationCountStatement.clearParameters();
			ResultSet getResCount = reservationCountStatement.executeQuery();
			getResCount.next();
			int newReservationID = getResCount.getInt(1);
			getResCount.close();
			newReservationID++;

			insertReservationStatement.clearParameters();
			insertReservationStatement.setInt(1, newReservationID);
			insertReservationStatement.setInt(2,userChoose.fid1);
			if(userChoose.fid2 != 0){
				insertReservationStatement.setInt(3, userChoose.fid2);
			} else {
				insertReservationStatement.setInt(3, 0);
			}
			insertReservationStatement.setInt(4, 0);
			insertReservationStatement.setInt(5, userChoose.price1 + userChoose.price2);
			insertReservationStatement.setString(6, this.username);
			insertReservationStatement.executeUpdate();
			commitTransaction();
			return "Booked flight(s), reservation ID: " + newReservationID + "\n";
		}catch (SQLException e) { e.printStackTrace();}
		return "Booking failed\n";
	}

	/**
	 * Implements the pay function.
	 *
	 * @param reservationId the reservation to pay for.
	 * @return If no user has logged in, then return "Cannot pay, not logged in\n"
	 * If the reservation is not found / not under the logged in user's name, then return
	 * "Cannot find unpaid reservation [reservationId] under user: [username]\n"
	 * If the user does not have enough money in their account, then return
	 * "User has only [balance] in account but itinerary costs [cost]\n"
	 * For all other errors, return "Failed to pay for reservation [reservationId]\n"
	 *
	 * If successful, return "Paid reservation: [reservationId] remaining balance: [balance]\n"
	 * where [balance] is the remaining balance in the user's account.
	 */
	public String transaction_pay (int reservationId)
	{
		if (this.username == null){
			return "Cannot pay, not logged in\n";
		}
		try{
			beginTransaction();
			PaidStatement.clearParameters();
			PaidStatement.setInt(1,reservationId);
			PaidStatement.setString(2,this.username);
			ResultSet results = PaidStatement.executeQuery();

			// if the reservation is not found / not under the logged in user's name
			if (!results.next()){
				rollbackTransaction();
				return "Cannot find unpaid reservation " + reservationId + " under user: " + this.username + "\n";
			}
			else{

				// if the reseravtion has already been paid
				int paid = results.getInt("paid");
				if (paid == 1){
					results.close();
					rollbackTransaction();
					return "Cannot find unpaid reservation " + reservationId + " under user: " + this.username + "\n";
				}

				// if the user does not have enough monney in thier account
				int cost = results.getInt("cost");
				getBalanceStatement.clearParameters();
				getBalanceStatement.setString(1,this.username);
				ResultSet getResBalance = getBalanceStatement.executeQuery();
				getResBalance.next();
				int balance = getResBalance.getInt("balance");
				if (cost > balance){
					results.close();
					getResBalance.close();
					rollbackTransaction();
					return "User has only " + balance + " in account but itinerary costs " + cost + "\n";
				}

				// if the user doesn't have a problem, update the balance and
				// the paid status
				setPaidStatusStatement.clearParameters();
				setPaidStatusStatement.setInt(1,1);
				setPaidStatusStatement.setInt(2,reservationId);
				setPaidStatusStatement.executeUpdate();
				setBalanceStatement.clearParameters();
				setBalanceStatement.setInt(1, balance - cost);
				setBalanceStatement.setString(2,this.username);
				setBalanceStatement.executeUpdate();
				commitTransaction();
				return "Paid reservation: " + reservationId + " remaining balance: " + (balance - cost) + "\n";
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return "Failed to pay for reservation " + reservationId + "\n";
	}

	/**
	 * Implements the reservations function.
	 *
	 * @return If no user has logged in, then return "Cannot view reservations, not logged in\n"
	 * If the user has no reservations, then return "No reservations found\n"
	 * For all other errors, return "Failed to retrieve reservations\n"
	 *
	 * Otherwise return the reservations in the following format:
	 *
	 * Reservation [reservation ID] paid: [true or false]:\n"
	 * [flight 1 under the reservation]
	 * [flight 2 under the reservation]
	 * Reservation [reservation ID] paid: [true or false]:\n"
	 * [flight 1 under the reservation]
	 * [flight 2 under the reservation]
	 * ...
	 *
	 * Each flight should be printed using the same format as in the {@code Flight} class.
	 *
	 * @see Flight#toString()
	 */
	public String transaction_reservations()
	{
		if (this.username == null){
		return "Cannot view reservations, not logged in\n";
		}
		try{
			beginTransaction();
			getReservationsStatement.clearParameters();
			getReservationsStatement.setString(1, this.username);
			ResultSet getRes = getReservationsStatement.executeQuery();

			// if the user has reservation
			if (getRes.isBeforeFirst()){
				StringBuilder sb = new StringBuilder();
				while (getRes.next()){
					int resid = getRes.getInt("rid");
					int respaid = getRes.getInt("paid");
					String paid = "";
					if (respaid == 1){
						paid = "true";
					}
					else{
						paid = "false";
					}
					sb.append("Reservation " + resid + " paid: " + paid + ":\n");

					// first flight
					getFlightStatement.clearParameters();
					getFlightStatement.setInt(1,getRes.getInt("fid1"));
					ResultSet getRes1 = getFlightStatement.executeQuery();
					getRes1.next();
					Flight temp = new Flight();
					temp.dayOfMonth1 = getRes1.getInt("day_of_month");
					temp.carrierId1 = getRes1.getString("carrier_id");
					temp.fid1 = getRes1.getInt("fid");
					temp.originCity1 = getRes1.getString("origin_city");
					temp.destCity1 = getRes1.getString("dest_city");
					temp.time1 = getRes1.getInt("actual_time");
					temp.capacity1 = getRes1.getInt("capacity");
					temp.price1 = getRes1.getInt("price");
					temp.flightNum1 = getRes1.getString("flight_num");
					temp.time2 = 0;
					temp.total_time = temp.time1 + temp.time2;
					sb.append(temp.toString2() + "\n");
					getRes1.close();

					// second flight
					getFlightStatement.clearParameters();
					getFlightStatement.setInt(1,getRes.getInt("fid2"));
					ResultSet getRes2 = getFlightStatement.executeQuery();

					// if there is a second filight for this iterary
					if (getRes2.next()) {
						temp = new Flight();
						temp.dayOfMonth1 = getRes2.getInt("day_of_month");
						temp.carrierId1 = getRes2.getString("carrier_id");
						temp.fid1 = getRes2.getInt("fid");
						temp.originCity1 = getRes2.getString("origin_city");
						temp.destCity1 = getRes2.getString("dest_city");
						temp.time1 = getRes2.getInt("actual_time");
						temp.capacity1 = getRes2.getInt("capacity");
						temp.price1 = getRes2.getInt("price");
						temp.flightNum1 = getRes2.getString("flight_num");
						temp.time2 = 0;
						temp.total_time = temp.time1 + temp.time2;
						sb.append(temp.toString2() + "\n");
						getRes2.close();
					}
				}
				getRes.close();
				commitTransaction();
				return sb.toString();

			}

			// if the user doesn't have reservation
			else{
				getRes.close();
				commitTransaction();
				return "No reservations found\n";
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return "Failed to retrieve reservations\n";
	}

	/**
	 * Implements the cancel operation.
	 *
	 * @param reservationId the reservation ID to cancel
	 *
	 * @return If no user has logged in, then return "Cannot cancel reservations, not logged in\n"
	 * For all other errors, return "Failed to cancel reservation [reservationId]"
	 *
	 * If successful, return "Canceled reservation [reservationId]"
	 *
	 * Even though a reservation has been canceled, its ID should not be reused by the system.
	 */
	public String transaction_cancel(int reservationId)
	{
		// only implement this if you are interested in earning extra credit for the HW!
		return "Failed to cancel reservation " + reservationId;
	}


	/* some utility functions below */

	public void beginTransaction() throws SQLException
	{
		conn.setAutoCommit(false);
		beginTransactionStatement.executeUpdate();
	}

	public void commitTransaction() throws SQLException
	{
		commitTransactionStatement.executeUpdate();
		conn.setAutoCommit(true);
	}

	public void rollbackTransaction() throws SQLException
	{
		rollbackTransactionStatement.executeUpdate();
		conn.setAutoCommit(true);
	}
}
