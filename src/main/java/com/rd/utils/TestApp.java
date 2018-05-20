package com.rd.utils;

import java.util.*;

public class TestApp {

	public static String[] highestPriority(List<String> priorityList, String skill) {
		String[] output = null;
		int max = 0;
		String priorityObj = null;
		for (int i = 0; i < priorityList.size(); i++) {
			String priority = priorityList.get(i);
			String[] data = priority.split("#");
			if (max < Integer.parseInt(data[1]) && priority.contains(skill)) {
				max = Integer.parseInt(data[1]);
				output = data;
				priorityObj = priority;
			}
		}
		if (priorityObj != null)
			priorityList.remove(priorityObj);
		return output;
	}

	public static String[] warehouseScalability(String[] emps, String[] works) {
		int minTime = 1;
		// Node root = createLinkedList(works);

		Linked linked = new Linked();
		linked.createLinkedList(works);

		int empTimeList[] = new int[emps.length];
		String empWorkList[] = new String[emps.length];

		while (linked.isEmpty()) {
			int k = 0;
			for (String emp : emps) {
				String data[] = emp.split("#");
				String employee = data[0];
				String skill = data[1];
				Integer time = empTimeList[k];
				if (time != null && time != 0) {
					empTimeList[k] = time - minTime;
				} else {
					Node node = linked.remove(skill);
					String skillData[] = node.data;
					System.out.println(skillData[0]+"  "+skillData[1]);
					if (skillData != null) {
						String empWork = empWorkList[k];
						if (empWork != null) {
							empWorkList[k] = empWork + "#" + skillData[3];
						} else {
							empWorkList[k] = employee + "#" + skillData[3];
						}
						int t = Integer.parseInt(skillData[2]);
						if (minTime > t) {
							minTime = t;
						}
						empTimeList[k] = t;
					}
				}
				k++;
			}
		}
		return empWorkList;
	}

//	public static void main(String[] args) {
//
//		String emps[] = { "W1#S1", "W2#S2", "W3#S3", "W4#S1" };
//		String works[] = { "S1#40#10#101", "S2#10#5#102", "S3#90#15#103", "S3#91#20#104", "S2#20#5#105", "S1#20#10#106",
//				"S1#90#15#107", "S2#30#20#108", "S3#40#5#109", "S1#50#5#110" };
//
//		
//		warehouseScalability(emps,works);
//
////		 for(String out:warehouseScalability(emps,works)){
////		 System.out.println(out);
////		
////		 }
//
//	}
}

class Node {
	String[] data;
	int priority;
	Node next;

	public Node(int priority, String[] data) {
		this.data = data;
		this.priority = priority;
	}

	public String[] getData() {
		return data;
	}

	public int getPriority() {
		return priority;
	}

	public void setData(String[] data) {
		this.data = data;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}

class Linked {

	Node root = null;

	public Linked() {
		this.root = null;
	}

	public Node createLinkedList(String[] works) {
		for (String work : works) {
			String data[] = work.split("#");
			int priority = Integer.parseInt(data[1]);
			insert(priority, data);
		}
		return root;
	}
	
	public boolean isEmpty(){
		return this.root == null;
	}
	
	public Node remove(String skill){
		Node node = this.root;
		
		if(node != null && node.data[0].equals(skill)){
			this.root = node.next;
			return node;
		}
		
		while (node != null && node.next != null) {
			if(node.data[0].equals(skill)){
				node.next = node.next.next;
				return node.next;
			}
			node = node.next;
		}
		return node;
	}

	public void insert(int priority, String[] value) {
		if (this.root == null) {
			this.root = new Node(priority, value);
		} else {
			if (this.root.getPriority() < priority) {
				Node newNode = new Node(priority, value);
				newNode.next = this.root;
				this.root = newNode;
				return;
			}
			Node node = this.root;
			while (node != null && node.next != null) {
				if (node.next != null && node.next.getPriority() < priority) {
					Node newNode = new Node(priority, value);
					newNode.next = node.next;
					node.next = newNode;
					return;
				}
				node = node.next;
			}
			node.next = new Node(priority, value);
		}
	}
}
