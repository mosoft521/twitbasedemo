package HBaseIA.TwitBase;

import HBaseIA.TwitBase.hbase.RelationsDAO;
import HBaseIA.TwitBase.hbase.TwitsDAO;
import HBaseIA.TwitBase.hbase.UsersDAO;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.util.Bytes;

/*
 * 用于初始化相关的表，变更适应1.0的API，去除一些参数判断，支持在没有安装hbase的本机中，访问远程的HBASE
 */
public class InitTables {

    static {
        System.setProperty("hadoop.home.dir", "F:/ws-ij-alvin-git-tmp/hadoop-common-2.2.0-bin/bin");
    }

    public static void main(String[] args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {
            // delete table
            TableName userTableName = TableName.valueOf(UsersDAO.TABLE_NAME);
            deleteTable(admin, userTableName);
            TableName twitsTableName = TableName.valueOf(TwitsDAO.TABLE_NAME);
            deleteTable(admin, twitsTableName);
            TableName followsTableName = TableName.valueOf(RelationsDAO.FOLLOWS_TABLE_NAME);
            deleteTable(admin, followsTableName);
            TableName followedTableName = TableName.valueOf(RelationsDAO.FOLLOWED_TABLE_NAME);
            deleteTable(admin, followedTableName);

            //create table
            if (admin.tableExists(userTableName)) {
                System.out.println("User table already exists.");
            } else {
                System.out.println("Creating User table...");
                HTableDescriptor desc = new HTableDescriptor(userTableName);
                HColumnDescriptor c = new HColumnDescriptor(UsersDAO.INFO_FAM);
                desc.addFamily(c);
                admin.createTable(desc);
                System.out.println("User table created.");
            }

            if (admin.tableExists(twitsTableName)) {
                System.out.println("Twits table already exists.");
            } else {
                System.out.println("Creating Twits table...");
                HTableDescriptor desc = new HTableDescriptor(twitsTableName);
                HColumnDescriptor c = new HColumnDescriptor(TwitsDAO.TWITS_FAM);
                c.setMaxVersions(1);
                desc.addFamily(c);
                admin.createTable(desc);
                System.out.println("Twits table created.");
            }

            if (admin.tableExists(followsTableName)) {
                System.out.println("Follows table already exists.");
            } else {
                System.out.println("Creating Follows table...");
                HTableDescriptor desc = new HTableDescriptor(followsTableName);
                HColumnDescriptor c = new HColumnDescriptor(RelationsDAO.RELATION_FAM);
                c.setMaxVersions(1);
                desc.addFamily(c);
                admin.createTable(desc);
                System.out.println("Follows table created.");
            }

            if (admin.tableExists(followedTableName)) {
                System.out.println("Followed table already exists.");
            } else {
                System.out.println("Creating Followed table...");
                HTableDescriptor desc = new HTableDescriptor(followedTableName);
                HColumnDescriptor c = new HColumnDescriptor(RelationsDAO.RELATION_FAM);
                c.setMaxVersions(1);
                desc.addFamily(c);
                admin.createTable(desc);
                System.out.println("Followed table created.");
            }
        }
    }

    private static void deleteTable(Admin admin, TableName tableName) throws Exception {
        if (admin.tableExists(tableName)) {
            System.out.printf("Deleting %s\n", Bytes.toString(tableName.getName()));
            if (admin.isTableEnabled(tableName)) {
                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            }
        }
    }


}
