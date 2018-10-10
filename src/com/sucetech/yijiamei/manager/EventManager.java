package com.sucetech.yijiamei.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihh on 2018/9/19.
 */

public class EventManager implements EventObservable {
    private static EventManager instance = new EventManager();
    private List<EventObserver> observerList = new ArrayList();

    public static EventManager getEventManager() {
        return instance;
    }

    @Override
    public void addObserver(EventObserver observer) {
        if (!observerList.contains(observer))
            observerList.add(observer);

    }

    @Override
    public void deleteObserver(EventObserver observer) {

    }

    @Override
    public void notifyObservers(EventStatus status, Object obj) {
        for (int i = 0; i <observerList.size() ; i++) {
            observerList.get(i).updata(status,obj);
        }
    }
}
