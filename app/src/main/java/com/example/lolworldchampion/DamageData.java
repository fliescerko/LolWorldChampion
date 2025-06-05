package com.example.lolworldchampion;

import java.io.Serializable;
import java.util.Objects;

public class DamageData implements Serializable {
    private boolean basic;
    private int magicDamage;
    private int physicalDamage;
    private int trueDamage;
    private String name;
    private Integer participantId;
    private String spellName;
    private Integer spellSlot;
    private String type;

    public boolean isBasic() { return basic; }
    public void setBasic(boolean basic) { this.basic = basic; }

    public int getMagicDamage() { return magicDamage; }
    public void setMagicDamage(int magicDamage) { this.magicDamage = magicDamage; }

    public int getPhysicalDamage() { return physicalDamage; }
    public void setPhysicalDamage(int physicalDamage) { this.physicalDamage = physicalDamage; }

    public int getTrueDamage() { return trueDamage; }
    public void setTrueDamage(int trueDamage) { this.trueDamage = trueDamage; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getParticipantId() { return participantId; }
    public void setParticipantId(Integer participantId) { this.participantId = participantId; }

    public String getSpellName() { return spellName; }
    public void setSpellName(String spellName) { this.spellName = spellName; }

    public Integer getSpellSlot() { return spellSlot; }
    public void setSpellSlot(Integer spellSlot) { this.spellSlot = spellSlot; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }


    public int getTotalDamage() {
        return physicalDamage + magicDamage + trueDamage;
    }

    public void setTotalDamage(int totalDamage) {
        float total = getTotalDamage();
        if (total > 0) {
            float ratio = (float) totalDamage / total;
            this.physicalDamage = (int) (physicalDamage * ratio);
            this.magicDamage = (int) (magicDamage * ratio);
            this.trueDamage = (int) (trueDamage * ratio);
        }
    }


    @Override
    public String toString() {
        return "伤害来源: " + (name != null ? name : "未知") + "\n" +
                "参与者ID: " + (participantId != null ? participantId : "未知") + "\n" +
                "物理伤害: " + physicalDamage + "\n" +
                "魔法伤害: " + magicDamage + "\n" +
                "真实伤害: " + trueDamage + "\n" +
                "总伤害: " + getTotalDamage() + "\n" +
                "技能名称: " + (spellName != null ? spellName : "未知") + "\n" +
                "技能槽位: " + (spellSlot != null ? spellSlot : "未知") + "\n" +
                "伤害类型: " + (type != null ? type : "未知") + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DamageData that = (DamageData) o;
        return basic == that.basic &&
                magicDamage == that.magicDamage &&
                physicalDamage == that.physicalDamage &&
                trueDamage == that.trueDamage &&
                Objects.equals(name, that.name) &&
                Objects.equals(participantId, that.participantId) &&
                Objects.equals(spellName, that.spellName) &&
                Objects.equals(spellSlot, that.spellSlot) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(basic, magicDamage, physicalDamage, trueDamage,
                name, participantId, spellName, spellSlot, type);
    }
}