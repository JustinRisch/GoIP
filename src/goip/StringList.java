package goip;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("serial")
public class StringList extends ArrayList<String> {
	public StringList(){
		super();
	}
	public StringList(String x){
		super(Arrays.asList(x.split("\n")));
	}

	public String toString(){
		StringBuilder result = new StringBuilder();
		this.stream().forEachOrdered(e->result.append(e));
		return result.toString();
	}
}
