package org.example.server;

import org.example.server.managers.CollectionManager;
import org.example.server.managers.ServerCommandManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.*;

public class Server {
    private final CollectionManager cm;
    private final ServerCommandManager scm;
    private final ForkJoinPool forkJoinPool;
    private final ExecutorService cachedThreadPool;
    private volatile boolean running;

    public Server(CollectionManager cm) {
        this.cm = cm;
        this.scm = new ServerCommandManager(cm);
        this.forkJoinPool = new ForkJoinPool();
        this.cachedThreadPool = Executors.newCachedThreadPool();
        running = true;
    }

    public void start(int port) {
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));

            System.out.println("Сервер запущен на порту " + port);

            forkJoinPool.submit(() -> readRequests(channel));
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Server interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }

    private void readRequests(DatagramChannel channel) {
        while (running && !forkJoinPool.isShutdown()) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);

                if (buffer.position() > 0) {
                    buffer.flip();
                    ByteBuffer bufferCopy = ByteBuffer.allocate(buffer.remaining());
                    bufferCopy.put(buffer);
                    bufferCopy.flip();
                    cachedThreadPool.submit(() -> processRequest(bufferCopy, clientAddress, channel));
                }
            } catch (IOException e) {
                System.out.println("Ошибка при чтении запроса: " + e.getMessage());
            }
        }
    }

    private void processRequest(ByteBuffer buffer, InetSocketAddress clientAddress, DatagramChannel channel) {
        try {
            ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
            Request request = (Request) oi.readObject();

            InetAddress clientIP = clientAddress.getAddress();
            int clientPort = clientAddress.getPort();
            System.out.println("Запрос от клиента " + clientIP + ":" + clientPort + ": " + request);

            Response res = scm.getServerCommand(request.getMessage().split(" ")[0]).execute(request);

            Thread responseThread = new Thread(() -> sendResponse(res, clientAddress, channel));
            responseThread.start();

        } catch (Exception e) {
            System.out.println("Ошибка при обработке запроса: " + e.getMessage());
        }
    }

    private void sendResponse(Response response, InetSocketAddress clientAddress, DatagramChannel channel) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(response);

            ByteBuffer responseBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            channel.send(responseBuffer, clientAddress);
            System.out.println("Ответ отправлен клиенту " + clientAddress + "!");
        } catch (IOException e) {
            System.out.println("Ошибка при отправке ответа: " + e.getMessage());
        }
    }

    public void shutdown() {
        forkJoinPool.shutdown();
        cachedThreadPool.shutdown();
        try {
            if (!forkJoinPool.awaitTermination(60, TimeUnit.SECONDS)) {
                forkJoinPool.shutdownNow();
            }
            if (!cachedThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                cachedThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            forkJoinPool.shutdownNow();
            cachedThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}