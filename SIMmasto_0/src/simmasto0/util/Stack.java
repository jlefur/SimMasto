package simmasto0.util;

public class Stack {

	final static int SIZE = 10; // assez grand ?

	private int sp;
	private int[] t;
	public Stack() { // Construire une pile vide
		t = new int[SIZE];
		sp = 0;
	}
	boolean isEmpty() {
		return sp == 0;
	}

	void push(int x) {
		if (sp >= SIZE) throw new Error("Push : pile pleine");
		t[sp++] = x; // Ou bien : t[sp] = x ; sp = sp+1 ;
	}

	int pop() {
		if (isEmpty()) throw new Error("Pop : pile vide");
		return t[--sp]; // Ou bien : sp = sp-1 ; return t[sp] ;
	}
}
