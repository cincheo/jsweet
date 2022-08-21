
package source.svregression;

import static def.dom.Globals.*;

public class OverloadConstructorParamScope {

	private String aaa;
	
  public OverloadConstructorParamScope( int xxx ) {
    // xxx here is clearly bound to the parameter definition, or should be,
    //   but JSweet claims we are accessing a variable before its definition.
    // Change the parameter name to yyy and you'll see why in the transpiled code.
    this( xxx + "" );
  }
  
  protected OverloadConstructorParamScope( String bbb ) {
    this.aaa = bbb;

    int xxx = 99; // This is the local variable definition that collides incorrectly with the parameter in the other constructor
    System.out.println( xxx );
  }
}
