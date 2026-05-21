package Models;

import java.math.BigDecimal;

public class ReferenceItem {

    private final int id;
    private final String label;
    private final Integer availableStock;
    private final BigDecimal unitWeightKg;

    public ReferenceItem(int id, String label) {
        this(id, label, null, null);
    }

    public ReferenceItem(int id, String label, Integer availableStock, BigDecimal unitWeightKg) {
        this.id = id;
        this.label = label == null ? "" : label;
        this.availableStock = availableStock;
        this.unitWeightKg = unitWeightKg;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public BigDecimal getUnitWeightKg() {
        return unitWeightKg;
    }

    @Override
    public String toString() {
        return label;
    }
}
