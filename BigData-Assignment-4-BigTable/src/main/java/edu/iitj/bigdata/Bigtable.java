package edu.iitj.bigdata;

import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.BulkMutation;
import com.google.cloud.bigtable.data.v2.models.Mutation;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Use Google Bigtable to store and analyze sensor data.
 */
public class Bigtable {

    // Google Cloud Project Information
    public final String projectId = "luminous-pier-486112-a4";
    public final String instanceId = "g25ai1055-bigtable";

    // Bigtable Information
    public final String COLUMN_FAMILY = "sensor";
    public final String tableId = "weather";

    // Clients
    public BigtableDataClient dataClient;
    public BigtableTableAdminClient adminClient;

    public static void main(String[] args) throws Exception {

        Bigtable testbt = new Bigtable();
        testbt.run();

    }

    /**
     * Connect to Google Bigtable.
     */
    public void connect() throws IOException {

        BigtableDataSettings dataSettings =
                BigtableDataSettings.newBuilder()
                        .setProjectId(projectId)
                        .setInstanceId(instanceId)
                        .build();

        dataClient = BigtableDataClient.create(dataSettings);

        BigtableTableAdminSettings adminSettings =
                BigtableTableAdminSettings.newBuilder()
                        .setProjectId(projectId)
                        .setInstanceId(instanceId)
                        .build();

        adminClient = BigtableTableAdminClient.create(adminSettings);

        System.out.println("Connected Successfully.");

    }

   public void run() throws Exception {

    connect();

    System.out.println("\n======================================");
    System.out.println(" BIGTABLE WEATHER ANALYSIS");
    System.out.println("======================================\n");

    // Uncomment ONLY when you want to reload data
    // deleteTable();
    // createTable();
    // loadData();

    System.out.println("\n---------- QUERY 1 ----------");
    int temp = query1();
    System.out.println("Temperature at Vancouver on 01-10-2022 10:00 AM : " + temp);

    System.out.println("\n---------- QUERY 2 ----------");
    int wind = query2();
    System.out.println("Maximum windspeed during September 2022 : " + wind);

    System.out.println("\n---------- QUERY 3 ----------");
    ArrayList<Object[]> result = query3();

    if(result.isEmpty()){
        System.out.println("No records found.");
    }else{
        for(Object[] r : result){
            System.out.println(
                    "Station : " + r[0]
                    + "   Temperature : " + r[1]
            );
        }
    }

    System.out.println("\n---------- QUERY 4 ----------");
    int maxTemp = query4();
    System.out.println("Maximum temperature : " + maxTemp);

    System.out.println("\n======================================");
    System.out.println(" Assignment Completed Successfully ");
    System.out.println("======================================");

    close();
}
    /**
     * Close clients.
     */
    public void close() {

        if (dataClient != null)
            dataClient.close();

        if (adminClient != null)
            adminClient.close();

    }

    // Crete Tables:
    public void createTable() {

    System.out.println("Creating table: " + tableId);

    try {

        CreateTableRequest request =
                CreateTableRequest.of(tableId)
                        .addFamily(COLUMN_FAMILY);

        adminClient.createTable(request);

        System.out.println("Table created successfully.");

    } catch (Exception e) {

        System.out.println(e.getMessage());

    }

}

    // Loading data from CSV file to Bigtable
    public void loadData() throws Exception {

    System.out.println("Starting Data Load...");

    String path = "data/";

    loadStation(path + "seatac.csv", "SEA");
    loadStation(path + "vancouver.csv", "YVR");
    loadStation(path + "portland.csv", "PDX");

    System.out.println("Data Loaded Successfully.");
}

