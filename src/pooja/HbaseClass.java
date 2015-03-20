package pooja;

/**
 * Created by parallels on 3/20/15.
 */

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;

public class HbaseClass {
    private Configuration conf;
    private HBaseAdmin admin;
    private TableName tableName;

    public HbaseClass(Configuration conf, HBaseAdmin admin, TableName tableName) {
        this.conf = conf;
        this.admin = admin;
        this.tableName = tableName;
    }

    public void createTable(TableName tableName, String[] columnFamilies) throws IOException {
        HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
        for (String cfName : columnFamilies) {
            tableDescriptor.addFamily(new HColumnDescriptor(cfName));
        }
        admin.createTable(tableDescriptor);
    }

    public void deleteTable(TableName tableName) throws IOException {
        try {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        } catch (TableNotFoundException e) {
            System.out.println("Table not there");
        }
    }

    public void addDataToTable(String rowName, String columnFamilyName,
                               String qualifier, String value) throws IOException {
        conf = HBaseConfiguration.create();
        HTable table = new HTable(conf, tableName);
        Put put = new Put(Bytes.toBytes(rowName));
        put.add(Bytes.toBytes(columnFamilyName), Bytes.toBytes(qualifier), Bytes.toBytes(value));
        table.put(put);
        table.close();
    }

    public ArrayList<KeyValue> getValue(String columnFamilyName) throws IOException {
        HTable table = new HTable(conf, tableName);
        Scan scan = new Scan();
        scan.setCaching(30);
        scan.addFamily(Bytes.toBytes(columnFamilyName));
        ResultScanner scanner = table.getScanner(scan);
        ArrayList<KeyValue> arrayList = new ArrayList<KeyValue>();

        String value = new String();
        for (Result result = scanner.next(); (result != null); result = scanner.next()) {
            for (KeyValue keyValue : result.list()) {
                value = Bytes.toString(keyValue.getValue());
                arrayList.add(keyValue);
            }
        }
        return arrayList;
    }
}