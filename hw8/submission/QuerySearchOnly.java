import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Collections;
import java.util.*;

/**
 * Runs queries against a back-end database.
 * This class is responsible for searching for flights.
 */
public class QuerySearchOnly
{
  // `dbconn.properties` config file
  private String configFilename;

  // DB Connection
  protected Connection conn;

  // Canned queries
  private static final String CHECK_FLIGHT_CAPACITY = "SELECT capacity FROM Flights WHERE fid = ?";
  private static final String DIRECT_FLIGHT_RESULT =
          "SELECT TOP (?) day_of_month,carrier_id,flight_num,origin_city,dest_city,actual_time,capacity,price,fid "
          + "FROM Flights "
          + "WHERE origin_city = ? AND dest_city = ? AND day_of_month = ? AND canceled = 0 "
          + "ORDER BY actual_time, fid ASC";
  private static final String INDIRECT_FLIGHT_RESULT =
          "SELECT TOP (?) F1.fid as fid1, F1.carrier_id as cid1, " +
                  "F1.day_of_month as day_of_month1, F2.day_of_month as day_of_month2, " +
                  "F1.flight_num as flight_num1, F1.origin_city as origin_city1, " +
                  "F1.dest_city as dest_city1, F1.actual_time as actual_time1, " +
                  "F2.fid as fid2, F2.carrier_id as cid2, " +
                  "F2.flight_num as flight_num2, F2.origin_city as origin_city2, " +
                  "F2.dest_city as dest_city2, F2.actual_time as actual_time2, " +
                  "F1.capacity AS capacity1, F2.capacity AS capacity2, " +
                  "F1.price AS price1, F2.price AS price2 "+
                  "FROM Flights F1, Flights F2 " +
                  "WHERE F1.actual_time IS NOT NULL AND " +
                  "F2.actual_time IS NOT NULL AND " +
                  "F1.day_of_month = ? AND " +
                  "F2.day_of_month = ? AND " +
                  "F1.origin_city = ? AND F2.dest_city = ? AND " +
                  "F1.dest_city = F2.origin_city AND " +
                  "F1.canceled = 0 AND F2.canceled = 0 " +
                  "ORDER BY F1.actual_time + F2.actual_time, F1.fid, F2.fid ASC";
  protected PreparedStatement checkFlightCapacityStatement;
  protected PreparedStatement directFlightStatement;
  protected PreparedStatement indirectFlightStatement;

  // store sortFlight in the fleid, so it can be used in book method
  public List<Flight> sortFlights;


  class Flight implements Comparable<Flight>
  {
    public int fid1;
    public int dayOfMonth1;
    public String carrierId1;
    public String flightNum1;
    public String originCity1;
    public String destCity1;
    public int time1;
    public int capacity1;
    public int price1;
    public int fid2;
    public int dayOfMonth2;
    public String carrierId2;
    public String flightNum2;
    public String originCity2;
    public String destCity2;
    public int time2;
    public int capacity2;
    public int price2;
    public int total_time;

    @Override
    public String toString()
    { if (time2 == 0) {
        return "1 flight(s), " + total_time + " minutes" + "\n" + "ID: " + fid1 + " Day: " + dayOfMonth1 + " Carrier: " + carrierId1 +
              " Number: " + flightNum1 + " Origin: " + originCity1 + " Dest: " + destCity1 + " Duration: " + time1 +
              " Capacity: " + capacity1 + " Price: " + price1;
      } else {
        return "2 flight(s), " + total_time + " minutes" + "\n" + "ID: " + fid1 + " Day: " + dayOfMonth1 + " Carrier: " + carrierId1 +
      " Number: " + flightNum1 + " Origin: " + originCity1 + " Dest: " + destCity1 + " Duration: " + time1 +
              " Capacity: " + capacity1 + " Price: " + price1 + "\n" +
            "ID: " + fid2 + " Day: " + dayOfMonth2 + " Carrier: " + carrierId2 +
      " Number: " + flightNum2 + " Origin: " + originCity2 + " Dest: " + destCity2 + " Duration: " + time2 +
              " Capacity: " + capacity2 + " Price: " + price2;
      }
    }

    public String toString2()
    {
      return  "ID: " + fid1 + " Day: " + dayOfMonth1 + " Carrier: " + carrierId1 +
              " Number: " + flightNum1 + " Origin: " + originCity1 + " Dest: " + destCity1 + " Duration: " + time1 +
              " Capacity: " + capacity1 + " Price: " + price1;
    }
    @Override
    public int compareTo(Flight other) {
      int result1 = Integer.compare(this.total_time, other.total_time);
      if (result1 != 0) {
        return result1;
      } else {
        int result2 = Integer.compare(this.fid1, other.fid1);
        if (result2 != 0) {
          return result2;
        } else {
          return Integer.compare(this.fid2, other.fid2);
        }
      }
    }
  }

  public QuerySearchOnly(String configFilename)
  {
    this.configFilename = configFilename;
  }

