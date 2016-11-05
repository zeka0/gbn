package protocol.GBN.sender;

/**
 * Created by AlienX
 */
public class SenderTimerThread extends Thread{
    private final Object timer_lock;
    private final Object stop_timer_lock;

    private GBNSenderThread senderThread;
    private final long wait_time;

    private volatile boolean isWaitRestart;

    public SenderTimerThread(GBNSenderThread senderThread,
                             Object timer_lock, Object stop_timer_lock,
                             long wait_time){
        isWaitRestart = false;
        this.senderThread = senderThread;
        this.wait_time = wait_time;
        this.timer_lock = timer_lock;
        this.stop_timer_lock = stop_timer_lock;
        this.senderThread.set_isTimeout(false);
    }

    public boolean get_isWaitRestart(){
        return isWaitRestart;
    }

    private void notify_timer_lock(){
        synchronized (timer_lock) {
            timer_lock.notifyAll();
        }
    }

    @Override
    public void run(){
        synchronized (stop_timer_lock) {
            try {
                stop_timer_lock.wait(wait_time);
                this.senderThread.set_isTimeout(true);
                notify_timer_lock();
            } catch (InterruptedException e) {
                /**
                 * Interrupted by receiver thread
                 * */
                this.senderThread.set_isTimeout(false);
                    notify_timer_lock();
            }
        }
    }
}
