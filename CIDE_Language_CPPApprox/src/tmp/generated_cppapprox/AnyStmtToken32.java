package tmp.generated_cppapprox;

import cide.gast.*;
import cide.gparser.*;
import cide.greferences.*;
import java.util.*;

public class AnyStmtToken32 extends AnyStmtToken {
  public AnyStmtToken32(Token firstToken, Token lastToken) {
    super(new Property[] {
    }, firstToken, lastToken);
  }
  public AnyStmtToken32(Property[] properties, IToken firstToken, IToken lastToken) {
    super(properties,firstToken,lastToken);
  }
  public IASTNode deepCopy() {
    return new AnyStmtToken32(cloneProperties(),firstToken,lastToken);
  }
}
