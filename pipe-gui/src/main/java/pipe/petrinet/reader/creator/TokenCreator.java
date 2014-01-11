package pipe.petrinet.reader.creator;

import org.w3c.dom.Element;
import pipe.models.component.Token;

import java.awt.*;

public class TokenCreator implements ComponentCreator<Token>{
    public Token create(Element element) {
        String id = element.getAttribute("id");
        boolean enabled = CreatorUtils.falseOrValueOf(element.getAttribute("enabled"));
        int red = CreatorUtils.zeroOrValueOfInt(element.getAttribute("red"));
        int green = CreatorUtils.zeroOrValueOfInt(element.getAttribute("green"));
        int blue = CreatorUtils.zeroOrValueOfInt(element.getAttribute("blue"));
        Color color = new Color(red, green, blue);
        return new Token(id, enabled, 0, color);
    }
}