  /** Open a connection to SQL Server in Microsoft Azure.  */
  public void openConnection() throws Exception
  {
    Properties configProps = new Properties();
    configProps.load(new FileInputStream(configFilename));

    String jSQLDriver = configProps.getProperty("flightservice.jdbc_driver");
    String jSQLUrl = configProps.getProperty("flightservice.url");
    String jSQLUser = configProps.getProperty("flightservice.sqlazure_username");
    String jSQLPassword = configProps.getProperty("flightservice.sqlazure_password");

    /* load jdbc drivers */
    Class.forName(jSQLDriver).newInstance();

    /* open connections to the flights database */
    conn = DriverManager.getConnection(jSQLUrl, // database
            jSQLUser, // user
            jSQLPassword); // password

    conn.setAutoCommit(true); //by default automatically commit after each statement
    /* In the full Query class, you will also want to appropriately set the transaction's isolation level:
          conn.setTransactionIsolation(...)
       See Connection class's JavaDoc for details.
    */
  }

  public void closeConnection() throws Exception
  {
    conn.close();
  }

  /**
   * prepare all the SQL statements in this method.
   * "preparing" a statement is almost like compiling it.
   * Note that the parameters (with ?) are still not filled in
   */
  public void prepareStatements() throws Exception
  {
    checkFlightCapacityStatement = conn.prepareStatement(CHECK_FLIGHT_CAPACITY);

    /* add here more prepare statements for all the other queries you need */
    /* . . . . . . */
    directFlightStatement = conn.prepareStatement(DIRECT_FLIGHT_RESULT);
    indirectFlightStatement = conn.prepareStatement(INDIRECT_FLIGHT_RESULT);
  }



  /**
   * Implement the search function.
   *
   * Searches for flights from the given origin city to the given destination
   * city, on the given day of the month. If {@code directFlight} is true, it only
   * searches for direct flights, otherwise it searches for direct flights
   * and flights with two "hops." Only searches for up to the number of
   * itineraries given by {@code numberOfItineraries}.
   *
   * The results are sorted based on total flight time.
   *
   * @param originCity
   * @param destinationCity
   * @param directFlight if true, then only search for direct flights, otherwise include indirect flights as well
   * @param dayOfMonth
   * @param numberOfItineraries number of itineraries to return
   *
   * @return If no itineraries were found, return "No flights match your selection\n".
   * If an error occurs, then return "Failed to search\n".
   *
   * Otherwise, the sorted itineraries printed in the following format:
   *
   * Itinerary [itinerary number]: [number of flights] flight(s), [total flight time] minutes\n
   * [first flight in itinerary]\n
   * ...
   * [last flight in itinerary]\n
   *
   * Each flight should be printed using the same format as in the {@code Flight} class. Itinerary numbers
   * in each search should always start from 0 and increase by 1.
   *
   * @see Flight#toString()
   */
  public String transaction_search(String originCity, String destinationCity, boolean directFlight, int dayOfMonth,
                                   int numberOfItineraries)
  {
    // Please implement your own (safe) version that uses prepared statements rather than string concatenation.
    // You may use the `Flight` class (defined above).
    // use a list in order to sort the result
    sortFlights = new ArrayList<Flight>();

    // user need direct flight
    int count = 0;
    StringBuffer sb = new StringBuffer();
    try
    {
      // one hop itineraries
      directFlightStatement.clearParameters();
      directFlightStatement.setInt(1, numberOfItineraries);
      directFlightStatement.setString(2, originCity);
      directFlightStatement.setString(3, destinationCity);
      directFlightStatement.setInt(4, dayOfMonth);
      ResultSet oneHopResults = directFlightStatement.executeQuery();

      while (oneHopResults.next())
      {
        Flight temp = new Flight();
        temp.dayOfMonth1 = oneHopResults.getInt("day_of_month");
        temp.carrierId1 = oneHopResults.getString("carrier_id");
        temp.fid1 = oneHopResults.getInt("fid");
        temp.originCity1 = oneHopResults.getString("origin_city");
        temp.destCity1 = oneHopResults.getString("dest_city");
        temp.time1 = oneHopResults.getInt("actual_time");
        temp.capacity1 = oneHopResults.getInt("capacity");
        temp.price1 = oneHopResults.getInt("price");
        temp.flightNum1 = oneHopResults.getString("flight_num");
        temp.time2 = 0;
        temp.total_time = temp.time1 + temp.time2;
        sortFlights.add(temp);

        // sb.append("Itinerary ").append(count).append(": ")
        //         .append(temp.toString()).append('\n');
        count++;
      }
      oneHopResults.close();
    } catch (SQLException e) { e.printStackTrace(); }

    // if user accept indirect flight and there are not enough direct itineraries
    if (!directFlight && count < numberOfItineraries) {
      int numberOfTwoHop = numberOfItineraries - count;

      // search for indirect flight
      try
      {
        // one hop itineraries
        indirectFlightStatement.clearParameters();
        indirectFlightStatement.setInt(1, numberOfTwoHop);
        indirectFlightStatement.setInt(2, dayOfMonth);
        indirectFlightStatement.setInt(3, dayOfMonth);
        indirectFlightStatement.setString(4, originCity);
        indirectFlightStatement.setString(5, destinationCity);
        ResultSet twoHopResults = indirectFlightStatement.executeQuery();

        while (twoHopResults.next())
        {
          Flight temp = new Flight();
          temp.dayOfMonth1 = twoHopResults.getInt("day_of_month1");
          temp.carrierId1 = twoHopResults.getString("cid1");
          temp.fid1 = twoHopResults.getInt("fid1");
          temp.originCity1 = twoHopResults.getString("origin_city1");
          temp.destCity1 = twoHopResults.getString("dest_city1");
          temp.time1 = twoHopResults.getInt("actual_time1");
          temp.capacity1 = twoHopResults.getInt("capacity1");
          temp.price1 = twoHopResults.getInt("price1");
          temp.flightNum1 = twoHopResults.getString("flight_num1");


          temp.dayOfMonth2 = twoHopResults.getInt("day_of_month2");
          temp.carrierId2 = twoHopResults.getString("cid2");
          temp.fid2 = twoHopResults.getInt("fid2");
          temp.originCity2 = twoHopResults.getString("origin_city2");
          temp.destCity2 = twoHopResults.getString("dest_city2");
          temp.time2 = twoHopResults.getInt("actual_time2");
          temp.capacity2 = twoHopResults.getInt("capacity2");
          temp.price2 = twoHopResults.getInt("price2");
          temp.flightNum2 = twoHopResults.getString("flight_num2");
          temp.total_time = temp.time1 + temp.time2;
          sortFlights.add(temp);
          count++;
        }
        twoHopResults.close();
      } catch (SQLException e) { e.printStackTrace();}
      }
      // sort the result if there is two-hop flight
      int countTwo = 0;
      Collections.sort(sortFlights);
      for (Flight each : sortFlights) {
        sb.append("Itinerary ").append(countTwo).append(": ")
                .append(each.toString()).append('\n');
        countTwo++;
      }
    // return transaction_search_unsafe(originCity, destinationCity, directFlight, dayOfMonth, numberOfItineraries);

    // if no flgihts match the selection
    if (count == 0) {
      return "No flights match your selection" + "\n";
    }
    return sb.toString();
  }

