package chat.core;

import chat.model.ActivableThread;
import chat.model.ChatPacket;
import chat.model.IHeartBeatTimeHolder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

public class WorkerCommandReceiver extends ActivableThread {

    public BlockingQueue<ChatPacket> commandQueue;

    private ObjectInputStream inputStream;
    private IHeartBeatTimeHolder heartBeatTimeHolder;

    public WorkerCommandReceiver(BlockingQueue<ChatPacket> commandQueue, InputStream inputStream, IHeartBeatTimeHolder heartBeatTimeHolder) throws IOException {
        this.commandQueue        = commandQueue;
        this.inputStream         = new ObjectInputStream(new BufferedInputStream(inputStream));
        this.heartBeatTimeHolder = heartBeatTimeHolder;
    }

    @Override
    public void run() {
        setActive(true);
        while (isActive()) {
            try {
                ChatPacket receivedMessage = (ChatPacket) inputStream.readObject();
                heartBeatTimeHolder.updateHeartBeatTime();

                commandQueue.put(receivedMessage);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}