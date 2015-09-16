package com.hablapps.cirbe.dominio

sealed trait Registro {
  def id: String
  def errores: List[TError]
}

case class DB010(
  relacion: Relacion,
  errores: List[TError] = List())
    extends Registro {
  val id: Id[DB010] =
    s"DB010_${relacion.codigoOperacion}_${relacion.codigoTitular}"
}

case class DB020(
  operacion: Operacion,
  errores: List[TError] = List())
    extends Registro {
  val id: Id[DB020] = s"DB020_${operacion.codigo}"
}