    private void loadStation(String fileName, String station) throws Exception {

    BufferedReader br = new BufferedReader(new FileReader(fileName));

    String line;
    boolean firstLine = true;
    String previousKey = "";

    while ((line = br.readLine()) != null) {

        if (firstLine) {
            firstLine = false;
            continue;
        }

        String[] cols = line.split(",");

        if (cols.length < 9)
            continue;

        // CSV Columns
        String julian = cols[0].trim();
        String date = cols[1].trim();
        String time = cols[2].trim();

        String hour = time.substring(0,2);

        String rowKey = station + "#" + date + "#" + hour;

        // Ignore duplicate readings in same hour
        if(rowKey.equals(previousKey))
            continue;

        previousKey = rowKey;

        RowMutation mutation =
                RowMutation.create(tableId,rowKey)

                        .setCell(COLUMN_FAMILY,"temperature",cols[3])
                        .setCell(COLUMN_FAMILY,"dewpoint",cols[4])
                        .setCell(COLUMN_FAMILY,"humidity",cols[5])
                        .setCell(COLUMN_FAMILY,"windspeed",cols[6])
                        .setCell(COLUMN_FAMILY,"gust",cols[7])
                        .setCell(COLUMN_FAMILY,"pressure",cols[8]);

        dataClient.mutateRow(mutation);

    }

    br.close();

    System.out.println(station + " Loaded.");

}
    //Query1
  public int query1() throws Exception {

    System.out.println("Executing Query 1");

    Query query = Query.create(tableId);

    for (Row row : dataClient.readRows(query)) {

        String key = row.getKey().toStringUtf8();

        if (!key.startsWith("YVR#"))
            continue;

        String[] parts = key.split("#");

        String date = parts[2];

        if (!date.equals("01-10-2022"))
            continue;

        for (RowCell cell : row.getCells(COLUMN_FAMILY)) {

            if (cell.getQualifier().toStringUtf8().equals("temperature")) {

                return Integer.parseInt(cell.getValue().toStringUtf8());

            }
        }

    }

    return -1;

}
    // Query 2– Maximum windspeed in the table
    public int query2() throws Exception {

    System.out.println("Executing Query 2");

    int maxWind = 0;

    Query query = Query.create(tableId);

    for (Row row : dataClient.readRows(query)) {

        for (RowCell cell : row.getCells(COLUMN_FAMILY)) {

            if (cell.getQualifier().toStringUtf8().equals("windspeed")) {

                try {

                    int speed = (int) Double.parseDouble(cell.getValue().toStringUtf8());

                    if (speed > maxWind)
                        maxWind = speed;

                } catch (Exception e) {
                    // Ignore invalid values
                }

            }

        }

    }

    return maxWind;
}

    // Query 3– Count of rows in the table
    public ArrayList<Object[]> query3() throws Exception {

    System.out.println("Executing Query 3");

    ArrayList<Object[]> data = new ArrayList<>();

    Query query = Query.create(tableId);

    for (Row row : dataClient.readRows(query)) {

        String rowKey = row.getKey().toStringUtf8();

        String temperature = "";
        String humidity = "";
        String pressure = "";

        for (RowCell cell : row.getCells(COLUMN_FAMILY)) {

            String qualifier = cell.getQualifier().toStringUtf8();
            String value = cell.getValue().toStringUtf8();

            if (qualifier.equals("temperature"))
                temperature = value;

            if (qualifier.equals("humidity"))
                humidity = value;

            if (qualifier.equals("pressure"))
                pressure = value;

        }

        data.add(new Object[]{
                rowKey,
                temperature,
                humidity,
                pressure
        });

    }

    return data;
}

    // Query 4 – Maximum temperature
    public int query4() throws Exception {

    System.out.println("Executing Query 4");

    int maxTemp = Integer.MIN_VALUE;

    Query query = Query.create(tableId);

    for (Row row : dataClient.readRows(query)) {

        for (RowCell cell : row.getCells(COLUMN_FAMILY)) {

            if (cell.getQualifier().toStringUtf8().equals("temperature")) {

                try {

                    int temp = (int) Double.parseDouble(cell.getValue().toStringUtf8());

                    if (temp > maxTemp)
                        maxTemp = temp;

                } catch (Exception e) {
                    // Ignore invalid values
                }

            }

        }

    }

    return maxTemp;
}

    /**
     * Delete table.
     */
    public void deleteTable() {

        System.out.println("Deleting table...");

        try {

            adminClient.deleteTable(tableId);
            System.out.println("Table deleted.");

        } catch (NotFoundException e) {

            System.out.println("Table does not exist.");

        }

    }

}