  /**
   * Same as {@code transaction_search} except that it only performs single hop search and
   * do it in an unsafe manner.
   *
   * @param originCity
   * @param destinationCity
   * @param directFlight
   * @param dayOfMonth
   * @param numberOfItineraries
   *
   * @return The search results. Note that this implementation *does not conform* to the format required by
   * {@code transaction_search}.
   */
  private String transaction_search_unsafe(String originCity, String destinationCity, boolean directFlight,
                                          int dayOfMonth, int numberOfItineraries)
  {
    StringBuffer sb = new StringBuffer();

    try
    {
      // one hop itineraries
      String unsafeSearchSQL =
              "SELECT TOP (" + numberOfItineraries + ") day_of_month,carrier_id,flight_num,origin_city,dest_city,actual_time,capacity,price "
                      + "FROM Flights "
                      + "WHERE origin_city = \'" + originCity + "\' AND dest_city = \'" + destinationCity + "\' AND day_of_month =  " + dayOfMonth + " "
                      + "ORDER BY actual_time ASC";

      Statement searchStatement = conn.createStatement();
      ResultSet oneHopResults = searchStatement.executeQuery(unsafeSearchSQL);

      while (oneHopResults.next())
      {
        int result_dayOfMonth = oneHopResults.getInt("day_of_month");
        String result_carrierId = oneHopResults.getString("carrier_id");
        String result_flightNum = oneHopResults.getString("flight_num");
        String result_originCity = oneHopResults.getString("origin_city");
        String result_destCity = oneHopResults.getString("dest_city");
        int result_time = oneHopResults.getInt("actual_time");
        int result_capacity = oneHopResults.getInt("capacity");
        int result_price = oneHopResults.getInt("price");

        sb.append("Day: ").append(result_dayOfMonth)
                .append(" Carrier: ").append(result_carrierId)
                .append(" Number: ").append(result_flightNum)
                .append(" Origin: ").append(result_originCity)
                .append(" Destination: ").append(result_destCity)
                .append(" Duration: ").append(result_time)
                .append(" Capacity: ").append(result_capacity)
                .append(" Price: ").append(result_price)
                .append('\n');
      }
      oneHopResults.close();
    } catch (SQLException e) { e.printStackTrace(); }

    return sb.toString();
  }

  /**
   * Shows an example of using PreparedStatements after setting arguments.
   * You don't need to use this method if you don't want to.
   */
  private int checkFlightCapacity(int fid) throws SQLException
  {
    checkFlightCapacityStatement.clearParameters();
    checkFlightCapacityStatement.setInt(1, fid);
    ResultSet results = checkFlightCapacityStatement.executeQuery();
    results.next();
    int capacity = results.getInt("capacity");
    results.close();

    return capacity;
  }
}
