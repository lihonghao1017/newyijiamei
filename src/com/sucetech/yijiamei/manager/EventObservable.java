package com.sucetech.yijiamei.manager;



public interface EventObservable {
	public void addObserver(EventObserver observer);

	public void deleteObserver(EventObserver observer);

	public void notifyObservers(EventStatus status, Object obj);
}
