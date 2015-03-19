package goip;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("serial")
public class StringList extends ArrayList<String> {
    public StringList() {
	super();
    }

    public StringList(String x) {
	super(Arrays.asList(x.split("\n")));
    }

    public StringList(String x, String y) {
	super(Arrays.asList(x.split(y)));
    }

    public String toString() {
	StringBuilder result = new StringBuilder();
	this.stream().forEachOrdered(e -> result.append(e));
	return result.toString();
    }
}
