//Playing with conditions
public class RequestIssuer extends Thread{
        private boolean available;

        public RequestIssuer() {
            available = false;
            start();
        }

        public void run() {
            while (true) { makeWaiting(); }
        }

        public synchronized void makeWaiting() {
            while (available) {
                try { wait(); } catch (InterruptedException e) {}
            }
            // take 1 second to produce a new request
            try { sleep(1000); } catch (InterruptedException e) {}
            available = true;
            notifyAll();
        }

        public synchronized void takeRequest() {
            while (!available) {
                try { wait(); } catch (InterruptedException e) {}
            }
            available = false;
            notifyAll();
        }
    }