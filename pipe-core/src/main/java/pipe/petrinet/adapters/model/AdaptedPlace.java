package pipe.petrinet.adapters.model;

import pipe.models.component.Token;
import pipe.petrinet.adapters.valueAdapter.IntValueAdapter;
import pipe.petrinet.adapters.modelAdapter.TokenSetIntegerAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class AdaptedPlace extends AdaptedConnectable {

    @XmlJavaTypeAdapter(IntValueAdapter.class)
    private Integer capacity;

    @XmlElement(name = "initialMarking")
    @XmlJavaTypeAdapter(TokenSetIntegerAdapter.class)
    private Map<Token, Integer> tokenCounts = new HashMap<Token, Integer>();

    public Map<Token, Integer> getTokenCounts() {
        return tokenCounts;
    }

    public void setTokenCounts(Map<Token, Integer> tokenCounts) {
        this.tokenCounts = tokenCounts;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }


}
