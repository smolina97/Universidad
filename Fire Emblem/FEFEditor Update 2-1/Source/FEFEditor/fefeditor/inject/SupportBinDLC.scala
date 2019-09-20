package fefeditor.inject

import java.io.File
import java.nio.file.{Files, Paths}

import fefeditor.bin.blocks.{CharSupport, SupportCharacter}
import fefeditor.common.feflib.inject.ArrayConvert
import fefeditor.common.io.ByteUtils

import scala.collection.mutable.{Buffer, ListBuffer}

class SupportBinDLC(file: File)
{
  private var data: Buffer[Byte] = _
  private var ptrOne: Buffer[Byte] = _
  private var ptrTwo: Buffer[Byte] = _
  private var labels: Buffer[Byte] = _

  private var tableStart = 0
  private var _offsets: Buffer[Int] = _

  processHeader()

  private def processHeader() = {
    val raw = Files.readAllBytes(Paths.get(file.getAbsolutePath))

    val dataSize = ArrayConvert.toInteger(raw.slice(0x4, 0x8))
    val ptrOneCount = ArrayConvert.toInteger(raw.slice(0x8, 0xC))
    val ptrTwoCount = ArrayConvert.toInteger(raw.slice(0xC, 0x10))

    val ptrOneStart = dataSize + 0x20
    val ptrTwoStart = ptrOneStart + ptrOneCount * 4
    val labelStart = ptrTwoStart + ptrTwoCount * 8

    data = raw.slice(0x20, dataSize + 0x20).toBuffer
    ptrOne = raw.slice(ptrOneStart, ptrTwoStart).toBuffer
    ptrTwo = raw.slice(ptrTwoStart, labelStart).toBuffer
    labels = raw.slice(labelStart, raw.length).toBuffer

    val temp = data.toArray
    tableStart = ArrayConvert.toInteger(temp.slice(0x8, 0xC))
    _offsets = offsets
  }

  def offsets: Buffer[Int] = {
    val temp = data.toArray
    val offsets = Buffer.empty[Int]
    for(x <- 0 until ArrayConvert.toInteger(temp.slice(tableStart, tableStart + 0x4))) {
      offsets.append(ArrayConvert.toInteger(temp.slice(tableStart + 4 + 4 * x, tableStart + 8 + 4 * x)))
    }
    offsets
  }

  def containsCharacter(character: SupportCharacter): Boolean = {
    val temp = data.toArray
    var result = false
    for(i <- offsets) {
      val id = ArrayConvert.toInteger(temp.slice(i, i + 2))
      if(id == character.getSupportId) {
        result = true
      }
    }
    result
  }

  def getTableOffset(character: SupportCharacter): Int = {
    val temp = data.toArray
    var result = -1
    for(i <- offsets) {
      val id = ArrayConvert.toInteger(temp.slice(i, i + 2))
      if(id == character.getSupportId) {
        result = i
      }
    }
    result
  }

  def supportTableLength: Int = {
    val temp = data.toArray
    val tableSize = ArrayConvert.toInteger(temp.slice(tableStart, tableStart + 0x4))
    tableSize
  }

  def tableInsertOffset: Int = {
    val temp = data.toArray
    val tableSize = ArrayConvert.toInteger(temp.slice(tableStart, tableStart + 0x4))
    tableStart + tableSize * 4 + 4
  }

  def changeSupportType(character: SupportCharacter, index: Int, supportType: Int): Unit = {
    val temp = data.toArray
    val tableOffset = getTableOffset(character)
    val typeOffset = tableOffset + 0xC * index + 8
    var bytes: Array[Byte] = null
    if(supportType == 0)
      bytes = CharSupport.ROMANTIC
    else if(supportType == 1)
      bytes = CharSupport.FAST_ROMANTIC
    else if(supportType == 2)
      bytes = CharSupport.PLATONIC
    else
      bytes = CharSupport.FAST_PLATONIC
    for(x <- 0 until 4)
      data(typeOffset + x) = bytes(x)
  }

  def getSupports(character: SupportCharacter): Array[CharSupport] = {
    val temp = data.toArray
    val tableOffset = getTableOffset(character)
    val tableSize = ByteUtils.getUInt16(temp, tableOffset + 2)
    val supports = Array.tabulate[CharSupport](tableSize)(n => {
      val charSupport = new CharSupport
      charSupport.setCharId(ByteUtils.getUInt16(temp, tableOffset + 4 + 0xC * n))
      charSupport.setType(temp.slice(tableOffset + 8 + 0xC * n, tableOffset + 0xC * (n + 1)))
      charSupport.setMuseumBytes(temp.slice(tableOffset + 0xC * (n + 1), tableOffset + 0x10 + 0xC * n))
      charSupport
    })
    supports
  }

