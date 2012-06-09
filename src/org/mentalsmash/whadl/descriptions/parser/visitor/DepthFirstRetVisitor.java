/* Generated by JTB 1.4.6 */
package org.mentalsmash.whadl.descriptions.parser.visitor;

import org.mentalsmash.whadl.descriptions.parser.nodes.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first order.<br>
 * In your "Ret" visitors extend this class and override part or all of these methods.
 *
 * @param <R> The user return information type
 */
public class DepthFirstRetVisitor<R> implements IRetVisitor<R> {


  /*
   * Base nodes classes visit methods (to be overridden if necessary)
   */

  /**
   * Visits a {@link NodeChoice} node.
   *
   * @param n the node to visit
   * @return the user return information
   */
  public R visit(final NodeChoice n) {
    final R nRes = n.choice.accept(this);
    return nRes;
  }

  /**
   * Visits a {@link NodeList} node.
   *
   * @param n the node to visit
   * @return the user return information
   */
  public R visit(final NodeList n) {
    R nRes = null;
    for (final Iterator<INode> e = n.elements(); e.hasNext();) {
      @SuppressWarnings("unused")
      final R sRes = e.next().accept(this);
    }
    return nRes;
  }

  /**
   * Visits a {@link NodeListOptional} node.
   *
   * @param n the node to visit
   * @return the user return information
   */
  public R visit(final NodeListOptional n) {
    if (n.present()) {
      R nRes = null;
      for (final Iterator<INode> e = n.elements(); e.hasNext();) {
        @SuppressWarnings("unused")
        R sRes = e.next().accept(this);
        }
      return nRes;
    } else
      return null;
  }

  /**
   * Visits a {@link NodeOptional} node.
   *
   * @param n the node to visit
   * @return the user return information
   */
  public R visit(final NodeOptional n) {
    if (n.present()) {
      final R nRes = n.node.accept(this);
      return nRes;
    } else
    return null;
  }

  /**
   * Visits a {@link NodeSequence} node.
   *
   * @param n the node to visit
   * @return the user return information
   */
  public R visit(final NodeSequence n) {
    R nRes = null;
    for (final Iterator<INode> e = n.elements(); e.hasNext();) {
      @SuppressWarnings("unused")
      R subRet = e.next().accept(this);
    }
    return nRes;
  }

  /**
   * Visits a {@link NodeToken} node.
   *
   * @param n the node to visit
   * @return the user return information
   */
  public R visit(final NodeToken n) {
    R nRes = null;
    @SuppressWarnings("unused")
    final String tkIm = n.tokenImage;
    return nRes;
  }

  /*
   * User grammar generated visit methods (to be overridden if necessary)
   */

  /**
   * Visits a {@link Descriptions} node, whose children are the following :
   * <p>
   * f0 -> EntityDescription()<br>
   * f1 -> ( EntityDescription() )*<br>
   *
   * @param n the node to visit
   * @return the user return information
   */
  public R visit(final Descriptions n) {
    R nRes = null;
    // f0 -> EntityDescription()
    n.f0.accept(this);
    // f1 -> ( EntityDescription() )*
    n.f1.accept(this);
    return nRes;
  }

  /**
   * Visits a {@link EntityDescription} node, whose children are the following :
   * <p>
   * f0 -> EntityId()<br>
   * f1 -> < TEXT ><br>
   *
   * @param n the node to visit
   * @return the user return information
   */
  public R visit(final EntityDescription n) {
    R nRes = null;
    // f0 -> EntityId()
    n.f0.accept(this);
    // f1 -> < TEXT >
    n.f1.accept(this);
    return nRes;
  }

  /**
   * Visits a {@link EntityId} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> ( #0 ( %0 "."<br>
   * .. .. . .. | %1 "#" ) #1 < IDENTIFIER > )*<br>
   *
   * @param n the node to visit
   * @return the user return information
   */
  public R visit(final EntityId n) {
    R nRes = null;
    // f0 -> < IDENTIFIER >
    n.f0.accept(this);
    // f1 -> ( #0 ( %0 "."
    // .. .. . .. | %1 "#" ) #1 < IDENTIFIER > )*
    n.f1.accept(this);
    return nRes;
  }

}
