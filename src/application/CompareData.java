package application;

import java.util.Comparator;

/**
 * Compare date to use at search
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class CompareData implements Comparator<Tag>{

	/**
	 * implement compare method
	 * @param o1 get tag1
	 * @param o2 get tag2
	 * 
	 * @return int 
	 */
	@Override
	public int compare(Tag o1, Tag o2) {
		int ret = String.CASE_INSENSITIVE_ORDER.compare(o1.getkey(),o2.getkey());
		if(ret == 0) {
			ret = 0;
		}
		return ret;
	}

}
