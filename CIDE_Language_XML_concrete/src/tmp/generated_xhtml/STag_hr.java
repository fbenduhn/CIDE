package tmp.generated_xhtml;

import cide.gast.*;
import cide.gparser.*;
import cide.greferences.*;
import java.util.*;

public class STag_hr extends GenASTNode {
  public STag_hr(ArrayList<Attribute> attribute, Token firstToken, Token lastToken) {
    super(new Property[] {
      new PropertyZeroOrMore<Attribute>("attribute", attribute)
    }, firstToken, lastToken);
  }
  public STag_hr(Property[] properties, IToken firstToken, IToken lastToken) {
    super(properties,firstToken,lastToken);
  }
  public ASTNode deepCopy() {
    return new STag_hr(cloneProperties(),firstToken,lastToken);
  }
  public ArrayList<Attribute> getAttribute() {
    return ((PropertyZeroOrMore<Attribute>)getProperty("attribute")).getValue();
  }
}
