package com.amazonaws.hive.serdes.s3;

/**
 * Created by fbaldo on 30/05/14.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.AbstractDeserializer;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.nio.charset.CharacterCodingException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * S3LogDeserializer.
 */
public class S3LogDeserializer extends AbstractDeserializer {

    public static final Log LOG = LogFactory.getLog(S3LogDeserializer.class
            .getName());

    static {
        StackTraceElement[] sTrace = new Exception().getStackTrace();
        sTrace[0].getClassName();
    }

    private ObjectInspector cachedObjectInspector;

    @Override
    public String toString() {
        return "S3LogDeserializer[]";
    }

    public S3LogDeserializer() throws SerDeException {
    }

    static Pattern regexpat = Pattern
            .compile("(\\S+) (\\S+) \\[(.*?)\\] (\\S+) (\\S+) (\\S+) (\\S+) (\\S+) (\".+\"|-) (\\S+) (\\S+) (\\S+) (\\S+) (\\S+) (\\S+) (\".+\"|-) (\".*\"|-) (.*)");

    S3LogStruct deserializeCache = new S3LogStruct();

    @Override
    public void initialize(Configuration job, Properties tbl)
            throws SerDeException {

        cachedObjectInspector = ObjectInspectorFactory
                .getReflectionObjectInspector(S3LogStruct.class,
                        ObjectInspectorFactory.ObjectInspectorOptions.JAVA);

        LOG.debug(getClass().getName() + ": initialized");
    }

    public static Integer toInt(String s) {
        s = s.replace("\"", "");
        if (s.compareTo("-") == 0) {
            return null;
        } else {
            return Integer.valueOf(s);
        }
    }

    // Number of rows not matching the regex
    long unmatchedRows = 0;

    public Object deserialize(S3LogStruct c, String row) throws Exception {
        Matcher match = regexpat.matcher(row);
        int t = 1;
        try {
            match.matches();
            c.bucketowner = match.group(t++);
            c.bucketname = match.group(t++);
            c.rdatetime = match.group(t++);
            c.rip = match.group(t++);
            c.requester = match.group(t++);
            c.requestid = match.group(t++);
            c.operation = match.group(t++);
            c.rkey = match.group(t++);
            c.requesturi = match.group(t++).replace("\"", "");
            c.httpstatus = toInt(match.group(t++));
            c.errorcode = match.group(t++);
            c.bytessent = toInt(match.group(t++));
            c.objsize = toInt(match.group(t++));
            c.totaltime = toInt(match.group(t++));
            c.turnaroundtime = toInt(match.group(t++));
            c.referer = match.group(t++).replace("\"", "");
            c.useragent = match.group(t++).replace("\"", "");
            c.versionid = match.group(t++);
        } catch (Exception e) {
            LOG.error(new StringBuilder().append("Unmatched row found: ").append(row).append(" | ").append(e.toString()).toString());
            unmatchedRows++;
            if (unmatchedRows > 50) {
                throw new SerDeException("S3 Log Regex did not match:" + row, e);
            }
        }
        return (c);
    }

    @Override
    public Object deserialize(Writable field) throws SerDeException {
        String row = null;
        if (field instanceof BytesWritable) {
            BytesWritable b = (BytesWritable) field;
            try {
                row = Text.decode(b.getBytes(), 0, b.getLength());
            } catch (CharacterCodingException e) {
                throw new SerDeException(e);
            }
        } else if (field instanceof Text) {
            row = field.toString();
        }
        try {
            deserialize(deserializeCache, row);
            return deserializeCache;
        } catch (ClassCastException e) {
            throw new SerDeException(this.getClass().getName()
                    + " expects Text or BytesWritable", e);
        } catch (Exception e) {
            throw new SerDeException(e);
        }
    }

    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return cachedObjectInspector;
    }

    @Override
    public SerDeStats getSerDeStats() {
        // no support for statistics
        return null;
    }
}
