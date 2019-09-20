package fefeditor.common.feflib.inject

import scala.collection.mutable.Buffer

abstract class InjectableObject() {
  protected var _pointerOne: Array[Int] = null
  protected var _pointerTwo: Array[(Int, String)] = null
  protected var _labels: Array[String] = null
  protected var _raw: Buffer[Byte] = null
  protected var id = 0

  def writeId(count: Int): Unit
  def pointerOne: Array[Int] = _pointerOne
  def pointerTwo: Array[(Int, String)] = _pointerTwo
  def labels: Array[String] = _labels
  def setLabels(arr: Array[String]): Unit = { _labels = arr }
  def raw: Buffer[Byte] = _raw

  def setPointers(offsets: Array[Int]): Unit = {
    for(x <- offsets.indices) {
      val bytes = ArrayConvert.toByteArray(offsets(x))
      for(y <- 0 until 4) {
        _raw(_pointerOne(x) + y) = bytes(y)
      }
    }
  }

  def setPointerTwo(ints: Array[Int], str: Array[String]): Unit = {
    val arr = Array.tabulate[(Int, String)](ints.length)(n => {(ints(n), str(n))})
    _pointerTwo = arr;
  }
}
