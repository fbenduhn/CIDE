package tmp.generated_people;

import cide.gast.*;
import cide.gparser.*;
import cide.greferences.*;
import java.util.*;

public class Content_Any73 extends Content_Any {
  public Content_Any73(Element_var element_var, Token firstToken, Token lastToken) {
    super(new Property[] {
      new PropertyOne<Element_var>("element_var", element_var)
    }, firstToken, lastToken);
  }
  public Content_Any73(Property[] properties, IToken firstToken, IToken lastToken) {
    super(properties,firstToken,lastToken);
  }
  public ASTNode deepCopy() {
    return new Content_Any73(cloneProperties(),firstToken,lastToken);
  }
  public Element_var getElement_var() {
    return ((PropertyOne<Element_var>)getProperty("element_var")).getValue();
  }
}
