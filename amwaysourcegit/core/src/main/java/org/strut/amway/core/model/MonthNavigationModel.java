package org.strut.amway.core.model;

public class MonthNavigationModel {

    private static final String NOT_FOUND_LABEL = "Not Found";

    private final String label;
    private final DateTimeRange dateTimeRange;

    public MonthNavigationModel(final String label, final DateTimeRange dateTimeRange) {
        if (label == null) {
            this.label = NOT_FOUND_LABEL;
        } else {
            this.label = label;
        }
        this.dateTimeRange = dateTimeRange;
    }

    public String getLabel() {
        return label;
    }

    public DateTimeRange getDateTimeRange() {
        return dateTimeRange;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateTimeRange == null) ? 0 : dateTimeRange.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MonthNavigationModel other = (MonthNavigationModel) obj;
        if (dateTimeRange == null) {
            if (other.dateTimeRange != null)
                return false;
        } else if (!dateTimeRange.equals(other.dateTimeRange))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MonthNavigationModel").append(" [");
        sb.append("label=").append(label);
        sb.append("dateTimeRange=").append(dateTimeRange);
        sb.append(" ]");
        return sb.toString();
    }

}
