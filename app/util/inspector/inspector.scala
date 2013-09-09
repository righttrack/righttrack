package util

import scala.reflect.runtime.universe._

package object inspector {

  object SymbolFlavor extends Enumeration {
    type Value = Texture

    case class Texture(shortDesc: String) extends Val(shortDesc)

    val IsAbstractOverride = Texture("abstract override")
    val IsClass = Texture("class")
    val IsErroneous = Texture("erroneous")
    val IsFinal = Texture("final key")
    val IsFreeTerm = Texture("free term")
    val IsFreeType = Texture("free type")
    val IsImplementationArtifact = Texture("implementation artifact")
    val IsImplicit = Texture("implicit")
    val IsJava = Texture("java")
    val IsLocal = Texture("local")
    val IsMacro = Texture("macro")
    val IsMethod = Texture("method")
    val IsModule = Texture("module")
    val IsModuleClass = Texture("module class")
    val IsOverride = Texture("override")
    val IsPackage = Texture("package")
    val IsParameter = Texture("parameter")
    val IsPrivate = Texture("private")
    val IsProtected = Texture("protected")
    val IsPublic = Texture("public")
    val IsSpecialized = Texture("specialized")
    val IsStatic = Texture("static")
    val IsSynthentic = Texture("synthetic")
    val IsTerm = Texture("term")
    val IsType = Texture("type")

    def getTypeFlavor(it: Symbol) = {
      Map(
        IsAbstractOverride -> it.isAbstractOverride,
        IsClass -> it.isClass,
        IsErroneous -> it.isErroneous,
        IsFinal -> it.isFinal,
        IsFreeTerm -> it.isFreeTerm,
        IsFreeType -> it.isFreeType,
        IsImplementationArtifact -> it.isImplementationArtifact,
        IsImplicit -> it.isImplicit,
        IsJava -> it.isJava,
        IsLocal -> it.isLocal,
        IsMacro -> it.isMacro,
        IsMethod -> it.isMethod,
        IsModule -> it.isModule,
        IsModuleClass -> it.isModuleClass,
        IsOverride -> it.isOverride,
        IsPackage -> it.isPackage,
        IsParameter -> it.isParameter,
        IsPrivate -> it.isPrivate,
        IsProtected -> it.isProtected,
        IsPublic -> it.isPublic,
        IsSpecialized -> it.isSpecialized,
        IsStatic -> it.isStatic,
        IsSynthentic -> it.isSynthetic,
        IsTerm -> it.isTerm,
        IsType -> it.isType
      )
    }
    
    def alphabetical: Seq[Texture] = {
      Seq(
        IsAbstractOverride,
        IsClass,
        IsErroneous,
        IsFinal,
        IsFreeTerm,
        IsFreeType,
        IsImplementationArtifact,
        IsImplicit,
        IsJava,
        IsLocal,
        IsMacro,
        IsMethod,
        IsModule,
        IsModuleClass,
        IsOverride,
        IsPackage,
        IsParameter,
        IsPrivate,
        IsProtected,
        IsPublic,
        IsSpecialized,
        IsStatic,
        IsSynthentic,
        IsTerm,
        IsType
      )
    }

    def classy: Seq[Texture] = {
      Seq(
        IsClass,
        IsFinal,
        IsImplementationArtifact,
        IsJava,
        IsLocal,
        IsModule,
        IsModuleClass,
        IsStatic,
        IsSynthentic
      )
    }
  }

  object Tabulator {
    def format(table: Seq[Seq[Any]]) = table match {
      case Seq() => ""
      case _ =>
        val sizes = for (row <- table) yield for (cell <- row) yield if (cell == null) 0 else cell.toString.length
        val colSizes = for (col <- sizes.transpose) yield col.max
        val rows = for (row <- table) yield formatRow(row, colSizes)
        formatRows(rowSeparator(colSizes), rows)
    }

    def formatRows(rowSeparator: String, rows: Seq[String]): String = (
      rowSeparator ::
        rows.head ::
        rowSeparator ::
        rows.tail.toList :::
        rowSeparator ::
        List()).mkString("\n")

    def formatRow(row: Seq[Any], colSizes: Seq[Int]) = {
      val cells = for ((item, size) <- row.zip(colSizes)) yield if (size == 0) "" else ("%" + size + "s").format(item)
      cells.mkString("|", "|", "|")
    }

    def rowSeparator(colSizes: Seq[Int]) = colSizes map { "-" * _ } mkString("+", "+", "+")
  }


  case class SymbolFlavorTable(symbols: Seq[Symbol]) {
    import util.inspector.SymbolFlavor._

    private def header(projection: Set[Texture] = Set.empty): Seq[Texture] = {
      if (projection.isEmpty) alphabetical
      else alphabetical filter (projection contains)
    }

    private def body(projection: Set[Texture] = Set.empty): Map[Symbol, Seq[String]] = Map(symbols map {
      symbol => {
        val flavor = getTypeFlavor(symbol)
        val row: Seq[String] = header(projection) map { texture => flavor(texture).toString }
        symbol -> row
      }
    }: _*)

    private def table(projection: Set[Texture] = Set.empty): Seq[Seq[String]] = {
      ("Symbol" +: header(projection).map(_.shortDesc)) +: body(projection).toSeq.map {
        case (symbol, properties) => symbol.name.decoded +: properties
      }
    }

    def display(projection: Set[Texture] = Set.empty): String = {
      Tabulator.format(table(projection))
    }

    def print(projection: Set[Texture] = Set.empty, output: String => Unit = println): Unit = {
      output(this.display(projection))
    }
  }

}
