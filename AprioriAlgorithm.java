import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;


public class AprioriAlgorithm {
	public class AssociationRule {
		HashSet<String> body;
		HashSet<String> head;
		public AssociationRule(HashSet<String> body, HashSet<String> head) {
			this.body = body;
			this.head = head;
		}
	}
	
	private static HashMap<HashSet<String>,Integer> setCount = new HashMap<HashSet<String>,Integer>();
	private static ArrayList<HashSet<String>> rowSets = new ArrayList<HashSet<String>>();
	private static ArrayList<AssociationRule> rules = new ArrayList<AssociationRule>();
	private int totalFrequentSets = 0;
	public static void main(String[] args) throws FileNotFoundException {
		AprioriAlgorithm apAlgo = new AprioriAlgorithm();
		// Reads the Dataset Given
		apAlgo.readDataSet();
		
		System.out.println("Dataset Load Complete | Part 1 Begins");
		double[] support = {0.3, 0.4, 0.5, 0.6, 0.7};
		int totalSamples = 100;
		// Creates a dummy HashMap of setCount to populate setCount after each iteration of support for loop
		HashMap<HashSet<String>,Integer> dummySetCount = new HashMap<HashSet<String>,Integer>();
		for(Map.Entry<HashSet<String>,Integer> entry : setCount.entrySet())
			dummySetCount.put(entry.getKey(), entry.getValue());
		// Iterates for each support value in the support array
		for(int i = 0; i < support.length; i++) {
			apAlgo.totalFrequentSets = 0;
			System.out.println("\nSupport set to be "+(int)(support[i]*totalSamples)+"%");
			// Populate setCount with the dummysetCount
			for(Map.Entry<HashSet<String>,Integer> entry : dummySetCount.entrySet())
				setCount.put(entry.getKey(), entry.getValue());
			// Remove itemsets with support < threshold support
			for(Iterator<Map.Entry<HashSet<String>, Integer>> it = setCount.entrySet().iterator(); it.hasNext();) {
    	  		Map.Entry<HashSet<String>, Integer> entry = it.next();
          		if(entry.getValue() < (int)(support[i]*totalSamples))
          			it.remove();
    		}
			// Single length frequent itemsets generated
			System.out.println("Number of length-1 Frequent Itemsets : "+setCount.size());
			apAlgo.totalFrequentSets += setCount.size();
			// Generates frequent itemsets with length 2 and higher
			apAlgo.generateFreqItemsets(2, (int)(support[i]*totalSamples));
			System.out.println("Total : "+apAlgo.totalFrequentSets);
			if((int)(support[i]*totalSamples) == 50)
				System.out.println("Total Rules generated : "+rules.size());
		}
		// Run the template query and print results
		apAlgo.template1Results();
		apAlgo.template2Results();
		apAlgo.template3Results();
	}
	
	public void generateFreqItemsets(int level, int support) {
		
		HashMap<HashSet<String>,Integer> highLevelItemsets = new HashMap<HashSet<String>,Integer>();
		Iterator<Map.Entry<HashSet<String>,Integer>> outerIter = setCount.entrySet().iterator();
		HashSet<String> outerItemset = new HashSet<String>();
		HashSet<String> innerItemset = new HashSet<String>();
		HashSet<String> subset = new HashSet<String>();
		// Picks an set from setCount
		while(outerIter.hasNext()) {
			Iterator<Map.Entry<HashSet<String>,Integer>> innerIter = setCount.entrySet().iterator();
			outerItemset = outerIter.next().getKey();
			// Picks a set from setCount
        	while(innerIter.hasNext()) {
        		subset.clear();
        		innerItemset = innerIter.next().getKey();
        		if(innerItemset.equals(outerItemset))
        			continue;
        		// create a higher length subset
        		subset.addAll(innerItemset);
        		subset.addAll(outerItemset);
        		// if subset length equal to level number
        		if(subset.size() == level) {
        			// calculate support
        			int frequency = getFrequency(subset);
        			// Add it to frequent itemsets
        			if(frequency >= support)
        				highLevelItemsets.put(new HashSet<String>(subset), frequency);
        		}
        	}
        	outerIter.remove();
		}
		// Populate setCount with high level frequent itemsets generated
		setCount.putAll(highLevelItemsets);
		if(highLevelItemsets.size() != 0) {
			System.out.println("Number of length-"+level+" Frequent Itemsets : "+highLevelItemsets.size());
			totalFrequentSets += highLevelItemsets.size();
			if(support == 50)
				generateAssociationRules(highLevelItemsets);
			// Recursively call the method to find higher level frequent itemsets
			generateFreqItemsets(level+1, support);
		} else {
			return;
		}
	}
	
