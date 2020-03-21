package ca.innov8solutions.packetsender;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.Timestamp;
import java.util.UUID;


public interface PacketLogDAO {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS packetlog (uuid VARCHAR(36) PRIMARY KEY, sent TIMESTAMP, received TIMESTAMP)")
    void createTable();

    @SqlUpdate("INSERT INTO packetlog (uuid, sent, received) VALUES(:uuid, :sent, :received) ON DUPLICATE KEY UPDATE sent=:sent, received=:received;")
    void insert(@Bind("uuid") String uuid, @Bind("sent")Timestamp sent, @Bind("received") Timestamp received);

    @SqlQuery("SELECT * FROM packetlog WHERE uuid=:uuid;")
    @RegisterConstructorMapper(PacketData.class)
    PacketData getPacket(@Bind("uuid") String uuid);
}
