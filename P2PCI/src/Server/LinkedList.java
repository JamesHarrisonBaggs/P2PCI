package Server;

import java.util.*;

public class LinkedList<E> extends AbstractList<E> {
	
	private Node<E> front;
	
	private int size;
	
	public LinkedList() {
		front = null;
		size = 0;
	}
	
	public void addFirst(E data) {
		if (data == null) {
			throw new NullPointerException();
		}
		
		if (front == null) {
			front = new Node<E>(data);
		} else {
			front = new Node<E>(data, front);
		}
		size++;
	}
	
	@Override
	public E get(int index) {
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException();
		}
		
		Node<E> temp = front;
		
		for (int i = 0; i < index; i++) {
			temp = temp.next;
		}
		
		return temp.data;
	}
	
	public boolean find(E data) {
		if (data == null) {
			throw new NullPointerException();
		}
		
		if(size == 0){
			return false;
		}
		
		if (front.data == data) {
			return true;
		}
		
		Node<E> temp = front.next;
		
		while (temp.next != null) {
			if (temp.data == data) {
				return true;
			} else {
				temp = temp.next;
			}
		}
		
		if (temp.next == null && temp.data == data) return true;
		return false;
	}
	
	public void delete(E data) {
		
		if (data == null) {
			throw new NullPointerException();
		}
		
		if (data == front.data) {
			front = front.next;
			size--;
		}
		Node<E> prev = front;
		Node<E> temp = front.next;
		
		while (temp.next != null) {
			if (temp.data == data) {
				prev.next = temp.next;
				size--;
			} else {
				prev = prev.next;
				temp = temp.next;
			}
		}
		
		if (temp.next == null && data == temp.data) {
			prev.next = null;
			size--;
		}
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@SuppressWarnings("hiding")
	private class Node<E> {
		private E data;
		private Node<E> next;
		
		public Node(E data) {
			this.data = data;
			this.next = null;
		}
		
		public Node(E data, Node<E> next) {
			this.data = data;
			this.next = next;
		}
	}

}
