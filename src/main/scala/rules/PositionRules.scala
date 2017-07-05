package rules

/**
  * Created by Kasim on 2017/6/26.
  */

import java.text.{ParseException, SimpleDateFormat}

import scala.collection._
import ctitc.seagoing.SEAGOING._

class PositionRules() extends Serializable{

  val provSet = Set("京","吉","辽","蒙","晋","冀",
    "津","新","宁","青","甘","陕", "藏","云","贵",
    "川","渝","琼", "桂","粤","湘","鄂","皖","鲁",
    "赣","闽","豫","浙","苏","沪","黑")
  val provHashMap = immutable.HashMap[Int, String](110000 -> "京",
    220000 -> "吉",
    210000 -> "辽",
    150000 -> "蒙",
    140000 -> "晋",
    130000 -> "冀",
    120000 -> "津",
    650000 -> "新",
    640000 -> "宁",
    630000 -> "青",
    620000 -> "甘",
    610000 -> "陕",
    540000 -> "藏",
    530000 -> "云",
    520000 -> "贵",
    510000 -> "川",
    500000 -> "渝",
    460000 -> "琼",
    450000 -> "桂",
    440000 -> "粤",
    430000 -> "湘",
    420000 -> "鄂",
    340000 -> "皖",
    370000 -> "鲁",
    360000 -> "赣",
    350000 -> "闽",
    410000 -> "豫",
    330000 -> "浙",
    320000 -> "苏",
    310000 -> "沪",
    230000 -> "黑")

  def repeatFilter(int:Int) : Int = {
    if(int == 1) 110000
    else if(int == 2) 120000
    else if(int == 3) 130000
    else if(int == 4) 140000
    else if(int == 5) 150000
    else if(int == 6) 210000
    else if(int == 7) 220000
    else if(int == 8) 230000
    else if(int == 9) 310000
    else if(int == 10) 320000
    else if(int == 11) 330000
    else if(int == 12) 340000
    else if(int == 13) 350000
    else if(int == 14) 360000
    else if(int == 15) 370000
    else if(int == 16) 410000
    else if(int == 17) 420000
    else if(int == 18) 430000
    else if(int == 19) 440000
    else if(int == 20) 450000
    else if(int == 21) 460000
    else if(int == 22) 500000
    else if(int == 23) 510000
    else if(int == 24) 520000
    else if(int == 25) 530000
    else if(int == 26) 540000
    else if(int == 27) 610000
    else if(int == 28) 620000
    else if(int == 29) 630000
    else if(int == 30) 640000
    else if(int == 31) 650000
    else if(int == 0) 0
    else 0
  }

  // 1001 车牌长度小于6或大于10
  // 1002 车牌第一位错误
  // 1003 车牌不符合规则
  // 1101 车牌颜色不是1,2,3,4,8,9
  // 1202 经度错误
  // 1203 纬度错误
  // 1302 海拔低于负200米或高于6000米
  // 1402 速度小于0或者大于160
  // 1502 方向小于0或者大于360
  // 1601 时间格式错误
  // 1602 接收时间早于定位时间
  def positionJudge(vehiclePosition: VehiclePosition) : Any = {
    val stringBuilder = new StringBuilder
    // 1001
    if((10 < vehiclePosition.vehicleNo.length()) || (vehiclePosition.vehicleNo.length() < 6)) stringBuilder.append("1")
    else stringBuilder.append("0")

    if(vehiclePosition.vehicleNo.length() > 0) {
      // 1002
      if(provHashMap.get(vehiclePosition.accessCode) == null || !provSet.contains(vehiclePosition.vehicleNo.substring(0, 1))) {
        stringBuilder.append("1")
      } else {
        stringBuilder.append("0")
      }

      // 1003
      if(!"^[A-Z]+[A-Z0-9]+[A-Z0-9挂学]$".r.pattern.matcher(vehiclePosition.vehicleNo.substring(1).toUpperCase()).matches()) {
        stringBuilder.append("1")
      } else stringBuilder.append("0")
    } else {
      stringBuilder.append("0")
      stringBuilder.append("0")
    }

    // 1101
    if(((vehiclePosition.getPlateColor > 0)  && vehiclePosition.getPlateColor < 5) || vehiclePosition.getPlateColor == 8 || vehiclePosition.getPlateColor == 9) stringBuilder.append("0")
    else stringBuilder.append("1")

    // 1202
    if((vehiclePosition.gnss.lon < 73330000) || (vehiclePosition.gnss.lon > 135050000)) stringBuilder.append("1")
    else stringBuilder.append("0")

    // 1202
    if((vehiclePosition.gnss.lat < 3510000) || (vehiclePosition.gnss.lat > 53330000)) stringBuilder.append("1")
    else stringBuilder.append("0")

    // 1301
    if(vehiclePosition.gnss.getAltitude > 6000) stringBuilder.append("1")
    else stringBuilder.append("0")

    // 1402
    if((vehiclePosition.gnss.getVec1 > 160) || (vehiclePosition.gnss.getVec1 < 0)) stringBuilder.append("1")
    else stringBuilder.append("0")

    // 1502
    if((vehiclePosition.gnss.getDirection > 360) || (vehiclePosition.gnss.getDirection < 0)) stringBuilder.append("1")
    else stringBuilder.append("0")

    // 1601
    try {
      if("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$".r.pattern.matcher(vehiclePosition.gnss.positionTime).matches()) {
        val pTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(vehiclePosition.gnss.positionTime).getTime / 1000
        stringBuilder.append("0")

        // 1602
        if(vehiclePosition.updateTime >= pTime) stringBuilder.append("0")
        else stringBuilder.append("1")

      } else {
        stringBuilder.append("1")
        stringBuilder.append("1")
      }
    } catch {
      case e:ParseException => {
        stringBuilder.append("1")
        stringBuilder.append("1")
      }
    }
  }
}
