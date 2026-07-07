import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class SQLonRDS {

    private Connection con;

    private String url = "g25ai1055-bigdata-assignment-db.c0r4aw0ka4d9.us-east-1.rds.amazonaws.com:3306";
    private String uid = "admin";
    private String pw = "6.-3A2uTGRz46My";


    public static void main(String[] args) {

        SQLonRDS q = new SQLonRDS();

        try {

            q.connect();

            q.drop();
            q.create();
            q.insert();

           System.out.println("\n========================================");
            System.out.println("QUERY 1 OUTPUT");
            System.out.println("========================================");
            q.queryOne();

            System.out.println("\n========================================");
            System.out.println("QUERY 2 OUTPUT");
            System.out.println("========================================");
            q.queryTwo();

            System.out.println("\n========================================");
            System.out.println("QUERY 3 OUTPUT");
            System.out.println("========================================");
            q.queryThree(); 

            q.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    
    public void connect() throws SQLException, ClassNotFoundException {

    Class.forName("com.mysql.cj.jdbc.Driver");

    String jdbcUrl =
        "jdbc:mysql://" + url + "/mydb";

    System.out.println("Connecting to database...");

    con = DriverManager.getConnection(jdbcUrl, uid, pw);

    System.out.println("Connection Successful");
}


        public void close() throws SQLException {

        if (con != null) {
            con.close();
        }
    }

    public void create() throws SQLException {

    Statement stmt = con.createStatement();

    String companyTable =
        "CREATE TABLE company (" +
        "id INT PRIMARY KEY," +
        "name VARCHAR(50)," +
        "ticker CHAR(10)," +
        "annualRevenue DECIMAL(15,2)," +
        "numEmployees INT" +
        ")";

    stmt.executeUpdate(companyTable);

    String stockTable =
        "CREATE TABLE stockprice (" +
        "companyId INT," +
        "priceDate DATE," +
        "openPrice DECIMAL(10,2)," +
        "highPrice DECIMAL(10,2)," +
        "lowPrice DECIMAL(10,2)," +
        "closePrice DECIMAL(10,2)," +
        "volume INT," +
        "PRIMARY KEY(companyId, priceDate)," +
        "FOREIGN KEY(companyId) REFERENCES company(id)" +
        ")";

    stmt.executeUpdate(stockTable);

    stmt.close();

    System.out.println("Tables created successfully.");
}

    public void insert() throws SQLException {

    String sql = "INSERT INTO company(id, name, ticker, annualRevenue, numEmployees) VALUES (?, ?, ?, ?, ?)";

    PreparedStatement ps = con.prepareStatement(sql);

    // Apple
    ps.setInt(1, 1);
    ps.setString(2, "Apple");
    ps.setString(3, "AAPL");
    ps.setDouble(4, 387540000000.00);
    ps.setInt(5, 154000);
    ps.executeUpdate();

    // GameStop
    ps.setInt(1, 2);
    ps.setString(2, "GameStop");
    ps.setString(3, "GME");
    ps.setDouble(4, 611000000.00);
    ps.setInt(5, 12000);
    ps.executeUpdate();

    // Handy Repair
    ps.setInt(1, 3);
    ps.setString(2, "Handy Repair");
    ps.setNull(3, java.sql.Types.CHAR);
    ps.setDouble(4, 2000000.00);
    ps.setInt(5, 50);
    ps.executeUpdate();

    // Microsoft
    ps.setInt(1, 4);
    ps.setString(2, "Microsoft");
    ps.setString(3, "MSFT");
    ps.setDouble(4, 198270000000.00);
    ps.setInt(5, 221000);
    ps.executeUpdate();

    // StartUp
    ps.setInt(1, 5);
    ps.setString(2, "StartUp");
    ps.setNull(3, java.sql.Types.CHAR);
    ps.setDouble(4, 50000.00);
    ps.setInt(5, 3);
    ps.executeUpdate();

    ps.close();

    System.out.println("Company records inserted successfully.");

    String stockSql = "INSERT INTO stockprice "
        + "(companyId, priceDate, openPrice, highPrice, lowPrice, closePrice, volume) "
        + "VALUES (?,?,?,?,?,?,?)";

    PreparedStatement Stockps = con.prepareStatement(stockSql);

    Object[][] stockData = {

    // Apple
    {1,"2022-08-15",171.52,173.39,171.35,173.19,54091700},
    {1,"2022-08-16",172.78,173.71,171.66,173.03,56377100},
    {1,"2022-08-17",172.77,176.15,172.57,174.55,79542000},
    {1,"2022-08-18",173.75,174.90,173.12,174.15,62290100},
    {1,"2022-08-19",173.03,173.74,171.31,171.52,70211500},
    {1,"2022-08-22",169.69,169.86,167.14,167.57,69026800},
    {1,"2022-08-23",167.08,168.71,166.65,167.23,54147100},
    {1,"2022-08-24",167.32,168.11,166.25,167.53,53841500},
    {1,"2022-08-25",168.78,170.14,168.35,170.03,51218200},
    {1,"2022-08-26",170.57,171.05,163.56,163.62,78823500},
    {1,"2022-08-29",161.15,162.90,159.82,161.38,73314000},
    {1,"2022-08-30",162.13,162.56,157.72,158.91,77906200},

    // GameStop
    {2,"2022-08-15",39.75,40.39,38.81,39.68,5243100},
    {2,"2022-08-16",39.17,45.53,38.60,42.19,23602800},
    {2,"2022-08-17",42.18,44.36,40.41,40.52,9766400},
    {2,"2022-08-18",39.27,40.07,37.34,37.93,8145400},
    {2,"2022-08-19",35.18,37.19,34.67,36.49,9525600},
    {2,"2022-08-22",34.31,36.20,34.20,34.50,5798600},
    {2,"2022-08-23",34.70,34.99,33.45,33.53,4836300},
    {2,"2022-08-24",34.00,34.94,32.44,32.50,5620300},
    {2,"2022-08-25",32.84,32.89,31.50,31.96,4726300},
    {2,"2022-08-26",31.50,32.38,30.63,30.94,4289500},
    {2,"2022-08-29",30.48,32.75,30.38,31.55,4292700},
    {2,"2022-08-30",31.62,31.87,29.42,29.84,5060200},

    // Microsoft
    {4,"2022-08-15",291.00,294.18,290.11,293.47,18085700},
    {4,"2022-08-16",291.99,294.04,290.42,292.71,18102900},
    {4,"2022-08-17",289.74,293.35,289.47,291.32,18253400},
    {4,"2022-08-18",290.19,291.91,289.08,290.17,17186200},
    {4,"2022-08-19",288.90,289.25,285.56,286.15,20557200},
    {4,"2022-08-22",282.08,282.46,277.22,277.75,25061100},
    {4,"2022-08-23",276.44,278.86,275.40,276.44,17527400},
    {4,"2022-08-24",275.41,277.23,275.11,275.79,18137000},
    {4,"2022-08-25",277.33,279.02,274.52,278.85,16583400},
    {4,"2022-08-26",279.08,280.34,267.98,268.09,27532500},
    {4,"2022-08-29",265.85,267.40,263.85,265.23,20338500},
    {4,"2022-08-30",266.67,267.05,260.66,262.97,22767100}
};

    for (Object[] row : stockData) {

    Stockps.setInt(1, (Integer) row[0]);
    Stockps.setDate(2, java.sql.Date.valueOf((String) row[1]));
    Stockps.setDouble(3, (Double) row[2]);
    Stockps.setDouble(4, (Double) row[3]);
    Stockps.setDouble(5, (Double) row[4]);
    Stockps.setDouble(6, (Double) row[5]);
    Stockps.setInt(7, (Integer) row[6]);

    Stockps.executeUpdate();
}

    Stockps.close();

System.out.println("Stock price records inserted successfully.");
}

public void drop() throws SQLException {

    Statement stmt = con.createStatement();

    stmt.executeUpdate("DROP TABLE IF EXISTS stockprice");
    stmt.executeUpdate("DROP TABLE IF EXISTS company");

    stmt.close();

    System.out.println("Tables dropped successfully.");
}

public ResultSet queryOne() throws SQLException {

    String sql =
        "SELECT name, annualRevenue, numEmployees " +
        "FROM company " +
        "WHERE numEmployees > 10000 " +
        "OR annualRevenue < 1000000 " +
        "ORDER BY name ASC";

    Statement stmt = con.createStatement();

    ResultSet rs = stmt.executeQuery(sql);

    System.out.println("Schema:");
    System.out.println(resultSetMetaDataToString(rs.getMetaData()));

    System.out.println("\nData:");
    System.out.println(resultSetToString(rs,100));

    return rs;
}

public ResultSet queryTwo() throws SQLException {

    String sql =
        "SELECT c.name,c.ticker," +
        "MIN(s.lowPrice) AS LowestPrice," +
        "MAX(s.highPrice) AS HighestPrice," +
        "AVG(s.closePrice) AS AverageClosePrice," +
        "AVG(s.volume) AS AverageVolume " +
        "FROM company c " +
        "JOIN stockprice s ON c.id=s.companyId " +
        "WHERE s.priceDate BETWEEN '2022-08-22' AND '2022-08-26' " +
        "GROUP BY c.id,c.name,c.ticker " +
        "ORDER BY AverageVolume DESC";

    Statement stmt = con.createStatement();

    ResultSet rs = stmt.executeQuery(sql);

    System.out.println("Schema:");
    System.out.println(resultSetMetaDataToString(rs.getMetaData()));

    System.out.println("\nData:");
    System.out.println(resultSetToString(rs,100));

return rs;
}

public ResultSet queryThree() throws SQLException {

    String sql =
        "SELECT c.name,c.ticker,s.closePrice " +
        "FROM company c " +

        "LEFT JOIN stockprice s " +
        "ON c.id=s.companyId " +
        "AND s.priceDate='2022-08-30' " +

        "LEFT JOIN " +
        "(SELECT companyId,AVG(closePrice) avgClose " +
        "FROM stockprice " +
        "WHERE priceDate BETWEEN '2022-08-15' AND '2022-08-19' " +
        "GROUP BY companyId) a " +

        "ON c.id=a.companyId " +

        "WHERE c.ticker IS NULL " +
        "OR s.closePrice>=a.avgClose*0.90 " +

        "ORDER BY c.name";

    Statement stmt = con.createStatement();

    ResultSet rs = stmt.executeQuery(sql);

    System.out.println("Schema:");
    System.out.println(resultSetMetaDataToString(rs.getMetaData()));

    System.out.println("\nData:");
    System.out.println(resultSetToString(rs,100));

    return rs;
}


public static String resultSetToString(ResultSet rst, int maxrows) throws SQLException {

    StringBuffer buf = new StringBuffer(5000);

    int rowCount = 0;

    if (rst == null)
        return "ERROR: No ResultSet";

    ResultSetMetaData meta = rst.getMetaData();

    buf.append("Total columns: " + meta.getColumnCount());
    buf.append('\n');

    if (meta.getColumnCount() > 0)
        buf.append(meta.getColumnName(1));

    for (int j = 2; j <= meta.getColumnCount(); j++)
        buf.append(", " + meta.getColumnName(j));

    buf.append('\n');

    while (rst.next()) {

        if (rowCount < maxrows) {

            for (int j = 0; j < meta.getColumnCount(); j++) {

                Object obj = rst.getObject(j + 1);

                buf.append(obj);

                if (j != meta.getColumnCount() - 1)
                    buf.append(", ");

            }

            buf.append('\n');
        }

        rowCount++;
    }

    buf.append("Total results: " + rowCount);

    return buf.toString();
}


public static String resultSetMetaDataToString(ResultSetMetaData meta) throws SQLException {

    StringBuffer buf = new StringBuffer(5000);

    buf.append(meta.getColumnName(1)
            + " (" + meta.getColumnLabel(1)
            + ", " + meta.getColumnType(1)
            + "-" + meta.getColumnTypeName(1)
            + ", " + meta.getColumnDisplaySize(1)
            + ", " + meta.getPrecision(1)
            + ", " + meta.getScale(1) + ")");

    for (int j = 2; j <= meta.getColumnCount(); j++) {

        buf.append(", "
                + meta.getColumnName(j)
                + " (" + meta.getColumnLabel(j)
                + ", " + meta.getColumnType(j)
                + "-" + meta.getColumnTypeName(j)
                + ", " + meta.getColumnDisplaySize(j)
                + ", " + meta.getPrecision(j)
                + ", " + meta.getScale(j) + ")");
    }

    return buf.toString();
}
}