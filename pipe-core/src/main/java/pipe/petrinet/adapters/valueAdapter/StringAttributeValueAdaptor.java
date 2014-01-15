package pipe.petrinet.adapters.valueAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class differs from {@link pipe.petrinet.adapters.valueAdapter.StringValueAdapter} only
 * by setting {@link pipe.petrinet.adapters.valueAdapter.StringAttributeValueAdaptor.AdaptedAttributeString}
 * to be an XmlAttribute
 * If there was a way to dynamically choose if it was an attribute/element
 * this would be better
 */
public class StringAttributeValueAdaptor extends XmlAdapter<StringAttributeValueAdaptor.AdaptedAttributeString, String> {
    @Override
    public String unmarshal(AdaptedAttributeString adaptedString) throws Exception {
        return adaptedString.value;
    }

    @Override
    public AdaptedAttributeString marshal(String s) throws Exception {
        AdaptedAttributeString adaptedString = new AdaptedAttributeString();
        adaptedString.value = s;
        return adaptedString;
    }

    public static class AdaptedAttributeString {
        @XmlAttribute
        public String value;
    }
}
