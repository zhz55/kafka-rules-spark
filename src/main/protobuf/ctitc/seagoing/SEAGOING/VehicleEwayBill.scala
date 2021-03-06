// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO2

package ctitc.seagoing.SEAGOING



/** 电子运单信息
  *
  * @param vehicleNo
  *   车牌号码
  * @param plateColor
  *   车牌颜色	
  * @param ewayBill
  *   电子运单信息
  * @param accessCode
  *   车辆归属省行政区域编码
  * @param reserved
  *   备注
  */
@SerialVersionUID(0L)
final case class VehicleEwayBill(
    vehicleNo: String,
    plateColor: scala.Option[Int] = None,
    ewayBill: scala.Option[String] = None,
    accessCode: scala.Option[Int] = None,
    reserved: scala.Option[String] = None
    ) extends com.trueaccord.scalapb.GeneratedMessage with com.trueaccord.scalapb.Message[VehicleEwayBill] with com.trueaccord.lenses.Updatable[VehicleEwayBill] {
    @transient
    private[this] var __serializedSizeCachedValue: Int = 0
    private[this] def __computeSerializedValue(): Int = {
      var __size = 0
      __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, vehicleNo)
      if (plateColor.isDefined) { __size += _root_.com.google.protobuf.CodedOutputStream.computeInt32Size(2, plateColor.get) }
      if (ewayBill.isDefined) { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(3, ewayBill.get) }
      if (accessCode.isDefined) { __size += _root_.com.google.protobuf.CodedOutputStream.computeUInt32Size(4, accessCode.get) }
      if (reserved.isDefined) { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(5, reserved.get) }
      __size
    }
    final override def serializedSize: Int = {
      var read = __serializedSizeCachedValue
      if (read == 0) {
        read = __computeSerializedValue()
        __serializedSizeCachedValue = read
      }
      read
    }
    def writeTo(`_output__`: _root_.com.google.protobuf.CodedOutputStream): Unit = {
      _output__.writeString(1, vehicleNo)
      plateColor.foreach { __v =>
        _output__.writeInt32(2, __v)
      };
      ewayBill.foreach { __v =>
        _output__.writeString(3, __v)
      };
      accessCode.foreach { __v =>
        _output__.writeUInt32(4, __v)
      };
      reserved.foreach { __v =>
        _output__.writeString(5, __v)
      };
    }
    def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): ctitc.seagoing.SEAGOING.VehicleEwayBill = {
      var __vehicleNo = this.vehicleNo
      var __plateColor = this.plateColor
      var __ewayBill = this.ewayBill
      var __accessCode = this.accessCode
      var __reserved = this.reserved
      var __requiredFields0: Long = 0x1L
      var _done__ = false
      while (!_done__) {
        val _tag__ = _input__.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __vehicleNo = _input__.readString()
            __requiredFields0 &= 0xfffffffffffffffeL
          case 16 =>
            __plateColor = Some(_input__.readInt32())
          case 26 =>
            __ewayBill = Some(_input__.readString())
          case 32 =>
            __accessCode = Some(_input__.readUInt32())
          case 42 =>
            __reserved = Some(_input__.readString())
          case tag => _input__.skipField(tag)
        }
      }
      if (__requiredFields0 != 0L) { throw new _root_.com.google.protobuf.InvalidProtocolBufferException("Message missing required fields.") } 
      ctitc.seagoing.SEAGOING.VehicleEwayBill(
          vehicleNo = __vehicleNo,
          plateColor = __plateColor,
          ewayBill = __ewayBill,
          accessCode = __accessCode,
          reserved = __reserved
      )
    }
    def withVehicleNo(__v: String): VehicleEwayBill = copy(vehicleNo = __v)
    def getPlateColor: Int = plateColor.getOrElse(0)
    def clearPlateColor: VehicleEwayBill = copy(plateColor = None)
    def withPlateColor(__v: Int): VehicleEwayBill = copy(plateColor = Some(__v))
    def getEwayBill: String = ewayBill.getOrElse("")
    def clearEwayBill: VehicleEwayBill = copy(ewayBill = None)
    def withEwayBill(__v: String): VehicleEwayBill = copy(ewayBill = Some(__v))
    def getAccessCode: Int = accessCode.getOrElse(0)
    def clearAccessCode: VehicleEwayBill = copy(accessCode = None)
    def withAccessCode(__v: Int): VehicleEwayBill = copy(accessCode = Some(__v))
    def getReserved: String = reserved.getOrElse("")
    def clearReserved: VehicleEwayBill = copy(reserved = None)
    def withReserved(__v: String): VehicleEwayBill = copy(reserved = Some(__v))
    def getFieldByNumber(__fieldNumber: Int): scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => vehicleNo
        case 2 => plateColor.orNull
        case 3 => ewayBill.orNull
        case 4 => accessCode.orNull
        case 5 => reserved.orNull
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(vehicleNo)
        case 2 => plateColor.map(_root_.scalapb.descriptors.PInt(_)).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 3 => ewayBill.map(_root_.scalapb.descriptors.PString(_)).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 4 => accessCode.map(_root_.scalapb.descriptors.PInt(_)).getOrElse(_root_.scalapb.descriptors.PEmpty)
        case 5 => reserved.map(_root_.scalapb.descriptors.PString(_)).getOrElse(_root_.scalapb.descriptors.PEmpty)
      }
    }
    override def toString: String = _root_.com.trueaccord.scalapb.TextFormat.printToUnicodeString(this)
    def companion = ctitc.seagoing.SEAGOING.VehicleEwayBill
}

