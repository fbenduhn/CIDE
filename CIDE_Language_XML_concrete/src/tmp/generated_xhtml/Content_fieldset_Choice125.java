package tmp.generated_xhtml;

import cide.gast.*;
import cide.gparser.*;
import cide.greferences.*;
import java.util.*;

public class Content_fieldset_Choice125 extends Content_fieldset_Choice1 {
  public Content_fieldset_Choice125(Element_map element_map, Token firstToken, Token lastToken) {
    super(new Property[] {
      new PropertyOne<Element_map>("element_map", element_map)
    }, firstToken, lastToken);
  }
  public Content_fieldset_Choice125(Property[] properties, IToken firstToken, IToken lastToken) {
    super(properties,firstToken,lastToken);
  }
  public ASTNode deepCopy() {
    return new Content_fieldset_Choice125(cloneProperties(),firstToken,lastToken);
  }
  public Element_map getElement_map() {
    return ((PropertyOne<Element_map>)getProperty("element_map")).getValue();
  }
}