	public void generateAssociationRules(HashMap<HashSet<String>,Integer> freqSets) {
		Iterator<Map.Entry<HashSet<String>,Integer>> iter = freqSets.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry pair = (Map.Entry)iter.next();
			HashSet<String> setK = (HashSet<String>) pair.getKey();
			int setKFreq = (int)pair.getValue() * 100;
			// Generate subsets of the frequent itemsets
			ArrayList<HashSet<String>> subsets = findSubsets(setK);
			// Iterate each subset
			for(HashSet<String> subset : subsets) {
				HashSet<String> dummySetK = new HashSet<String>();
				for(String str : setK)
					dummySetK.add(new String(str));
				// calculate confidence of rule
				int confidence = setKFreq / getFrequency(subset);
				if(confidence >= 60) {
					dummySetK.removeAll(subset);
					AssociationRule obj = new AssociationRule(subset, dummySetK);
					// Add generated association rule obj to the rules ArrayList
					rules.add(obj);
				}
			}
		}
	}
	
	public int getFrequency(HashSet<String> subset) {
		int freq = 0;
		// gets the frequency of the set in each row of the dataset
		for(HashSet<String> rowset : rowSets) {
			if(rowset.containsAll(subset))
				freq++;
		}
		return freq;
	}
	
	public ArrayList<HashSet<String>> findSubsets(HashSet<String> setK) {
		ArrayList<HashSet<String>> result = new ArrayList<HashSet<String>>();
		Iterator<String> it = setK.iterator();
		// find subsets of length 2
		if(setK.size() == 2) {
			while(it.hasNext()) {
				HashSet<String> temp = new HashSet<String>();
				temp.add(it.next());
				result.add(temp);
			}
		} else if(setK.size() == 3) {
			// Finds subsets of length 3
			String str1 = it.next();String str2 = it.next();String str3 = it.next();
			HashSet<String> temp = new HashSet<String>();
			temp.add(str1);result.add(new HashSet<String>(temp));
			temp.add(str2);result.add(new HashSet<String>(temp));temp.clear();
			temp.add(str2);result.add(new HashSet<String>(temp));
			temp.add(str3);result.add(new HashSet<String>(temp));temp.clear();
			temp.add(str3);result.add(new HashSet<String>(temp));
			temp.add(str1);result.add(new HashSet<String>(temp));
		}
		return result;
	}
	
	public void readDataSet() throws FileNotFoundException {
		Scanner dataset = new Scanner(new File("gene_expression.txt"));
		while(dataset.hasNextLine()) {
			HashSet<String> rowset = new HashSet<String>();
			int idx = 1;
			// Input each row of dataset
			Scanner datasetRow = new Scanner(dataset.nextLine());
			// Skip the sampleID
			datasetRow.next();
			// Iterate each row 101 times
			while(datasetRow.hasNext()) {
				HashSet<String> singleItemSet = new HashSet<String>();
				if(idx <= 100) {
					// Append gene no. to gene data
					String set = Integer.toString(idx)+datasetRow.next();
					singleItemSet.add(set);
					rowset.add(set);
				} else {
					// get the disease name
					String lastSet = datasetRow.nextLine().replace("\t", "");
    				singleItemSet.add(lastSet);
    				rowset.add(lastSet);
				}
				// populate the setCount with the itemset and its count
				if(setCount.containsKey(singleItemSet))
					setCount.put(singleItemSet,setCount.get(singleItemSet)+1);
				else
					setCount.put(singleItemSet,1);
				idx++;
			}
			datasetRow.close();
			rowSets.add(rowset);
		}
		dataset.close();
	}
	
	public void template1Results() {
		int count1 = 0, count2 = 0, count3 = 0, count4 = 0, count5 = 0, count6 = 0, count7 = 0, count8 = 0;
		int count9 = 0, count10 = 0, count11 = 0;
		String givenSet1 = new String("6UP");
		String givenSet2 = new String("1UP");
		String givenSet3 = new String("10Down");
		String givenSet4 = new String("72UP");
		String givenSet5 = new String("8UP");
		
		for(AssociationRule assn : rules) {
			if(assn.body.contains(givenSet1) || assn.head.contains(givenSet1))
				count1++;
			if(assn.body.contains(givenSet2) || assn.head.contains(givenSet2))
				count2++;
			if((assn.body.contains(givenSet2) && !assn.body.contains(givenSet3))
					|| (assn.head.contains(givenSet2) && !assn.head.contains(givenSet3))
					|| (assn.body.contains(givenSet3) && !assn.body.contains(givenSet2))
					|| (assn.head.contains(givenSet3) && !assn.head.contains(givenSet2)))
				count3++;
			if(assn.body.contains(givenSet1))
				count4++;
			if(!assn.body.contains(givenSet4))
				count5++;
			if((assn.body.contains(givenSet2) && !assn.body.contains(givenSet3))
					|| (assn.body.contains(givenSet3) && !assn.body.contains(givenSet2)))
				count6++;
			if(assn.head.contains(givenSet1))
				count7++;
			if(!assn.head.contains(givenSet1) && !assn.head.contains(givenSet2))
				count8++;
			if((assn.head.contains(givenSet1) && !assn.head.contains(givenSet5))
						|| (assn.head.contains(givenSet5) && !assn.head.contains(givenSet1)))
				count9++;
			if((assn.body.contains(givenSet1) && !assn.body.contains(givenSet2) && !assn.body.contains(givenSet4) && !assn.head.contains(givenSet2) && !assn.head.contains(givenSet4)) 
					|| (!assn.body.contains(givenSet1) && assn.body.contains(givenSet2) && !assn.body.contains(givenSet4) && !assn.head.contains(givenSet1) && !assn.head.contains(givenSet4))
					|| (!assn.body.contains(givenSet1) && !assn.body.contains(givenSet2) && assn.body.contains(givenSet4) && !assn.head.contains(givenSet2) && !assn.head.contains(givenSet1))
					|| (assn.head.contains(givenSet1) && !assn.head.contains(givenSet2) && !assn.head.contains(givenSet4) && !assn.body.contains(givenSet2) && !assn.body.contains(givenSet4))
					|| (!assn.head.contains(givenSet1) && assn.head.contains(givenSet2) && !assn.head.contains(givenSet4) && !assn.body.contains(givenSet4) && !assn.body.contains(givenSet1))
					|| (!assn.head.contains(givenSet1) && !assn.head.contains(givenSet2) && assn.head.contains(givenSet4) && !assn.body.contains(givenSet2) && !assn.body.contains(givenSet1)))
				count10++;
			if(assn.body.contains(givenSet1) || assn.body.contains(givenSet2) || assn.body.contains(givenSet4) 
					|| assn.head.contains(givenSet1) || assn.head.contains(givenSet2) || assn.head.contains(givenSet4))
				count11++;
			
		}
		System.out.println("");
		System.out.println("Template 1 | Part 2 Begins");
		System.out.println("1. RULE HAS ANY OF G6_UP => "+count1);
		System.out.println("2. RULE HAS 1 OF G1_UP => "+count2);
		System.out.println("3. RULE HAS 1 OF (G1_UP, G10_DOWN) => "+count3);
		System.out.println("4. BODY HAS ANY OF G6_UP => "+count4);
		System.out.println("5. BODY HAS NONE OF G72_UP => "+count5);
		System.out.println("6. BODY HAS 1 OF (G1_UP, G10_DOWN) => "+count6);
		System.out.println("7. HEAD HAS ANY OF G6_UP => "+count7);
		System.out.println("8. HEAD HAS NONE OF (G1_UP, G6_UP) => "+count8);
		System.out.println("9. HEAD HAS 1 OF (G6_UP, G8_UP) => "+count9);
		System.out.println("10. RULE HAS 1 OF (G1_UP, G6_UP, G72_UP) => "+count10);
		System.out.println("11. RULE HAS ANY OF (G1_UP, G6_UP, G72_UP) => "+count11);
	}
	
	public void template2Results() {
		int ruleSize = 0, bodySize = 0, headSize = 0;
		for(AssociationRule assn : rules) {
			if(assn.body.size() >= 2)
				bodySize++;
			if(assn.head.size() >= 2)
				headSize++;
			if(assn.head.size() + assn.body.size() >= 3)
				ruleSize++;
		}
		System.out.println("");
		System.out.println("Template 2");
		System.out.println("1. SIZE OF RULE >= 3 => "+ruleSize);
		System.out.println("2. SIZE OF BODY >= 2 => "+bodySize);
		System.out.println("3. SIZE OF HEAD >= 2 => "+headSize);
	}

	public void template3Results() {
		int count1 = 0, count2 = 0, count3 = 0, count4 = 0, count5 = 0, count6 = 0;
		String givenSet1 = new String("1UP");
		String givenSet2 = new String("59UP");
		String givenSet3 = new String("6UP");
		String givenSet4 = new String("72UP");
		String givenSet5 = new String("96Down");
		
		for(AssociationRule assn : rules) {
			if(assn.body.contains(givenSet1) && assn.head.contains(givenSet2))
				count1++;
			if(assn.body.contains(givenSet1) || assn.head.contains(givenSet3))
				count2++;
			if(assn.body.contains(givenSet1))
				count3++;
			if(assn.head.contains(givenSet1))
				count4++;
			if((assn.body.contains(givenSet4) && !assn.body.contains(givenSet5))
					|| (assn.head.contains(givenSet4) && !assn.head.contains(givenSet5))
					|| (assn.body.contains(givenSet5) && !assn.body.contains(givenSet4))
					|| (assn.head.contains(givenSet5) && !assn.head.contains(givenSet4)))
				count5++;
			if((assn.head.size() + assn.body.size() >= 3) 
					&& ((assn.body.contains(givenSet2) && !assn.body.contains(givenSet5))
							|| (!assn.body.contains(givenSet2) && assn.body.contains(givenSet5))))
				count6++;
		}
		System.out.println("");
		System.out.println("Template 3");
		System.out.println("1. BODY HAS ANY OF G1_UP AND HEAD HAS 1 OF G59_UP => "+count1);
		System.out.println("2. BODY HAS ANY OF G1_UP OR HEAD HAS 1 OF G6_UP => "+count2);
		System.out.println("3. BODY HAS 1 OF G1_UP OR HEAD HAS 2 OF G6_UP => "+count3);
		System.out.println("4. HEAD HAS 1 OF G1_UP AND BODY HAS 0 OF DISEASE => "+count4);
		System.out.println("5. HEAD HAS 1 OF DISEASE OR RULE HAS 1 OF (G72_UP, G96_DOWN) => "+count5);
		System.out.println("6. BODY HAS 1 of (G59_UP, G96_DOWN) AND SIZE OF RULE >=3 => "+count6);
	}
}
