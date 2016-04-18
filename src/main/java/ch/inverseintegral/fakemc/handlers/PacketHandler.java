package ch.inverseintegral.fakemc.handlers;

import ch.inverseintegral.fakemc.Protocol;
import ch.inverseintegral.fakemc.packets.*;
import ch.inverseintegral.fakemc.packets.handshake.Handshake;
import ch.inverseintegral.fakemc.packets.login.Kick;
import ch.inverseintegral.fakemc.packets.login.LoginRequest;
import ch.inverseintegral.fakemc.packets.status.Ping;
import ch.inverseintegral.fakemc.packets.status.StatusRequest;
import ch.inverseintegral.fakemc.packets.status.StatusResponse;
import ch.inverseintegral.fakemc.ping.Chat;
import ch.inverseintegral.fakemc.ping.Player;
import ch.inverseintegral.fakemc.ping.Players;
import ch.inverseintegral.fakemc.ping.Version;
import com.google.gson.Gson;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Collections;
import java.util.UUID;

/**
 * Handles the specific packets that are received from the server.
 *
 * @author Inverse Integral
 * @version 1.0
 * @since 1.0
 */
public class PacketHandler extends SimpleChannelInboundHandler<Packet> {

    /**
     * The current protocol state.
     * This should not be confused with the {@link MinecraftHandler#protocol protocol in the minecraft handler}.
     */
    private ProtocolState currentState = ProtocolState.HANDSHAKE;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {

        //TODO: Should be handled dynamically of split up into multiple methods.

        if (msg instanceof Handshake) {
            this.checkState(ProtocolState.HANDSHAKE);
            Handshake handshake = (Handshake) msg;

            if (handshake.getRequestedProtocol() == 1) {
                ctx.channel().pipeline().get(MinecraftHandler.class).setProtocol(Protocol.STATUS);
                this.currentState = ProtocolState.STATUS;
            } else if (handshake.getRequestedProtocol() == 2) {
                ctx.channel().pipeline().get(MinecraftHandler.class).setProtocol(Protocol.LOGIN);
                this.currentState = ProtocolState.USERNAME;
            }

        } else if (msg instanceof StatusRequest) {
            this.checkState(ProtocolState.STATUS);

            StatusResponse statusResponse = new StatusResponse(getResponseData(10, 100, "Test", null));
            ctx.channel().writeAndFlush(statusResponse);

            this.currentState = ProtocolState.PING;
        } else if (msg instanceof Ping) {
            this.checkState(ProtocolState.PING);

            ctx.channel().writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
        } else if (msg instanceof LoginRequest) {
            this.checkState(ProtocolState.USERNAME);

            Kick kick = new Kick(getKickData());
            ctx.channel()
                    .writeAndFlush(kick)
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Checks if the current state is equal to the given state.
     * Otherwise throws an exception.
     *
     * @param expectedState The expected state.
     */
    private void checkState(ProtocolState expectedState) {
        if (this.currentState != expectedState) {
            throw new IllegalStateException(expectedState.name() + " is expected but currently is " + this.currentState.name());
        }
    }

    /**
     * Gets a server list ping response in the json format.
     *
     * @param currentPlayers    The current amount of players.
     * @param maxPlayer         The maximum amount of players.
     * @param motd              The message of the day.
     * @param favicon           The favicon (base64 string).
     * @return                  Returns the json formatted ping response.
     */
    private String getResponseData(int currentPlayers, int maxPlayer, String motd, String favicon) {
        Player player = new Player("§dMagentan", UUID.randomUUID().toString());
        Version version = new Version("1.8", 47);
        Players players = new Players(maxPlayer, currentPlayers, Collections.singletonList(player));
        Chat description = new Chat(motd);

        Gson gson = new Gson();
        return gson.toJson(new ch.inverseintegral.fakemc.ping.StatusResponse(description, players, version, favicon));
    }

    /**
     * Gets some kick data.
     * @return  Returns the json string of the kick data.
     */
    private String getKickData() {
        Gson gson = new Gson();
        return gson.toJson(new Chat("Nope"));
    }

    /**
     * The different state the protocol can have.
     */
    private enum  ProtocolState {

        HANDSHAKE,
        STATUS,
        PING,
        USERNAME

    }

}
