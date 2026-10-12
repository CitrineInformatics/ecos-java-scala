package io.citrine.ecos

import breeze.linalg.CSCMatrix
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
  * Loads the native library and solves small problems with known answers.
  * It runs on every CI platform. A library that builds but cannot load, or
  * returns wrong numbers, fails that platform's build.
  */
class QpSolverSpec extends AnyFlatSpec with Matchers {

  /** Each row holds coefficients on four variables, then a lower and an upper bound. */
  private val boxAndSums: Seq[(Array[Double], Double, Double)] = Seq(
    (Array(1.0, 1.0, 0.0, 0.0), 0.7, 0.8),
    (Array(0.0, 0.0, 1.0, 0.0), 0.1, 0.4),
    (Array(1.0, 0.0, 0.0, 0.0), 0.0, 1.0),
    (Array(0.0, 1.0, 0.0, 0.0), 0.0, 1.0),
    (Array(0.0, 0.0, 1.0, 0.0), 0.0, 1.0),
    (Array(0.0, 0.0, 0.0, 1.0), 0.0, 1.0)
  )

  /** Each bounded row becomes two inequality rows: a x <= ub and -a x <= -lb. */
  private def inequalities(rows: Seq[(Array[Double], Double, Double)], n: Int): (CSCMatrix[Double], Array[Double]) = {
    val builder = new CSCMatrix.Builder[Double](2 * rows.size, n)
    val h = Array.fill(2 * rows.size)(0.0)
    rows.zipWithIndex.foreach { case ((coefficients, lower, upper), i) =>
      coefficients.zipWithIndex.foreach { case (value, j) =>
        if (value != 0.0) {
          builder.add(2 * i, j, value)
          builder.add(2 * i + 1, j, -value)
        }
      }
      h(2 * i) = upper
      h(2 * i + 1) = -lower
    }
    (builder.result, h)
  }

  /** Equalities: the variables sum to one, and the last variable is zero. */
  private def equalities(n: Int): CSCMatrix[Double] = {
    val builder = new CSCMatrix.Builder[Double](2, n)
    (0 until n).foreach(j => builder.add(0, j, 1.0))
    builder.add(1, n - 1, 1.0)
    builder.result
  }

  "NativeECOS" should "report the ECOS version" in {
    new NativeECOS().EcosVer() should not be empty
  }

  "QpSolver" should "project the origin onto the constraint set" in {
    val n = 4
    val (g, h) = inequalities(boxAndSums, n)
    val solver = new QpSolver(nHessian = n, diagonal = true, Equalities = Some(equalities(n)), Inequalities = Some(g))
    solver.updateEquality(Array(1.0, 0.0))
    solver.updateInequality(g, h)

    val (status, x) = solver.solve(Array.fill(n)(1.0), Array.fill(n)(0.0))

    status shouldBe Optimal
    val expected = Array(0.35, 0.35, 0.3, 0.0)
    x.zip(expected).foreach { case (actual, want) => actual shouldBe want +- 1e-4 }
  }

  it should "report an infeasible problem" in {
    val n = 2
    // x0 must be 2, but the box keeps x0 within [0, 1].
    val rows = Seq(
      (Array(1.0, 0.0), 2.0, 2.0),
      (Array(1.0, 0.0), 0.0, 1.0),
      (Array(0.0, 1.0), 0.0, 1.0)
    )
    val (g, h) = inequalities(rows, n)
    val solver = new QpSolver(nHessian = n, diagonal = true, Inequalities = Some(g))
    solver.updateInequality(g, h)

    val (status, _) = solver.solve(Array.fill(n)(1.0), Array.fill(n)(0.0))

    status should (be(PrimalInfeasible) or be(PrimalInfeasibleInaccurate))
  }
}
