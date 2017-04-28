package visual.thesaurus;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Moses Muigai Gitau
 */
public class ThreadProcess extends Thread {

    final ArrayList<ThreadListener> threadListeners = new ArrayList();
    final ArrayList<Thread> threads = new ArrayList();

    public ThreadProcess(Thread... threads) {
        for (Thread t : threads) {
            this.threads.add(t);
        }
        start();
    }

    @Override
    public void run() {
        boolean allAlive = true;
        while (allAlive) {
            int value = 0;
            for (Thread t : threads) {
                if (!t.isAlive()) {
                    value++;
                }
            }
            if (value == threads.size()) {
                allAlive = false;
            } else {
                for (ThreadListener threadListener : threadListeners) {
                    threadListener.alive();
                }
            }
        }
        for (ThreadListener threadListener : threadListeners) {
            threadListener.dead();
        }
    }

    public void addThreadListener(ThreadListener threadListener) {
        threadListeners.add(threadListener);
    }

    public void removeThreadListener(ThreadListener threadListener) {
        threadListeners.remove(threadListener);
    }
}
