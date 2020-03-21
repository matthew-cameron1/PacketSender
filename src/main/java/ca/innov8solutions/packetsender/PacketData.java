package ca.innov8solutions.packetsender;

import java.beans.ConstructorProperties;
import java.sql.Timestamp;
import java.util.UUID;

public class PacketData implements Comparable<PacketData> {

    private UUID uuid;
    private Timestamp sent;
    private Timestamp received;

    @ConstructorProperties({"uuid", "sent", "received"})
    public PacketData(String id, Timestamp sent, Timestamp received) {
        this.uuid = UUID.fromString(id);
        this.sent = sent;
        this.received = received;
    }

    @Override
    public String toString() {
        return String.format("Packet with ID: %s was sent at %s and received at %s", this.uuid, this.sent, this.received);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Timestamp getSent() {
        return sent;
    }

    public void setSent(Timestamp sent) {
        this.sent = sent;
    }

    public Timestamp getReceived() {
        return received;
    }

    public void setReceived(Timestamp received) {
        this.received = received;
    }

    @Override
    public int compareTo(PacketData o) {
        return 0;
    }
}
