package com.amazonaws.hive.serdes.s3;

import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * TestRegexSerDe.
 */
public class TestS3LogDeserializer extends TestCase {

    private S3LogDeserializer createSerDe() throws Throwable {
        Properties schema = new Properties();
        Configuration conf = new Configuration();

        S3LogDeserializer serde = new S3LogDeserializer();
        serde.initialize(conf, schema);
        return serde;
    }

    private List<Text> createStringsList() {
        List<Text> strings = new ArrayList<Text>();
        strings.add(new Text("79a59df900b949e55d96a1e698fbacedfd6e09d98eacf8f8d5218e7cd47ef2be mybucket [06/Feb/2014:00:00:38 +0000] 192.0.2.3 79a59df900b949e55d96a1e698fbacedfd6e09d98eacf8f8d5218e7cd47ef2be A1206F460EXAMPLE REST.GET.BUCKETPOLICY - \"GET /mybucket?policy HTTP/1.1\" 404 NoSuchBucketPolicy 297 - 38 - \"-\" \"S3Console/0.4\" -"));
        strings.add(new Text("c9e4b53db2c7c1e433009a1209c80d8b55d98f11732b53c4c1c146b5a6213e87 public-amzn [18/Apr/2013:12:02:11 +0000] 172.25.26.21 c9e4b53db2c7c1e433009a1209c80d8b55d98f11732b53c4c1c146b5a6213e87 A0B2E57459AFDD79 REST.COPY.OBJECT_GET script.sh - 200 - - 48 - - - - -"));
        strings.add(new Text("c9e4b53db2c7c1e433009a1209c80d8b55d98f11732b53c4c1c146b5a6213e87 public-amzn [25/Jan/2013:09:27:23 +0000] 10.29.51.12 c9e4b53db2c7c1e433009a1209c80d8b55d98f11732b53c4c1c146b5a6213e87 831E610D686CF4FC REST.GET.BUCKET - \"GET /?prefix=&max-keys=1000&delimiter=%2f HTTP/1.1\" 200 - 1621 - 216 215 \"-\" \"CloudBerryLab.Base.HttpUtil.Client 3.7.0 (http://www.cloudberrylab.com/)\" -"));
        return strings;
    }


    /**
     * Test the LazySimpleSerDe class.
     */
    public void testRegexSerDe() throws Throwable {
        try {
            // Create the SerDe
            S3LogDeserializer serDe = createSerDe();

            // Data as list
            List<Text> strings = createStringsList();

            for (Text t : strings) {
                // Deserialize
                Object row = serDe.deserialize(t);
                ObjectInspector rowOI = serDe.getObjectInspector();

                System.out.println("Deserialized row: " + row);

            }


        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

}
