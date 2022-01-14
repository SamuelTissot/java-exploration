package com.tutorial;


import java.util.Map;
import java.util.concurrent.*;

/**
 * Normal Collections are not thread safe we need
 * to use the Concurrent Collection in a multithreaded environment
 * <p>
 * Concurrent Collection offer RW looking
 * so multiple thread can read from it at the same time but not Write
 */
public class ConcurrentCollections {
    public static void main(String[] args) {
        System.out.println("----- Thread Problems ------");
        threadSafeLooping();

        System.out.println("----- ConcurrentMap ------");
        concurrentMapOverview();

        System.out.println("----- ConcurrentQueue ------");
        concurrentQueueOverview();

    }

    private static void threadSafeLooping() {
        // HashMap is not thread safe while looping
//        Map<String, String> m = new HashMap<>();

        // concurrent version of hashmap is thread safe
        Map<String, String> m = new ConcurrentHashMap<>();

        m.put("Jean-loup", "wolf-jean");
        m.put("Karina", "cold");


        m.keySet().parallelStream().forEach(k -> {
            System.out.println(m.get(k));
            m.remove(k);
        });

        // same error has the for-in loop
//        for (String k: m.keySet()) {
//            System.out.println(m.get(k));
//            m.remove(k);
//        }
    }

    private static void concurrentMapOverview() {

        // refer to it by interface
        // it's a child of Map
        ConcurrentMap<String, String> concurrentMap = new ConcurrentHashMap();
//        ConcurrentMap<String, String> concurrentMap = new ConcurrentSkipListMap<>();
        concurrentMap.put("Alexander", "Theofanopoulos");
        concurrentMap.put("Emilio", "Fugarolas");
        concurrentMap.put("Jean-loup", "Kahloun");
        concurrentMap.put("Jim", "Nidositko");
        concurrentMap.put("Karina", "Montgomery");
        concurrentMap.put("Maxime", "MilletteCoulombe");
        concurrentMap.put("Moe", "unknown");
        concurrentMap.put("Sabrina", "Piccioni");
        concurrentMap.put("Sam", "T");
        concurrentMap.put("Seb", "Paiva");
        concurrentMap.put("shawn", "leblanc");

        System.out.println(concurrentMap.size());
    }

    private static void concurrentQueueOverview() {
        // BlockingQueue Interface
        BlockingDeque<String> queue = new LinkedBlockingDeque<>();
        queue.offer("Alexander Theofanopoulos");
        queue.offer("Emilio Fugarolas");
        queue.offer("Jean-loup Kahloun");
        queue.offer("Jim Nidositko");
        queue.offer("Karina Montgomery");
        queue.offer("Maxime MilletteCoulombe");
        queue.offer("Moe unknown");
        queue.offer("Sabrina Piccioni");
        queue.offer("Sam T");
        queue.offer("Seb Paiva");
        queue.offer("shawn leblanc");

        System.out.println(queue.peek());
    }
}
