package org.example.server;

import org.example.server.managers.CollectionManager;
import org.example.server.managers.ServerCommandManager;
import org.example.server.system.ReaderXML;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Server {
    CollectionManager cm;
    ServerCommandManager scm;

    public Server(CollectionManager cm) throws FileNotFoundException {
        this.cm = cm;
        this.scm = new ServerCommandManager(cm);
        cm.setCollection(ReaderXML.read(new FileInputStream(System.getenv("FILE_LAB5"))));
    }

    public void start(int port) {
        try {
            // сетевой канал
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            // канал к порту
            channel.bind(new InetSocketAddress(port));

            System.out.println("Server launched on the port " + port);

            while (true) {
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(4096);
                    InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);

                    if (buffer.position() > 0) {
                        buffer.flip();

                        ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
                        Request request = (Request) oi.readObject();

                        InetAddress clientIP = clientAddress.getAddress();
                        int clientPort = clientAddress.getPort();

                        System.out.println("Request from the client " + clientIP + ":" + clientPort + ": " + request);

                        Response res = scm.getServerCommand(request.getMessage().split(" ")[0]).execute(request);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(res);

                        ByteBuffer responseBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
                        channel.send(responseBuffer, clientAddress);
                        System.out.println("Response sent to client!");

                    }
                } catch (Exception e) {
                    System.out.println("Exception during request processing: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error while starting the server: " + e.getMessage());
        }
    }
}