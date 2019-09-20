package fefeditor.common.feflib.inject

abstract class InjectableTable() {
  protected var _offset: Int = -1
  protected var _count: Int = -1
  protected var _countOffset: Int = -1
  protected var _countLength: Int = -1

  def parseInfo(data: Array[Byte]): Unit
  def offset: Int = _offset
  def count: Int = _count
  def countOffset: Int = _countOffset
  def countLength: Int = _countLength
}
