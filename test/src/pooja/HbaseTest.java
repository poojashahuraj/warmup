package pooja;

/**
 * Created by parallels on 3/20/15.
 */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class HbaseTest {
    @Test
    public void testCreateAndDeleteTable() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        HBaseAdmin admin = new HBaseAdmin(conf);
        TableName tableName = TableName.valueOf("sample4");
        HbaseClass hBase = new HbaseClass(conf, admin, tableName);
        hBase.deleteTable(tableName);
        assertFalse(admin.isTableAvailable(tableName));

        String[] columnFamilies = new String[]{"cf1", "cf2", "cf3"};
        hBase.createTable(tableName, columnFamilies);
        assertTrue(admin.isTableAvailable(tableName));

        HTable table = new HTable(conf, tableName);
        final HColumnDescriptor[] families = table.getTableDescriptor().getColumnFamilies();
        assertEquals(families.length, columnFamilies.length);
        populate(hBase);

        System.out.println(hBase.getValue("cf3"));
        ArrayList<KeyValue> keyValueList = hBase.getValue("cf3");
        assertEquals(3, keyValueList.size());
    }

    private void populate(HbaseClass hBase) throws IOException {
        hBase.addDataToTable("row1", "cf1", "a", "value1");
        hBase.addDataToTable("row2", "cf2", "b", "value2");
        hBase.addDataToTable("row3", "cf3", "c", "value3");
        hBase.addDataToTable("row4", "cf3", "c", "value4");
        hBase.addDataToTable("row4", "cf3", "d", "value4");
    }
    @Before
    public void setUp() throws Exception {}
    @After
    public void tearDown() throws Exception {}
}