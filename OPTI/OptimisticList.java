
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class OptimisticList{
  private Node head;
  private Node tail;
  public OptimisticList() {
    this.head  = new Node(Integer.MIN_VALUE);
    this.tail = new Node(Integer.MAX_VALUE);
    this.head.next = this.tail;
    this.tail.prev = this.head;
  }
  public boolean add(int item) {
    int key = item;
    while (true) {
      Node pred = this.head;
      Node curr = pred.next;
      while (curr.key <= key) {
        pred = curr; curr = curr.next;
      }
      pred.lock(); curr.lock();
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present
            return false;
          } else {               // not present
            Node node = new Node(item);
            node.prev = pred;
            node.next = curr;
            pred.next = node;
             curr.prev = node;
             
            return true;
          }
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  /* Remove an element.*/
  public boolean remove(int item) {
    int key = item;
    while (true) {
      Node pred = this.head;
      Node curr = pred.next;
      while (curr.key < key) {
        pred = curr; curr = curr.next;
      }
      pred.lock(); curr.lock();
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present in list
            pred.next = curr.next;
             curr.next.prev = pred;
            return true;
          } else {               // not present in list
            return false;
          }
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  /*Test whether element is present*/
  public boolean contains(int item) {
    int key = item;
    while (true) {
      Node pred = this.head; // sentinel node;
      Node curr = pred.next;
      while (curr.key < key) {
        pred = curr; curr = curr.next;
      }
      try {
        pred.lock(); curr.lock();
        if (validate(pred, curr)) {
          return (curr.key == key);
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  private boolean validate(Node pred, Node curr) {
    Node node = head;
    while (node.key <= pred.key) {
      if (node == pred)
        return pred.next == curr && curr.prev == pred;
      node = node.next;
    }
    return false;
  }
public void display()
{
      Node temp=head;
      while(temp.next!=null){
            System.out.print("\t"+temp.key);
            temp=temp.next;
      }
}
/**
* list entry
*/
private class Node {
    int key;
    Node prev;
    Node next;
    Lock lock;
    Node(int item) {
      this.key = item;
       this.prev= null;
       this.next=null;
      lock = new ReentrantLock();
      
    }
    void lock() {lock.lock();}
    void unlock() {lock.unlock();}
  }
}

