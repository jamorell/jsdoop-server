package com.jsdoop.server.utils;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jsdoop.server.constants.Constants;
import com.jsdoop.server.controllers.TopologyController;

@Service
public class StatsAnalyzer {
	private static ConcurrentHashMap<Long, ConcurrentHashMap<String, Long>> lastRequest = new ConcurrentHashMap<>();

	
	private static final Logger log = LoggerFactory.getLogger(TopologyController.class);
	
	public static void removeJob(long idJob) {
		StatsAnalyzer.lastRequest.remove(idJob);
	}
	
	public int newRequest(long idJob, String username, String infoWorker, String remoteAddr, long timeRequest) {
		log.info("newRequest init" + " Thread {} " + Thread.currentThread().toString());
		lastRequest.putIfAbsent(idJob, new ConcurrentHashMap<String, Long>());
		ConcurrentHashMap<String, Long> map = lastRequest.get(idJob);
		String id = "" + username + "" + infoWorker + "" + remoteAddr;
		map.put(id, timeRequest);
		int counter = 0;
		for (Entry<String, Long> set : map.entrySet()) {	 
			Long next = set.getValue();
			log.info("timeRequest - next <= MAX_TIME");
			log.info((timeRequest - next) + "  <= " + Constants.MAX_TIME_TO_DISCONNECT_WORKER);
			log.info("newrequest time " + (timeRequest - next));
			if (timeRequest - next <= Constants.MAX_TIME_TO_DISCONNECT_WORKER) {
				counter ++;
			}
		}
//		Set<Entry<String, Long>> set = map.entrySet();
//		set.
//		Collection<Long> values = map.values();
//		int counter = 0;
//		
//		Iterator<Long> it = values.iterator();
//		while (it.hasNext()) {
//			log.debug("timeRequest - MAX_TIME > it.next()");
//			log.debug((timeRequest - MAX_TIME) + "  > " + it.next());
//			if (timeRequest - MAX_TIME > it.next()) {
//				counter ++;
//			}
//		}
		log.info("...............NWORKERS = " + counter);
		log.info("newRequest end" + " Thread {} " + Thread.currentThread().toString());
		return counter;
	}
}
