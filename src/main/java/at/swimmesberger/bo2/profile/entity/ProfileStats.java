package at.swimmesberger.bo2.profile.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProfileStats {
    public static final double MAXIMUM_STAT_VALUE = 9975792.3;
    public static final double MINIMUM_STAT_VALUE = 0;
    public static final double PROFILE_STEP_VALUE = 0.75;
    private static final int STAT_VALUE_COUNT = 14;

    private final double maximumHealth;
    private final double shieldCapacity;
    private final double shieldRechargeDelay;
    private final double shieldRechargeRate;
    private final double meleeDamage;
    private final double grenadeDamage;
    private final double gunAccuracy;
    private final double gunDamage;
    private final double fireRate;
    private final double recoilReduction;
    private final double reloadSpeed;
    private final double elementalEffectChance;
    private final double elementalEffectDamage;
    private final double criticalHitDamage;

    public ProfileStats(double maximumHealth, double shieldCapacity, double shieldRechargeDelay, double shieldRechargeRate, double meleeDamage,
                        double grenadeDamage, double gunAccuracy, double gunDamage, double fireRate, double recoilReduction, double reloadSpeed,
                        double elementalEffectChance, double elementalEffectDamage, double criticalHitDamage) {
        this.maximumHealth = maximumHealth;
        this.shieldCapacity = shieldCapacity;
        this.shieldRechargeDelay = shieldRechargeDelay;
        this.shieldRechargeRate = shieldRechargeRate;
        this.meleeDamage = meleeDamage;
        this.grenadeDamage = grenadeDamage;
        this.gunAccuracy = gunAccuracy;
        this.gunDamage = gunDamage;
        this.fireRate = fireRate;
        this.recoilReduction = recoilReduction;
        this.reloadSpeed = reloadSpeed;
        this.elementalEffectChance = elementalEffectChance;
        this.elementalEffectDamage = elementalEffectDamage;
        this.criticalHitDamage = criticalHitDamage;
    }

    public static ProfileStats fromDoubleValues(List<Double> values) {
        if (values.size() != STAT_VALUE_COUNT) throw new IllegalArgumentException();

        return new ProfileStats(values.get(0), values.get(1), values.get(2), values.get(3), values.get(4),
                values.get(5), values.get(6), values.get(7), values.get(8), values.get(9), values.get(10),
                values.get(11), values.get(12), values.get(13));
    }

    public static ProfileStats max() {
        List<Double> values = new ArrayList<>(STAT_VALUE_COUNT);
        for (int i = 0; i < STAT_VALUE_COUNT; i++) {
            values.add(MAXIMUM_STAT_VALUE);
        }
        return fromDoubleValues(values);
    }

    public static ProfileStats min() {
        List<Double> values = new ArrayList<>(STAT_VALUE_COUNT);
        for (int i = 0; i < STAT_VALUE_COUNT; i++) {
            values.add(MINIMUM_STAT_VALUE);
        }
        return fromDoubleValues(values);
    }

    public List<Double> toDoubleList() {
        return Arrays.asList(this.maximumHealth, this.shieldCapacity, this.shieldRechargeDelay, this.shieldRechargeRate, this.meleeDamage,
                            this.grenadeDamage, this.gunAccuracy, this.gunDamage, this.fireRate, this.recoilReduction, this.reloadSpeed,
                            this.elementalEffectChance, this.elementalEffectDamage, this.criticalHitDamage);
    }

    public double getMaximumHealth() {
        return maximumHealth;
    }

    public double getShieldCapacity() {
        return shieldCapacity;
    }

    public double getShieldRechargeDelay() {
        return shieldRechargeDelay;
    }

    public double getShieldRechargeRate() {
        return shieldRechargeRate;
    }

    public double getMeleeDamage() {
        return meleeDamage;
    }

    public double getGrenadeDamage() {
        return grenadeDamage;
    }

    public double getGunAccuracy() {
        return gunAccuracy;
    }

    public double getGunDamage() {
        return gunDamage;
    }

    public double getFireRate() {
        return fireRate;
    }

    public double getRecoilReduction() {
        return recoilReduction;
    }

    public double getReloadSpeed() {
        return reloadSpeed;
    }

    public double getElementalEffectChance() {
        return elementalEffectChance;
    }

    public double getElementalEffectDamage() {
        return elementalEffectDamage;
    }

    public double getCriticalHitDamage() {
        return criticalHitDamage;
    }

    @Override
    public String toString() {
        return "ProfileStats{" +
                "maximumHealth=" + maximumHealth +
                ", shieldCapacity=" + shieldCapacity +
                ", shieldRechargeDelay=" + shieldRechargeDelay +
                ", shieldRechargeRate=" + shieldRechargeRate +
                ", meleeDamage=" + meleeDamage +
                ", grenadeDamage=" + grenadeDamage +
                ", gunAccuracy=" + gunAccuracy +
                ", gunDamage=" + gunDamage +
                ", fireRate=" + fireRate +
                ", recoilReduction=" + recoilReduction +
                ", reloadSPeed=" + reloadSpeed +
                ", elementalEffectChance=" + elementalEffectChance +
                ", elementalEffectDamage=" + elementalEffectDamage +
                ", criticalHitDamage=" + criticalHitDamage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileStats stats = (ProfileStats) o;
        return Double.compare(stats.maximumHealth, maximumHealth) == 0 &&
                Double.compare(stats.shieldCapacity, shieldCapacity) == 0 &&
                Double.compare(stats.shieldRechargeDelay, shieldRechargeDelay) == 0 &&
                Double.compare(stats.shieldRechargeRate, shieldRechargeRate) == 0 &&
                Double.compare(stats.meleeDamage, meleeDamage) == 0 &&
                Double.compare(stats.grenadeDamage, grenadeDamage) == 0 &&
                Double.compare(stats.gunAccuracy, gunAccuracy) == 0 &&
                Double.compare(stats.gunDamage, gunDamage) == 0 &&
                Double.compare(stats.fireRate, fireRate) == 0 &&
                Double.compare(stats.recoilReduction, recoilReduction) == 0 &&
                Double.compare(stats.reloadSpeed, reloadSpeed) == 0 &&
                Double.compare(stats.elementalEffectChance, elementalEffectChance) == 0 &&
                Double.compare(stats.elementalEffectDamage, elementalEffectDamage) == 0 &&
                Double.compare(stats.criticalHitDamage, criticalHitDamage) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maximumHealth, shieldCapacity, shieldRechargeDelay, shieldRechargeRate, meleeDamage, grenadeDamage, gunAccuracy, gunDamage, fireRate, recoilReduction, reloadSpeed, elementalEffectChance, elementalEffectDamage, criticalHitDamage);
    }
}
