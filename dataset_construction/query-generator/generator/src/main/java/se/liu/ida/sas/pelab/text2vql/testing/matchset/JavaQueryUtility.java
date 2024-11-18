package se.liu.ida.sas.pelab.text2vql.testing.matchset;

import java.util.Arrays;
import java.util.List;

public class JavaQueryUtility {
	static int countCommon(List<Object[]> ls1, List<Object[]> ls2){
		int common = 0;
		base: 
		for(Object[] item1 : ls1){
			for(Object[] item2 : ls2){
				if(Arrays.equals(item1,item2)){
					common++;
					continue base;
				}
			}
		}
		return common;

	}
}
