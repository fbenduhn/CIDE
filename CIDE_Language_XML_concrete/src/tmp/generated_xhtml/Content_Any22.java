package tmp.generated_xhtml;

import cide.gast.*;
import cide.gparser.*;
import cide.greferences.*;
import java.util.*;

public class Content_Any22 extends Content_Any {
  public Content_Any22(Element_map element_map, Token firstToken, Token lastToken) {
    super(new Property[] {
      new PropertyOne<Element_map>("element_map", element_map)
    }, firstToken, lastToken);
  }
  public Content_Any22(Property[] properties, IToken firstToken, IToken lastToken) {
    super(properties,firstToken,lastToken);
  }
  public ASTNode deepCopy() {
    return new Content_Any22(cloneProperties(),firstToken,lastToken);
  }
  public Element_map getElement_map() {
    return ((PropertyOne<Element_map>)getProperty("element_map")).getValue();
  }
}
