package ca.innov8solutions.packetsender;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class TestDBSending {

    @Test
    public void testPacketLogging() {
        Properties props = new Properties();
        props.put("user", "root");
        props.put("password", "t967vsTzA3");
        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost:3306/test", props);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerRowMapper(ConstructorMapper.factory(PacketData.class));

        PacketLogDAO log = jdbi.onDemand(PacketLogDAO.class);
        log.createTable();

        UUID uuid = UUID.randomUUID();

        Timestamp current = Timestamp.valueOf(LocalDateTime.now());
        Timestamp later = Timestamp.valueOf(LocalDateTime.now());
        log.insert(uuid.toString(), current, current);
        log.insert(uuid.toString(), current, later);


        PacketData data = log.getPacket(uuid.toString());
        assertNotNull("Data must not be null", data);
    }
}
