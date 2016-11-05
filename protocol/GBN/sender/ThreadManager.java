package protocol.GBN.sender;

/**
 * Created by AlienX
 */
public class ThreadManager {
    private static SenderTimerThread timerThread;
    private static final Object timer_clock = new Object();
    private static final Object stop_timer_clock = new Object();
    private static GBNSenderReceiverThread receiverThread;
    private static GBNSenderThread senderThread;
    private static long timeout;

    //called before starting senderThread and receiverThread
    public static void register(GBNSenderThread senderThread,
                                GBNSenderReceiverThread receiverThread,
                                long timeout){
        ThreadManager.senderThread = senderThread;
        ThreadManager.receiverThread = receiverThread;
        ThreadManager.timeout = timeout;
    }

    public static void stop_receiver_thread(){
        receiverThread.set_sdStop();
    }

    public static void stop_sender_thread(){
        senderThread.set_sdStop();
    }

    public static Object get_timer_clock(){
        return timer_clock;
    }

    public static Object get_stop_timer_lock(){
        return stop_timer_clock;
    }

    public static void start_timer(){
        timerThread = new SenderTimerThread(senderThread, timer_clock, stop_timer_clock, timeout);
        timerThread.start();
    }
}
