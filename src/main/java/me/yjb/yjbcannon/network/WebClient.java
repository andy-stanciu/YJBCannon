package me.yjb.yjbcannon.network;

import me.yjb.yjbcannon.YJBCannon;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;

public class WebClient extends WebSocketClient
{
    private final YJBCannon core;

    public WebClient(URI uri, YJBCannon core)
    {
        super(uri);
        this.core = core;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {}

    @Override
    public void onClose(int code, String reason, boolean remote) {}

    @Override
    public void onMessage(String message)
    {
        switch (message.substring(0, 4))
        {
            case "lic:":
                readValidationResponse(message.substring(4));
                break;
            case "upd:":
                readUpdateResponse(message.substring(4));
                break;
        }
    }

    private void readValidationResponse(String message)
    {
        String[] information = message.split("/");

        if (information.length == 1)
        {
            this.core.setValid(false);
        }
        else if (information.length == 2)
        {
            this.core.setClientIP(information[1].trim());
            this.core.setValid(false);
        }
        else
        {
            this.core.setClientIP(information[1].trim());
            this.core.setClientName(information[2].trim());
            this.core.setValid(information[0].trim().equalsIgnoreCase("1"));
        }
    }

    private void readUpdateResponse(String message)
    {
        String[] information = message.split("/");

        if (information.length < 2)
        {
            this.core.setOutdated(false);
        }
        else
        {
            this.core.setOutdated(information[0].trim().equalsIgnoreCase("true"));
            this.core.setUpdatedVersion(information[1].trim());
        }

        this.core.setCheckedForUpdates(true);
    }

    @Override
    public void onMessage(ByteBuffer message)
    {
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(this.core.getDataFolder().getParent() + "/YJBCannon-" + this.core.getUpdatedVersion() + ".jar");
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            bufferedOutputStream.write(message.array(), 0, message.capacity());

            bufferedOutputStream.flush();
            fileOutputStream.close();
            bufferedOutputStream.close();

            this.core.setUpdateDownloaded(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e) { e.printStackTrace(); }
}