object VehicleEwayBill extends com.trueaccord.scalapb.GeneratedMessageCompanion[ctitc.seagoing.SEAGOING.VehicleEwayBill] {
  implicit def messageCompanion: com.trueaccord.scalapb.GeneratedMessageCompanion[ctitc.seagoing.SEAGOING.VehicleEwayBill] = this
  def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): ctitc.seagoing.SEAGOING.VehicleEwayBill = {
    require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
    val __fields = javaDescriptor.getFields
    ctitc.seagoing.SEAGOING.VehicleEwayBill(
      __fieldsMap(__fields.get(0)).asInstanceOf[String],
      __fieldsMap.get(__fields.get(1)).asInstanceOf[scala.Option[Int]],
      __fieldsMap.get(__fields.get(2)).asInstanceOf[scala.Option[String]],
      __fieldsMap.get(__fields.get(3)).asInstanceOf[scala.Option[Int]],
      __fieldsMap.get(__fields.get(4)).asInstanceOf[scala.Option[String]]
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[ctitc.seagoing.SEAGOING.VehicleEwayBill] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
      ctitc.seagoing.SEAGOING.VehicleEwayBill(
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).get.as[String],
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).flatMap(_.as[scala.Option[Int]]),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).flatMap(_.as[scala.Option[String]]),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).flatMap(_.as[scala.Option[Int]]),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(5).get).flatMap(_.as[scala.Option[String]])
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = SEAGOINGProto.javaDescriptor.getMessageTypes.get(4)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = SEAGOINGProto.scalaDescriptor.messages(4)
  def messageCompanionForFieldNumber(__fieldNumber: Int): _root_.com.trueaccord.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__fieldNumber)
  def enumCompanionForFieldNumber(__fieldNumber: Int): _root_.com.trueaccord.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = ctitc.seagoing.SEAGOING.VehicleEwayBill(
    vehicleNo = ""
  )
  implicit class VehicleEwayBillLens[UpperPB](_l: _root_.com.trueaccord.lenses.Lens[UpperPB, ctitc.seagoing.SEAGOING.VehicleEwayBill]) extends _root_.com.trueaccord.lenses.ObjectLens[UpperPB, ctitc.seagoing.SEAGOING.VehicleEwayBill](_l) {
    def vehicleNo: _root_.com.trueaccord.lenses.Lens[UpperPB, String] = field(_.vehicleNo)((c_, f_) => c_.copy(vehicleNo = f_))
    def plateColor: _root_.com.trueaccord.lenses.Lens[UpperPB, Int] = field(_.getPlateColor)((c_, f_) => c_.copy(plateColor = Some(f_)))
    def optionalPlateColor: _root_.com.trueaccord.lenses.Lens[UpperPB, scala.Option[Int]] = field(_.plateColor)((c_, f_) => c_.copy(plateColor = f_))
    def ewayBill: _root_.com.trueaccord.lenses.Lens[UpperPB, String] = field(_.getEwayBill)((c_, f_) => c_.copy(ewayBill = Some(f_)))
    def optionalEwayBill: _root_.com.trueaccord.lenses.Lens[UpperPB, scala.Option[String]] = field(_.ewayBill)((c_, f_) => c_.copy(ewayBill = f_))
    def accessCode: _root_.com.trueaccord.lenses.Lens[UpperPB, Int] = field(_.getAccessCode)((c_, f_) => c_.copy(accessCode = Some(f_)))
    def optionalAccessCode: _root_.com.trueaccord.lenses.Lens[UpperPB, scala.Option[Int]] = field(_.accessCode)((c_, f_) => c_.copy(accessCode = f_))
    def reserved: _root_.com.trueaccord.lenses.Lens[UpperPB, String] = field(_.getReserved)((c_, f_) => c_.copy(reserved = Some(f_)))
    def optionalReserved: _root_.com.trueaccord.lenses.Lens[UpperPB, scala.Option[String]] = field(_.reserved)((c_, f_) => c_.copy(reserved = f_))
  }
  final val VEHICLENO_FIELD_NUMBER = 1
  final val PLATECOLOR_FIELD_NUMBER = 2
  final val EWAYBILL_FIELD_NUMBER = 3
  final val ACCESSCODE_FIELD_NUMBER = 4
  final val RESERVED_FIELD_NUMBER = 5
}