  def getData: Array[Byte] = data.toArray

  private def labelStart = data.length + ptrOne.length + ptrTwo.length

  private def fileSize = 0x20 + data.length + ptrOne.length + ptrTwo.length + labels.length

  def replaceLabels(name: String): Unit = {
    val bytes = name.getBytes("shift-jis")
    labels.clear()
    labels.appendAll(bytes)
    labels.append(0)
  }

  def toBin: Array[Byte] = {
    val out: ListBuffer[Byte] = ListBuffer()
    out.appendAll(ArrayConvert.toByteArray(fileSize))
    out.appendAll(ArrayConvert.toByteArray(data.length))
    out.appendAll(ArrayConvert.toByteArray(ptrOne.length / 4))
    out.appendAll(ArrayConvert.toByteArray(ptrTwo.length / 8))
    out.appendAll(List.fill(0x10)(0x0))
    out.appendAll(data)
    out.appendAll(ptrOne)
    out.appendAll(ptrTwo)
    out.appendAll(labels)
    out.toArray
  }

  def addSupport(character: SupportCharacter, bytes: Array[Byte]): Unit = {
    val temp = data.toArray
    val tableOffset = getTableOffset(character)
    val tableSize = ByteUtils.getUInt16(temp, tableOffset + 2)
    val insertOffset = tableOffset + 0x4 + tableSize * 0xC

    val sizeBytes = ArrayConvert.toByteArray(tableSize + 1)
    for(x <- 0 until 2)
      data(tableOffset + 2 + x) = sizeBytes(x)

    // Fix pointers.
    var newPtrOne = ptrOneList().toBuffer
    var newPtrTwo = ptrTwoList().toBuffer
    newPtrOne = List.tabulate(newPtrOne.length) (n => {
      var newPtr = newPtrOne(n)._1
      var newDataPtr = newPtrOne(n)._2
      if(newPtr >= insertOffset) {
        newPtr += bytes.length
      }
      if(newDataPtr >= insertOffset) {
        newDataPtr += bytes.length
      }
      (newPtr, newDataPtr)
    }).toBuffer
    newPtrTwo = List.tabulate(newPtrTwo.length) (n => {
      var newPtr = newPtrTwo(n)._1
      if(newPtr > insertOffset)
        newPtr += bytes.length
      (newPtr, newPtrTwo(n)._2)
    }).toBuffer

    // Append data.
    data.insertAll(insertOffset, bytes)

    // Fix data and pointer one using recalculated pointers.
    for(x <- newPtrOne.indices) {
      val ptrBytes = ArrayConvert.toByteArray(newPtrOne(x)._1)
      val dataBytes = ArrayConvert.toByteArray(newPtrOne(x)._2)
      for(y <- 0 until 4) {
        ptrOne(x * 4 + y) = ptrBytes(y)
        data(newPtrOne(x)._1 + y) = dataBytes(y)
      }
    }

    // Fix pointer two using recalculated pointers.
    for(x <- newPtrTwo.indices) {
      val ptrBytes = ArrayConvert.toByteArray(newPtrTwo(x)._1)
      for(y <- 0 until 4) {
        ptrTwo(x * 8 + y) = ptrBytes(y)
      }
    }
  }

  def addSupportTable(bytes: Array[Byte]): Unit = {
    val temp = data.toArray
    val tableOffset = offsets.last
    val tableSize = ByteUtils.getUInt16(temp, tableOffset + 2)
    val insertOffset = tableOffset + 0x4 + tableSize * 0xC

    var newPtrOne = ptrOneList().toBuffer
    var newPtrTwo = ptrTwoList().toBuffer

    // Fix pointer one offsets.
    newPtrOne = List.tabulate(newPtrOne.length) (n => {
      var newPtr = newPtrOne(n)._1
      var newDataPtr = newPtrOne(n)._2
      if(newPtr >= insertOffset) {
        newPtr += bytes.length
      }
      if(newDataPtr >= insertOffset && newDataPtr < labelStart) {
        newDataPtr += bytes.length
      }
      else if(newDataPtr >= labelStart) {
        newDataPtr += bytes.length
      }
      (newPtr, newDataPtr)
    }).toBuffer

    // Append data.
    data.insertAll(insertOffset, bytes)

    // Fix pointer two.
    newPtrTwo = List.tabulate(newPtrTwo.length) (n => {
      var newPtr = newPtrTwo(n)._1
      if(newPtr > insertOffset)
        newPtr += bytes.length
      (newPtr, newPtrTwo(n)._2)
    }).toBuffer

    // Fix data and pointer one using recalculated pointers.
    for(x <- newPtrOne.indices) {
      val ptrBytes = ArrayConvert.toByteArray(newPtrOne(x)._1)
      val dataBytes = ArrayConvert.toByteArray(newPtrOne(x)._2)
      for(y <- 0 until 4) {
        ptrOne(x * 4 + y) = ptrBytes(y)
        data(newPtrOne(x)._1 + y) = dataBytes(y)
      }
    }

    // Fix pointer two using recalculated pointers.
    for(x <- newPtrTwo.indices) {
      val ptrBytes = ArrayConvert.toByteArray(newPtrTwo(x)._1)
      for(y <- 0 until 4) {
        ptrTwo(x * 8 + y) = ptrBytes(y)
      }
    }

    // Add character to main table.
    fixSupportTable(insertOffset + 4)
  }

