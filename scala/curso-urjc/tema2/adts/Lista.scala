
sealed trait Lista[+A]

case class Cons[A](cabeza: A, cola: Lista[A]) extends Lista[A]

case object Nada extends Lista[Nothing]

object Lista {

  def map[A, B](xs: Lista[A])(f: A => B): Lista[B] = xs match {
    case Nada => Nada
    case Cons(x, xs) => Cons(f(x), map(xs)(f))
  }

  def sumList(xs: Lista[Int]): Int = xs match {
    case Nada => 0
    case Cons(x, xs) => x + sumList(xs)
  }

  def mulList(xs: Lista[Int]): Int = xs match {
    case Nada => 1
    case Cons(x, xs) => x + mulList(xs)
  }

  def catList(xs: Lista[String]): String = xs match {
    case Nada => ""
    case Cons(x, xs) => x + catList(xs)
  }

  def fold[A](xs: Lista[A])(z: A)(op: (A, A) => A): A = xs match {
    case Nada => z
    case Cons(x, xs) => op(x, fold(xs)(z)(op))
  }

  def foldLeft[A, B](xs: Lista[A])(z: B)(op: (B, A) => B): B = xs match {
    case Nada => z
    case Cons(x, xs) => foldLeft(xs)(op(z, x))(op)
  }

  def foldRight[A, B](xs: Lista[A])(z: B)(op: (A, B) => B): B = xs match {
    case Nada => z
    case Cons(x, xs) => op(x, foldRight(xs)(z)(op))
  }
}
