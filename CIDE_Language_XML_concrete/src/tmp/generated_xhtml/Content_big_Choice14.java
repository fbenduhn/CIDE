package tmp.generated_xhtml;

import cide.gast.*;
import cide.gparser.*;
import cide.greferences.*;
import java.util.*;

public class Content_big_Choice14 extends Content_big_Choice1 {
  public Content_big_Choice14(Element_span element_span, Token firstToken, Token lastToken) {
    super(new Property[] {
      new PropertyOne<Element_span>("element_span", element_span)
    }, firstToken, lastToken);
  }
  public Content_big_Choice14(Property[] properties, IToken firstToken, IToken lastToken) {
    super(properties,firstToken,lastToken);
  }
  public ASTNode deepCopy() {
    return new Content_big_Choice14(cloneProperties(),firstToken,lastToken);
  }
  public Element_span getElement_span() {
    return ((PropertyOne<Element_span>)getProperty("element_span")).getValue();
  }
}
