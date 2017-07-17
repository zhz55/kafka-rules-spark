package rules

/**
  * Created by Kasim on 2017/7/17.
  */
class Stopwatch {

  private val start = System.currentTimeMillis()

  override def toString() = (System.currentTimeMillis() - start) + " ms"

}
