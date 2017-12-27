package com.kekayan;

import com.kekayan.interfaces.Observer;
import com.kekayan.interfaces.Subject;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class FilesMonitor extends Thread implements Subject {
    private ArrayList<Observer> observers;
    private ArrayList<String> events = new ArrayList<>();

    public FilesMonitor() {
        observers = new ArrayList<>();

    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyAllObservers() {
        for (Observer observer : observers) {
            observer.notifyMe(events);
        }
    }

    public void runMonitor() {
        Runnable r1 = new Runnable() {
            public void run() {
                try {
                    watchservice();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable r2 = new Runnable() {
            public void run() {
                while (true) {
                    if (events.size() > 0)
                        notifyAllObservers();

                    events.clear();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(10000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thr1 = new Thread(r1);
        Thread thr2 = new Thread(r2);
        thr1.start();
        thr2.start();


    }

    public void watchservice() throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("E:/Test");
        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {

                events.add(
                        "File : " + event.context() + " Created.");

                key.reset();
            }
        }

    }


}
