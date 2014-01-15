package pipe.petrinet.adapters.valueAdapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Used for elements which need a value field in their element
 * E.g.
 * <name>
 *     <value>
 *         value goes here
 *     </value>
 * </name>
 */
public class StringValueAdapter extends XmlAdapter<StringValueAdapter.AdaptedString, String> {
    @Override
    public String unmarshal(AdaptedString adaptedString) throws Exception {
        return adaptedString.value;
    }

    @Override
    public AdaptedString marshal(String s) throws Exception {
        AdaptedString adaptedString = new AdaptedString();
        adaptedString.value = s;
        return adaptedString;
    }

    public static class AdaptedString {
         public String value;
    }
}
