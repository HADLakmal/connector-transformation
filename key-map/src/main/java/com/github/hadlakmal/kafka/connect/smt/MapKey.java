package com.github.hadlakmal.kafka.connect.smt;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.apache.kafka.connect.transforms.util.Requirements.requireStructOrNull;

public abstract class MapKey<R extends ConnectRecord<R>> implements Transformation<R> {
    private static final Logger log = LoggerFactory.getLogger(MapKey.class);

    private String fieldMap;
    private static final String PURPOSE = "key map with email and number";

    private interface ConfigName {
        String MAP_FIELD = "field";
    }

    public static final ConfigDef CONFIG_DEF =
            new ConfigDef().define(ConfigName.MAP_FIELD, ConfigDef.Type.STRING, "key", ConfigDef.Importance.HIGH,
                    "Field for record header");

    @Override
    public R apply(R record) {
        log.trace("applying the headed");
        final Schema schema = operatingSchema(record);

        if (schema == null) {
            throw new IllegalStateException("Unknown schema value (use HoistField SMT with [header] field): " + fieldMap);
        } else {
            final Struct value = requireStructOrNull(operatingValue(record), PURPOSE);
            Field field = schema.field(fieldMap);

            if (field == null) {
                throw new IllegalArgumentException("Unknown field (use HoistField SMT with [header] field): " + fieldMap);
            }
            String str = value.getString(fieldMap);

            return record.newRecord(record.topic(), record.kafkaPartition(), org.apache.kafka.connect.data.Schema.STRING_SCHEMA, str, record.valueSchema(), record.value(), record.timestamp());
        }

    }

    @Override
    public ConfigDef config() {
        log.trace("config def loaded");
        return CONFIG_DEF;
    }

    @Override
    public void close() {
        log.trace("closed");
    }

    @Override
    public void configure(Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        fieldMap = config.getString(ConfigName.MAP_FIELD);
    }


    protected abstract Schema operatingSchema(R record);

    protected abstract Object operatingValue(R record);

    protected abstract R newRecord(R record, Schema updatedSchema, Object updatedValue);

    public static class Key<R extends ConnectRecord<R>> extends MapKey<R> {

        @Override
        protected Schema operatingSchema(R record) {
            return record.keySchema();
        }

        @Override
        protected Object operatingValue(R record) {
            return record.key();
        }

        @Override
        protected R newRecord(R record, Schema updatedSchema, Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), updatedSchema, updatedValue, record.valueSchema(), record.value(), record.timestamp());
        }

    }

    public static class Value<R extends ConnectRecord<R>> extends MapKey<R> {

        @Override
        protected Schema operatingSchema(R record) {
            return record.valueSchema();
        }

        @Override
        protected Object operatingValue(R record) {
            return record.value();
        }

        @Override
        protected R newRecord(R record, Schema updatedSchema, Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), updatedSchema, updatedValue, record.timestamp());
        }

    }
}


