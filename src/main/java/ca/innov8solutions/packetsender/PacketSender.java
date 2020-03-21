package ca.innov8solutions.packetsender;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.UUID;

public class PacketSender extends JavaPlugin {

    private PacketLogDAO dao;
    private Queue<PacketData> queue = new PriorityQueue<>();

    @Override
    public void onEnable() {
        Properties props = new Properties();
        props.put("user", "root");
        props.put("password", "password");
        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost:3306/test", props);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerRowMapper(ConstructorMapper.factory(PacketData.class));
        this.dao = jdbi.onDemand(PacketLogDAO.class);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("sendpacket").setExecutor(new CommandHandler(this));
    }

    @Override
    public void onDisable() {

    }

    Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
    }

    private Object constructPacket(Object player) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        return getNmsClass("PacketPlayOutEntityStatus").getConstructor(getNmsClass("Entity"), byte.class).newInstance(player, (byte) 35);
    }

    public void sendPacket(Player p) {

        String id = UUID.randomUUID().toString();

        try {
            Object craftPlayer = p.getClass().getMethod("getHandle").invoke(p);
            Object packet = constructPacket(craftPlayer);
            Object connection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            queue.add(new PacketData(id, Timestamp.valueOf(LocalDateTime.now()), null));
            connection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(connection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Channel getChannel(Player player) throws Exception {
        Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object connection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);

        Field networkManagerField = connection.getClass().getDeclaredField("networkManager");
        networkManagerField.setAccessible(true);
        Object networkManager = networkManagerField.get(connection);


        Field channelField = networkManager.getClass().getField("channel");
        channelField.setAccessible(true);

        return (Channel) channelField.get(networkManager);
    }

    void removePacketListener(Player player) throws Exception {
        Channel channel = getChannel(player);
        channel.eventLoop().submit(() -> channel.pipeline().remove(player.getName()));
    }

    void addPacketListener(Player player) throws Exception {

        ChannelDuplexHandler handler = new ChannelDuplexHandler() {

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg.getClass().getName().contains("EntityStatus")) {
                    PacketData d = queue.poll();
                    if (d == null) {
                        return;
                    }

                    d.setReceived(Timestamp.valueOf(LocalDateTime.now()));
                    getDao().insert(d.getUuid().toString(), d.getSent(), d.getReceived());
                }
                super.write(ctx, msg, promise);
            }
        };

        Channel channel = getChannel(player);
        channel.pipeline().addBefore("packet_handler", player.getName(), handler);
    }

    private PacketLogDAO getDao() {
        return dao;
    }
}