  private def fixSupportTable(tableOffset: Int): Unit = {
    val temp = data.toArray
    val insertOffset = tableInsertOffset

    var newPtrOne = ptrOneList().toBuffer
    var newPtrTwo = ptrTwoList().toBuffer

    val bytes = ArrayConvert.toByteArray(tableOffset)

    // Fix pointer one offsets.
    newPtrOne = List.tabulate(newPtrOne.length) (n => {
      var newPtr = newPtrOne(n)._1
      var newDataPtr = newPtrOne(n)._2
      if(newPtr >= insertOffset) {
        newPtr += bytes.length
      }
      if(newDataPtr >= insertOffset && newDataPtr < labelStart) {
        newDataPtr += bytes.length
      }
      else if(newDataPtr >= labelStart) {
        newDataPtr += bytes.length + 4
      }
      (newPtr, newDataPtr)
    }).toBuffer

    // Append data.
    data.insertAll(insertOffset, bytes)

    // Append pointer one.
    ptrOne.appendAll(ArrayConvert.toByteArray(insertOffset))

    // Fix pointer two.
    newPtrTwo = List.tabulate(newPtrTwo.length) (n => {
      var newPtr = newPtrTwo(n)._1
      if(newPtr > insertOffset)
        newPtr += bytes.length
      (newPtr, newPtrTwo(n)._2)
    }).toBuffer

    // Fix data and pointer one using recalculated pointers.
    for(x <- newPtrOne.indices) {
      val ptrBytes = ArrayConvert.toByteArray(newPtrOne(x)._1)
      val dataBytes = ArrayConvert.toByteArray(newPtrOne(x)._2)
      for(y <- 0 until 4) {
        ptrOne(x * 4 + y) = ptrBytes(y)
        data(newPtrOne(x)._1 + y) = dataBytes(y)
      }
    }

    // Fix pointer two using recalculated pointers.
    for(x <- newPtrTwo.indices) {
      val ptrBytes = ArrayConvert.toByteArray(newPtrTwo(x)._1)
      for(y <- 0 until 4) {
        ptrTwo(x * 8 + y) = ptrBytes(y)
      }
    }

    val tableSize = ArrayConvert.toInteger(data.toArray.slice(tableStart, tableStart + 4))
    val sizeBytes = ArrayConvert.toByteArray(tableSize + 1)
    for(x <- sizeBytes.indices) {
      data(tableStart + x) = sizeBytes(x)
    }
  }

  private def label(offset: Int): String = {
    if(offset == 0)
      return ""
    val index = offset - labelStart
    var length = 0
    while (labels(index + length + 1) != 0) { length += 1 }
    new String(labels.slice(index, index + length + 1).toArray, "shift-jis")
  }

  private def ptrOneList(): List[(Int, Int)] = {
    val ptrList = List.tabulate(ptrOne.length / 4)(n => {
      val ptr = ArrayConvert.toInteger(ptrOne.slice(n * 4, n * 4 + 4).toArray)
      val dataPtr = ArrayConvert.toInteger(data.slice(ptr, ptr + 4).toArray)
      (ptr, dataPtr)
    })
    ptrList
  }

  private def ptrTwoList(): List[(Int, Int)] = {
    val ptrList = List.tabulate(ptrTwo.length / 8) (n => {
      val ptr = ArrayConvert.toInteger(ptrTwo.slice(n * 8, n * 8 + 4).toArray)
      val str = ArrayConvert.toInteger(ptrTwo.slice(n * 8 + 4, n * 8 + 8).toArray)
      (ptr, str)
    })
    ptrList
  }
}
