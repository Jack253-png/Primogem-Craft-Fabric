package com.primogemstudio.primogemcraft.database;

import com.primogemstudio.primogemcraft.gacha.serialize.GachaRecordModel;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class GachaDatabase {
    private final Connection conn;
    public GachaDatabase(File file) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + file.toString());

        checkOrCreateTable();
    }
    private void checkOrCreateTable() throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("create table if not exists gacha_pity(id integer primary key autoincrement unique,uuid varchar(36) unique,pity5 integer,pity4 integer)");
        statement.executeUpdate("create table if not exists gacha_history(id integer primary key autoincrement unique,username varchar(64),uuid varchar(36),timestamp long, level integer,item varchar(2048))");
        statement.close();
    }
    public void write(GachaRecordModel.DataModel data) throws SQLException {
        conn.createStatement().executeUpdate("drop table if exists gacha_pity");
        conn.createStatement().executeUpdate("drop table if exists gacha_history");

        data.gachaRecord.forEach(m -> {
            PreparedStatement state;
            try {
                state = conn.prepareStatement("insert into gacha_history(username,uuid,timestamp,level,item) values(?,?,?,?,?)");
                state.setString(1, m.name);
                state.setString(2, m.uuid == null ? null : m.uuid.toString());
                state.setLong(3, m.timestamp);
                state.setInt(4, m.level);
                state.setString(5, m.item == null ? null : m.item.toString());
                state.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        data.pity_5.entrySet().stream()
                .map(uuidIntegerEntry -> new ImmutablePair<>(
                        uuidIntegerEntry.getKey(),
                        new ImmutablePair<>(
                                uuidIntegerEntry.getValue(),
                                data.pity_4.get(uuidIntegerEntry.getKey()))
                ))
                .forEach(data2 -> {
                    try {
                        PreparedStatement state = conn.prepareStatement("insert into gacha_pity(uuid,pity5,pity4) values(?,?,?)");
                        state.setString(1, data2.left.toString());
                        state.setInt(2, data2.right.left);
                        state.setInt(3, data2.right.right);
                        state.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
    }
    public GachaRecordModel.DataModel read() throws SQLException {
        var model = new GachaRecordModel.DataModel();
        ResultSet set = conn.createStatement().executeQuery("select * from gacha_pity");
        while (set.next()) {
            UUID uuid = UUID.fromString(set.getString(2));
            int pity5 = set.getInt(3);
            int pity4 = set.getInt(4);
            model.pity_4.put(uuid, pity4);
            model.pity_5.put(uuid, pity5);
        }

        ResultSet set2 = conn.createStatement().executeQuery("select * from gacha_history");
        while (set2.next()) {
            var data = new GachaRecordModel();
            data.name = set2.getString(2);
            data.uuid = set2.getString(3) == null ? null : UUID.fromString(set2.getString(3));
            data.timestamp = set2.getLong(4);
            data.level = set2.getInt(5);
            data.item = set2.getString(6) == null ? null : new ResourceLocation(set2.getString(6));
            model.gachaRecord.add(data);
        }
        return model;
    }
}
