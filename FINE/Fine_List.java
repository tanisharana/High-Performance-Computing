
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class FineList{
  private Node head;
  private Node tail;
  public FineList() {
    head      = new Node(Integer.MIN_VALUE);
    tail      = new Node(Integer.MIN_VALUE);
    head.next = tail;
    tail.prev = head;
  }
  /* Add an element.*/
  public boolean add(int item) {
    int key = item;
    head.lock();
    Node pred = head;
    try {
      Node curr = pred.next;
      curr.lock();
      try {
        while (curr.key < key && curr != tail) {
          pred.unlock();
          pred = curr;
          curr = curr.next;
          curr.lock();
        }
        if (curr.key == key) {
          return false;
        }
        Node newNode = new Node(item);
        newNode.next = curr;
          newNode.prev = pred; 
        curr.prev = newNode;
        pred.next = newNode;
        return true;
      } finally {
        curr.unlock();
      }
    } finally {
      pred.unlock();
    }
  }
  /* Remove an element.*/
  public boolean remove(int item) {
    Node pred = null, curr = null;
    int key = item;
    head.lock();
    try {
      pred = head;
      curr = pred.next;
      curr.lock();
      try {
        while (curr.key < key && curr != tail) {
          pred.unlock();
          pred = curr;
          curr = curr.next;
          curr.lock();
        }
        if (curr.key == key) {
          pred.next = curr.next;
            curr.next.prev = pred;
          return true;
        }
        return false;
      } finally {
        if (curr != null){
        curr.unlock();
        }
      }
    } finally {
      pred.unlock();
    }
  }
  public boolean contains(int item) {
    Node last = null, pred = null, curr = null;
    int key = item;
    head.lock();
    try {
      pred = head;
      curr = pred.next;
      curr.lock();
      try {
        while (curr.key < key && curr != tail) {
          pred.unlock();
          pred = curr;
          curr = curr.next;
          curr.lock();
        }
        return (curr.key == key);
      } finally {
        if (curr != null){
        curr.unlock();
        }
      }
    } finally {
      pred.unlock();
    }
  }
  public void display()
  {
      Node temp=head;
      while(temp.next!=null){
            System.out.print("\t"+temp.key);
            temp=temp.next;
      }
  }
 /* Node*/
  private class Node {
    int key;
    Node next;
    Node prev;
    Lock lock;
    Node(int item) {
      this.key = item;
      this.lock = new ReentrantLock();
      this.next=null;
      this.prev = null;
    }
    void lock() {lock.lock();}
    void unlock() {lock.unlock();}
  }
}

