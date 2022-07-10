/*
 * Designed and developed by 2020 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.development.hiltpractices.data.local.room

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.development.hiltpractices.util.JsonUtil
import com.development.hiltpractices.util.debug.LogInfo
import java.util.*

@ProvidedTypeConverter
class RoomConverter {

  @TypeConverter
  fun fromArrayLogInfoString(value: String): List<LogInfo> {
    return JsonUtil.fromJson(value)
  }

  @TypeConverter
  fun fromArrayLogInfoType(type: List<LogInfo>): String {
    return JsonUtil.toJson(type)
  }

  @TypeConverter
  fun fromString(value: String): Date {
    return JsonUtil.fromJson(value)
  }

  @TypeConverter
  fun fromInfoType(type: Date): String {
    return JsonUtil.toJson(type)
  }
}
