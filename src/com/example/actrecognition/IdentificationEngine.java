package com.example.actrecognition;

import java.util.ArrayList;

public final class IdentificationEngine {
	public final static IdentificationEngine INSTANCE = new IdentificationEngine();

	private IdentificationEngine() {
		// Exists only to defeat instantiation.
	}
	
	
	public AccActivity findClosestMatch(AccActivity query, ArrayList<AccActivity> library){
		AccActivity guess = library.get(0);
		
		
		
		return query;
	}
}
