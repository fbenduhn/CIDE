package tmp.generated_cs;

import cide.gast.*;
import cide.gparser.*;
import cide.greferences.*;
import java.util.*;

public class primary_expression_postfixInternal2 extends primary_expression_postfixInternal {
  public primary_expression_postfixInternal2(invocation_expression invocation_expression, Token firstToken, Token lastToken) {
    super(new Property[] {
      new PropertyOne<invocation_expression>("invocation_expression", invocation_expression)
    }, firstToken, lastToken);
  }
  public primary_expression_postfixInternal2(Property[] properties, IToken firstToken, IToken lastToken) {
    super(properties,firstToken,lastToken);
  }
  public IASTNode deepCopy() {
    return new primary_expression_postfixInternal2(cloneProperties(),firstToken,lastToken);
  }
  public invocation_expression getInvocation_expression() {
    return ((PropertyOne<invocation_expression>)getProperty("invocation_expression")).getValue();
  }
}
