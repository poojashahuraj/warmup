import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
/**
 * Created by parallels on 2/9/15.
 */
public class MainClass {
    public final int cacheSize;
    public ConcurrentLinkedQueue<Integer> queue;
    public ConcurrentMap <Integer, String>map;

    public MainClass(final int c) {
    this.cacheSize= c;
    this.queue = new ConcurrentLinkedQueue<Integer>();
        this.map=new ConcurrentHashMap<Integer, String>(c);
    }
    public synchronized boolean put(final Integer key, final String value) {
      if(key==null || value == null){
                }
        if(map.containsKey(key)){
            queue.remove(key);
        }
        while (queue.size() >= cacheSize){
            Integer expiredKey = queue.poll();
            if (expiredKey !=null){
                map.remove(expiredKey);
            }
        }
        queue.add(key);
        map.put(key,value);
        return true;
        }
    public String get(final Integer key) {
        return map.get(key);
    }
    public void remove(final Integer key){
         map.remove(key);
        queue.remove(key);
    }
    public void clear(){
        queue.clear();
        map.clear();
    }
    public Integer sizeQueue(){
        return queue.size();
    }
    public Integer sizeMap(){
        return map.size();
    }
    public static void main(String[] args) {
        System.out.print("Just the main class");
    }
}

