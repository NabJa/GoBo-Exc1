package gobi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RVcomperator {

	public void printRV(RegionVector rv) {
		for (Region r : rv.regions) {
			System.out.println(r.getX1() + " " + r.getX2() + " ");
		}
	}

	/**
	 * Takes 2 RegionVectors and returns a set with all overlapping starts
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public Set<Integer> compareStarts(RegionVector o1, RegionVector o2) {
		Set<Integer> sameStarts = new TreeSet<Integer>();
		for (int i = 0; i < o1.regions.size(); i++) {
			if (o1.regions.get(i).getX1() == o2.regions.get(i).getX1()) { // o1.inverse().regions.get(i).getX1() ==
																			// o2.inverse().regions.get(i).getX1()
				sameStarts.add(i);
			}
		}
		return sameStarts;
	}

	/**
	 * Takes 2 RegionVectors and returns a set with all overlapping ends
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public Set<Integer> compareEnds(RegionVector o1, RegionVector o2) {
		Set<Integer> sameEnds = new TreeSet<Integer>();
		for (int i = 0; i < o1.regions.size(); i++) {
			if (o1.regions.get(i).getX2() == o2.regions.get(i).getX2()) { // o1.inverse().regions.get(i).getX2() ==
																			// o2.inverse().regions.get(i).getX2())
				sameEnds.add(i);
			}
		}
		return sameEnds;
	}

	/**
	 * If position == 0 it returns true if RV contains same start as input intron.
	 * If position != 0 it returns true if RV contains same end as input intron.
	 * 
	 * @param intron
	 * @param rv
	 * @param position
	 * @return
	 */
	public boolean containsSamePosition(Region intron, RegionVector rv, int position) {
		if (position == 0) {
			int intronStart = intron.getX1();
			for (int i = 0; i < rv.regions.size(); i++) {
				Region RVintrons = rv.regions.get(i);
				int RVstarts = RVintrons.getX1();
				if (intronStart == RVstarts) {
					return true;
				}
			}
			return false;
		} else {
			int intronEnd = intron.getX2();
			for (int i = 0; i < rv.regions.size(); i++) {
				Region RVintrons = rv.regions.get(i);
				int RVends = RVintrons.getX2();
				if (intronEnd == RVends) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * It will return all IDs of transcripts with same start as input start
	 * 
	 * @param start
	 * @param transcripts
	 * @return
	 */
	public HashSet<String> getStarts(int start, HashMap<String, RegionVector> transcripts) {

		HashSet<String> sameStarts = new HashSet<String>();

		transcripts.forEach((id, rv) -> {
			for (Region intron : rv.regions) { // rv.inverse().regions removed inverse to check if it works without
				if (intron.getX1() == start) {
					sameStarts.add(rv.id);
				}
			}
		});

		return sameStarts;
	}

	/**
	 * Returns all IDs of transcripts with same ends as input start
	 * 
	 * @param start
	 * @param transcripts
	 * @return
	 */
	public HashSet<String> getEnds(int end, HashMap<String, RegionVector> transcripts) {

		HashSet<String> sameEnds = new HashSet<String>();

		transcripts.forEach((id, rv) -> {
			for (Region intron : rv.regions) { // rv.inverse().regions removed inverse to check if it works without
				if (intron.getX2() == end) {
					sameEnds.add(rv.id);
				}
			}
		});
		return sameEnds;
	}

	/**
	 * Retains 2 sets. You get all elements that are in BOTH sets.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public HashSet<String> getSameIntron(HashSet<String> setA, HashSet<String> setB) {
		setA.retainAll(setB);
		return setA;
	}

	/**
	 * Returns Region(0,0) if input RegionVector contains only 1 or 0 regions. Else
	 * it returns the inverse of b...
	 * 
	 * @param a
	 * @param b
	 * @return skippedExons as RegionVector
	 */
	public RegionVector subtractFALSE(Region a, RegionVector b) {

		RegionVector skippedExons = new RegionVector();

		if (b.regions.size() < 2) {
			Region zero = new Region(0, 0);
			skippedExons.addRegion(zero);
		} else {
			for (int i = 0; i < b.regions.size() - 1; i++) {
				Region exon = new Region(b.regions.get(i).getX2(), b.regions.get(i + 1).getX1());
				skippedExons.addRegion(exon);
			}
		}

		return skippedExons;
	}

	public RegionVector subtract(Region a, RegionVector b) {
		RegionVector skippedExons = new RegionVector();
		int i = 0;

		if (b.regions.size() < 2) {
			skippedExons.addRegion(new Region(0, 0));
		} else {
			while (b.regions.get(i).getX1() != a.getX1() && i < b.regions.size() - 1) {
				i++;
			}
			while (b.regions.get(i).getX2() != a.getX2() && i < b.regions.size() - 1) {
				int start = b.regions.get(i).getX2();
				int end = b.regions.get(i + 1).getX1();
				Region skippedExon = new Region(start, end);
				skippedExons.addRegion(skippedExon);
				i++;
			}
		}

		return skippedExons;
	}

	public HashSet<String> getOverlappingIntrons(int start, int end, HashMap<String, RegionVector> transcripts) {

		HashSet<String> sameStarts = new HashSet<String>();
		HashSet<String> sameEnds = new HashSet<String>();
		HashSet<String> sameIntrons = new HashSet<String>();

		sameStarts = getStarts(start, transcripts);
		sameEnds = getEnds(end, transcripts);

		sameIntrons = getSameIntron(sameStarts, sameEnds);

		return sameIntrons;
	}

	
	
/**
 * 
 * @param intron
 * @param gene
 * @param transID
 * @return
 */
	public Output getSkippedExon(Region intron, Gene gene, String transID) {

		HashSet<String> sameIntrons = new HashSet<String>();
		sameIntrons = getOverlappingIntrons(intron.getX1(), intron.getX2(), gene.transcripts);
		sameIntrons.remove(transID);

		Output out = new Output(gene.geneID,gene.geneName, gene.geneChr);

		for (String id : sameIntrons) {
			// if(transcripts.containsKey(id)) {}

			RegionVector skippedExons = new RegionVector();
			RegionVector queryRV = gene.transcripts.get(id);

			//System.out.println("ID: " + transcripts.get(id).id);
			// printRV(queryRV);

			skippedExons = subtract(intron, queryRV);
			// System.out.println("Skipped Exons: ");
			printRV(skippedExons);
			if (skippedExons.regions.size() > 0) {
				out.strand = gene.strand;
				out.nprots = gene.transcripts.size();
				out.ntrans = gene.nTrans();
				out.sv = intron;
				out.wt = skippedExons;
				String[] sv_prot = { "id1", "id2" };
				String[] wt_prot = { "id31", "id12" };
				out.sv_prots = sv_prot;
				out.wt_prots = wt_prot;
				out.minSkippedExons = 1;
				out.maxSkippedExons = 1;
				out.minSkippedBases = 1;
				out.maxSkippedBases = 1;
				out.printOutTxt();
				out.closeW();

			}

		}

		return out;

	}

	public void getSkippedExonFromGen(Gene gene) {

		for (RegionVector rv : gene.transcripts.values()) {
			for(Region r : rv.regions) {
				//System.out.println(r);
				getSkippedExon(r, gene, rv.id);
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
