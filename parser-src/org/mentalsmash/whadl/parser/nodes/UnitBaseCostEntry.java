/* Generated by JTB 1.4.6 */
package org.mentalsmash.whadl.parser.nodes;

import org.mentalsmash.whadl.parser.visitor.*;

/**
 * JTB node class for the production UnitBaseCostEntry:<br>
 * Corresponding grammar :<br>
 * f0 -> < COST ><br>
 * f1 -> Expression()<br>
 * f2 -> < SEMICOLON ><br>
 */
public class UnitBaseCostEntry implements INode {

  /** A child node */
  public NodeToken f0;

  /** A child node */
  public Expression f1;

  /** A child node */
  public NodeToken f2;

  /** The serial version uid */
  private static final long serialVersionUID = 146L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 first child node
   * @param n1 next child node
   * @param n2 next child node
   */
  public UnitBaseCostEntry(final NodeToken n0, final Expression n1, final NodeToken n2) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
  }

  /**
   * Constructs the node with only its non NodeToken child node(s).
   *
   * @param n0 first child node
   */
  public UnitBaseCostEntry(final Expression n0) {
    f0 = new NodeToken("cost");
    f1 = n0;
    f2 = new NodeToken(";");
  }

  /**
   * Accepts the IRetArguVisitor visitor.
   *
   * @param <R> the user return type
   * @param <A> the user argument type
   * @param vis the visitor
   * @param argu a user chosen argument
   * @return a user chosen return information
   */
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  /**
   * Accepts the IRetVisitor visitor.
   *
   * @param <R> the user return type
   * @param vis the visitor
   * @return a user chosen return information
   */
  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  /**
   * Accepts the IVoidArguVisitor visitor.
   *
   * @param <A> the user argument type
   * @param vis the visitor
   * @param argu a user chosen argument
   */
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

  /**
   * Accepts the IVoidVisitor visitor.
   *
   * @param vis the visitor
   */
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

}
