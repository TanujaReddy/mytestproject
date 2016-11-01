package org.strut.amway.core.enumeration;

public enum MonthMessageKey {

    JANUARY("janMonth"),
    FEBRUARY("febMonth"),
    MARCH("marMonth"),
    APRIL("aprMonth"),
    MAY("mayMonth"),
    JUNE("junMonth"),
    JULY("julMonth"),
    AUGUST("augMonth"),
    SEPTEMBER("sepMonth"),
    OCTOBER("octMonth"),
    NOVEMBER("novMonth"),
    DECEMBER("decMonth");

    private String messageKey;

    MonthMessageKey(final String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public static MonthMessageKey resolve(int month) {
        switch (month) {
        case 1:
            return MonthMessageKey.JANUARY;
        case 2:
            return MonthMessageKey.FEBRUARY;
        case 3:
            return MonthMessageKey.MARCH;
        case 4:
            return MonthMessageKey.APRIL;
        case 5:
            return MonthMessageKey.MAY;
        case 6:
            return MonthMessageKey.JUNE;
        case 7:
            return MonthMessageKey.JULY;
        case 8:
            return MonthMessageKey.AUGUST;
        case 9:
            return MonthMessageKey.SEPTEMBER;
        case 10:
            return MonthMessageKey.OCTOBER;
        case 11:
            return MonthMessageKey.NOVEMBER;
        case 12:
            return MonthMessageKey.DECEMBER;

        default:
            throw new IllegalStateException("Invalid Month " + month);
        }
    }

}
