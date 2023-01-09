package me.blvckbytes.bukkitevaluable.section;

import lombok.Getter;
import me.blvckbytes.bbconfigmapper.sections.IConfigSection;
import me.blvckbytes.bukkitevaluable.BukkitEvaluable;
import me.blvckbytes.gpeee.interpreter.IEvaluationEnvironment;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

/*
  Author: BlvckBytes <blvckbytes@gmail.com>
  Created On: 06/28/2022

  Represents the base effect set on a potion item stack.

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published
  by the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
@Getter
public class ItemStackBaseEffectSection implements IConfigSection {

  private @Nullable BukkitEvaluable type;
  private @Nullable Boolean extended;
  private @Nullable Boolean upgraded;

  public @Nullable PotionData asData(IEvaluationEnvironment environment) {
    boolean _upgraded = upgraded != null && upgraded;
    boolean _extended = extended != null && extended;

    PotionType type = this.type == null ? null : this.type.asPotionType(environment);

    if (type == null)
      return null;

    // Potions cannot be both extended and upgraded at the same
    // time, focus the priority on the upgraded flag
    return new PotionData(type, !_upgraded && _extended, _upgraded);
  }

  public boolean describesData(PotionData data, IEvaluationEnvironment environment) {
    if (this.type != null) {
      PotionType potionType = this.type.asPotionType(environment);
      if (potionType != null && !potionType.equals(data.getType()))
        return false;
    }

    if (this.extended != null) {
      if (this.extended != data.isExtended())
        return false;
    }

    if (this.upgraded != null) {
      if (this.upgraded != data.isExtended())
        return false;
    }

    return true;
  }
}
