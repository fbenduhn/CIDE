package tmp.generated_people;

import cide.gast.*;
import cide.gparser.*;
import cide.greferences.*;
import java.util.*;

public class STag_var extends GenASTNode {
  public STag_var(ArrayList<Attribute> attribute, Token firstToken, Token lastToken) {
    super(new Property[] {
      new PropertyZeroOrMore<Attribute>("attribute", attribute)
    }, firstToken, lastToken);
  }
  public STag_var(Property[] properties, IToken firstToken, IToken lastToken) {
    super(properties,firstToken,lastToken);
  }
  public ASTNode deepCopy() {
    return new STag_var(cloneProperties(),firstToken,lastToken);
  }
  public ArrayList<Attribute> getAttribute() {
    return ((PropertyZeroOrMore<Attribute>)getProperty("attribute")).getValue();
  }
}
