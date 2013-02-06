package org.liverpool.utils;

import java.util.*;

public class SortHashMapAccordingToField {
	
	HashMap<String, long[] > geneLocationMap;
	TreeMap <Long, String> start_name_map;
	
	/**
	 * 
	 * @param geneLocationMap
	 */
	public SortHashMapAccordingToField(HashMap<String, long[] > geneLocationMap){
		 this.geneLocationMap = new HashMap<String, long[] >(geneLocationMap);
		 this.start_name_map = new TreeMap <Long, String>(); 
	}
	
	/**
	 * 
	 */
	public void fillStart_name_map(){
		Iterator<String> genes = this.geneLocationMap.keySet().iterator();
		
		while(genes.hasNext()){
			String gene = genes.next();
			long start = this.geneLocationMap.get(gene)[0];
			this.start_name_map.put(new Long(start),gene);
		}
	}
	
	/**
	 * 
	 * @param locationToSearch
	 * @return
	 */
	public String findGeneNameForLocation(long locationToSearch_start, long locationToSearch_end){
		
		Set<Long> startSet = this.start_name_map.keySet(); // will return sorted keys
		Long [] startLocations = new Long[startSet.size()];
		startLocations = startSet.toArray(startLocations);
		
		//Arrays.sort(startLocations);
		long min_start_coord = startLocations[0];
		long max_start_coord = startLocations[startLocations.length -1];
		
		if(locationToSearch_start < min_start_coord || locationToSearch_start > max_start_coord)
			return null;
		
		int index = Arrays.binarySearch(startLocations,locationToSearch_start);
		
		if((index < 0))
			index = (-(index) - 1 ) - 1; // According to Java docs for the insertion point
		
		long searchedLocationValue = 0;
		String correspondingGene = null;
		
		try{
			searchedLocationValue = startLocations[index];
			correspondingGene = this.start_name_map.get(new Long(searchedLocationValue));
		}catch(Exception e){
			System.out.println("Problem during co-ord lookup : Index is " + index + "\t Location to search = " + locationToSearch_start);
			return null;
		}

		long gene_start = this.geneLocationMap.get(correspondingGene)[0];
		long gene_end   = this.geneLocationMap.get(correspondingGene)[1];
		
		if(locationToSearch_start >= gene_start && locationToSearch_end <= gene_end)
			return correspondingGene;
		else return null;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String [] args){
		// somehow read the hash to pass to the constructor
		HashMap<String, long[] > geneLocationMap = new HashMap<String, long[] > ();
		long locateThis_start = 2312; // some number for testing..
		long locateThis_end = 2312; // some number for testing..
		
		SortHashMapAccordingToField se = new SortHashMapAccordingToField(geneLocationMap);
		se.fillStart_name_map();
		String geneName = se.findGeneNameForLocation(locateThis_start,locateThis_end);

	}
}
