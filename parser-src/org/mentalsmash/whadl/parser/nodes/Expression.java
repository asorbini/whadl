/* Generated by JTB 1.4.6 */
package org.mentalsmash.whadl.parser.nodes;

import org.mentalsmash.whadl.parser.visitor.*;

/**
 * JTB node class for the production Expression:<br>
 * Corresponding grammar :<br>
 * f0 -> . %0 ConditionalExpression()<br>
 * .. .. | %1 LabeledExpression()<br>
 */
public class Expression implements INode {

  /** A child node */
  public NodeChoice f0;

  /** The serial version uid */
  private static final long serialVersionUID = 146L;

  /**
   * Constructs the node with its child node.
   *
   * @param n0 the child node
   */
  public Expression(final NodeChoice n0) {
    f0 = n0;
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
