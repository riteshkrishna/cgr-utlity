package org.liverpool.utils;

import java.util.ArrayList;

public class BlastStructure{
	
	public ArrayList<String> subjects ;
	public ArrayList<Double> e_values ;
	
	public BlastStructure(){
		subjects = new ArrayList<String>();
		e_values = new ArrayList<Double>() ;
	}
	
	public void fill(String subject,double e_value){
		subjects.add(subject);
		e_values.add(e_value);
	}
	
	public void printContent(){
		for(int i = 0; i < subjects.size(); i++)
			System.out.println("\n" + subjects.get(i) + "\t" + e_values.get(i));
	}
}
