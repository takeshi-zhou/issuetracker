package cn.edu.fudan.issueservice.util;

import cn.edu.fudan.issueservice.domain.Location;
import cn.edu.fudan.issueservice.domain.RawIssue;

import java.util.ArrayList;
import java.util.List;

public class LocationCompare {
	
	private static final double threshold_commonality = 0.95;
	private static final double threshold_overlapping = 0.9;
	private static final double threshold_lcs = 0.9;

	
	public static boolean isUniqueIssue(RawIssue issue_1, RawIssue issue_2) {
//		if(!issue_1.getType().equals(issue_2.getType()))
////			return false;
		if(!issue_1.equals(issue_2))return false;
		List<Location> intersect_locations = intersect(issue_1.getLocations() , issue_2.getLocations());
		List<Location> intersect_locations2 = intersect(issue_2.getLocations() , issue_1.getLocations());
		List<Location> union_locations = union(issue_1.getLocations() , issue_2.getLocations());
	    double commonality = intersect_locations.size() /union_locations.size();

	    if (commonality < threshold_commonality)
	         return false;

	    int count_overlapping = 0;
	    for(Location location : intersect_locations) {
	    	//theSameItem( issue_1.contentOf(location), issue_2.contentOf((Location) it))
	    	for(Location location2 : intersect_locations2) { //may exist the situation of  n to n
	    		if(location2.equals(location) && theSameItem( location.getCode(), location2.getCode()))
	    				count_overlapping++;
	    	}
	    }


	    if (count_overlapping/union_locations.size()<threshold_overlapping)
	         return false;
	    return true;
	}

	private static List<Location> union(List<Location> locations1, List<Location> locations2) {
		// TODO Auto-generated method stub
		List<Location> unionList = new ArrayList<>();
		for (Location location : locations1) {
			unionList.add(location);
		}
		for (Location location : locations2) {
			if(!unionList.contains(location))
			     unionList.add(location);
		}
		return unionList;
	}

	private static  List<Location> intersect(List<Location> locations1, List<Location> locations2) {
		// TODO Auto-generated method stub
		List<Location> insertList = new ArrayList<>();
		//intersectSet's locations are from locations1 
		for (Location location : locations1) {
			if ( locations2.contains(location) ) {
				insertList.add(location);
			}
		}
		
		return insertList;
	}
	
	private static boolean theSameItem(String content_1,String content_2){
		   if (lcs(content_1, content_2)/Math.max(content_1.length(), content_2.length())>threshold_lcs)
		       return true;
		   return false;
	}
	
	private static int lcs(String content_1,String content_2) {
		
        char ch1[]=content_1.toCharArray();
        char ch2[]=content_2.toCharArray();

        int length1=ch1.length;
        int length2=ch2.length;

        int max=0;
        int sign=0;
        int[] mat=new int[length1]; 

        for(int i=0;i<length2;i++){
            for(int j=length1-1;j>=0;j--){
                if(ch2[i]==ch1[j]){
                    if(i==0 || j==0){
                        mat[j]=1;
                        if(max==0){
                            max=1;
                            sign=j;
                        }       
                    }
                    else{
                        mat[j]=mat[j-1]+1;
                        if(mat[j]>max){
                            max=mat[j];
                            sign=j;
                        }
                    }       
                }
                else{
                    mat[j]=0;
                }
            }
        }
        return new String(ch1, sign-max+1, max).length();
	}
}
