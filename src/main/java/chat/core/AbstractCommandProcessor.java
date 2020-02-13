package chat.core;

import chat.model.ActivableThread;
import chat.model.AppPacket;
import chat.model.IManagerStartable;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import tools.log.Flogger;

public abstract class AbstractCommandProcessor extends ActivableThread {

    protected BlockingQueue<AppPacket> processCommandQueue;
    protected IManagerStartable manager;

    public AbstractCommandProcessor(IManagerStartable manager, BlockingQueue<AppPacket> processCommandQueue) {
        this.processCommandQueue = processCommandQueue;
        this.manager             = manager;
    }

    @Override
    public void run() {
        setActive(true);
        AppPacket appPacket = null;
        while (isActive()) {
            try {
                appPacket = this.processCommandQueue.poll(Globals.PROCESSORS_THREAD_TIMEOUT, TimeUnit.SECONDS);
                if (appPacket != null) {
                    processCommand(appPacket);
                }
                if (!manager.isManagerAlive())
                    throw new SocketException();
            } catch (SocketException se) {
                Flogger.atWarning().withCause(se).log("ER-CP-0001");       //(manager closed) TODO msg: connection lost
                setActive(false);
            } catch (InterruptedException ie) {
                Flogger.atWarning().withCause(ie).log("ER-CP-0002");
            } catch (Exception e) {
                Flogger.atWarning().withCause(e).log("ER-CP-0000");
            }
        }
    }

    protected abstract void processCommand(AppPacket appPacket);
}
