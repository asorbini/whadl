/* Generated by JTB 1.4.6 */
package org.mentalsmash.whadl.descriptions.parser.nodes;

import org.mentalsmash.whadl.descriptions.parser.visitor.*;

/**
 * JTB node class for the production EntityDescription:<br>
 * Corresponding grammar :<br>
 * f0 -> EntityId()<br>
 * f1 -> < TEXT ><br>
 */
public class EntityDescription implements INode {

  /** A child node */
  public EntityId f0;

  /** A child node */
  public NodeToken f1;

  /** The serial version uid */
  private static final long serialVersionUID = 146L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 first child node
   * @param n1 next child node
   */
  public EntityDescription(final EntityId n0, final NodeToken n1) {
    f0 = n0;
    f1 = n1;
  }

  /**
   * Constructs the node with only its non NodeToken child node(s).
   *
   * @param n0 first child node
   */
  public EntityDescription(final EntityId n0) {
    f0 = n0;
    f1 = new NodeToken("\n]]]>");
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
