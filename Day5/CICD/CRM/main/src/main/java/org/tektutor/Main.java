package org.tektutor;

public class Main {

	public String getModuleName() {
		return "Main Module";
	}

	public static void main ( String[] args ) {
		Main mainObj = new Main();
		System.out.println ( mainObj.getModuleName() );

		Frontend fe = new Frontend();
		System.out.println ( fe.getModuleName() );
	}

}
