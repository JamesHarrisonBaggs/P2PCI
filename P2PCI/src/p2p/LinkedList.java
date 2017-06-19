package p2p;

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
	
	public void delete(int index) {
		
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException();
		}
		
		Node<E> temp = front;
		if (index == 0) {
			front = front.next;
		} else {
			for (int i = 0; i < index - 1; i++) {
				temp = temp.next;
			}
			temp.next = temp.next.next;
		}
		size--;
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
