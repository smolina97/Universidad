package fefeditor.common.feflib.inject

import java.io.File
import java.nio.file.{Files, Paths}

import scala.collection.mutable.{Buffer, ListBuffer}

class InjectableBin(file: File)
{
  private var data: Buffer[Byte] = null
  private var ptrOne: Buffer[Byte] = null
  private var ptrTwo: Buffer[Byte] = null
  private var labels: Buffer[Byte] = null

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
  }

  def getData: Array[Byte] = data.toArray

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

  def inject(table: InjectableTable, obj: InjectableObject): Unit = {
    table.parseInfo(data.toArray)

    val newData = obj.raw.length
    val ptrTwoDiff = newData + obj.pointerOne.length * 4
    val labelDiff = ptrTwoDiff + obj.pointerTwo.length * 8
    val dataOffset = table.offset + table.count * obj.raw.length

    val labelBytes: ListBuffer[Byte] = ListBuffer()
    val labelOffsets: ListBuffer[Int] = ListBuffer()
    var ptrTwoLblMap: Map[String, Int] = Map()

    var newPtrOne = ptrOneList().toBuffer
    var newPtrTwo = ptrTwoList().toBuffer

    // Prepare raw data for injection.
    for(x <- obj.pointerOne.indices) {
      val ptr = labelStart + labels.length + labelBytes.size + labelDiff
      val str = obj.labels(x)
      if(!str.equals("NULL")) {
        labelOffsets.append(ptr)
        ptrTwoLblMap = ptrTwoLblMap.+(str -> (labels.length + labelBytes.size))
        labelBytes.appendAll(str.getBytes("shift-jis"))
        labelBytes.append(0x0)
      }
      else {
        labelOffsets.append(0)
      }
    }
    obj.setPointers(labelOffsets.toArray)
    obj.writeId(table.count)

    // Fix pointer one offsets.
    newPtrOne = List.tabulate(newPtrOne.length) (n => {
      var newPtr = newPtrOne(n)._1
      var newDataPtr = newPtrOne(n)._2
      if(newPtr > dataOffset) {
        newPtr += newData
      }
      if(newDataPtr > dataOffset && newDataPtr < labelStart) {
        newDataPtr += newData
      }
      else if(newDataPtr >= labelStart) {
        newDataPtr += labelDiff
      }
      (newPtr, newDataPtr)
    }).toBuffer

    // Append data.
    data.insertAll(dataOffset, obj.raw)

    // Append pointer one.
    for(ptrInt <- obj.pointerOne) {
      ptrOne.appendAll(ArrayConvert.toByteArray(ptrInt + dataOffset))
    }

    // Fix pointer two.
    newPtrTwo = List.tabulate(newPtrTwo.length) (n => {
      var newPtr = newPtrTwo(n)._1
      if(newPtr > dataOffset)
        newPtr += newData
      (newPtr, newPtrTwo(n)._2)
    }).toBuffer

    // Append pointer two.
    for(ptr <- obj.pointerTwo) {
      ptrTwo.appendAll(ArrayConvert.toByteArray(dataOffset))
      ptrTwo.appendAll(ArrayConvert.toByteArray(ptrTwoLblMap(ptr._2)))
    }

    // Append labels.
    labels.appendAll(labelBytes)

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


    // Correct table count.
    if(table.count != -1) {
      val bytes = ArrayConvert.toByteArray(table.count + 1)
      for(x <- 0 until table.countLength) {
        data(table.countOffset + x) = bytes(x)
      }
    }

    //format()
  }

  private def labelStart = data.length + ptrOne.length + ptrTwo.length

  private def fileSize = 0x20 + data.length + ptrOne.length + ptrTwo.length + labels.length

  private def label(offset: Int): String = {
    if(offset == 0)
      return ""
    val index = offset - labelStart
    var length = 0
    while (labels(index + length + 1) != 0) { length += 1 }
    new String(labels.slice(index, index + length + 1).toArray, "shift-jis")
  }

  private def format() = {
    val labelBytes: ListBuffer[Byte] = ListBuffer()
    val ptrOneBytes: ListBuffer[Byte] = ListBuffer()
    val ptrTwoBytes: ListBuffer[Byte] = ListBuffer()

    // Sort pointers and store them in mutable lists.
    var sortedPtrOne = sortPtrOne()
    var sortedPtrTwo = ptrTwoList.sortBy(_._1)

    // Sort labels; remove everything that's not used.
    var labelStrings: ListBuffer[String] = ListBuffer()
    var offsets: ListBuffer[Int] = ListBuffer()

    // First, add labels from pointer two.
    sortedPtrTwo = List.tabulate(sortedPtrTwo.length) (n => {
      val ptr = sortedPtrTwo(n)
      val str = label(ptr._2 + labelStart)
      val offset = labelBytes.length
      labelStrings.append(str)
      offsets.append(offset)
      labelBytes.appendAll(str.getBytes("shift-jis"))
      labelBytes.append(0)
      (ptr._1, offset)
    })

    // Next, add labels from pointer one.
    sortedPtrOne = List.tabulate(sortedPtrOne.length) (n => {
      if(sortedPtrOne(n)._2 >= labelStart) {
        var searchOffset = sortedPtrOne(n)._2 - labelStart
        if(!offsets.contains(searchOffset)) {
          val str = label(searchOffset + labelStart)
          val offset = labelBytes.length
          labelStrings.append(str)
          offsets.append(offset)
          labelBytes.appendAll(str.getBytes("shift-jis"))
          labelBytes.append(0)
          searchOffset = offset
        }
        (sortedPtrOne(n)._1, offsets(offsets.indexOf(searchOffset)) + labelStart)
      }
      sortedPtrOne(n)
    })

    // Rebuild pointer one and fix data region.
    for(ptr <- sortedPtrOne) {
      ptrOneBytes.appendAll(ArrayConvert.toByteArray(ptr._1))
      val dataPtrBytes = ArrayConvert.toByteArray(ptr._2)
      for(x <- 0 until 4)
        data(ptr._1 + x) = dataPtrBytes(x)
    }

    // Rebuild pointer two.
    for(ptr <- sortedPtrTwo) {
      ptrTwoBytes.appendAll(ArrayConvert.toByteArray(ptr._1))
      ptrTwoBytes.appendAll(ArrayConvert.toByteArray(ptr._2))
    }

    // Reassign modified regions.
    ptrOne = ptrOneBytes
    ptrTwo = ptrTwoBytes
    labels = labelBytes
  }

  private def sortPtrOne(): List[(Int, Int)] = {
    val sorted = ptrOneList.sortBy(_._1).toBuffer
    for(x <- 0 until sorted.length) {
      if(sorted(x)._2 > labelStart) {
        var index = x + 1
        for(y <- index until sorted.length) {
          if(sorted(y)._2 == sorted(x)._2) {
            val matching = sorted(y)
            sorted.remove(y)
            sorted.insert(index, matching)
            index += 1
          }
        }
      }
    }
    sorted.toList
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
